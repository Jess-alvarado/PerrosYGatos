# PerrosYGatos
![GitHub repo views](https://komarev.com/ghpvc/?username=Jess-alvarado&repo=PerrosYGatos&color=brightgreen&style=flat)

**PerrosYGatos** es una plataforma web diseñada para conectar **dueños de mascotas** con **profesionales especializados** (entrenadores, etólogos, cuidadores o asesores de comportamiento).
El objetivo es facilitar la búsqueda, agendamiento y comunicación entre ambos, además de permitir que los profesionales compartan contenido educativo que refleje su experiencia y calidad de servicio.

---

## 🧩 Arquitectura General

El sistema se construye bajo una **arquitectura de microservicios híbrida**, donde cada servicio cumple una responsabilidad específica y puede estar implementado en **Java (Spring Boot)** o **TypeScript (NestJS)**, según su propósito.
Esto permite escalabilidad, independencia entre módulos y flexibilidad tecnológica.


### 📁 Estructura de carpetas

```
PerrosYGatos/
├── backend/
│   ├── pyg-auth/           # Autenticación y gestión de usuarios (Java)
│   ├── pyg-owner/          # Perfiles de usuarios y mascotas (Java)
│   ├── pyg-professional/   # Profesionales y publicaciones (Java)
│   ├── pyg-session/        # Gestión de sesiones entre usuarios y profesionales (Java)
│   ├── pyg-notification/   # Notificaciones en tiempo real con WebSocket (NestJS)
│   ├── pyg-content/        # Manejo de contenido y publicaciones (NestJS)
│   ├── pyg-search/         # Búsqueda y filtrado de profesionales (NestJS)
│   └── pyg-bff/           # Backend For Frontend (NestJS)
├── frontend/
│   └── web/               # Aplicación principal en React + TypeScript
├── docs/                  # Documentación técnica y diagramas
│   ├── arquitectura.md
│   ├── decisiones-tecnicas.md
│   ├── entorno.md
│   └── diagramas/
└── README.md
```

---

## ⚙️ Tecnologías principales

### 🧠 Backend (microservicios)

#### 🔹 Basados en Java (Spring Boot)
- **Java 17** (compatible con Spring Boot 3.x)
- **Spring Boot** → framework principal para servicios robustos
- **Spring Security + JWT** → autenticación y control de roles
- **Spring Data JPA + PostgreSQL** → persistencia de datos
- **Spring WebFlux / WebSocket** → comunicación asíncrona (para sesiones)
- **Maven** → gestión de dependencias
- **Lombok** → simplificación de código

#### 🔹 Basados en TypeScript (NestJS)
- **NestJS** → framework modular para Node.js
- **Socket.IO / WebSocket** → envío de notificaciones en tiempo real
- **TypeORM + PostgreSQL** → persistencia
- **REST API** y **GraphQL (futuro)** → exposición de servicios
- **JWT y Guards** → autenticación integrada

#### 🧩 Comunicación
- API REST entre microservicios.
- WebSocket para eventos en tiempo real (notificaciones, recordatorios, actualizaciones).

---

### 🖥️ Frontend
- **React + TypeScript** → interfaz moderna y eficiente
- **Axios / React Query** → manejo de datos y peticiones HTTP
- **TailwindCSS / Material UI** → diseño limpio y responsivo
- **Socket.IO Client** → conexión en tiempo real con el servicio de notificaciones

---

### 🗄️ Base de Datos
- **PostgreSQL** como base principal
(una instancia o esquema por microservicio según el dominio)

---

## 🧩 Descripción de los microservicios

| Servicio | Descripción | Lenguaje / Tecnología | Puerto |
|-----------|--------------|----------------------|--------|
| **pyg-auth** | Registro, login, validación de tokens JWT y gestión de roles. | Java + Spring Security + JWT | 8081 |
| **pyg-owner** | Gestión de perfiles de dueños de mascotas y sus animales. | Java + Spring Boot + PostgreSQL | 8082 |
| **pyg-professional** | Información de profesionales, especialidades y publicaciones. | Java + Spring Boot + PostgreSQL | 8083 |
| **pyg-session** | Manejo de sesiones entre usuarios y profesionales (videollamadas, recordatorios). | Java + Spring Boot + WebSocket | Planificado |
| **pyg-notification** | Envío de notificaciones en tiempo real al frontend. | NestJS + Socket.IO | Planificado |
| **pyg-content** | Gestión de publicaciones y contenido educativo. | NestJS + TypeORM + PostgreSQL | Planificado |
| **pyg-search** | Filtrado y búsqueda de profesionales por especialidad o ubicación. | NestJS + Elasticsearch (futuro) | Planificado |
| **pyg-bff** | Backend For Frontend - Gateway y orquestación de microservicios. | NestJS / Spring Cloud Gateway | Planificado |

---

## 🔄 Flujo general de usuarios

### 🐕 Dueño de mascota
1. Se registra y completa su perfil y el de su mascota.
2. Busca profesionales según su necesidad.
3. Visualiza el perfil y contenido publicado por los profesionales.
4. Agenda una sesión o contacto directo desde la plataforma.
5. Recibe notificaciones sobre confirmaciones, recordatorios o respuestas.

### 👩‍⚕️ Profesional
1. Se registra como profesional y completa su perfil (especialidad, experiencia, tarifas).
2. Recibe solicitudes de clientes y decide aceptarlas o rechazarlas.
3. Publica contenido educativo o promocional.
4. Gestiona sesiones agendadas y comunicación con clientes.
5. Envía y recibe notificaciones.

---

## 🧱 Estado del proyecto

### 🎯 Fase MVP Actual

| Módulo | Descripción | Estado | Endpoints Principales |
|---------|--------------|--------|----------------------|
| `pyg-auth` | Servicio de autenticación y emisión de tokens JWT. | ✅ MVP Completo | `/auth/register`, `/auth/login`, `/auth/validate` |
| `pyg-owner` | Gestión de perfiles de dueños y mascotas. | ✅ MVP Completo | `/owners` (POST/GET), `/pets` (POST/GET/GET by ID) |
| `pyg-professional` | Información de profesionales y publicaciones. | 🟡 En desarrollo activo | `/professionals` (POST/GET), `/posts` (POST/GET) |
| `frontend/web` | Interfaz principal en React + TypeScript. | 🔜 Próxima fase | Dashboard, búsqueda, perfiles |

### 📋 Funcionalidades Implementadas

#### ✅ pyg-auth (Puerto 8081)
- Registro de usuarios con roles (OWNER/PROFESSIONAL)
- Login con generación de JWT
- Validación centralizada de tokens
- Documentación Swagger completa

#### ✅ pyg-owner (Puerto 8082)
- Creación y actualización de perfiles de dueños
- Registro de mascotas (nombre, tipo, raza, edad, esterilización, sexo)
- Listado de mascotas por dueño
- Obtener mascota específica con verificación de ownership
- Validación custom de tipos de mascota (DOG/CAT)
- Seguridad: Solo acceso a recursos propios

#### 🟡 pyg-professional (Puerto 8083)
- Creación de perfiles profesionales (en desarrollo)
- Gestión de publicaciones educativas (en desarrollo)
- Especialidades y experiencia (en desarrollo)

### 🔮 Próximas Fases

#### Fase 2: Frontend y Funcionalidades Adicionales
- [ ] Desarrollo del frontend React
- [ ] Implementar UPDATE endpoints (mascotas, perfiles)
- [ ] Implementar DELETE endpoints (soft delete)
- [ ] Refresh token en pyg-auth
- [ ] Paginación y filtros avanzados
- [ ] Carga de imágenes (perfiles y mascotas)

#### Fase 3: Servicios Avanzados
| Módulo | Descripción | Estado |
|---------|--------------|--------|
| `pyg-session` | Agendamiento y gestión de sesiones. | ⏳ Planificado |
| `pyg-notification` | Notificaciones en tiempo real con WebSocket. | ⏳ Planificado |
| `pyg-content` | Sistema de contenido educativo avanzado. | ⏳ Planificado |
| `pyg-search` | Búsqueda con filtros y Elasticsearch. | ⏳ Planificado |
| `pyg-bff` | Gateway y orquestación de servicios. | ⏳ Planificado |

---

## 🚀 Roadmap y Próximos Pasos

### 🎯 Fase Actual: Completar pyg-professional MVP
- [x] ✅ pyg-auth: Autenticación y validación JWT
- [x] ✅ pyg-owner: CRUD de perfiles y mascotas
- [ ] 🟡 pyg-professional: Endpoints principales (en progreso)
  - [ ] POST /professionals - Crear perfil profesional
  - [ ] GET /professionals/profile - Obtener perfil propio

### 📱 Siguiente: Desarrollo Frontend
- [ ] Configurar proyecto React + TypeScript
- [ ] Implementar autenticación en frontend
- [ ] Dashboard para dueños
- [ ] Dashboard para profesionales
- [ ] Búsqueda y visualización de profesionales
- [ ] Gestión de mascotas
- [ ] Visualización de publicaciones

### 🔧 Mejoras Post-MVP
- [ ] Implementar endpoints UPDATE (PUT) para todos los recursos
- [ ] Implementar endpoints DELETE (soft delete)
- [ ] Refresh token y logout en pyg-auth
- [ ] Paginación en listados
- [ ] Filtros y búsqueda avanzada
- [ ] Carga de imágenes (AWS S3 / Cloudinary)
- [ ] Validaciones de negocio adicionales
- [ ] Tests unitarios e integración

### 🏗️ Infraestructura y DevOps
- [x] ✅ Dockerizar todos los microservicios (pyg-auth, pyg-owner, pyg-professional)
- [x] ✅ Docker Compose para desarrollo local (funcionando correctamente)
- [ ] Configurar Spring Cloud Gateway (BFF)
- [ ] Implementar CI/CD con GitHub Actions
- [ ] Despliegue en cloud (AWS/Heroku/Railway)

### 🚀 Funcionalidades Avanzadas (Fase 3)
- [ ] pyg-session: Agendamiento de citas
- [ ] pyg-notification: WebSocket para notificaciones en tiempo real
- [ ] Sistema de reviews y calificaciones
- [ ] Chat en tiempo real entre usuarios y profesionales
- [ ] Integración con pasarelas de pago
- [ ] Elasticsearch para búsqueda avanzada

---

## 🐳 Ejecución con Docker Compose

### ✅ Configuración Actual

El proyecto cuenta con un `docker-compose.yml` completamente funcional que levanta:
- **PostgreSQL 15** con bases de datos separadas para cada servicio
- **pyg-auth** (Puerto 8081)
- **pyg-owner** (Puerto 8082)
- **pyg-professional** (Puerto 8083)

### 🚀 Cómo Ejecutar

1. **Configurar variables de entorno:**
   ```powershell
   # Copiar el archivo de ejemplo
   copy .env.example .env

   # Editar .env con tus valores
   ```

2. **Levantar todos los servicios:**
   ```powershell
   docker-compose up -d
   ```

3. **Ver logs:**
   ```powershell
   # Todos los servicios
   docker-compose logs -f

   # Solo un servicio específico
   docker-compose logs -f pyg-auth
   ```

4. **Detener servicios:**
   ```powershell
   docker-compose down
   ```

5. **Reconstruir imágenes (después de cambios en código):**
   ```powershell
   docker-compose up -d --build
   ```

### 📋 Endpoints Disponibles

Una vez levantados los servicios:
- **pyg-auth**: http://localhost:8081/swagger-ui.html
- **pyg-owner**: http://localhost:8082/swagger-ui.html
- **pyg-professional**: http://localhost:8083/swagger-ui.html
- **PostgreSQL**: localhost:5432

### 🗄️ Bases de Datos

El `init.sql` crea automáticamente 3 bases de datos:
- `pyg_auth` - Usuarios y autenticación
- `pyg_owner` - Perfiles de dueños y mascotas
- `pyg_professional` - Perfiles profesionales y publicaciones

---

## ⚙️ Configuración de Entorno (.env)

Este repositorio utiliza un archivo `.env` para gestionar variables de entorno. Los servicios Spring Boot leen estas variables mediante `spring.config.import: optional:file:.env[.properties]`.

### Variables Principales

```env
# Base de Datos
DB_USERNAME=postgres
DB_PASSWORD=root
DB_DRIVER=org.postgresql.Driver

# URLs de Bases de Datos
AUTH_DATABASE_URL=jdbc:postgresql://postgres:5432/pyg_auth
OWNER_DATABASE_URL=jdbc:postgresql://postgres:5432/pyg_owner
PROFESSIONAL_DATABASE_URL=jdbc:postgresql://postgres:5432/pyg_professional

# JWT
JWT_SECRET=tu_secreto_jwt_super_seguro_aqui
JWT_EXPIRATION=86400000

# Puertos
SERVER_PORT_AUTH=8081
SERVER_PORT_OWNER=8082
SERVER_PORT_PROFESSIONAL=8083

# Spring JPA
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=false

# Nombres de Aplicaciones
SPRING_APPLICATION_NAME_AUTH=pyg-auth
SPRING_APPLICATION_NAME_OWNER=pyg-owner
SPRING_APPLICATION_NAME_PROFESSIONAL=pyg-professional
```

### 📝 Recomendaciones

- ✅ Crear archivo `.env` desde `.env.example`
- ✅ Nunca subir `.env` con secretos al repositorio (está en `.gitignore`)
- ✅ Usar valores seguros en producción
- ✅ Regenerar `JWT_SECRET` para cada entorno

### 🔧 Ejecución Local (Sin Docker)

Si prefieres ejecutar sin Docker:

```powershell
# Configurar variables de entorno
copy .env.example .env

# Ejecutar cada servicio
cd backend/pyg-auth
mvnw.cmd spring-boot:run

# En otra terminal
cd backend/pyg-owner
mvnw.cmd spring-boot:run

# En otra terminal
cd backend/pyg-professional
mvnw.cmd spring-boot:run
```

**Nota:** Necesitarás PostgreSQL corriendo localmente y crear las bases de datos manualmente.

---

## 📚 Documentación Técnica

### Documentos Disponibles
- **[ARCHITECTURE.md](docs/ARCHITECTURE.md)** - Decisiones arquitectónicas y patrones de diseño
- **[TROUBLESHOOTING.md](docs/TROUBLESHOOTING.md)** - Problemas comunes y soluciones
- **[pyg-auth/README.md](backend/pyg-auth/README.md)** - Documentación del servicio de autenticación
- **[pyg-owner/README.md](backend/pyg-owner/README.md)** - Documentación del servicio de dueños y mascotas

### Patrones Arquitectónicos Clave

#### 🔐 Validación JWT Centralizada
Todos los microservicios delegan la validación de tokens JWT a `pyg-auth` mediante llamadas HTTP con **Spring Cloud OpenFeign**:

```
Cliente → pyg-owner/pyg-professional (con JWT)
    ↓
    Filtro JWT intercepta
    ↓
    Llama a pyg-auth/api/auth/validate
    ↓
    pyg-auth valida y retorna info del usuario
    ↓
    SecurityContext se establece
    ↓
    Request continúa al controlador
```

**Ventajas:**
- ✅ Secreto JWT solo en un servicio
- ✅ Lógica de validación centralizada
- ✅ Facilita rotación de claves
- ✅ Consistencia en todos los servicios

#### 🛡️ Seguridad a Nivel de Datos
Todos los endpoints verifican **ownership** de recursos:
- `GET /pets/{id}` → Solo retorna si la mascota pertenece al usuario autenticado
- Queries custom: `findByIdAndOwnerUserId(petId, userId)`
- Previene **IDOR** (Insecure Direct Object Reference)

#### ✅ Validación en Capas
1. **Bean Validation** (`@NotBlank`, `@Min`, etc.)
2. **Validaciones Custom** (`@ValidPetType`)
3. **Lógica de Negocio** (verificar perfil existe antes de crear mascota)

---

## 📬 Propósito del proyecto

Proyecto desarrollado por **Jessica Alvarado** con fines de:
- Aprendizaje y mejora profesional en **arquitectura de microservicios**.
- Consolidación de conocimientos en **Java Spring Boot, NestJS y React**.
- Creación de un portafolio técnico real, aplicando **comunicación entre servicios y WebSocket**.

📍 Proyecto de código abierto para fines educativos y de portafolio.

---

## Licencia

Código abierto para uso educativo y de demostración.
No destinado a uso comercial sin autorización de la autora.
