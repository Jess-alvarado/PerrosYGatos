# 🧠 pyg-auth — Authentication & Registration Microservice

El servicio **`pyg-auth`** forma parte del ecosistema **PerrosYGatos**, una plataforma diseñada para conectar **dueños de mascotas** con **profesionales especializados en comportamiento animal** (etólogos, entrenadores, etc.).

Este microservicio gestiona el **registro, login y roles de usuarios**, diferenciando entre:
- 👤 **Dueños de mascotas**
- 🧑‍⚕️ **Profesionales del área animal**

---

## ⚙️ Tecnologías utilizadas

| Componente | Tecnología |
|-------------|-------------|
| Lenguaje | Java 17 |
| Framework | Spring Boot 3.x |
| Seguridad | Spring Security + JWT |
| ORM | Spring Data JPA (Hibernate) |
| Base de datos | PostgreSQL |
| Librerías adicionales | Lombok, Validation API |
| Build Tool | Maven |
| Testing | JUnit 5, Mockito |

---

## 🧩 Descripción general

El servicio **pyg-auth** se encarga de:

- Registro y autenticación de **dueños y profesionales**.
- Asignación automática de roles (`ROLE_OWNER`, `ROLE_PROFESSIONAL`).
- Generación de tokens JWT seguros para acceso a otros microservicios.
- Validación y protección de endpoints mediante filtros JWT.
- Hash de contraseñas con **BCryptPasswordEncoder**.

---

## 🧱 Estructura del proyecto

pyg-auth/
├── src/main/java/com/auth/pyg_auth/
│ ├── controller/
│ │ └── AuthController.java # Controlador REST principal
│ ├── service/
│ │ └── AuthService.java # Lógica de negocio (registro/login)
│ ├── model/
│ │ ├── User.java # Entidad base para ambos tipos de usuario
│ │ ├── PetProfile.java # Perfil de mascotas (solo para dueños)
│ │ └── Role.java # Roles de usuario
│ ├── repository/
│ │ ├── UserRepository.java
│ │ └── RoleRepository.java
│ └── security/
│ ├── JwtService.java # Generación y validación de tokens JWT
│ ├── JwtAuthenticationFilter.java
│ └── SecurityConfig.java
│
├── src/main/resources/
│ ├── application.properties
│ └── application-dev.properties
│
└── pom.xml

---

## 🔐 Roles y permisos

| Rol | Descripción | Acceso |
|------|--------------|--------|
| `ROLE_OWNER` | Dueño de mascota que busca ayuda profesional. | CRUD de mascota, creación de solicitudes. |
| `ROLE_PROFESSIONAL` | Profesional especializado (etólogo, entrenador). | Gestión de perfil profesional y sesiones. |
| `ROLE_ADMIN` | (Opcional, futuro) administración general del sistema. | Todos los endpoints. |

---

## 📦 Endpoints principales

Base URL:
`http://localhost:8081/api/auth`

---

### 🐾 **1. POST /register**

Registra un nuevo usuario (dueño o profesional).
El rol se asigna automáticamente según el campo `"role"` recibido en el cuerpo de la solicitud.

#### 🧩 Body (Dueño de mascota)
```json
{
  "role": "OWNER",
  "ownerName": "Juan",
  "email": "correo@example.com",
  "password": "123456",
  "petProfile": {
    "type": "DOG",            // o "CAT"
    "breed": "Mestizo",
    "petName": "Toby",
    "age": 3,
    "sterilized": true
  }
}
🧩 Body (Profesional)

{
  "role": "PROFESSIONAL",
  "name": "Juan Pérez",
  "email": "correo@example.com",
  "password": "trainer123",
  "yearsExperience": 5,
  "specialization": "Ethologist"
}
✅ Respuesta exitosa
{
  "message": "User registered successfully",
  "userId": 21,
  "role": "ROLE_PROFESSIONAL",
  "token": "eyJhbGciOiJIUzI1NiIsInR..."
}
⚙️ Detalles técnicos

La contraseña se encripta con BCryptPasswordEncoder antes de guardarse.

Si el correo ya existe, retorna 409 Conflict.

Se asigna automáticamente un rol mediante RoleRepository.

Genera un token JWT inmediatamente tras el registro (inicio de sesión automático).

🔑 2. POST /login

Autentica un usuario existente.

🧩 Body
{
  "email": "correo@example.com",
  "password": "123456"
}

✅ Respuesta
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600000,
  "user": {
    "id": 21,
    "name": "Juan",
    "role": "ROLE_OWNER"
  }
}
⚙️ Detalles técnicos

Valida credenciales con AuthenticationManager.

Si son válidas, genera un nuevo token JWT con el rol del usuario.

Si no, retorna 401 Unauthorized.


♻️ 3. POST /refresh

Renueva un token JWT antes de que expire.

🧩 Body
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}

{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600000
}

🙋‍♀️ 4. GET /me

Devuelve la información del usuario autenticado (extraída del JWT).

🔐 Header
Authorization: Bearer <token>

✅ Respuesta
{
  "id": 21,
  "name": "Juan",
  "email": "correo@example.com",
  "role": "ROLE_OWNER"
}


🧠 5. GET /roles (opcional)

Devuelve todos los roles disponibles.

✅ Respuesta
[
  "ROLE_OWNER",
  "ROLE_PROFESSIONAL",
  "ROLE_ADMIN"
]


| Recurso                      | Implementación                                         |
| ---------------------------- | ------------------------------------------------------ |
| **Hash de contraseñas**      | `BCryptPasswordEncoder`                                |
| **Tokens JWT**               | Generados con firma HMAC SHA-256                       |
| **Expiración de tokens**     | 1 hora (configurable en `application.properties`)      |
| **Autorización**             | Filtros de Spring Security (`JwtAuthenticationFilter`) |
| **Roles y privilegios**      | Validados con anotaciones `@PreAuthorize` o `@Secured` |
| **Validación de entrada**    | `@Valid` + `@NotBlank`, `@Email`, etc.                 |
| **Manejo de errores global** | `@ControllerAdvice` con `ExceptionHandler`             |


🧩 Variables importantes (application.properties)
spring.application.name=pyg-auth
server.port=8081

spring.datasource.url=jdbc:postgresql://localhost:5432/pyg_auth_db
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:root}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.show-sql=true

jwt.secret=${JWT_SECRET:default-secret}
jwt.expiration=3600000

🧪 Pruebas

Ejecutar pruebas unitarias:

mvn test

Pruebas a realizar:

Registro correcto de usuarios (dueño y profesional).

Validación de duplicados (correo existente).

Login con contraseña incorrecta.

Validación de roles (OWNER, PROFESSIONAL).

Acceso a /me solo con token válido.

🚀 Ejecución local
mvn spring-boot:run

App disponible en: http://localhost:8081

Swagger UI (si habilitado): http://localhost:8081/swagger-ui.html

🧭 Relación con otros microservicios
Servicio	Interacción
pyg-user	Recibe tokens JWT para validar identidad de usuarios.
pyg-session	Usa ROLE_PROFESSIONAL y ROLE_OWNER para validar reservas.
pyg-bff	Redirige peticiones autenticadas hacia este servicio.

🧱 Próximos pasos

 Implementar endpoint /logout (invalidar token manualmente).

 Enviar correo de bienvenida tras el registro (eventos asíncronos).

 Añadir verificación de correo electrónico.

 Separar flujos de registro en OwnerAuthController y ProfessionalAuthController.

🐾 Autora

Desarrollado por Jessica Alvarado
Proyecto: PerrosYGatos 🐶🐱
Fines educativos y de portafolio técnico.
