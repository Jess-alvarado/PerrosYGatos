# Perros&Gatos


Backend platform designed to connect **pet owners** with **animal behavior professionals**, such as trainers and ethologists.

The system focuses on building a **scalable and secure backend architecture**, using domain-oriented microservices, centralized authentication, and service-to-service communication.

It provides the foundation for managing users, pets, and professional services within the platform.

----------

## Engineering Highlights

-   **Centralized JWT validation** handled by `pyg-auth`, isolating authentication logic from business services.


-   **Ownership-based authorization** to ensure users can only access their own resources.


-   **Domain-driven service separation**, where each service owns its business logic and persistence layer.


-   **Docker-based local development environment** using Docker Compose.


-   **Architecture designed to scale** with future services such as scheduling, notifications, and search.
    

----------

## Architecture

* **Frontend:** `pyg-frontend` (Vite + React)
* **API Gateway:** `pyg-gateway` (Port 9090) - *Single entry point*
    * ──► `/api/auth/**` ──► **pyg-auth** (Port 8081)
    * ──► `/api/owner/**` ──► **pyg-owner** (Port 8082)
    * ──► `/api/prof/**` ──► **pyg-professional** (Port 8083)

## Tech Stack

### Backend

-   Java 17
    
-   Spring Boot
    
-   Spring Security
    
-   JWT Authentication
    
-   Spring Data JPA
    
-   PostgreSQL
    
-   Maven


### Frontend

-   React 18

-   Vite

-   Tailwind CSS

-   Axios

-   React Router Dom

-   Lucide React

-   JWT Decode
    

### Infrastructure & Tools

-   Docker
    
-   Docker Compose

-   API Gateway

-   PostgreSQL
    

----------

## Services

| Service | Description |
|---|---|
| `pyg-gateway` | Entry Point. Centralized routing and request forwarding to microservices |
| `pyg-frontend` | User Interface. React + Vite |
| `pyg-auth` | Authentication and JWT validation |
| `pyg-owner` | Pet owners and pets management |
| `pyg-professional` | Professional profiles |

## Running Locally
### How to Run

1. **Clone the repository:**
   ```bash
   git clone <repo-url>
1. **Start the platform with Docker:**
   ```bash
   docker-compose up
----------

## Swagger Documentation

pyg-auth
[http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)

pyg-owner
[http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html)

pyg-professional
[http://localhost:8083/swagger-ui.html](http://localhost:8083/swagger-ui.html)

----------


## Project Purpose

This project is being developed as a backend platform focused on **scalable architecture, secure authentication, and service-oriented backend design**.

It explores practical backend engineering concepts such as:

-   microservices architecture
   
-   centralized authentication with JWT
   
-   service-to-service communication
   
-   secure resource access and authorization
   
-   containerized development environments
    

The goal is to build a clean and extensible backend foundation that can support future platform features and additional services.
    

----------

## Author

Jessica Alvarado

----------
