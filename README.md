=================================================================
🏢 FRANCHISE MANAGEMENT SERVICE (REACTIVE)
=================================================================

Servicio reactivo desarrollado con Spring Boot 3.2.3 y Java 21 para 
la gestión integral de franquicias, sucursales y productos. 
El sistema aplica Arquitectura Hexagonal y está desplegado en AWS.

-----------------------------------------------------------------
## INFORMACIÓN DEL AUTOR
-----------------------------------------------------------------
* Desarrollador: Mg. Oscar Rodriguez
* Contacto: oscarpino711@gmail.com
* Telefono: +57 3108375232
* Repositorio: https://github.com/oscarpinog/franchise-api-reactive

-----------------------------------------------------------------
## 🌍 ACCESO AL SERVICIO DESPLEGADO (AWS)
-----------------------------------------------------------------
La API se encuentra operativa y documentada en la siguiente URL:

🚀 SWAGGER UI NUBE:
https://iz5f632zbj.us-east-2.awsapprunner.com/webjars/swagger-ui/index.html

(Seleccionar Server: "Producción" en el menú desplegable de Swagger)

-----------------------------------------------------------------
## 🚀 DESPLIEGUE Y EJECUCIÓN LOCAL
-----------------------------------------------------------------

### 🔑 1. CONFIGURACIÓN DEL ARCHIVO .ENV (CRÍTICO)
Es **indispensable** crear el archivo ".env" en la raíz del proyecto. El archivo "docker-compose.yml" actual está diseñado para recibir esta información e inyectarla al contenedor; sin ella, la aplicación fallará al iniciar.

Contenido sugerido para el archivo .env:

# --- CONFIGURACIÓN DE BASE DE DATOS (MySQL) ---
# IMPORTANTE: Si desea realizar la prueba utilizando la base de datos 
# configurada por el autor en AWS RDS, deberá SOLICITAR los datos de 
# acceso (Host, User, Password...) por interno. 

# Si usa una propia, asegúrese de que sea MySQL y use el driver r2dbc
Archivo .env debe contener:

DB_URL=r2dbc:mysql://SU_HOST_AQUI:3306/franchise_db
DB_USERNAME=su_usuario
DB_PASSWORD=su_password
JWT_SECRET=msc_oscar_rodriguez_ingeniero_de_software_prueba_tecnica_seti
APP_AWS_URL=http://localhost:8080


### 💻 2. OPCIONES DE EJECUCIÓN LOCAL

* OPCIÓN 1: Conexión a Base de Datos Externa (AWS RDS / Propia)
  Comando: docker-compose up --build --force-recreate

* OPCIÓN 2: Base de Datos Local en Contenedor (RECOMENDADO / RÁPIDO)
  NO requiere configuraciones previas ni crear el archivo ".env", ya que utiliza valores predefinidos.
  1. Renombre "docker-compose-bdlocal.txt" a "docker-compose.yml".
  2. Ejecute: docker-compose up --build

-----------------------------------------------------------------
## 🏗️ DISEÑO Y ARQUITECTURA
-----------------------------------------------------------------
Se optó por una Arquitectura Hexagonal (Ports & Adapters) para garantizar 
la independencia de la lógica de negocio.

* Paradigma Reactivo: Project Reactor (WebFlux) y R2DBC.
* Mappers Manuales: Control total sobre transformación Entities <-> Models.
* Puertos e Interfaces: Javadoc centralizado en Output Ports del dominio.
* Observabilidad: Logs mediante SLF4J para trazabilidad de peticiones.



-----------------------------------------------------------------
## 🔐 AUTENTICACIÓN Y SEGURIDAD
-----------------------------------------------------------------
El sistema utiliza JWT para proteger los endpoints de escritura.
1. Login: POST /api/auth/login -> {"username": "admin", "password": "admin123"}
2. Uso: Incluir Header "Authorization: Bearer <token>".

-----------------------------------------------------------------
## 🛠️ STACK TECNOLÓGICO
-----------------------------------------------------------------
* Java 21: Última versión LTS con mejoras en rendimiento.
* Spring WebFlux: Stack reactivo para endpoints no bloqueantes.
* Spring Data R2DBC: Conectividad reactiva a bases de datos relacionales.
* MySQL (AWS RDS): Motor de base de datos en la nube.
* Docker: Contenerización de la aplicación.
* AWS App Runner: Despliegue escalable del servicio.

-----------------------------------------------------------------
## 📈 PRUEBAS UNITARIAS
-----------------------------------------------------------------
Cobertura >80% con JUnit 5, Mockito y StepVerifier (flujos Mono/Flux).
Manejo de Errores: GlobalExceptionHandler con @RestControllerAdvice (400/404).

-----------------------------------------------------------------
## 📡 ENDPOINTS DEL SISTEMA
-----------------------------------------------------------------

### Auth Controller
* POST   /api/auth/login                  : Autenticación y obtención de JWT.

### Franchise Controller
* POST   /api/franchises                  : Crear una nueva franquicia.
* PUT    /api/franchises/{id}/name        : Actualizar nombre de franquicia.
* POST   /api/franchises/{id}/branches    : Agregar sucursal a una franquicia.
* GET    /api/franchises/{id}/top-products: Reporte de productos con más stock.

### Branch Controller
* PUT    /api/branches/{id}/name          : Actualizar nombre de sucursal.
* POST   /api/branches/{id}/products      : Agregar producto a una sucursal.

### Product Controller
* PUT    /api/products/{id}/name          : Actualizar nombre de un producto.
* PATCH  /api/products/{id}/stock         : Modificar stock de un producto.
* DELETE /api/products/{id}               : Eliminar un producto.

-----------------------------------------------------------------
## 🛠️ ANÁLISIS DE DEUDA TÉCNICA Y ROADMAP
-----------------------------------------------------------------

### ⚠️ DEFICIENCIAS IDENTIFICADAS (Hallazgos Corregidos)
* Seguridad Crítica: Credenciales expuestas (Corregido con .env).
* Autenticación: Falta de protección (Corregido con JWT).
* CORS: Acceso global (Corregido con configuración dinámica).
* Arquitectura: Lógica en adaptadores y patrones repetidos.

### 📈 ROADMAP DE MEJORAS
* [ ] Resiliencia: Circuit Breaker (Resilience4j) y Rate Limiting.
* [ ] Refactorización: Migrar logs a Spring AOP para reducir redundancia.
* [ ] Robustez: Manejo estricto de nulos y externalización de mensajes.

Nota: El script schema.sql se ejecuta automáticamente para asegurar las tablas.
=================================================================