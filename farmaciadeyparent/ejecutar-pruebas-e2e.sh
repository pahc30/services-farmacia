#!/bin/bash
# Script de pruebas E2E completas - Farmacia Dey
# Fecha: $(date +"%Y-%m-%d %H:%M:%S")

set -e

echo "=================================================="
echo "  PRUEBAS END-TO-END - FARMACIA DEY"
echo "=================================================="
echo ""

# Colores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

EVIDENCIA_DIR="evidencias-e2e-$(date +%Y%m%d-%H%M%S)"
mkdir -p "$EVIDENCIA_DIR"

echo "📁 Directorio de evidencias: $EVIDENCIA_DIR"
echo ""

# 1. Verificar servicios backend
echo "🔍 1. VERIFICANDO SERVICIOS BACKEND..."
echo "========================================"

SERVICES=("auth:7011" "usuario:7012" "producto:7013" "metodopago:7014" "compra:7015")
ALL_OK=true

for svc in "${SERVICES[@]}"; do
  name="${svc%%:*}"
  port="${svc##*:}"
  code=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:$port/$name/actuator/health" 2>/dev/null || echo "000")
  
  if [ "$code" = "200" ] || [ "$code" = "403" ]; then
    echo -e "${GREEN}✅${NC} $name (puerto $port): ACTIVO"
    echo "$name:$port:OK:$code" >> "$EVIDENCIA_DIR/servicios-status.txt"
  else
    echo -e "${RED}❌${NC} $name (puerto $port): NO RESPONDE"
    echo "$name:$port:FAIL:$code" >> "$EVIDENCIA_DIR/servicios-status.txt"
    ALL_OK=false
  fi
done
echo ""

if [ "$ALL_OK" = false ]; then
  echo -e "${RED}ERROR: Algunos servicios no están activos${NC}"
  exit 1
fi

# 2. Verificar frontend
echo "🔍 2. VERIFICANDO FRONTEND ANGULAR..."
echo "====================================="

FRONTEND_CODE=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:4200" 2>/dev/null || echo "000")
if [ "$FRONTEND_CODE" = "200" ]; then
  echo -e "${GREEN}✅${NC} Frontend Angular (puerto 4200): ACTIVO"
  echo "frontend:4200:OK:$FRONTEND_CODE" >> "$EVIDENCIA_DIR/servicios-status.txt"
else
  echo -e "${RED}❌${NC} Frontend no responde"
  echo "frontend:4200:FAIL:$FRONTEND_CODE" >> "$EVIDENCIA_DIR/servicios-status.txt"
  exit 1
fi
echo ""

# 3. Probar Login API
echo "🧪 3. PROBANDO LOGIN API..."
echo "==========================="

LOGIN_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "http://localhost:9000/auth/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"test1","password":"test1"}')

HTTP_CODE=$(echo "$LOGIN_RESPONSE" | tail -n1)
BODY=$(echo "$LOGIN_RESPONSE" | sed '$d')

if [ "$HTTP_CODE" = "200" ]; then
  echo -e "${GREEN}✅${NC} Login exitoso - Credenciales: test1/test1"
  echo "$BODY" | python3 -m json.tool > "$EVIDENCIA_DIR/login-response.json" 2>/dev/null || echo "$BODY" > "$EVIDENCIA_DIR/login-response.json"
  TOKEN=$(echo "$BODY" | grep -o '"accessToken":"[^"]*"' | cut -d'"' -f4)
  echo "Token JWT generado: ${TOKEN:0:50}..." 
else
  echo -e "${RED}❌${NC} Login falló - HTTP $HTTP_CODE"
  echo "$BODY" > "$EVIDENCIA_DIR/login-error.txt"
  exit 1
fi
echo ""

# 4. Ejecutar pruebas Selenium
echo "🎭 4. EJECUTANDO PRUEBAS SELENIUM..."
echo "===================================="

cd "$(dirname "$0")/selenium-tests"
../mvnw -q test -Dtest=pe.com.farmaciadey.selenium.FarmaciaDeYTests 2>&1 | tee "../$EVIDENCIA_DIR/selenium-output.txt"

SELENIUM_EXIT=$?

if [ $SELENIUM_EXIT -eq 0 ]; then
  echo -e "${GREEN}✅${NC} Pruebas Selenium: EXITOSAS"
else
  echo -e "${YELLOW}⚠️${NC} Pruebas Selenium: Algunas fallaron (revisar evidencias)"
fi

# Copiar evidencias de Selenium
if [ -d "target/selenium-screenshots" ]; then
  cp -r target/selenium-screenshots "../$EVIDENCIA_DIR/"
  echo "📸 Screenshots copiados a: $EVIDENCIA_DIR/selenium-screenshots/"
fi

if [ -d "target/surefire-reports" ]; then
  cp -r target/surefire-reports "../$EVIDENCIA_DIR/"
  echo "📊 Reportes copiados a: $EVIDENCIA_DIR/surefire-reports/"
fi

cd ..
echo ""

# 5. Generar resumen
echo "📋 5. GENERANDO RESUMEN..."
echo "=========================="

cat > "$EVIDENCIA_DIR/RESUMEN.md" << EOF
# Reporte de Pruebas E2E - Farmacia Dey

**Fecha:** $(date +"%Y-%m-%d %H:%M:%S")
**Branch:** $(git rev-parse --abbrev-ref HEAD 2>/dev/null || echo "N/A")
**Commit:** $(git rev-parse --short HEAD 2>/dev/null || echo "N/A")

## Servicios Backend

$(cat "$EVIDENCIA_DIR/servicios-status.txt" | while IFS=: read -r name port status code; do
  if [ "$status" = "OK" ]; then
    echo "- ✅ **$name** (puerto $port): HTTP $code"
  else
    echo "- ❌ **$name** (puerto $port): HTTP $code"
  fi
done)

## Frontend

- ✅ **Angular App** (puerto 4200): Activo

## Pruebas de Autenticación

- ✅ Login API: Exitoso con credenciales test1/test1
- Token JWT generado correctamente

## Pruebas Selenium (E2E)

$(if [ -f "$EVIDENCIA_DIR/surefire-reports/pe.com.farmaciadey.selenium.FarmaciaDeYTests.txt" ]; then
  grep "Tests run:" "$EVIDENCIA_DIR/surefire-reports/pe.com.farmaciadey.selenium.FarmaciaDeYTests.txt"
else
  echo "Reportes no disponibles"
fi)

## Evidencias Generadas

- \`servicios-status.txt\` - Estado de servicios
- \`login-response.json\` - Respuesta de login API
- \`selenium-output.txt\` - Log completo de Selenium
- \`selenium-screenshots/\` - Capturas de pantalla
- \`surefire-reports/\` - Reportes XML/TXT de JUnit

## Conclusión

$(if [ $SELENIUM_EXIT -eq 0 ]; then
  echo "✅ **TODAS LAS PRUEBAS PASARON EXITOSAMENTE**"
else
  echo "⚠️ **ALGUNAS PRUEBAS REQUIEREN ATENCIÓN** (ver detalles en selenium-output.txt)"
fi)

EOF

echo -e "${GREEN}✅${NC} Resumen generado en: $EVIDENCIA_DIR/RESUMEN.md"
echo ""

# 6. Resumen final
echo "=================================================="
echo "  PRUEBAS COMPLETADAS"
echo "=================================================="
echo ""
echo "📦 Todas las evidencias están en: $EVIDENCIA_DIR/"
echo ""
echo "Para ver el resumen completo:"
echo "  cat $EVIDENCIA_DIR/RESUMEN.md"
echo ""
echo "Para abrir las capturas de pantalla:"
echo "  open $EVIDENCIA_DIR/selenium-screenshots/"
echo ""

exit $SELENIUM_EXIT
