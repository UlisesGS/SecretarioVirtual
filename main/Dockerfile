# Imagen base con OpenJDK 21
FROM eclipse-temurin:21-jdk AS builder

# Establecemos el directorio de trabajo
WORKDIR /app

# Instalar Maven
RUN apt-get update && apt-get install -y maven

COPY . .

# Ejecutar Maven para limpiar y empaquetar el proyecto
RUN mvn clean package -DskipTests

# Copiamos el archivo JAR generado en la carpeta /app
COPY target/*.jar app.jar

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

# Exponemos el puerto 8080
EXPOSE 8080

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "app.jar"]
