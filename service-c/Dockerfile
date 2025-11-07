FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY . .
RUN mvn -q package -DskipTests

FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=build /workspace/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","/app/app.jar"]