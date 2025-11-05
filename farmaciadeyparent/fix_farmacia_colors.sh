#!/bin/bash

# Script para agregar colores faltantes específicos de farmacia
ANDROID_PROJECT_PATH="/Users/pablohuerta/Documents/UTP/Ciclo_09/Integrador II/farmacia-android"
COLORS_PATH="$ANDROID_PROJECT_PATH/app/src/main/res/values/colors.xml"

echo "🎨 Agregando colores faltantes específicos de farmacia..."

# Verificar si el archivo existe
if [ ! -f "$COLORS_PATH" ]; then
    echo "❌ colors.xml no encontrado en: $COLORS_PATH"
    exit 1
fi

# Crear una copia de respaldo
cp "$COLORS_PATH" "$COLORS_PATH.backup"

# Crear archivo temporal con todos los colores necesarios
cat > "$COLORS_PATH" << 'EOF'
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!-- Colores principales -->
    <color name="primary">#2196F3</color>
    <color name="primary_dark">#1976D2</color>
    <color name="accent">#FF4081</color>
    
    <!-- Colores específicos de Farmacia -->
    <color name="farmacia_primary">#2196F3</color>
    <color name="farmacia_primary_dark">#1976D2</color>
    <color name="farmacia_accent">#FF4081</color>
    <color name="farmacia_background">#FAFAFA</color>
    
    <!-- Colores de estado -->
    <color name="success_green">#4CAF50</color>
    <color name="error_red">#F44336</color>
    <color name="warning_orange">#FF9800</color>
    
    <!-- Colores de texto -->
    <color name="text_primary">#212121</color>
    <color name="text_secondary">#757575</color>
    <color name="text_hint">#BDBDBD</color>
    
    <!-- Colores de fondo -->
    <color name="background_light">#FAFAFA</color>
    <color name="background_white">#FFFFFF</color>
    <color name="divider">#E0E0E0</color>
    
    <!-- Colores Material Design adicionales -->
    <color name="teal_200">#FF03DAC5</color>
    <color name="teal_700">#FF018786</color>
    <color name="purple_200">#FFBB86FC</color>
    <color name="purple_500">#FF6200EE</color>
    <color name="purple_700">#FF3700B3</color>
    
    <!-- Colores básicos -->
    <color name="white">#FFFFFF</color>
    <color name="black">#000000</color>
    <color name="transparent">#00000000</color>
    
    <!-- Colores para botones y componentes -->
    <color name="button_primary">#2196F3</color>
    <color name="button_secondary">#757575</color>
    <color name="ripple_color">#40000000</color>
    
    <!-- Colores para estados -->
    <color name="selected">#E3F2FD</color>
    <color name="pressed">#BBDEFB</color>
    <color name="disabled">#BDBDBD</color>
</resources>
EOF

echo "✅ Colores actualizados en: $COLORS_PATH"
echo "💾 Copia de respaldo creada: $COLORS_PATH.backup"
echo ""
echo "📋 Colores agregados específicos de farmacia:"
echo "   - farmacia_primary: #2196F3"
echo "   - farmacia_primary_dark: #1976D2"
echo "   - farmacia_accent: #FF4081"
echo "   - farmacia_background: #FAFAFA"
echo "   - teal_700: #FF018786"
echo "   - Otros colores Material Design"
echo ""
echo "🔧 Ahora ejecuta: ./gradlew clean assembleDebug"