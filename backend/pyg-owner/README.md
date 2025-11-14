# 🐾 pyg-owner — Owner & Pet Management Microservice

Este microservicio forma parte del proyecto **PerrosYGatos**, una plataforma que conecta **dueños de mascotas** con **profesionales especializados** (etólogos y entrenadores).

El servicio `pyg-owner` es responsable de la **gestión de perfiles de dueños y sus mascotas**, permitiendo operaciones CRUD completas para ambos recursos.

---

## ⚙️ Funcionalidad principal

`pyg-owner` maneja todo lo relacionado con los **dueños de mascotas y sus animales**:

- Creación y actualización de perfiles de dueños
- Consulta de información personal del dueño autenticado
- Registro de mascotas asociadas al dueño
- Listado de mascotas del dueño
- Actualización de información de mascotas
- Validación de tokens JWT mediante **pyg-auth**
- Documentación automática con **Swagger / OpenAPI**

> 🔐 Este servicio **NO valida tokens JWT localmente**. Todas las validaciones se realizan mediante llamadas al servicio `pyg-auth` usando **Spring Cloud OpenFeign**, siguiendo el patrón de validación centralizada.

---

## 🧩 Arquitectura y estructura

```
pyg-owner/
├── src/
│   ├── main/java/com/owner/pyg_owner/
│   │   ├── clients/         # Clientes Feign (AuthServiceClient)
│   │   ├── config/          # Configuración (Security, OpenAPI, Beans)
│   │   ├── controllers/     # Controladores REST (Owner, Pet)
│   │   ├── dto/             # DTOs (requests y responses)
│   │   ├── models/          # Entidades JPA (Owner, Pet)
│   │   ├── repositories/    # Interfaces JPA
│   │   ├── security/        # Filtro JWT (JwtAuthenticationFilter)
│   │   └── services/        # Lógica de negocio (OwnerService, PetService)
│   └── resources/
│       └── application.yml
└── pom.xml
```

---

## 🧠 Tecnologías utilizadas

| Componente | Descripción |
|-----------|-------------|
| ☕ **Java 17** | Lenguaje base |
| 🌱 **Spring Boot 3.5.7** | Framework principal |
| 🔐 **Spring Security** | Protección de endpoints |
| 🗄️ **Spring Data JPA + PostgreSQL** | Persistencia de datos |
| ☁️ **Spring Cloud OpenFeign 4.3.0** | Cliente HTTP para comunicación con pyg-auth |
| 🧩 **Lombok** | Reducción de código repetitivo |
| ✅ **Spring Validation** | Validación de datos de entrada |
| 📘 **Swagger (Springdoc OpenAPI)** | Documentación interactiva de endpoints |
| 🧰 **Maven** | Gestión de dependencias y build |

---

## 🔗 Endpoints principales

### 👤 Gestión de Dueños

| Método | Endpoint | Descripción | Acceso |
|--------|----------|-------------|--------|
| **POST** | `/owners` | Crear o actualizar perfil de dueño | Protegido (ROLE_OWNER) |
| **GET** | `/owners/profile` | Obtener perfil del dueño autenticado | Protegido (ROLE_OWNER) |

### 🐕 Gestión de Mascotas

| Método | Endpoint | Descripción | Acceso | Estado |
|--------|----------|-------------|--------|--------|
| **POST** | `/pets` | Registrar nueva mascota | Protegido (ROLE_OWNER) | ✅ Implementado |
| **GET** | `/pets` | Listar mascotas del dueño autenticado | Protegido (ROLE_OWNER) | ✅ Implementado |
| **GET** | `/pets/{id}` | Obtener detalles de una mascota específica | Protegido (ROLE_OWNER) | ✅ Implementado |
| **PUT** | `/pets/{id}` | Actualizar datos de una mascota | Protegido (ROLE_OWNER) | 🚧 Pendiente |

---

## 🧾 Ejemplos de uso

### 🔹 Crear perfil de dueño (`POST /owners`)

**Request:**
```json
{
  "userId": 5,
  "phone": "+56912345678",
  "address": "Av. Providencia 123, Santiago"
}
```

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response:**
```json
{
  "id": 1,
  "userId": 5,
  "phone": "+56912345678",
  "address": "Av. Providencia 123, Santiago"
}
```

---

### 🔹 Obtener perfil propio (`GET /owners/profile`)

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response:**
```json
{
  "id": 1,
  "userId": 5,
  "phone": "+56912345678",
  "address": "Av. Providencia 123, Santiago"
}
```

---

### 🔹 Registrar mascota (`POST /pets`)

**Request:**
```json
{
  "name": "Max",
  "species": "Perro",
  "breed": "Golden Retriever",
  "age": 3,
  "weight": 30.5,
  "description": "Muy juguetón y amigable"
}
```

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response:**
```json
{
  "id": 1,
  "name": "Max",
  "species": "Perro",
  "breed": "Golden Retriever",
  "age": 3,
  "weight": 30.5,
  "description": "Muy juguetón y amigable",
  "ownerId": 1
}
```

---

### 🔹 Listar mascotas (`GET /pets`)

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response:**
```json
[
  {
    "id": 1,
    "name": "Max",
    "species": "Perro",
    "breed": "Golden Retriever",
    "age": 3,
    "weight": 30.5,
    "description": "Muy juguetón y amigable",
    "ownerId": 1
  },
  {
    "id": 2,
    "name": "Luna",
    "species": "Gato",
    "breed": "Siamés",
    "age": 2,
    "weight": 4.2,
    "description": "Muy independiente",
    "ownerId": 1
  }
]
```

---

## 🔒 Seguridad y Validación de Tokens

### Arquitectura de Validación Centralizada

Este servicio implementa un **patrón de validación JWT centralizada**:

1. **Cliente recibe token JWT** de `pyg-auth` (después de login/registro)
2. **Cliente envía request** a `pyg-owner` con header `Authorization: Bearer <token>`
3. **JwtAuthenticationFilter intercepta** la request
4. **Validación delegada a pyg-auth** mediante llamada HTTP usando Feign Client:
   ```
   POST http://pyg-auth:8081/api/auth/validate
   Header: Authorization: Bearer <token>
   ```
5. **pyg-auth responde** con información del usuario si el token es válido
6. **SecurityContext se establece** con la autenticación y roles del usuario
7. **Spring Security evalúa** las reglas de autorización (`.hasRole("OWNER")`)
8. **Request continúa** hacia el controlador si todo es válido

### Ventajas de esta arquitectura

✅ **Seguridad**: No se duplica el secreto JWT en múltiples servicios
✅ **Mantenibilidad**: Cambios en lógica de validación solo en un lugar  
✅ **Consistencia**: Todos los servicios validan de la misma manera  
✅ **Flexibilidad**: Facilita cambios futuros en autenticación  
✅ **Separación de responsabilidades**: Cada servicio tiene su propósito claro

### Implementación técnica

**AuthServiceClient (Feign):**
```java
@FeignClient(
    name = "pyg-auth",
    url = "${AUTH_SERVICE_URL:http://localhost:8081}",
    fallback = AuthServiceClientFallback.class
)
public interface AuthServiceClient {
    @PostMapping("/api/auth/validate")
    TokenValidationResponse validateToken(@RequestHeader("Authorization") String token);
}
```

**JwtAuthenticationFilter:**
```java
var validation = authServiceClient.validateToken(normalizedHeader);

if (validation != null && validation.isValid()) {
    var authorities = List.of(new SimpleGrantedAuthority(validation.getRole()));
    var authToken = new UsernamePasswordAuthenticationToken(
        validation.getUsername(), null, authorities
    );
    SecurityContextHolder.getContext().setAuthentication(authToken);
}
```

### Configuración requerida

La aplicación debe tener habilitado Feign:
```java
@SpringBootApplication
@EnableFeignClients  // ✅ CRÍTICO: Sin esto Feign no funciona
public class PygOwnerApplication {
    // ...
}
```

### Rutas protegidas

Solo usuarios con rol `ROLE_OWNER` pueden acceder a los endpoints:

```java
http
    .authorizeHttpRequests(auth -> auth
        .requestMatchers("/owners/**").hasRole("OWNER")
        .requestMatchers("/pets/**").hasRole("OWNER")
        .anyRequest().authenticated()
    )
```

---

## 📘 Documentación interactiva

Swagger UI está disponible en:

👉 **http://localhost:8082/swagger-ui.html**

Desde allí puedes:
- Ver todos los endpoints disponibles
- Probar requests directamente desde el navegador
- Ver esquemas de requests/responses
- Autenticarte usando el botón "Authorize" con tu token JWT

---

## 🧱 Entidades principales

### 🧍 Owner

Representa el perfil de un dueño de mascotas.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | Long | Identificador único |
| `userId` | Long | Referencia al usuario en pyg-auth |
| `phone` | String | Teléfono de contacto |
| `address` | String | Dirección |
| `createdAt` | Timestamp | Fecha de creación |
| `updatedAt` | Timestamp | Última actualización |

### 🐕 Pet

Representa una mascota registrada por un dueño.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | Long | Identificador único |
| `name` | String | Nombre de la mascota |
| `species` | String | Especie (Perro, Gato, etc.) |
| `breed` | String | Raza |
| `age` | Integer | Edad en años |
| `weight` | Double | Peso en kilogramos |
| `description` | String | Descripción adicional |
| `owner` | Owner | Dueño asociado (relación ManyToOne) |

---

## 🔄 Estado actual

| Componente | Estado |
|-----------|--------|
| Crear/Actualizar Owner | ✅ Implementado |
| Obtener perfil propio | ✅ Implementado |
| Registrar mascota | ✅ Implementado |
| Listar mascotas | ✅ Implementado |
| Obtener mascota específica | ✅ Implementado |
| Actualizar mascota | 🚧 Pendiente |
| Validación JWT vía Feign | ✅ Funcionando |
| Spring Security | ✅ Configurado |
| Swagger (OpenAPI) | ✅ Activo |
| Tests Unitarios | 🔜 Próximamente |

---

## 🚀 Cómo ejecutar el servicio localmente

### 🔧 Requisitos previos

1. Tener instalado **Java 17+**
2. Tener **PostgreSQL** corriendo localmente
3. Tener **pyg-auth** ejecutándose en `http://localhost:8081`

### ⚙️ Configuración

Crear archivo `.env` en la raíz del proyecto con:

```properties
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/pyg_owner_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=root

# Auth Service
AUTH_SERVICE_URL=http://localhost:8081
```

O configurar en `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/pyg_owner_db
    username: postgres
    password: root
  jpa:
    hibernate:
      ddl-auto: update

auth:
  service:
    url: http://localhost:8081
```

### ▶️ Ejecución

Desde la raíz del proyecto:

```bash
cd backend/pyg-owner
./mvnw spring-boot:run
```

o en Windows:

```cmd
mvnw.cmd spring-boot:run
```

El servicio estará disponible en:
👉 **http://localhost:8082**

---

## 🐞 Troubleshooting

### Problema: Feign fallback ejecutándose inmediatamente

**Causa:** Falta la anotación `@EnableFeignClients` en la clase principal.

**Solución:**
```java
@SpringBootApplication
@EnableFeignClients  // ✅ Agregar esta anotación
public class PygOwnerApplication {
    // ...
}
```

### Problema: 403 Forbidden a pesar de token válido

**Causa:** `SecurityContext` no se establece después de validar el token.

**Solución:** Ver documentación completa en [`docs/TROUBLESHOOTING.md`](../../docs/TROUBLESHOOTING.md)

### Problema: Cannot connect to pyg-auth

**Verificar:**
1. pyg-auth está ejecutándose en el puerto correcto
2. La variable `AUTH_SERVICE_URL` está configurada correctamente
3. No hay firewalls bloqueando la comunicación

---

## 📬 Próximos pasos

- [ ] Implementar `PUT /pets/{id}` para actualizar mascotas
- [ ] Agregar validaciones adicionales de negocio
- [ ] Implementar paginación en listado de mascotas
- [ ] Agregar filtros de búsqueda (por especie, raza, etc.)
- [ ] Añadir tests unitarios y de integración
- [ ] Implementar soft delete para mascotas
- [ ] Agregar imágenes de mascotas (integración con storage)

---

## 🐾 Autora

Desarrollado por **Jessica Alvarado**
Proyecto: **PerrosYGatos**
Fines educativos y de portafolio técnico.

---

## 📚 Referencias

- [Documentación de pyg-auth](../pyg-auth/README.md)
- [Troubleshooting completo](../../docs/TROUBLESHOOTING.md)
- [Arquitectura del proyecto](../../docs/ARCHITECTURE.md)
- [Spring Cloud OpenFeign](https://docs.spring.io/spring-cloud-openfeign/docs/current/reference/html/)
- [Spring Security](https://docs.spring.io/spring-security/reference/)
