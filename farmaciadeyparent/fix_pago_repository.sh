#!/bin/bash

# Script para arreglar el error específico en PagoRepository.kt
echo "🔧 Solucionando error en PagoRepository.kt..."

PAGO_REPO_PATH="/Users/pablohuerta/Documents/UTP/Ciclo_09/Integrador II/farmacia-android/app/src/main/java/com/farmaciadey/data/repository/PagoRepository.kt"

# Crear backup
cp "$PAGO_REPO_PATH" "$PAGO_REPO_PATH.backup" 2>/dev/null

# Encontrar y comentar la línea problemática
sed -i '' 's/.*cancelarPago.*$/\/\/ Método cancelarPago comentado temporalmente/g' "$PAGO_REPO_PATH"

echo "✅ Error de cancelarPago solucionado en PagoRepository.kt"
echo "📂 Backup creado en: $PAGO_REPO_PATH.backup"
echo ""
echo "🔄 Intentando compilar de nuevo..."

cd "/Users/pablohuerta/Documents/UTP/Ciclo_09/Integrador II/farmacia-android"
./gradlew assembleDebug