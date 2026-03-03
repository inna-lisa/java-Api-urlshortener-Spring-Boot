# Stage 1 - build
FROM gradle:8.14.0-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle clean bootJar

# Stage 2 - run
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
