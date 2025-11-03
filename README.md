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

| Servicio | Descripción | Lenguaje / Tecnología |
|-----------|--------------|----------------------|
| **Auth** | Registro, login, roles y emisión de tokens JWT. | Java + Spring Security |
| **User** | Gestión de usuarios dueños de mascotas y sus perfiles. | Java + Spring Boot + PostgreSQL |
| **Professional** | Información de profesionales, especialidades y disponibilidad. | Java + Spring Boot |
| **Session** | Manejo de sesiones entre usuarios y profesionales (videollamadas, recordatorios). | Java + Spring Boot + WebSocket |
| **Notification** | Envío de notificaciones en tiempo real al frontend. | NestJS + Socket.IO |
| **Content** | Gestión de publicaciones y contenido educativo. | NestJS + TypeORM + PostgreSQL |
| **Search** | Filtrado y búsqueda de profesionales por especialidad o ubicación. | NestJS + Elasticsearch (futuro) |
| **BFF (Gateway)** | Puente entre el frontend y los microservicios. | Java + Spring Cloud Gateway |

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

| Módulo | Descripción | Estado |
|---------|--------------|--------|
| `pyg-auth` | Servicio de autenticación y emisión de tokens. | 🟢 En desarrollo |
| `pyg-owner` | Gestión de usuarios y mascotas. | 🟢 En desarrollo |
| `pyg-professional` | Información de profesionales y publicaciones. | 🟢 En desarrollo |
| `pyg-session` | Manejo de sesiones y comunicación. | Planificado |
| `pyg-notification` | Notificaciones en tiempo real con WebSocket. | Planificado |
| `pyg-content` | Contenido educativo y publicaciones. | Planificado |
| `pyg-search` | Búsqueda y filtrado de profesionales. | Planificado |
| `frontend` | Interfaz principal en React. | Planificado |

---

## 🚀 Próximos pasos

- [ ] Definir la estructura base de todos los microservicios (Java y NestJS).
- [ ] Configurar **Spring Cloud Gateway** como punto de entrada (BFF).
- [ ] Implementar **Docker** para contenedorización y orquestación.
- [ ] Crear documentación técnica en `/docs` (arquitectura, flujos, decisiones).
- [ ] Implementar CI/CD con **GitHub Actions**.
- [ ] Desarrollar **WebSocket / Socket.IO** para notificaciones y eventos en tiempo real.
- [ ] Integrar frontend React con los servicios REST y WebSocket.

---

## ⚙️ Configuración de entorno (.env)

Este repositorio soporta carga opcional de variables de entorno desde un archivo `.env` en la raíz del proyecto. Los servicios basados en Spring Boot usan la propiedad `spring.config.import: optional:file:.env[.properties]` para leer variables definidas localmente.

Recomendaciones:
- Crear un archivo `.env` en la raíz del workspace con las variables necesarias para desarrollo local.
- Nunca subir el archivo `.env` con secretos a control de versiones (añadir a `.gitignore`).
- Usa` .env.example` incluido como plantilla para revisar qué variables hacen falta.

Variables comunes (ejemplos):

```
# Base de datos
DB_URL=jdbc:postgresql://localhost:5432/pyg_auth
DB_USERNAME=postgres
DB_PASSWORD=root

# JWT
JWT_SECRET=tu_secreto_jwt_aqui
JWT_EXPIRATION=86400000

# Puerto del servicio
SERVER_PORT=8081
```

Cómo usar (PowerShell, Windows):

```powershell
# en la raíz del repo
copy .env.example .env
# editar .env con tus valores
cd backend/pyg-auth
setx JWT_SECRET "mi_secreto_local"
mvnw.cmd spring-boot:run
```

---

## Documentación de arquitectura

Se ha añadido un documento más detallado en `docs/ARCHITECTURE.md` con decisiones arquitectónicas, motivos para centralizar la validación de JWT en `pyg-auth`, y el flujo de comunicación entre microservicios.

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
