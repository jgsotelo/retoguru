# API REST Serverless de Pedidos (Reto Guru)

Este proyecto es una API REST de alta disponibilidad para la gestión de pedidos, construida con una arquitectura serverless nativa de AWS. Utiliza Spring WebFlux (programación reactiva) y está diseñada para desplegarse como una sola función AWS Lambda con Spring Cloud Function.

El núcleo de la aplicación sigue los principios de la Arquitectura Limpia (Clean Architecture) y su implementación de Puertos y Adaptadores (Hexagonal) para mantener un dominio desacoplado de la infraestructura.

---

## Core Tecnologías

* **Java 17**
* **Spring Boot 3 / Spring WebFlux:** Framework reactivo (no bloqueante) para la API.
* **Spring Cloud Function:** Adaptador para ejecutar la aplicación Spring como una AWS Lambda.
* **Project Reactor:** Librería de programación reactiva (`Mono`/`Flux`) usada en todo el proyecto.
* **AWS DynamoDB:** Base de datos NoSQL serverless (usando el SDK v2 Asíncrono).
* **Serverless Framework:** Infraestructura como Código (IaC) para definir y desplegar los recursos en AWS.
* **Maven:** Gestión de dependencias y construcción.
* **Lombok:** Para reducir el código *boilerplate*.

---

## ⚛️ Programación Reactiva y Funcional

Este proyecto está construido sobre un *stack* 100% reactivo y no bloqueante, lo cual es ideal para aplicaciones serverless de alta concurrencia.

* **Flujo Reactivo (`Mono`/`Flux`):** Toda la lógica, desde el *handler* HTTP hasta la llamada a la base de datos, está encapsulada en publicadores reactivos (`Mono` para 0-1 elementos, `Flux` para 0-N elementos). Esto asegura que ningún hilo se bloquee esperando I/O.
  * **Ejemplos:** `OrderHandler` y `OrderAdapter` retornan `Mono<ServerResponse>` y `Mono<Order>`.
* **Endpoints Funcionales (WebFlux):** En lugar de usar anotaciones (`@RestController`), la capa de API se define funcionalmente en `OrderRouter.java`. Esto proporciona un control explícito sobre el enrutamiento y la composición de la lógica HTTP.
* **Manejo de I/O No Bloqueante:** El `OrderAdapter` utiliza `DynamoDbAsyncTable` (basado en `CompletableFuture`) y lo integra al flujo reactivo usando `Mono.fromCompletionStage()`.

---

## 🏛️ Arquitectura Limpia (Puertos y Adaptadores)

El código sigue los principios de **Clean Architecture** (Arquitectura Limpia), implementada mediante el patrón de **Puertos y Adaptadores (Hexagonal)**. El objetivo es proteger la lógica de negocio (Dominio) de las dependencias externas (Frameworks, Bases de Datos, API).

* **Domain (`com.guru.reto.domain`)**:
  * **El Núcleo.** Contiene las entidades de negocio puras (`Order`, `OrderItem`).
  * No tiene dependencias de Spring, DynamoDB o WebFlux. Es Java puro.

* **Application (`com.guru.reto.application`)**:
  * **Puertos de Entrada (Use Cases):** Define las acciones que la aplicación puede hacer (`OrderMutationPort`, `OrderSearchPort`). Son las interfaces que conducen al dominio.
  * **Puertos de Salida:** Define las interfaces que el dominio necesita para comunicarse con el exterior (`OrderPort`).
  * **Adaptadores de Aplicación:** Orquestan la lógica (`OrderMutationAdapter`, `OrderSearchAdapter`).

* **Infrastructure (`com.guru.reto.infrastructure`)**:
  * **Adaptadores de Entrada (Inbound):** `in.rest.router`.
    * Implementa la API REST (`OrderRouter`, `OrderHandler`). Llama a los Puertos de Entrada.
  * **Adaptadores de Salida (Outbound):** `out.persistence`.
    * Implementa el `OrderPort` (`OrderAdapter`) conectándose a DynamoDB.

---

## 🏛️ Patrones de Diseño Aplicados

Además de la arquitectura Hexagonal, el proyecto utiliza varios patrones de diseño clave:

* **Arquitectura Hexagonal (Puertos y Adaptadores):** Es el patrón principal para implementar la Arquitectura Limpia.
  * **Puertos de Entrada:** `OrderMutationPort`, `OrderSearchPort`.
  * **Puerto de Salida:** `OrderPort`.
  * **Adaptadores:** `OrderHandler` (Entrada), `OrderAdapter` (Salida).

* **Patrón Builder:** Se utiliza (vía Lombok `@Builder`) para la construcción compleja de objetos de dominio (`Order`, `OrderItem`) y DTOs de respuesta (`OrderResponse`).

* **Factory Method (Método de Fábrica Estático):** Se usa para encapsular la lógica de conversión de DTOs (infraestructura) a entidades de Dominio, promoviendo el desacoplamiento.
  * `Order.fromRegister(OrderRegisterReq req)`.
  * `Order.fromUpdate(OrderUpdateReq req)`.
  * `OrderResponse.from(Order order)`.

* **Data Transfer Object (DTO):** Se utiliza para separar la representación de datos de la API (capa de infraestructura) del modelo de negocio (capa de dominio).
  * Entrada: `OrderRegisterReq`, `OrderUpdateReq`.
  * Salida: `OrderResponse`, `ErrorResponse`.

* **Inyección de Dependencias (DI):** Se utiliza en todo el proyecto a través del constructor (vía Lombok `@AllArgsConstructor`) para permitir que Spring (el contenedor IoC) inyecte las implementaciones de los puertos (interfaces).

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

## ☁️ Infraestructura como Código (Serverless Framework)

Toda la infraestructura de AWS se define como código en el archivo `serverless.yml`. Esto garantiza despliegues consistentes y reproducibles.

Componentes clave definidos:

* **`provider`**:
  * **Runtime:** `java17`.
  * **Memoria/Timeout:** `1536MB` y `29` segundos.
  * **Variables de Entorno:** Pasa dinámicamente el nombre de la tabla (`DYNAMODB_TABLE`) y la región (`DYNAMODB_REGION`) a la aplicación Spring.
  * **Permisos IAM:** Otorga permisos granulares a la Lambda para realizar operaciones CRUD (`PutItem`, `GetItem`, `UpdateItem`, etc.) únicamente sobre la tabla de pedidos definida.

* **`package`**:
  * Define el artefacto a desplegar: el "fat jar" generado por Maven (`target/reto-0.0.1-SNAPSHOT-aws.jar`).

* **`functions`**:
  * Define la función Lambda principal (`api`).
  * **Handler:** Utiliza `org.springframework.cloud.function.adapter.aws.FunctionInvoker` como punto de entrada, que traduce los eventos de API Gateway en solicitudes de Spring WebFlux.
  * **Eventos:** Crea un **HTTP API Gateway** (más rápido y económico) que captura todas las solicitudes (`ANY`) en `/orders` y `/orders/{proxy+}` y las dirige a la única función Lambda.

* **`resources`**:
  * Define la tabla de DynamoDB (`OrdersDynamoDBTable`) usando sintaxis de CloudFormation.
  * **Clave de Partición:** `orderId` (String).
  * **Modo de Facturación:** `PAY_PER_REQUEST` (pago por uso), verdaderamente serverless.
  * **Política de Eliminación:** `DeletionPolicy: Retain` para proteger la base de datos contra eliminaciones accidentales al borrar el stack.

---

## ⚙️ Despliegue (CI/CD con GitHub Actions)

El despliegue está automatizado mediante el flujo de trabajo definido en `.github/workflows/deploy.yml`.

El flujo se dispara automáticamente en cada `push` a la rama `feat/crud`.

El proceso de despliegue (`job: deploy`) sigue estos pasos:
1.  **Checkout:** Descarga el código fuente.
2.  **Configurar Java:** Instala Java 17 (Temurin).
3.  **Compilar el JAR:** Ejecuta `mvn clean package -DskipTests` para crear el artefacto desplegable.
4.  **Configurar Credenciales de AWS:** Utiliza `aws-actions/configure-aws-credentials` para autenticarse en AWS usando secretos de GitHub (`AWS_ACCESS_KEY_ID` y `AWS_SECRET_ACCESS_KEY`).
5.  **Instalar Serverless:** Instala la versión 4 de Serverless Framework (`npm install -g serverless@4`).
6.  **Despliegue:** Ejecuta `serverless deploy --stage dev`, utilizando una licencia de Serverless (`SERVERLESS_ACCESS_KEY`) para el despliegue.