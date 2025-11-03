# 🧠 pyg-auth — Authentication & Registration Microservice

Este microservicio forma parte del proyecto **PerrosYGatos**, una plataforma que conecta **dueños de mascotas** con **profesionales especializados** (etólogos, entrenadores, cuidadores, etc.).

El servicio `pyg-auth` es responsable de la **autenticación, generación de tokens JWT y gestión de roles**, asegurando un acceso seguro al resto de microservicios.

---

## ⚙️ Funcionalidad principal

`pyg-auth` maneja todo lo relacionado con la **identidad del usuario**:

- Registro de usuarios (dueños o profesionales)
- Inicio de sesión (login)
- Generación y validación de tokens JWT
- Gestión de roles (`ROLE_OWNER`, `ROLE_PROFESSIONAL`)
- Documentación automática con **Swagger / OpenAPI**
- En desarrollo: Logout y Refresh Token

> 🔐 Este servicio centraliza la autenticación para los demás microservicios (`user`, `professional`, etc.), manteniendo una arquitectura limpia y desacoplada.

---

## 🧩 Arquitectura y estructura


pyg-auth/
├── src/
│   ├── main/java/com/auth/pyg_auth/
│   │   ├── config/          # Configuración general (Security, OpenAPI, Beans)
│   │   ├── controllers/     # Controladores REST (AuthController)
│   │   ├── models/          # Entidades, DTOs y requests/responses
│   │   ├── repositories/    # Interfaces JPA
│   │   ├── security/        # Filtros JWT y autenticación
│   │   └── services/        # Lógica de negocio (AuthService, JwtService)
│   └── resources/
│       └── application.properties
└── pom.xml

---

🧠 Tecnologías utilizadas
Componente	Descripción
☕ Java 17	Lenguaje base
🌱 Spring Boot 3.5.6	Framework principal
🔐 Spring Security + JWT	Autenticación y autorización
🗄️ Spring Data JPA + PostgreSQL	Persistencia de datos
🧩 Lombok	Reducción de código repetitivo
📘 Swagger (Springdoc OpenAPI)	Documentación interactiva de endpoints
🧰 Maven	Gestión de dependencias y build
🔗 Endpoints principales
Método	Endpoint	Descripción	Acceso
POST	/auth/register	Registra un nuevo usuario general	Público
POST	/auth/login	Autentica y devuelve token JWT	Público
POST	/auth/validate	Valida un token JWT	Protegido
POST	/auth/logout	Invalida el token activo (en desarrollo)	Protegido
POST	/auth/refresh	Renueva token JWT (pendiente)	Protegido

> 📝 **Nota sobre validación centralizada**:
> Se tomó la decisión de implementar un endpoint `/auth/validate` para centralizar la validación de tokens JWT.
> Esta decisión arquitectónica tiene los siguientes beneficios:
>
> - **Seguridad**: Evita duplicar la lógica de validación y el secreto JWT en cada microservicio
> - **Mantenibilidad**: Cambios en la lógica de validación solo se aplican en un lugar
> - **Consistencia**: Garantiza que todos los servicios validen los tokens de la misma manera
> - **Performance**: Reduce la carga de trabajo en los otros microservicios
> - **Flexibilidad**: Facilita cambios futuros en el mecanismo de autenticación
>
> Los otros microservicios deberán hacer una llamada a este endpoint cuando necesiten validar un token,
> en lugar de implementar su propia lógica de validación.

---


🧾 Ejemplos de uso
🔹 Registro (/auth/register)
{
  "username": "jess.alvarado",
  "password": "StrongPass123!",
  "firstname": "Jessica",
  "lastname": "Alvarado",
  "rolename": "ROLE_OWNER"
}


📤 Respuesta:

{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}

🔹 Login (/auth/login)
{
  "username": "jess.alvarado",
  "password": "StrongPass123!"
}


📤 Respuesta:

{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}

---

🔒 Seguridad

El servicio utiliza Spring Security + JWT para proteger los endpoints.

Solo las rutas /auth/**, /v3/api-docs/** y /swagger-ui/** están permitidas públicamente.

Los endpoints protegidos requieren el header:

Authorization: Bearer <token>

---

Documentación disponible en:
👉 http://localhost:8081/swagger-ui.html

🧱 Entidades principales
🧍 User

Representa a cualquier usuario (dueño o profesional).

Campo	Tipo	Descripción
id	Long	Identificador único
username	String	Nombre de usuario (único)
password	String	Contraseña cifrada
firstname	String	Nombre
lastname	String	Apellido
role	Role	Rol asignado
createdAt	Date	Fecha de creación
updatedAt	Date	Última actualización
🛡️ Role
Campo	Tipo	Descripción
id	Long	Identificador del rol
name	String	Nombre del rol (ROLE_OWNER o ROLE_PROFESSIONAL)

---

🔄 Estado actual
Componente	Estado
Registro/Login	✅ Implementado
JWT + Seguridad	✅ Funcionando
Swagger (OpenAPI)	✅ Activo
Validate token ✅ Implementado
Logout	🚧 En desarrollo
Refresh Token	🚧 Pendiente
Tests Unitarios	🔜 Próximamente

---


🚀 Cómo ejecutar el servicio localmente
🔧 Requisitos previos

Tener instalado Java 17+

Tener PostgreSQL corriendo localmente

Configurar la base de datos en application.properties:

spring.datasource.url=jdbc:postgresql://localhost:5432/pyg_auth_db
spring.datasource.username=postgres
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update

▶️ Ejecución

Desde la raíz del proyecto:

cd backend/pyg-auth
./mvnw spring-boot:run


o en Windows:

mvnw.cmd spring-boot:run


Luego abre en el navegador:
👉 http://localhost:8081/swagger-ui.html


---

🐞 Known Issues y Soluciones

### JwtAuthenticationFilter y Respuestas Vacías

**Problema**:
Se identificó un issue donde las peticiones a endpoints protegidos retornaban respuestas vacías (Content-Length: 0) a pesar de que la autenticación era exitosa.

**Causa**:
En `JwtAuthenticationFilter`, después de validar el token y establecer la autenticación, no se llamaba a `filterChain.doFilter()`, lo que impedía que la petición llegara al controlador.

**Solución**:
Se corrigió asegurando que `filterChain.doFilter(request, response)` se llame siempre al final del método `doFilterInternal`, permitiendo que la cadena de filtros continúe hasta el controlador.

**Impacto**:
Este fix asegura que todos los endpoints protegidos funcionen correctamente, devolviendo las respuestas esperadas cuando la autenticación es exitosa.

**Lección Aprendida**:
En filtros de Spring Security, es crucial mantener la cadena de filtros intacta llamando a `filterChain.doFilter()` después de realizar la lógica del filtro.

---


📬 Próximos pasos

 Implementar /auth/logout

 Crear /auth/refresh

 Añadir validaciones en registro

 Añadir tests unitarios

 Integrar con microservicios user y professional

---


🐾 Autora

Desarrollado por Jessica Alvarado
Proyecto: PerrosYGatos
Fines educativos y de portafolio técnico.

---