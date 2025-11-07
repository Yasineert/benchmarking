## Build stage: compile the Spring Boot application
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY . .
RUN mvn -q package -DskipTests

## Runtime stage: use a lightweight JDK to run the jar
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=build /workspace/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/app.jar"]