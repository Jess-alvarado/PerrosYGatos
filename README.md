# Perros&Gatos


Backend platform designed to connect **pet owners** with **animal behavior professionals**, such as trainers and ethologists.

The system focuses on building a **scalable and secure backend architecture**, using domain-oriented microservices, centralized authentication, and service-to-service communication.

It provides the foundation for managing users, pets, and professional services within the platform.

----------

## Engineering Highlights

* **Layered JWT security architecture**, where `pyg-gateway` validates access tokens, extracts user claims, propagates identity context through secure headers, and enforces token revocation using Redis.

* **Defense-in-depth authentication strategy**, with each microservice independently validating JWT signatures before executing business logic, preventing trust on network boundaries alone.

* **Ownership-based authorization** to ensure users can only access and modify their own resources.

* **Domain-driven service separation**, where each service owns its business logic, data model, and persistence layer.

* **Docker-based local development environment** using Docker Compose.

* **Architecture designed to scale**, supporting future services such as scheduling, notifications, search, and additional domain-specific capabilities.


----------

## Architecture

* **Frontend:** `pyg-frontend` (Vite + React)

* **API Gateway:** `pyg-gateway` (Port 9090)

  * Single entry point for all client requests.

  * Validates JWT access tokens.

  * Extracts user claims (userId, username, role).

  * Propagates identity context through secure request headers.

  * Enforces token revocation using Redis-backed blacklist validation.

  * ──► `/api/auth/**` ──► **pyg-auth** (Port 8081)

  * ──► `/api/owner/**` ──► **pyg-owner** (Port 8082)

  * ──► `/api/professional/**` ──► **pyg-professional** (Port 8083)

* **Authentication Service:** `pyg-auth`

  * User authentication and credential verification.
  * JWT issuance and refresh token management.

* **Business Services:** `pyg-owner`, `pyg-professional`

  * Own their domain logic and persistence layer.
  * Validate JWT signatures before processing business operations.
  * Consume identity information propagated by the gateway.


----------

## Tech Stack

### Backend

* Java 17
* Spring Boot
* Spring Security
* JWT Authentication
* Spring Data JPA
* PostgreSQL
* Redis
* Maven

### Frontend

* React
* Vite
* Tailwind CSS

### Infrastructure & DevOps

* Docker & Docker Compose
* API Gateway Pattern
* GitHub Actions (CI/CD)
* Automated Unit Testing


----------

## Services

| Service | Description                                                                             |
|---|-----------------------------------------------------------------------------------------|
| `pyg-gateway` | Entry point handling routing, JWT validation, claim extraction, and request forwarding  |
| `pyg-frontend` | User Interface. React + Vite                                                            |
| `pyg-auth` | Authentication, JWT issuance, and token management                                                       |
| `pyg-owner` | Pet owners and pets management                                                          |
| `pyg-professional` | Professional profiles                                                                   |

## Running Locally

### Requirements

- Docker
- Docker Compose

### Setup

1. Clone the repository

```bash
git clone <repo-url>
cd perrosygatos
```

2. Create the environment file

```bash
cp .env.example .env
```

3. Start the platform

```bash
docker-compose up --build
```

### Notes

- The project is configured to run using Docker Compose networking.
- Database hosts use container names (e.g. `postgres`) instead of `localhost`.
- JWT configuration is shared across microservices through the `.env` file.

## Swagger Documentation

pyg-auth
[http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)

pyg-owner
[http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html)

pyg-professional
[http://localhost:8083/swagger-ui.html](http://localhost:8083/swagger-ui.html)

----------


## Project Purpose

Perros y Gatos is a platform aimed at supporting collaboration between pet owners and animal behavior professionals, helping manage behavioral cases through a secure, scalable, and maintainable architecture.

Key engineering areas explored in this project include:

* microservices architecture
* API Gateway pattern
* JWT-based authentication and authorization
* Redis integration
* automated testing and CI/CD
* containerized development and deployment
* domain-driven service separation


----------

## Author

Jessica Alvarado

----------
