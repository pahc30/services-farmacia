#!/bin/zsh
# Script para exportar los reportes de cobertura de los 5 servicios

set -e

# Carpeta destino para los reportes
DEST="reportes-cobertura"
mkdir -p "$DEST"

# Servicios
SERVICIOS=("auth" "compra" "metodopago" "producto" "usuario")

for SERVICIO in $SERVICIOS; do
    SRC="businessdomain/$SERVICIO/target/site/jacoco"
    if [ -d "$SRC" ]; then
        cp -R "$SRC" "$DEST/jacoco-$SERVICIO"
        echo "Reporte de $SERVICIO exportado."
    else
        echo "No se encontró reporte para $SERVICIO. Ejecuta los tests si es necesario."
    fi
done

echo "Reportes exportados en la carpeta $DEST"