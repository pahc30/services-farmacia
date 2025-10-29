#!/bin/bash

echo "🛑 Deteniendo todos los servicios de la Farmacia..."

# Crear directorio de logs si no existe
mkdir -p logs

# Función para detener servicio por PID
stop_service() {
    local service_name=$1
    local pid_file="logs/${service_name}.pid"
    
    if [ -f "$pid_file" ]; then
        local pid=$(cat "$pid_file")
        if kill -0 "$pid" > /dev/null 2>&1; then
            echo "🔄 Deteniendo $service_name (PID: $pid)..."
            kill "$pid"
            sleep 2
            
            # Verificar si el proceso aún existe
            if kill -0 "$pid" > /dev/null 2>&1; then
                echo "⚠️  Forzando detención de $service_name..."
                kill -9 "$pid"
            fi
            echo "✅ $service_name detenido"
        else
            echo "⚠️  $service_name ya estaba detenido"
        fi
        rm -f "$pid_file"
    else
        echo "⚠️  No se encontró archivo PID para $service_name"
    fi
}

# Detener servicios usando los archivos PID
stop_service "Gateway"
stop_service "Auth"
stop_service "Usuario"
stop_service "Producto"
stop_service "MetodoPago"
stop_service "Compra"

# También detener cualquier proceso Java que pueda estar corriendo en los puertos
echo ""
echo "🔍 Verificando procesos en puertos específicos..."

ports=(9000 7011 7012 7013 7014 7015)

for port in "${ports[@]}"; do
    pid=$(lsof -ti:$port)
    if [ -n "$pid" ]; then
        echo "🔄 Deteniendo proceso en puerto $port (PID: $pid)..."
        kill "$pid" 2>/dev/null || kill -9 "$pid" 2>/dev/null
    fi
done

echo ""
echo "🔍 Verificación final:"
echo "===================="

all_stopped=true

for port in "${ports[@]}"; do
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null ; then
        echo "⚠️  Puerto $port aún ocupado"
        all_stopped=false
    else
        echo "✅ Puerto $port libre"
    fi
done

echo "===================="
if [ "$all_stopped" = true ]; then
    echo "🎉 ¡TODOS LOS SERVICIOS HAN SIDO DETENIDOS!"
else
    echo "⚠️  Algunos puertos aún están ocupados"
    echo "💡 Puedes usar 'lsof -i :PUERTO' para verificar qué proceso está usando un puerto específico"
fi

# Limpiar logs antiguos (opcional)
echo ""
read -p "¿Deseas limpiar los archivos de log? (y/N): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    rm -f logs/*.log
    echo "🧹 Logs limpiados"
fi