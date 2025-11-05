# Multi-stage build for metodopago microservice
FROM eclipse-temurin:21-jdk-alpine AS builder

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom files
COPY mvnw mvnw.cmd ./
COPY .mvn .mvn
COPY pom.xml ./
COPY businessdomain/pom.xml businessdomain/
COPY businessdomain/metodopago/pom.xml businessdomain/metodopago/

# Download dependencies (cached layer)
RUN ./mvnw dependency:go-offline -pl businessdomain/metodopago

# Copy source code
COPY businessdomain/shared/ businessdomain/shared/
COPY businessdomain/metodopago/src businessdomain/metodopago/src

# Build the application
RUN ./mvnw clean package -pl businessdomain/metodopago -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine AS runtime

# Create non-root user
RUN addgroup -g 1000 farmacia && \
    adduser -u 1000 -G farmacia -s /bin/sh -D farmacia

# Set working directory
WORKDIR /app

# Copy built JAR from builder stage
COPY --from=builder /app/businessdomain/metodopago/target/*.jar app.jar

# Change ownership to non-root user
RUN chown -R farmacia:farmacia /app

# Switch to non-root user
USER farmacia

# Expose port
EXPOSE 7014

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:7014/metodopago/actuator/health || exit 1

# Set Spring profile for Render
ENV SPRING_PROFILES_ACTIVE=render

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]