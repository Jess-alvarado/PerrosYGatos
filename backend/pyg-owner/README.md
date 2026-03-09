# pyg-owner

Microservice responsible for managing **pet owners and their pets** in the PerrosYGatos platform.

The service provides APIs to create and manage owner profiles and register pets associated with each owner.

Authentication is handled by **pyg-auth**, and this service trusts the validated JWT information received from it.

---

## Responsibilities

This service handles:

- owner profile creation and updates
- retrieving the authenticated owner's profile
- registering pets
- listing pets for the authenticated owner
- retrieving a specific pet
- future pet updates and management features

---

## Tech Stack

- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL
- Spring Cloud OpenFeign
- Maven
- Swagger / OpenAPI

---

## Project Structure

pyg-owner/
├── clients/ # Feign clients (Auth service communication)
├── config/ # Security and application configuration
├── controllers/ # REST controllers (Owner, Pet)
├── dto/ # Request and response DTOs
├── models/ # JPA entities (Owner, Pet)
├── repositories/ # Spring Data repositories
├── security/ # JWT filter and security logic
└── services/ # Business logic


---

## Authentication

JWT validation is **centralized in `pyg-auth`**.

`pyg-owner` delegates token validation through a Feign client and receives user information if the token is valid.

Typical flow:

1. Client authenticates via `pyg-auth`
2. Client sends request with `Authorization: Bearer <token>`
3. `pyg-owner` calls `pyg-auth` to validate the token
4. If valid, Spring Security sets the authentication context
5. Endpoint access is granted according to user roles

---

## Main Endpoints

### Owner

Create or update owner profile

POST /owners


Get authenticated owner's profile


GET /owners/profile


---

### Pets

Register a new pet


POST /pets


List pets for the authenticated owner


GET /pets


Get pet by id


GET /pets/{id}


Update pet (planned)


PUT /pets/{id}


---

## Example Response

{
  "id": 1,
  "name": "Max",
  "species": "Dog",
  "breed": "Golden Retriever",
  "age": 3,
  "weight": 30.5,
  "description": "Very playful and friendly",
  "ownerId": 1
}

Run the Service

From the project directory:

mvn spring-boot:run

Swagger UI:

http://localhost:8082/swagger-ui.html

Related Services

pyg-auth → authentication and JWT validation

pyg-professional → professional profiles and services