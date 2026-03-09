# pyg-professional

Microservice responsible for managing **professional profiles** in the PerrosYGatos platform.

Professionals include trainers, ethologists, and pet behavior specialists who can create profiles and share educational content.

---

## Responsibilities

This service handles:

- professional profile creation
- professional information management (bio, profession, experience)
- future educational posts from professionals

Each professional profile is linked to a **userId** provided by the authentication service (`pyg-auth`).

---

## Tech Stack

- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL
- Maven

---

## Role in the Architecture
Client
│
▼
pyg-professional
│
└── PostgreSQL


Authentication is handled by **pyg-auth** using JWT tokens.

The service trusts the authenticated `userId` extracted from the token.

---

## Main Endpoints

Create professional profile

POST /professionals
POST /professionals


Get current professional profile


GET /professionals/profile


Example response

```json
{
  "id": 1,
  "userId": 12,
  "phone": "+56912345678",
  "address": "Linares, Chile",
  "profession": "Feline Ethologist",
  "bio": "Specialist in feline behavior and environmental enrichment",
  "experienceYears": 5
}
Run the Service

From project root (recommended)

docker-compose up -d

Or run locally

mvn spring-boot:run

Swagger

http://localhost:8083/swagger-ui.html