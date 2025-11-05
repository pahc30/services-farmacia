# Multi-stage build for auth microservice
FROM eclipse-temurin:21-jdk-alpine AS builder

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom files
COPY mvnw mvnw.cmd ./
COPY .mvn .mvn
COPY pom.xml ./
COPY businessdomain/pom.xml businessdomain/
COPY businessdomain/auth/pom.xml businessdomain/auth/

# Download dependencies (cached layer)
RUN ./mvnw dependency:go-offline -pl businessdomain/auth

# Copy source code
COPY businessdomain/shared/ businessdomain/shared/
COPY businessdomain/auth/src businessdomain/auth/src

# Build the application
RUN ./mvnw clean package -pl businessdomain/auth -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine AS runtime

# Create non-root user
RUN addgroup -g 1000 farmacia && \
    adduser -u 1000 -G farmacia -s /bin/sh -D farmacia

# Set working directory
WORKDIR /app

# Copy built JAR from builder stage
COPY --from=builder /app/businessdomain/auth/target/*.jar app.jar

# Change ownership to non-root user
RUN chown -R farmacia:farmacia /app

# Switch to non-root user
USER farmacia

# Expose port
EXPOSE 7011

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:7011/auth/actuator/health || exit 1

# Set Spring profile for Render
ENV SPRING_PROFILES_ACTIVE=render

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]