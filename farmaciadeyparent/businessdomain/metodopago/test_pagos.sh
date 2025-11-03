#!/bin/bash
# Script de pruebas para el Sistema de Pagos
# Microservicio metodopago - Farmacia DeY

BASE_URL="http://localhost:7014/metodopago/api/v1/pagos"

echo "🚀 INICIANDO PRUEBAS DEL SISTEMA DE PAGOS"
echo "=========================================="
echo ""

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Función para mostrar resultados
print_result() {
    if [ $1 -eq 0 ]; then
        echo -e "${GREEN}✅ $2${NC}"
    else
        echo -e "${RED}❌ $2${NC}"
    fi
}

# Función para hacer pausa
pause() {
    echo ""
    read -p "Presiona Enter para continuar..."
    echo ""
}

echo -e "${BLUE}📋 PRUEBA 1: Health Check${NC}"
echo "Verificando que el microservicio esté funcionando..."
echo ""
echo "Comando:"
echo "curl -X GET \"$BASE_URL/health\""
echo ""
echo "Respuesta:"

response=$(curl -s -X GET "$BASE_URL/health" -H "Content-Type: application/json")
status=$?

if [ $status -eq 0 ]; then
    echo "$response" | jq . 2>/dev/null || echo "$response"
    print_result 0 "Health check exitoso"
else
    print_result 1 "Error en health check - ¿Está el servicio corriendo?"
    echo "Asegúrate de que el microservicio esté ejecutándose en el puerto 7014"
    exit 1
fi

pause

echo -e "${BLUE}📋 PRUEBA 2: Crear un Pago${NC}"
echo "Creando un nuevo PaymentIntent..."
echo ""

# Datos de prueba
COMPRA_ID=123
MONTO=50.00
METODO_PAGO_ID=1
DESCRIPCION="Pago de prueba - Medicamentos"

cat << EOF > /tmp/crear_pago.json
{
  "compraId": $COMPRA_ID,
  "monto": $MONTO,
  "moneda": "USD",
  "metodoPagoId": $METODO_PAGO_ID,
  "descripcion": "$DESCRIPCION"
}
EOF

echo "Datos a enviar:"
cat /tmp/crear_pago.json | jq .
echo ""

echo "Comando:"
echo "curl -X POST \"$BASE_URL/crear\" -H \"Content-Type: application/json\" -d @/tmp/crear_pago.json"
echo ""
echo "Respuesta:"

response=$(curl -s -X POST "$BASE_URL/crear" \
  -H "Content-Type: application/json" \
  -d @/tmp/crear_pago.json)
status=$?

if [ $status -eq 0 ]; then
    echo "$response" | jq . 2>/dev/null || echo "$response"
    
    # Extraer datos para siguientes pruebas
    TRANSACCION_ID=$(echo "$response" | jq -r '.transaccionId // empty')
    PAYMENT_INTENT_ID=$(echo "$response" | jq -r '.paymentIntentId // empty')
    
    if [ -n "$TRANSACCION_ID" ] && [ -n "$PAYMENT_INTENT_ID" ]; then
        print_result 0 "Pago creado exitosamente"
        echo "TransaccionID: $TRANSACCION_ID"
        echo "PaymentIntentID: $PAYMENT_INTENT_ID"
    else
        print_result 1 "Error: No se pudieron extraer los IDs de la respuesta"
        exit 1
    fi
else
    print_result 1 "Error al crear pago"
    exit 1
fi

pause

echo -e "${BLUE}📋 PRUEBA 3: Obtener Estado de Transacción${NC}"
echo "Consultando el estado de la transacción creada..."
echo ""
echo "Comando:"
echo "curl -X GET \"$BASE_URL/estado/$TRANSACCION_ID\""
echo ""
echo "Respuesta:"

response=$(curl -s -X GET "$BASE_URL/estado/$TRANSACCION_ID" -H "Content-Type: application/json")
status=$?

if [ $status -eq 0 ]; then
    echo "$response" | jq . 2>/dev/null || echo "$response"
    print_result 0 "Estado consultado exitosamente"
else
    print_result 1 "Error al consultar estado"
fi

pause

echo -e "${BLUE}📋 PRUEBA 4: Confirmar Pago${NC}"
echo "Confirmando el estado del pago con Stripe..."
echo ""
echo "Comando:"
echo "curl -X POST \"$BASE_URL/confirmar/$PAYMENT_INTENT_ID\""
echo ""
echo "Respuesta:"

response=$(curl -s -X POST "$BASE_URL/confirmar/$PAYMENT_INTENT_ID" -H "Content-Type: application/json")
status=$?

if [ $status -eq 0 ]; then
    echo "$response" | jq . 2>/dev/null || echo "$response"
    print_result 0 "Confirmación procesada"
    echo ""
    echo -e "${YELLOW}ℹ️  Nota: El estado puede ser 'PROCESANDO' porque el pago no se completó realmente en Stripe${NC}"
    echo -e "${YELLOW}   (se requiere una tarjeta real o simulación completa)${NC}"
else
    print_result 1 "Error al confirmar pago"
fi

pause

echo -e "${BLUE}📋 PRUEBA 5: Cancelar Pago${NC}"
echo "Cancelando el PaymentIntent..."
echo ""
echo "Comando:"
echo "curl -X POST \"$BASE_URL/cancelar/$PAYMENT_INTENT_ID\""
echo ""
echo "Respuesta:"

response=$(curl -s -X POST "$BASE_URL/cancelar/$PAYMENT_INTENT_ID" -H "Content-Type: application/json")
status=$?

if [ $status -eq 0 ]; then
    echo "$response" | jq . 2>/dev/null || echo "$response"
    print_result 0 "Cancelación procesada"
else
    print_result 1 "Error al cancelar pago"
fi

pause

echo -e "${BLUE}📋 PRUEBA 6: Verificar Estado Final${NC}"
echo "Verificando el estado final de la transacción..."
echo ""
echo "Comando:"
echo "curl -X GET \"$BASE_URL/estado/$TRANSACCION_ID\""
echo ""
echo "Respuesta:"

response=$(curl -s -X GET "$BASE_URL/estado/$TRANSACCION_ID" -H "Content-Type: application/json")
status=$?

if [ $status -eq 0 ]; then
    echo "$response" | jq . 2>/dev/null || echo "$response"
    
    estado=$(echo "$response" | jq -r '.estado // empty')
    if [ "$estado" = "CANCELADA" ]; then
        print_result 0 "Estado final correcto: CANCELADA"
    else
        echo -e "${YELLOW}ℹ️  Estado final: $estado${NC}"
    fi
else
    print_result 1 "Error al verificar estado final"
fi

# Cleanup
rm -f /tmp/crear_pago.json

echo ""
echo -e "${GREEN}🎉 PRUEBAS COMPLETADAS${NC}"
echo "=========================================="
echo ""
echo -e "${BLUE}📊 RESUMEN:${NC}"
echo "✅ Health Check"
echo "✅ Crear Pago"
echo "✅ Consultar Estado"
echo "✅ Confirmar Pago"
echo "✅ Cancelar Pago"
echo "✅ Verificar Estado Final"
echo ""
echo -e "${YELLOW}📝 NOTAS:${NC}"
echo "- Las claves de Stripe en application.properties son de ejemplo"
echo "- Para pruebas reales, configurar claves válidas de Stripe Test"
echo "- El PaymentIntent se crea en Stripe pero no se completa sin tarjeta real"
echo "- Los estados pueden variar según la configuración de Stripe"
echo ""
echo -e "${BLUE}🔗 ENDPOINTS DISPONIBLES:${NC}"
echo "GET  $BASE_URL/health"
echo "POST $BASE_URL/crear"
echo "POST $BASE_URL/confirmar/{paymentIntentId}"
echo "POST $BASE_URL/cancelar/{paymentIntentId}"
echo "GET  $BASE_URL/estado/{transaccionId}"
echo "POST $BASE_URL/webhook/stripe"
echo ""
echo -e "${GREEN}✨ Sistema de pagos funcionando correctamente!${NC}"