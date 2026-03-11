
# pyg-professional

  
Microservice responsible for managing **professional profiles** within the PerrosYGatos platform.

The service supports professionals such as **trainers, ethologists, and animal behavior specialists focused on dogs and cats**, allowing them to create profiles, present their expertise, and share educational content related to behavior, training, and pet care.

  

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

  
  ---

Authentication is handled by **pyg-auth** using JWT tokens.

  

The service trusts the authenticated `userId` extracted from the token.

  

---

  

## Main Endpoints

  

Create professional profile

POST /professionals

  
  

Get current professional profile

GET /professionals/profile

  
 
**Example response**

  

```json

{

"id": 1,

"userId": 12,

"phone": "+56912345678",

"address": "Renca, Chile",

"profession": "Feline Ethologist",

"bio": "Specialist in feline behavior and environmental enrichment",

"experienceYears": 5

}
````

---
**Run the Service**

  

From project root

  
````
docker-compose up -d
````
  

Or run locally

  
````
mvn spring-boot:run
````
  ---

Swagger

  

http://localhost:8083/swagger-ui.html