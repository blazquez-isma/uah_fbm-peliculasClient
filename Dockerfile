FROM maven:3.9.9-eclipse-temurin-23 AS builder
WORKDIR /app

COPY pom.xml .
COPY src ./src
COPY uploads ./uploads
RUN mvn clean package -DskipTests

FROM eclipse-temurin:23-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
COPY --from=builder /app/uploads ./uploads

EXPOSE 9000
ENTRYPOINT ["java", "-jar", "app.jar"]