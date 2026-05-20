FROM gradle:8.6-jdk17 AS build
WORKDIR /app


# Copy the Gradle configuration files first (helps with caching layers)
COPY build.gradle.kts settings.gradle.kts ./


# Copy the source code
COPY src ./src


# Build the application and skip tests to speed up deployment
RUN gradle bootJar -x test


# Use a lightweight JRE image for execution
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app


# Copy the compiled JAR file from the Gradle build directory
COPY --from=build /app/build/libs/*.jar app.jar


# Expose the port
EXPOSE 8080


# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
