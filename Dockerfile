# Utilizar la imagen oficial de Maven para compilar el proyecto
FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /app

# Copiar los archivos del proyecto al contenedor
COPY . .

# Construir el proyecto con Maven
RUN mvn clean package -DskipTests

# Crear la imagen final basada en OpenJDK
FROM eclipse-temurin:23-jdk
WORKDIR /app

# Copiar el archivo JAR generado al contenedor
COPY --from=builder /app/target/*.jar app.jar

# Exponer el puerto definido en el application.properties
EXPOSE 9000

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
