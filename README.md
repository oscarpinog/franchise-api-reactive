# ?? Franchise Management Service (Reactive)

Servicio reactivo desarrollado con Spring Boot 3.2.3 y Java 21 para la gesti車n integral de franquicias, sucursales y productos. El sistema aplica Arquitectura Hexagonal y est芍 desplegado en AWS.

---

## Informacion del Autor
* **Desarrollador:** Mg.Oscar Rodriguez
* **Contacto:** oscarpino711@gmail.com

---

## ?? Despliegue y Ejecuci車n

### Ejecuci車n Local con Docker (Recomendado)
El proyecto est芍 configurado para conectarse autom芍ticamente a la base de datos AWS RDS desde tu contenedor local.
1. Aseg迆rate de estar en la ra赤z del proyecto.
2. Ejecuta:
   docker-compose up --build --force-recreate
3. Accede a la documentaci車n interactiva: http://localhost:8080/webjars/swagger-ui/index.html

### Acceso en la Nube (AWS)
La soluci車n est芍 desplegada y operativa en la nube:
* Swagger UI: https://iz5f632zbj.us-east-2.awsapprunner.com/webjars/swagger-ui/index.html

---

## ??? Consideraciones de Dise?o y Arquitectura

Se opt車 por una Arquitectura Hexagonal (Ports & Adapters) para garantizar que la l車gica de negocio sea independiente de agentes externos (DB, Frameworks, UI).

### Decisiones Clave:
* Paradigma Reactivo: Se utiliz車 Project Reactor (WebFlux) y R2DBC para el manejo de hilos no bloqueantes, permitiendo una mayor escalabilidad con menos recursos de hardware.
* Mappers Manuales: Se evitaron librer赤as de mapeo autom芍tico (como MapStruct) para mantener un control total sobre la transformaci車n de datos entre la capa de Infraestructura (Entities) y Dominio (Models).
* Puertos e Interfaces: La documentaci車n Javadoc se centraliz車 en los Output Ports del dominio, definiendo claramente el contrato que cualquier persistencia debe cumplir.
* Observabilidad: Implementaci車n exhaustiva de logs mediante SLF4J en capas de controlador y servicio para trazabilidad de peticiones y errores.

---

## ??? Stack Tecnol車gico

* Java 21: 迆ltima versi車n LTS con mejoras en rendimiento.
* Spring WebFlux: Stack reactivo para endpoints no bloqueantes.
* Spring Data R2DBC: Conectividad reactiva a bases de datos relacionales.
* MySQL (AWS RDS): Motor de base de datos en la nube.
* Docker: Contenerizaci車n de la aplicaci車n.
* AWS App Runner: Despliegue escalable del servicio.

---

## ?? Pruebas Unitarias
Se logr車 una cobertura de c車digo superior al 80%, validando flujos de 谷xito y casos de borde (excepciones).
* Herramientas: JUnit 5, Mockito y StepVerifier (para flujos Mono/Flux).
* Manejo de Excepciones: Se implement車 un GlobalExceptionHandler con @RestControllerAdvice para transformar errores de l車gica (IllegalArgumentException) en respuestas HTTP estandarizadas (404/400).

---

## ?? Endpoints Principales (RESTful)

### Franquicias
* POST /api/franchises: Crear franquicia.
* PUT /api/franchises/{id}/name: (Plus) Actualizar nombre.
* GET /api/franchises/{id}/top-products: (Req 6) Reporte de productos con m芍s stock por sucursal.

### Sucursales
* POST /api/franchises/{id}/branches: Agregar sucursal a franquicia.
* PUT /api/branches/{id}/name: (Plus) Actualizar nombre.

### Productos
* POST /api/branches/{id}/products: Agregar producto a sucursal.
* DELETE /api/products/{id}: Eliminar producto.
* PATCH /api/products/{id}/stock: Modificar stock.
* PUT /api/products/{id}/name: (Plus) Actualizar nombre.

---
Nota: El script schema.sql se ejecuta autom芍ticamente al levantar el contenedor de Docker para asegurar que la estructura de tablas est谷 lista para AWS RDS.