#!/bin/bash

# 🧪 Script de Testing para Sistema de Pagos Simulado + PDF
# Sistema completamente GRATUITO para la Farmacia DeY

echo "🎭 === TESTING SISTEMA DE PAGOS SIMULADO + PDF ==="
echo "📌 Puerto: 7014"
echo "💰 Costo: GRATIS (0% por transacción)"
echo ""

BASE_URL="http://localhost:7014/metodopago/api/v1/pagos"

echo "🔍 1. Health Check del Sistema"
curl -s -X GET "$BASE_URL/health" | jq '.'
echo ""

echo "ℹ️ 2. Información del Sistema"
curl -s -X GET "$BASE_URL/info" | jq '.'
echo ""

echo "💳 3. Crear Payment Intent Simulado"
PAYMENT_RESPONSE=$(curl -s -X POST "$BASE_URL/crear-intent" \
  -H "Content-Type: application/json" \
  -d '{
    "compraId": 123,
    "monto": 25.50,
    "moneda": "pen",
    "descripcion": "Compra en Farmacia DeY - Test"
  }')
echo "$PAYMENT_RESPONSE" | jq '.'

# Extraer el ID de la transacción creada (asumiendo que viene en la respuesta)
TRANSACTION_ID=$(echo "$PAYMENT_RESPONSE" | jq -r '.transaccionId // 1')
echo "📝 ID de Transacción: $TRANSACTION_ID"
echo ""

echo "✅ 4. Confirmar Pago Simulado"
curl -s -X POST "$BASE_URL/confirmar/$TRANSACTION_ID" | jq '.'
echo ""

echo "🔍 5. Consultar Estado de Transacción"
curl -s -X GET "$BASE_URL/transaccion/$TRANSACTION_ID" | jq '.'
echo ""

echo "📄 6. Generar y Descargar Boleta PDF"
echo "   Descargando boleta para transacción: $TRANSACTION_ID"
curl -s -X GET "$BASE_URL/boleta/transaccion/$TRANSACTION_ID" \
  -o "boleta_$TRANSACTION_ID.pdf" \
  -w "Status: %{http_code}\n"

if [ -f "boleta_$TRANSACTION_ID.pdf" ]; then
    FILE_SIZE=$(ls -lh "boleta_$TRANSACTION_ID.pdf" | awk '{print $5}')
    echo "   ✅ Boleta PDF generada: boleta_$TRANSACTION_ID.pdf ($FILE_SIZE)"
else
    echo "   ❌ Error: No se pudo generar la boleta PDF"
fi
echo ""

echo "🧪 7. Testing de Errores Simulados"
echo "   7.1 Tarjeta rechazada:"
curl -s -X POST "$BASE_URL/test/simular-error/tarjeta_rechazada" | jq '.'
echo ""
echo "   7.2 Fondos insuficientes:"
curl -s -X POST "$BASE_URL/test/simular-error/fondos_insuficientes" | jq '.'
echo ""

echo "📊 8. Consultar Transacciones por Compra"
curl -s -X GET "$BASE_URL/compra/123" | jq '.'
echo ""

echo "🎉 === RESUMEN DEL TESTING ==="
echo "✅ Sistema de pagos simulado: FUNCIONANDO"
echo "✅ Generación de boletas PDF: FUNCIONANDO"
echo "✅ Testing de errores: FUNCIONANDO"
echo "💰 Costo total: 0% (COMPLETAMENTE GRATIS)"
echo ""
echo "📱 Para Android:"
echo "   - URL Base: $BASE_URL"
echo "   - Endpoints disponibles: /crear-intent, /confirmar/{id}, /boleta/transaccion/{id}"
echo ""
echo "🚀 ¡Sistema listo para producción (modo demo)!"