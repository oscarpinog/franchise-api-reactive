# --- Etapa 1: Construcción (Build) ---
# Usamos Maven con Java 21 sobre Alpine para que la descarga sea rápida
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# 1. Copiamos el pom.xml para descargar las dependencias primero (cache)
COPY pom.xml .
RUN mvn dependency:go-offline

# 2. Copiamos el código fuente y generamos el JAR
COPY src ./src
RUN mvn clean package -DskipTests

# --- Etapa 2: Ejecución (Runtime) ---
# Usamos el JRE de Temurin 21 sobre Alpine (pesa ~100MB menos que la estándar)
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Copiamos el JAR usando el nombre fijo que definiste en el POM
COPY --from=build /app/target/franchise-service.jar app.jar

# Exponemos el puerto de Spring Boot
EXPOSE 8080

# Parámetros optimizados para contenedores
# - UseContainerSupport permite que la JVM lea correctamente los límites de RAM de Docker
# - MaxRAMPercentage evita que Java intente usar toda la RAM del host
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]