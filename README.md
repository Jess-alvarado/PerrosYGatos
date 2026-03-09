# PerrosYGatos

![GitHub repo views](https://komarev.com/ghpvc/?username=Jess-alvarado&repo=PerrosYGatos&color=brightgreen&style=flat)

Backend platform designed to connect **pet owners** with **animal behavior professionals** such as trainers, ethologists and pet caregivers.

This project focuses on **microservices architecture, authentication flows, and scalable backend design**.

---

# Engineering Highlights

- **Centralized JWT validation** through `pyg-auth`, isolating authentication logic from business services.
- **Ownership-based authorization** in protected endpoints to prevent unauthorized resource access (IDOR protection).
- **Domain-driven microservice separation**, where each service owns its business logic and persistence layer.
- **Docker-based local environment** using Docker Compose for multi-service development.
- **Architecture designed to scale** with future services such as scheduling, notifications and search.

---

# Architecture

Microservices based system where each service owns its domain and database.

Client
│
▼
API Gateway (planned)
│
├── pyg-auth
├── pyg-owner
└── pyg-professional


Services communicate using **REST APIs**.

Future services will extend the architecture without modifying existing domains.

---

# Tech Stack

## Backend

- Java 17
- Spring Boot
- Spring Security
- JWT Authentication
- Spring Data JPA
- PostgreSQL
- Maven

## Infrastructure

- Docker
- Docker Compose

## Planned Services

- NestJS
- WebSocket notifications
- Elasticsearch search

## Frontend (planned)

- React
- TypeScript

---

# Services

| Service | Description |
|------|------|
| pyg-auth | Authentication and JWT validation |
| pyg-owner | Pet owners and pets management |
| pyg-professional | Professional profiles |
| pyg-session | Session scheduling (planned) |
| pyg-notification | Real-time notifications (planned) |
| pyg-content | Educational content (planned) |
| pyg-search | Professional discovery (planned) |
| pyg-bff | Backend-for-Frontend gateway (planned) |

---

Running Locally

Start the platform with Docker:
docker-compose up -d

Swagger documentation

pyg-auth: http://localhost:8081/swagger-ui.html
pyg-owner: http://localhost:8082/swagger-ui.html
pyg-professional: http://localhost:8083/swagger-ui.html

Project Purpose

This project was created as a backend portfolio project to demonstrate:

microservices architecture

secure authentication with JWT

service-to-service communication

scalable backend design

containerized development environments

Author
Jessica Alvarado