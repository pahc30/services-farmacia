# Multi-stage build for producto microservice
FROM eclipse-temurin:21-jdk-alpine AS builder

# Install Maven
RUN apk add --no-cache maven

# Set working directory
WORKDIR /app

# Copy all files needed for build
COPY . .

# Build the application
RUN mvn clean package -pl businessdomain/producto -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine AS runtime

# Create non-root user
RUN addgroup -g 1000 farmacia && \
    adduser -u 1000 -G farmacia -s /bin/sh -D farmacia

# Set working directory
WORKDIR /app

# Create uploads directory for file handling with correct path
RUN mkdir -p /app/uploads && chown -R farmacia:farmacia /app/uploads

# Copy built JAR from builder stage
COPY --from=builder /app/businessdomain/producto/target/*.jar app.jar

# Change ownership to non-root user
RUN chown -R farmacia:farmacia /app

# Switch to non-root user
USER farmacia

# Expose port
EXPOSE 7013

# Install curl for health check
USER root
RUN apk add --no-cache curl
USER farmacia

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=120s --retries=3 \
    CMD curl -f http://localhost:${PORT:-7013}/actuator/health || exit 1

# Set Spring profile for Render
ENV SPRING_PROFILES_ACTIVE=render

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]