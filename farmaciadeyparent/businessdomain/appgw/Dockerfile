# Multi-stage build for appgw (gateway) microservice
FROM eclipse-temurin:21-jdk-alpine AS builder

# Install Maven
RUN apk add --no-cache maven

# Set working directory
WORKDIR /app

# Copy all files needed for build
COPY . .

# Build the application
RUN mvn clean package -pl businessdomain/appgw -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine AS runtime

# Create non-root user
RUN addgroup -g 1000 farmacia && \
    adduser -u 1000 -G farmacia -s /bin/sh -D farmacia

# Set working directory
WORKDIR /app

# Copy built JAR from builder stage
COPY --from=builder /app/businessdomain/appgw/target/*.jar app.jar

# Change ownership to non-root user
RUN chown -R farmacia:farmacia /app

# Switch to non-root user
USER farmacia

# Expose port (Gateway - main entry point)
EXPOSE 9000

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:9000/actuator/health || exit 1

# Set Spring profile for Render
ENV SPRING_PROFILES_ACTIVE=render

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]