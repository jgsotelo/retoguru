# API REST Serverless de Pedidos (Reto Guru)

Este proyecto es una API REST de alta disponibilidad para la gestión de pedidos, construida con una arquitectura serverless nativa de AWS. Utiliza Spring WebFlux (programación reactiva) y está diseñada para desplegarse como una sola función AWS Lambda con Spring Cloud Function.

El núcleo de la aplicación sigue los principios de la arquitectura Hexagonal (Puertos y Adaptadores) para mantener un dominio desacoplado de la infraestructura.

---

## Core Tecnologías

* **Java 17**
* **Spring Boot 3 / Spring WebFlux:** Framework reactivo (no bloqueante) para la API.
* **Spring Cloud Function:** Adaptador para ejecutar la aplicación Spring como una AWS Lambda.
* **AWS DynamoDB:** Base de datos NoSQL serverless (usando el SDK v2 Asíncrono).
* **Serverless Framework:** Infraestructura como Código (IaC) para definir y desplegar los recursos en AWS.
* **Maven:** Gestión de dependencias y construcción.
* **Lombok:** Para reducir el código *boilerplate*.

---

## 🏛️ Arquitectura (Puertos y Adaptadores)

El código está organizado en capas siguiendo el patrón de Puertos y Adaptadores:

* **Domain (`com.guru.reto.domain`)**:
    * Contiene las entidades de negocio puras (`Order`, `OrderItem`).
    * No tiene dependencias de *frameworks* de infraestructura.

* **Application (`com.guru.reto.application`)**:
    * **Puertos de Entrada (Use Cases):** Define las acciones que la aplicación puede hacer (`OrderMutationPort`, `OrderSearchPort`).
    * **Puertos de Salida:** Define las interfaces que la infraestructura debe implementar (`OrderPort`).
    * **Adaptadores de Aplicación:** Orquestan la lógica (`OrderMutationAdapter`, `OrderSearchAdapter`).

* **Infrastructure (`com.guru.reto.infrastructure`)**:
    * **Entrada (Inbound):** `in.rest.router`.
        * Implementa la API REST usando Endpoints Funcionales de WebFlux (`OrderRouter`, `OrderHandler`).
    * **Salida (Outbound):** `out.persistence`.
        * Implementa el `OrderPort` (`OrderAdapter`) conectándose a DynamoDB.

---

## 🚀 Endpoints de la API

Las rutas se definen en `OrderRouter.java`:

| Método | Ruta | Descripción |
| :--- | :--- | :--- |
| `GET` | `/orders` | Obtiene una lista de las últimas 10 órdenes registradas. |
| `GET` | `/orders/{id}` | Busca una orden específica por su ID. |
| `POST` | `/orders` | Registra una nueva orden. |
| `PUT` | `/orders` | Actualiza los datos de una orden existente (basado en el ID en el body). |

---

## 🛡️ Validación Reactiva

La validación de los DTOs de entrada (ej. `OrderRegisterReq`) se maneja de forma reactiva y centralizada:

1.  Los DTOs usan anotaciones `jakarta.validation` (ej. `@NotNull`, `@NotBlank`).
2.  La clase `WebFluxConfig` "activa" el validador de Spring para que se dispare automáticamente durante la deserialización de `bodyToMono()`.
3.  El `OrderHandler` captura la excepción `WebExchangeBindException` usando `.onErrorResume()`.
4.  Si la validación falla, el método `handleValidationException` transforma los errores en una respuesta JSON estructurada (`ErrorResponse`) y retorna un **HTTP 400 Bad Request**.

---

## 💻 Desarrollo Local

Para ejecutar la aplicación localmente:

1.  **DynamoDB:** Necesitas una instancia de DynamoDB Local (ej. en Docker) accesible.
2.  **Configuración:** La configuración regional y el nombre de la tabla se leen desde `application.yml` (valores por defecto: `us-east-1` y `orders-table-dev`).
3.  **Ejecutar:** Inicia la aplicación desde `RetoApplication.java`. El servidor WebFlux (Netty) se iniciará en el puerto 8080.

---

## ☁️ Despliegue (AWS)

El despliegue está automatizado:

1.  **Infraestructura como Código:** El archivo `serverless.yml` define la AWS Lambda, el HTTP API Gateway, la tabla DynamoDB y los permisos IAM necesarios.
2.  **CI/CD:** El *workflow* de GitHub Actions en `.github/workflows/deploy.yml` compila el JAR, se autentica en AWS y ejecuta `serverless deploy` automáticamente.

```bash
# Para desplegar manualmente (requiere credenciales de AWS y Serverless Framework)
mvn clean package
serverless deploy --stage dev