# Troubleshooting - Problemas Resueltos en Autenticación JWT

Este documento describe los problemas encontrados durante la implementación del sistema de autenticación JWT centralizado y las soluciones aplicadas.

## Contexto

El sistema utiliza un patrón de **validación JWT centralizada**:
- **pyg-auth** (puerto 8081): Servicio que emite y valida tokens JWT
- **pyg-owner** (puerto 8082): Servicio de gestión de dueños de mascotas
- **pyg-professional** (puerto 8083): Servicio de gestión de profesionales

Los servicios `pyg-owner` y `pyg-professional` NO validan tokens localmente. En su lugar, llaman al endpoint `/api/auth/validate` de `pyg-auth` mediante **Spring Cloud OpenFeign**.

---

## Problema #1: @EnableFeignClients faltante

### Síntoma
- Los endpoints protegidos de `pyg-owner` respondían `403 Forbidden`
- Los logs mostraban: `"Error communicating with pyg-auth. Circuit breaker triggered."`
- El fallback de Feign se ejecutaba inmediatamente sin intentar llamar a pyg-auth

### Diagnóstico
```java
// AuthServiceClientFallback.java
@Override
public TokenValidationResponse validateToken(String token) {
    log.error("Error communicating with pyg-auth. Circuit breaker triggered.");
    throw new RuntimeException("Authentication service unavailable");
}
```

El fallback se ejecutaba porque **Feign no estaba inicializado**. Spring no creaba proxies dinámicos para los clientes Feign.

### Causa Raíz
Faltaba la anotación `@EnableFeignClients` en la clase principal:

```java
@SpringBootApplication
// @EnableFeignClients <- FALTABA ESTA ANOTACIÓN
public class PygOwnerApplication {
    public static void main(String[] args) {
        SpringApplication.run(PygOwnerApplication.class, args);
    }
}
```

### Solución
```java
@SpringBootApplication
@EnableFeignClients  // ✅ AGREGADO
public class PygOwnerApplication {
    public static void main(String[] args) {
        SpringApplication.run(PygOwnerApplication.class, args);
    }
}
```

### Impacto
- Spring ahora escanea interfaces anotadas con `@FeignClient`
- Se crean proxies HTTP dinámicos para `AuthServiceClient`
- Las llamadas a `authServiceClient.validateToken()` ahora ejecutan requests HTTP reales
- El fallback solo se ejecuta en caso de error real de comunicación

---

## Problema #2: JWT Filter bloqueando endpoint de validación

### Síntoma
- Después de arreglar `@EnableFeignClients`, las llamadas desde `pyg-owner` a `pyg-auth/api/auth/validate` fallaban
- pyg-auth respondía `403 Forbidden` a su propio endpoint de validación
- Los logs de pyg-auth mostraban que el `JwtAuthenticationFilter` procesaba requests a `/api/auth/validate`

### Diagnóstico
```
pyg-owner -> POST /api/auth/validate con header Authorization: Bearer <token>
    ↓
pyg-auth recibe request
    ↓
JwtAuthenticationFilter de pyg-auth valida el token
    ↓
¿Pero quién valida el token para validar tokens? 🤔
    ↓
LÓGICA CIRCULAR → 403 Forbidden
```

### Causa Raíz
El `JwtAuthenticationFilter` en **pyg-auth** estaba aplicándose a TODAS las rutas, incluyendo `/api/auth/*`:

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, ...) {
        // Este filtro se ejecutaba para /api/auth/validate también
        // creando lógica circular
    }
}
```

### Solución
Excluir rutas públicas y de autenticación del filtro JWT:

```java
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Rutas que no requieren validación JWT - continuar sin autenticar
        String path = request.getRequestURI();
        if (path.startsWith("/api/auth/") ||    // ✅ EXCLUIR /api/auth/*
            path.startsWith("/v3/api-docs") ||   // ✅ EXCLUIR Swagger
            path.startsWith("/swagger-ui")) {
            filterChain.doFilter(request, response);
            return;
        }

        // ... resto de la lógica de validación ...
    }
}
```

### Alternativa considerada
También se podría usar `shouldNotFilter()`:

```java
@Override
protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();
    return path.startsWith("/api/auth/") ||
           path.startsWith("/v3/api-docs") ||
           path.startsWith("/swagger-ui");
}
```

### Impacto
- `/api/auth/register`, `/api/auth/login`, `/api/auth/validate` son públicos
- pyg-owner y pyg-professional pueden llamar a `/api/auth/validate` sin autenticación previa
- Se elimina la lógica circular

---

## Problema #3: SecurityContext no establecido

### Síntoma
- Después de arreglar los dos problemas anteriores:
  - ✅ Feign funcionaba correctamente
  - ✅ pyg-auth respondía con token válido
  - ✅ `JwtAuthenticationFilter` de pyg-owner recibía validación exitosa
- **PERO** Spring Security seguía respondiendo `403 Forbidden`
- Los logs mostraban: "Token validated successfully" pero luego 403

### Diagnóstico
```java
// pyg-owner/security/JwtAuthenticationFilter.java (VERSIÓN CON ERROR)
var validation = authServiceClient.validateToken(normalizedHeader);

if (validation.isValid()) {
    log.info("Token validated successfully");
    
    // ❌ PROBLEMA: Solo guardamos datos en request attributes
    request.setAttribute("auth.userId", validation.getUserId());
    request.setAttribute("auth.username", validation.getUsername());
    request.setAttribute("auth.role", validation.getRole());
    
    filterChain.doFilter(request, response);
}
```

Spring Security evaluaba:
```java
// SecurityConfig.java
.requestMatchers("/owners/**").hasRole("OWNER")
```

**Spring Security no sabía que el usuario estaba autenticado** porque:
- `request.setAttribute()` NO comunica con Spring Security
- `SecurityContextHolder.getContext().getAuthentication()` devolvía `null`
- Spring Security interpretaba: "No hay autenticación → 403 Forbidden"

### Causa Raíz
**No se estableció el contexto de seguridad de Spring Security.**

Spring Security utiliza `SecurityContextHolder` (ThreadLocal) para almacenar la autenticación. Los filtros deben crear un `Authentication` y establecerlo explícitamente.

### Solución
Establecer `SecurityContext` correctamente:

```java
var validation = authServiceClient.validateToken(normalizedHeader);

if (validation == null || !validation.isValid()) {
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    response.getWriter().write("Invalid or expired token");
    return;
}

// ✅ 1. Crear autoridades de Spring Security
var authorities = List.of(
    new SimpleGrantedAuthority(validation.getRole())  // "ROLE_OWNER"
);

// ✅ 2. Crear token de autenticación (3-arg constructor marca como authenticated)
var authToken = new UsernamePasswordAuthenticationToken(
    validation.getUsername(),  // principal
    null,                      // credentials (no necesarias después de autenticación)
    authorities                // authorities
);

// ✅ 3. Establecer detalles adicionales (opcional)
authToken.setDetails(validation);

// ✅ 4. CRÍTICO: Establecer en SecurityContextHolder
SecurityContextHolder.getContext().setAuthentication(authToken);

// Opcional: también en request attributes para capas posteriores
request.setAttribute("auth.userId", validation.getUserId());
request.setAttribute("auth.username", validation.getUsername());
request.setAttribute("auth.role", validation.getRole());

filterChain.doFilter(request, response);
```

### Imports necesarios
```java
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;
```

### Flujo completo después de la corrección
```
1. Request: POST /owners con Authorization: Bearer <token>
   ↓
2. JwtAuthenticationFilter.doFilterInternal()
   ↓
3. authServiceClient.validateToken() → pyg-auth responde {valid: true, role: "ROLE_OWNER"}
   ↓
4. Crear SimpleGrantedAuthority("ROLE_OWNER")
   ↓
5. Crear UsernamePasswordAuthenticationToken con authorities
   ↓
6. SecurityContextHolder.getContext().setAuthentication(authToken)  ← CRÍTICO
   ↓
7. filterChain.doFilter() → pasa a SecurityConfig
   ↓
8. SecurityConfig evalúa .hasRole("OWNER")
   ↓
9. SecurityContextHolder tiene Authentication con ROLE_OWNER ✅
   ↓
10. Acceso permitido → 200 OK
```

### Impacto
- Spring Security ahora ve la autenticación
- `.hasRole("OWNER")` funciona correctamente
- Los endpoints protegidos responden 200 en lugar de 403

---

## Resumen de Soluciones

| Problema | Componente | Solución |
|----------|-----------|----------|
| **Feign no inicializado** | `PygOwnerApplication` | Agregar `@EnableFeignClients` |
| **Filtro JWT bloqueando validación** | `pyg-auth/JwtAuthenticationFilter` | Excluir `/api/auth/*` del filtro |
| **SecurityContext no establecido** | `pyg-owner/JwtAuthenticationFilter` | Llamar `SecurityContextHolder.getContext().setAuthentication()` |

---

## Testing del flujo completo

### 1. Registrar usuario
```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "juanperez",
    "password": "mipassword123",
    "firstname": "Juan",
    "lastname": "Pérez",
    "email": "juan.perez@example.com",
    "role": "ROLE_OWNER"
  }'
```

### 2. Obtener token
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "juanperez",
    "password": "mipassword123"
  }'
```

Respuesta:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expirationDate": "2025-11-08T12:00:00"
}
```

### 3. Crear owner con token
```bash
curl -X POST http://localhost:8082/owners \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -d '{
    "userId": 5,
    "phone": "+56912345679",
    "address": "Calle Principal 456"
  }'
```

Respuesta exitosa:
```json
{
  "id": 1,
  "userId": 5,
  "phone": "+56912345679",
  "address": "Calle Principal 456"
}
```


---

## Lecciones aprendidas

1. **@EnableFeignClients es obligatorio**: Spring no inicializa clientes Feign sin esta anotación.

2. **Filtros JWT deben excluir endpoints públicos**: Siempre excluir rutas de autenticación para evitar lógica circular.

3. **SecurityContextHolder es la fuente de verdad**: Spring Security solo conoce autenticación si está en `SecurityContextHolder`, no en `request.setAttribute()`.

4. **UsernamePasswordAuthenticationToken 3-arg constructor**: Usar el constructor de 3 argumentos marca el token como autenticado automáticamente.

5. **Debugging de autenticación**: Verificar en orden:
   - ¿El cliente HTTP está configurado correctamente?
   - ¿El servicio de autenticación responde sin bloqueos?
   - ¿El SecurityContext se estableció correctamente?

---

## Referencias

- [Spring Security Architecture](https://docs.spring.io/spring-security/reference/servlet/architecture.html)
- [Spring Cloud OpenFeign](https://docs.spring.io/spring-cloud-openfeign/docs/current/reference/html/)
- [SecurityContextHolder Javadoc](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/core/context/SecurityContextHolder.html)
