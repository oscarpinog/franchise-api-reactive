# ?? Franchise Management Service (Reactive)

Servicio reactivo desarrollado con Spring Boot 3.2.3 y Java 21 para la gestion integral de franquicias, sucursales y productos. El sistema aplica Arquitectura Hexagonal y esta desplegado en AWS.

---

## Informacion del Autor
* **Desarrollador:** Mg.Oscar Rodriguez
* **Contacto:** oscarpino711@gmail.com

## ?? Despliegue y Ejecucion

### Ejecucion Local con Docker (Recomendado)
El proyecto esta configurado para conectarse automaticamente a la base de datos AWS RDS desde tu contenedor local.
1. Asegurate de estar en la raiz del proyecto.
2. Ejecuta:
   docker-compose up --build --force-recreate
3. Accede a la documentacion interactiva: http://localhost:8080/webjars/swagger-ui/index.html

### Acceso en la Nube (AWS)
La solucion esta desplegada y operativa en la nube:
* Swagger UI: https://iz5f632zbj.us-east-2.awsapprunner.com/webjars/swagger-ui/index.html

---

## ??? Consideraciones de Diseno y Arquitectura

Se opto por una Arquitectura Hexagonal (Ports & Adapters) para garantizar que la logica de negocio sea independiente de agentes externos (DB, Frameworks, UI).

### Decisiones Clave:
* Paradigma Reactivo: Se utilizo Project Reactor (WebFlux) y R2DBC para el manejo de hilos no bloqueantes, permitiendo una mayor escalabilidad con menos recursos de hardware.
* Mappers Manuales: Se evitaron librerias de mapeo automatico (como MapStruct) para mantener un control total sobre la transformacion de datos entre la capa de Infraestructura (Entities) y Dominio (Models).
* Puertos e Interfaces: La documentacion Javadoc se centralizo en los Output Ports del dominio, definiendo claramente el contrato que cualquier persistencia debe cumplir.
* Observabilidad: Implementacion exhaustiva de logs mediante SLF4J en capas de controlador y servicio para trazabilidad de peticiones y errores.

---

## ??? Stack Tecnologico

* Java 21: Ultima version LTS con mejoras en rendimiento.
* Spring WebFlux: Stack reactivo para endpoints no bloqueantes.
* Spring Data R2DBC: Conectividad reactiva a bases de datos relacionales.
* MySQL (AWS RDS): Motor de base de datos en la nube.
* Docker: Contenerizacion de la aplicacion.
* AWS App Runner: Despliegue escalable del servicio.

---

## ?? Pruebas Unitarias
Se logro una cobertura de codigo superior al 80%, validando flujos de exito y casos de borde (excepciones).
* Herramientas: JUnit 5, Mockito y StepVerifier (para flujos Mono/Flux).
* Manejo de Excepciones: Se implemento un GlobalExceptionHandler con @RestControllerAdvice para transformar errores de logica (IllegalArgumentException) en respuestas HTTP estandarizadas (404/400).

---

## ?? Endpoints Principales (RESTful)

### Franquicias
* POST /api/franchises: Crear franquicia.
* PUT /api/franchises/{id}/name: (Plus) Actualizar nombre.
* GET /api/franchises/{id}/top-products: (Req 6) Reporte de productos con mas stock por sucursal.

### Sucursales
* POST /api/franchises/{id}/branches: Agregar sucursal a franquicia.
* PUT /api/branches/{id}/name: (Plus) Actualizar nombre.

### Productos
* POST /api/branches/{id}/products: Agregar producto a sucursal.
* DELETE /api/products/{id}: Eliminar producto.
* PATCH /api/products/{id}/stock: Modificar stock.
* PUT /api/products/{id}/name: (Plus) Actualizar nombre.

---
Nota: El script schema.sql se ejecuta automaticamente al levantar el contenedor de Docker para asegurar que la estructura de tablas este lista para AWS RDS.