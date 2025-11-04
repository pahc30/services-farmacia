#!/bin/bash

echo "🔍 Probando el endpoint de imágenes del servicio producto..."
echo ""

# Probar el health endpoint primero
echo "1. Verificando que el servicio está corriendo:"
curl -s http://localhost:7013/producto/actuator/health | head -1

echo ""
echo "2. Verificando headers de una imagen específica:"
curl -s -I http://localhost:7013/producto/c69f3481-eb12-49b3-90cb-5503d221eea0paracetamol.jpeg

echo ""
echo "3. Listando archivos en el directorio uploads:"
ls -la /Users/pablohuerta/Documents/UTP/Ciclo_09/Integrador\ II/services-farmacia/farmaciadeyparent/businessdomain/producto/uploads/

echo ""
echo "✅ Pruebas completadas"