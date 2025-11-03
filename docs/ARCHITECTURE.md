# Arquitectura — PerrosYGatos

Este documento describe a alto nivel las decisiones arquitectónicas del proyecto PerrosYGatos, el modelo de microservicios y la justificación para centralizar la validación de JWT en `pyg-auth`.

## Visión general

- Arquitectura: Microservicios independientes comunicándose por HTTP REST y WebSocket para eventos en tiempo real.
- Lenguajes: Java (Spring Boot) para la mayoría de backends críticos; NestJS (TypeScript) para servicios rápidos y en tiempo real.
- Autenticación: Centralizada en `pyg-auth` (emisión y validación de JWT).

## Microservicios principales

- pyg-auth: Autenticación, emisión y validación de JWT. Punto único de confianza para identidad y roles.
- pyg-owner: Gestión de perfiles de dueños y sus mascotas.
- pyg-professional: Gestión de profesionales, disponibilidad y publicaciones.
- pyg-session: Organización de sesiones.
- pyg-notification: Servicio en NestJS para notificaciones push y en tiempo real.
- pyg-content / pyg-search / etc.: Servicios adicionales.

## Decisión: Validación de tokens centralizada

Motivación:
- Evitar duplicar secretos y lógica de validación en múltiples servicios.
- Facilitar rotación de secretos y cambios en las reglas de validación en un solo punto.
- Simplificar el despliegue y el cumplimiento de seguridad.

Patrón propuesto:
- `pyg-auth` expone un endpoint seguro (por ejemplo `POST /auth/validate`) que acepta un token y devuelve la validez y claims necesarios.
- Los servicios consumidores (owner, professional, etc.) llaman al endpoint de validación cuando necesiten confirmar un token externo (por ejemplo, en llamadas desde clientes que no usan el gateway).

## Comunicación y contratos

- APIs REST con esquemas OpenAPI (springdoc / swagger) para cada servicio.
- Contratos claros: las respuestas de validación deben ser compactas (ej. { valid: boolean, uid, role, exp }) para minimizar ancho de banda.

## Despliegue local

- Cada servicio tiene su propia base de datos en desarrollo (puede ser una instancia única con distintos schemas o múltiples bases de datos).
- Uso de `.env` para variables sensibles y configuración local (`spring.config.import` en `application.yml`).

## Siguientes pasos

- Añadir un `docker-compose` para orquestar Postgres + servicios base (auth, owner, professional).
- Definir Gateway (BFF) que gestione autenticación en el borde y reduzca llamadas inter-servicio.
- Implementar caché de validaciones si la latencia lo requiere.

---

Documento generado como complemento del README principal.
