# Multi-stage build for Nexxus Backend
FROM gradle:8.5-jdk21 AS build

# Set working directory
WORKDIR /app

# Copy gradle files first for better caching
COPY gradle/ gradle/
COPY gradlew gradlew.bat ./
COPY build.gradle.kts settings.gradle.kts ./
COPY buildSrc/ buildSrc/

# Copy source code
COPY libs/ libs/
COPY services/ services/

# Build the application
RUN ./gradlew build

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

# Install curl for health checks
RUN apk add --no-cache curl

# Set working directory
WORKDIR /app

# Create non-root user
RUN addgroup -S nexxus && adduser -S -G nexxus nexxus

# Copy the built JAR from build stage
COPY --from=build /app/services/core/build/libs/*.jar app.jar

# Change ownership to non-root user
RUN chown nexxus:nexxus app.jar

# Switch to non-root user
USER nexxus

# Expose port
EXPOSE 8000

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8000/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
