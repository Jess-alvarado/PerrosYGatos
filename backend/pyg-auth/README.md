# pyg-auth

Microservice responsible for **authentication and identity management** within the PerrosYGatos platform.

The service handles **user registration, authentication, JWT generation, and role management**, providing secure access to the rest of the platform's microservices.

----------

## Responsibilities

This service manages all **user identity and authentication flows**, including:

-   user registration
    
-   user login
    
-   JWT token generation
    
-   JWT token validation
    
-   role management (`ROLE_OWNER`, `ROLE_PROFESSIONAL`)
    

Future capabilities include **logout and refresh token support**.

----------

## Centralized Authentication

Authentication is intentionally **centralized in this service** to maintain a clean and secure microservices architecture.

Other services (such as `pyg-owner` and `pyg-professional`) delegate token validation to `pyg-auth` instead of implementing their own JWT logic.

This architectural decision provides several advantages:

-   **Security** — the JWT secret and validation logic are stored in a single service
    
-   **Consistency** — all services validate tokens in the same way
    
-   **Maintainability** — authentication changes are implemented in one place
    
-   **Decoupling** — business services remain focused on their domain logic
    

Services validate tokens through the endpoint:

POST /auth/validate

----------

## Tech Stack

-   Java 17
    
-   Spring Boot
    
-   Spring Security
    
-   JWT Authentication
    
-   Spring Data JPA
    
-   PostgreSQL
    
-   Maven
    
-   Swagger / OpenAPI
    

----------

## Main Endpoints

Register a new user

POST /auth/register

Authenticate user and obtain JWT

POST /auth/login

Validate JWT token (used by other services)

POST /auth/validate

Planned endpoints

POST /auth/logout  
POST /auth/refresh

----------

## Example Response
````json
{  
 "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."  
}
````
----------

## Run the Service


````
docker-compose up -d
````

Or run locally
````
mvn spring-boot:run
````
Swagger documentation:

[http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)

----------

## Related Services

-   pyg-owner → pet owners and pets management
    
-   pyg-professional → professional profiles
    

----------

## Author

Jessica Alvarado  