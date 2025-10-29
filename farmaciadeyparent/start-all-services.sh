#!/bin/bash

echo "🚀 Iniciando todos los servicios de la Farmacia..."

# Función para verificar si un puerto está ocupado
check_port() {
    local port=$1
    local service=$2
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null ; then
        echo "✅ $service ya está corriendo en puerto $port"
        return 0
    else
        echo "❌ Puerto $port libre para $service"
        return 1
    fi
}

# Función para iniciar servicio en background
start_service() {
    local module=$1
    local port=$2
    local service_name=$3
    
    echo "🔄 Iniciando $service_name..."
    nohup ./mvnw spring-boot:run -pl $module > logs/${service_name}.log 2>&1 &
    echo $! > logs/${service_name}.pid
    
    # Esperar a que el servicio esté listo
    local counter=0
    local max_attempts=30
    
    while [ $counter -lt $max_attempts ]; do
        if curl -f http://localhost:$port/actuator/health > /dev/null 2>&1; then
            echo "✅ $service_name iniciado exitosamente en puerto $port"
            return 0
        fi
        sleep 2
        counter=$((counter + 1))
        echo "   ⏳ Esperando $service_name... ($counter/$max_attempts)"
    done
    
    echo "❌ $service_name no pudo iniciarse en $max_attempts intentos"
    return 1
}

# Crear directorio de logs si no existe
mkdir -p logs

echo "📋 Verificando estado actual de servicios..."

# Verificar servicios existentes
check_port 9000 "Gateway"
gateway_running=$?

check_port 7011 "Auth"
auth_running=$?

check_port 7012 "Usuario"
usuario_running=$?

check_port 7013 "Producto"
producto_running=$?

check_port 7014 "MetodoPago"
metodopago_running=$?

check_port 7015 "Compra"
compra_running=$?

# Iniciar servicios que no están corriendo
echo ""
echo "🚀 Iniciando servicios faltantes..."

if [ $gateway_running -ne 0 ]; then
    start_service "businessdomain/appgw" 9000 "Gateway"
fi

if [ $auth_running -ne 0 ]; then
    start_service "businessdomain/auth" 7011 "Auth"
fi

if [ $usuario_running -ne 0 ]; then
    start_service "businessdomain/usuario" 7012 "Usuario"
fi

if [ $producto_running -ne 0 ]; then
    start_service "businessdomain/producto" 7013 "Producto"
fi

if [ $metodopago_running -ne 0 ]; then
    start_service "businessdomain/metodopago" 7014 "MetodoPago"
fi

if [ $compra_running -ne 0 ]; then
    start_service "businessdomain/compra" 7015 "Compra"
fi

echo ""
echo "🔍 Verificación final de todos los servicios:"
echo "=============================================="

services=(
    "Gateway:9000"
    "Auth:7011"
    "Usuario:7012"
    "Producto:7013"
    "MetodoPago:7014"
    "Compra:7015"
)

all_running=true

for service_info in "${services[@]}"; do
    IFS=':' read -r service_name port <<< "$service_info"
    
    if curl -f http://localhost:$port/actuator/health > /dev/null 2>&1; then
        echo "✅ $service_name (puerto $port) - FUNCIONANDO"
    else
        echo "❌ $service_name (puerto $port) - NO RESPONDE"
        all_running=false
    fi
done

echo "=============================================="
if [ "$all_running" = true ]; then
    echo "🎉 ¡TODOS LOS SERVICIOS ESTÁN FUNCIONANDO!"
    echo ""
    echo "📱 URL del Gateway: http://localhost:9000"
    echo "🔐 Credenciales de prueba:"
    echo "   Usuario: test1 / Contraseña: test1"
    echo "   Admin: admin / Contraseña: admin123"
    echo ""
    echo "🛑 Para detener todos los servicios, ejecuta: ./stop-all-services.sh"
else
    echo "⚠️  Algunos servicios no están funcionando correctamente"
    echo "📋 Revisa los logs en el directorio 'logs/' para más detalles"
fi