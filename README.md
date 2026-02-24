# 🏢 Franchise Management Service (Reactive)

Servicio reactivo desarrollado con Spring Boot 3.2.3 y Java 21 para la gestion integral de franquicias, sucursales y productos. El sistema aplica Arquitectura Hexagonal y esta desplegado en AWS.

---

## Informacion del Autor
* **Desarrollador:** Mg.Oscar Rodriguez
* **Contacto:** oscarpino711@gmail.com

## 🚀 Despliegue y Ejecución

### Ejecución Local con Docker (Recomendado)
El proyecto está configurado para conectarse automáticamente a la base de datos AWS RDS desde tu contenedor local.
1. Asegúrate de estar en la raíz del proyecto.
2. Ejecuta:
   docker-compose up --build --force-recreate
3. Accede a la documentación interactiva: http://localhost:8080/webjars/swagger-ui/index.html

### Acceso en la Nube (AWS)
La solución está desplegada y operativa en la nube:
* Swagger UI: https://iz5f632zbj.us-east-2.awsapprunner.com/webjars/swagger-ui/index.html

---

## 🏗️ Consideraciones de Diseño y Arquitectura

Se optó por una Arquitectura Hexagonal (Ports & Adapters) para garantizar que la lógica de negocio sea independiente de agentes externos (DB, Frameworks, UI).

### Decisiones Clave:
* Paradigma Reactivo: Se utilizó Project Reactor (WebFlux) y R2DBC para el manejo de hilos no bloqueantes, permitiendo una mayor escalabilidad con menos recursos de hardware.
* Mappers Manuales: Se evitaron librerías de mapeo automático (como MapStruct) para mantener un control total sobre la transformación de datos entre la capa de Infraestructura (Entities) y Dominio (Models).
* Puertos e Interfaces: La documentación Javadoc se centralizó en los Output Ports del dominio, definiendo claramente el contrato que cualquier persistencia debe cumplir.
* Observabilidad: Implementación exhaustiva de logs mediante SLF4J en capas de controlador y servicio para trazabilidad de peticiones y errores.

---

## 🛠️ Stack Tecnológico

* Java 21: Última versión LTS con mejoras en rendimiento.
* Spring WebFlux: Stack reactivo para endpoints no bloqueantes.
* Spring Data R2DBC: Conectividad reactiva a bases de datos relacionales.
* MySQL (AWS RDS): Motor de base de datos en la nube.
* Docker: Contenerización de la aplicación.
* AWS App Runner: Despliegue escalable del servicio.

---

## 📈 Pruebas Unitarias
Se logró una cobertura de código superior al 80%, validando flujos de éxito y casos de borde (excepciones).
* Herramientas: JUnit 5, Mockito y StepVerifier (para flujos Mono/Flux).
* Manejo de Excepciones: Se implementó un GlobalExceptionHandler con @RestControllerAdvice para transformar errores de lógica (IllegalArgumentException) en respuestas HTTP estandarizadas (404/400).

---

## 📡 Endpoints Principales (RESTful)

### Franquicias
* POST /api/franchises: Crear franquicia.
* PUT /api/franchises/{id}/name: (Plus) Actualizar nombre.
* GET /api/franchises/{id}/top-products: (Req 6) Reporte de productos con más stock por sucursal.

### Sucursales
* POST /api/franchises/{id}/branches: Agregar sucursal a franquicia.
* PUT /api/branches/{id}/name: (Plus) Actualizar nombre.

### Productos
* POST /api/branches/{id}/products: Agregar producto a sucursal.
* DELETE /api/products/{id}: Eliminar producto.
* PATCH /api/products/{id}/stock: Modificar stock.
* PUT /api/products/{id}/name: (Plus) Actualizar nombre.

---
Nota: El script schema.sql se ejecuta automáticamente al levantar el contenedor de Docker para asegurar que la estructura de tablas esté lista para AWS RDS.
