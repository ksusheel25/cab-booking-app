# Stage 1: Build the jar using Maven and Java 21
FROM maven:3.9.4-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy pom and download dependencies first (for caching)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build jar (skip tests for faster build)
RUN mvn clean package -DskipTests

# Stage 2: Run the jar with Java 21 JRE
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/user-service-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# Run the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
ENV SPRING_PROFILES_ACTIVE=docker