#!/bin/bash

# Función para iniciar un servicio
start_service() {
    local service=$1
    local port=$2
    cd "businessdomain/$service"
    SPRING_PROFILES_ACTIVE=test ./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=$port" &
    cd ../..
    echo "Iniciando $service en puerto $port..."
    sleep 10  # Esperar a que el servicio inicie
}

# Iniciar servicios en orden
echo "Iniciando servicios en modo test..."

# 1. Auth (debe iniciar primero)
start_service "auth" 9000

# 2. Producto
start_service "producto" 9002

# 3. Usuario
start_service "usuario" 9003

# 4. Método de Pago
start_service "metodopago" 9004

# 5. Compra (último porque depende de los demás)
start_service "compra" 9001

echo "Todos los servicios iniciados en modo test"
echo "Puedes obtener un token de prueba con:"
echo "curl -X POST \"http://localhost:9000/test/token?username=testuser\""