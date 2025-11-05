#!/bin/bash

# 🚀 Script de Configuración para Producción - Farmacia DeY
# Este script ayuda a configurar las variables de entorno para producción

echo "🔧 Configurando variables de entorno para PRODUCCIÓN..."
echo ""

# Crear archivo .env si no existe
if [ ! -f .env ]; then
    echo "📁 Creando archivo .env..."
    touch .env
fi

echo "⚠️  IMPORTANTE: Necesitas obtener las claves REALES de Stripe para producción"
echo ""
echo "1. Ve a https://dashboard.stripe.com/"
echo "2. Asegúrate de estar en modo 'Live' (no Test)"
echo "3. Ve a Developers > API keys"
echo "4. Copia las claves de producción"
echo ""

# Pedir las claves de Stripe
read -p "🔑 Ingresa tu Stripe Secret Key (sk_live_...): " STRIPE_SECRET_KEY
read -p "🔑 Ingresa tu Stripe Publishable Key (pk_live_...): " STRIPE_PUBLISHABLE_KEY

# Validar que las claves sean de producción
if [[ $STRIPE_SECRET_KEY != sk_live_* ]]; then
    echo "⚠️  ADVERTENCIA: La Secret Key no parece ser de producción (debe empezar con sk_live_)"
    read -p "¿Continuar de todos modos? (y/N): " confirm
    if [[ $confirm != [yY] ]]; then
        echo "❌ Cancelado"
        exit 1
    fi
fi

if [[ $STRIPE_PUBLISHABLE_KEY != pk_live_* ]]; then
    echo "⚠️  ADVERTENCIA: La Publishable Key no parece ser de producción (debe empezar con pk_live_)"
    read -p "¿Continuar de todos modos? (y/N): " confirm
    if [[ $confirm != [yY] ]]; then
        echo "❌ Cancelado"
        exit 1
    fi
fi

# Configuración de base de datos
echo ""
echo "🗄️  Configuración de Base de Datos de Producción"
read -p "📍 URL de la base de datos (ej: jdbc:postgresql://servidor:5432/farmacia_prod): " DB_URL
read -p "👤 Usuario de la base de datos: " DB_USERNAME
read -s -p "🔐 Contraseña de la base de datos: " DB_PASSWORD
echo ""

# Configuración del servidor
echo ""
echo "🌐 Configuración del Servidor"
read -p "🏠 Dominio de producción (ej: api.farmaciadey.com): " DOMAIN
read -p "🔒 ¿Usar HTTPS? (y/N): " USE_HTTPS

# Generar archivo .env
echo ""
echo "📝 Generando archivo .env..."

cat > .env << EOF
# 🚀 Configuración de Producción - Farmacia DeY
# Generado el $(date)

# Stripe Configuration (PRODUCCIÓN)
STRIPE_SECRET_KEY=$STRIPE_SECRET_KEY
STRIPE_PUBLISHABLE_KEY=$STRIPE_PUBLISHABLE_KEY
STRIPE_WEBHOOK_SECRET=whsec_TU_WEBHOOK_SECRET_AQUI

# Database Configuration
DB_URL=$DB_URL
DB_USERNAME=$DB_USERNAME
DB_PASSWORD=$DB_PASSWORD

# Server Configuration
DOMAIN=$DOMAIN
USE_HTTPS=$USE_HTTPS

# Security
JWT_SECRET=\$(openssl rand -base64 32)
ENCRYPTION_KEY=\$(openssl rand -base64 32)

# Logging
LOG_LEVEL=INFO
LOG_FILE=/var/log/farmacia/metodopago.log

# Rate Limiting
RATE_LIMIT_REQUESTS_PER_MINUTE=100
RATE_LIMIT_REQUESTS_PER_HOUR=1000
EOF

# Crear application-prod.properties
echo ""
echo "📝 Generando application-prod.properties..."

cat > src/main/resources/application-prod.properties << EOF
# 🚀 Configuración de Producción - Metodopago
# Generado el $(date)

# Server Configuration
server.port=8443
EOF

if [[ $USE_HTTPS == [yY] ]]; then
cat >> src/main/resources/application-prod.properties << EOF
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=\${SSL_KEYSTORE_PASSWORD}
server.ssl.key-store-type=PKCS12
EOF
fi

cat >> src/main/resources/application-prod.properties << EOF

# Database Configuration
spring.datasource.url=\${DB_URL}
spring.datasource.username=\${DB_USERNAME}
spring.datasource.password=\${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.format_sql=false

# Stripe Configuration
stripe.secret.key=\${STRIPE_SECRET_KEY}
stripe.publishable.key=\${STRIPE_PUBLISHABLE_KEY}
stripe.webhook.secret=\${STRIPE_WEBHOOK_SECRET}

# Logging Configuration
logging.level.root=WARN
logging.level.pe.com.farmaciadey=INFO
logging.level.com.stripe=ERROR
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
logging.file.name=\${LOG_FILE:/var/log/farmacia/metodopago.log}

# Actuator
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=when_authorized

# CORS (restrictivo para producción)
cors.allowed.origins=https://$DOMAIN,https://www.$DOMAIN
cors.allowed.methods=GET,POST,PUT,DELETE
cors.allowed.headers=Authorization,Content-Type

# Rate Limiting
rate.limit.requests.per.minute=\${RATE_LIMIT_REQUESTS_PER_MINUTE:100}
rate.limit.requests.per.hour=\${RATE_LIMIT_REQUESTS_PER_HOUR:1000}
EOF

# Crear script de deployment
echo ""
echo "📝 Generando script de deployment..."

cat > deploy-prod.sh << 'EOF'
#!/bin/bash

# 🚀 Script de Deployment para Producción

echo "🚀 Iniciando deployment de producción..."

# Cargar variables de entorno
if [ -f .env ]; then
    export $(cat .env | grep -v '^#' | xargs)
else
    echo "❌ Archivo .env no encontrado"
    exit 1
fi

# Validar variables críticas
if [ -z "$STRIPE_SECRET_KEY" ]; then
    echo "❌ STRIPE_SECRET_KEY no configurada"
    exit 1
fi

if [ -z "$DB_URL" ]; then
    echo "❌ DB_URL no configurada"
    exit 1
fi

# Backup de la base de datos
echo "💾 Creando backup de la base de datos..."
pg_dump $DB_URL > backup_$(date +%Y%m%d_%H%M%S).sql

# Construir aplicación
echo "🔨 Construyendo aplicación..."
mvn clean package -Pprod -DskipTests

# Verificar que el JAR se creó
if [ ! -f target/metodopago-0.0.1.jar ]; then
    echo "❌ Error: JAR no encontrado"
    exit 1
fi

# Detener servicio actual (si existe)
echo "⏹️  Deteniendo servicio actual..."
sudo systemctl stop metodopago || true

# Copiar nuevo JAR
echo "📦 Desplegando nueva versión..."
sudo cp target/metodopago-0.0.1.jar /opt/farmacia/metodopago.jar

# Iniciar servicio
echo "▶️  Iniciando servicio..."
sudo systemctl start metodopago

# Verificar que esté funcionando
sleep 10
if curl -f http://localhost:8443/metodopago/health > /dev/null 2>&1; then
    echo "✅ Deployment exitoso!"
    echo "🌐 Servicio disponible en: https://$DOMAIN/metodopago"
else
    echo "❌ Error: El servicio no responde"
    echo "📋 Revisar logs: sudo journalctl -u metodopago -f"
    exit 1
fi
EOF

chmod +x deploy-prod.sh

# Proteger archivos sensibles
chmod 600 .env
chmod 600 src/main/resources/application-prod.properties

echo ""
echo "✅ Configuración completada!"
echo ""
echo "📋 Archivos generados:"
echo "   📄 .env (variables de entorno)"
echo "   📄 application-prod.properties (configuración Spring)"
echo "   📄 deploy-prod.sh (script de deployment)"
echo ""
echo "🔐 IMPORTANTE: Los archivos .env y application-prod.properties contienen información sensible"
echo "   NO los subas a Git"
echo "   Agrega .env al .gitignore"
echo ""
echo "🚀 Próximos pasos:"
echo "1. Configurar Webhooks en Stripe Dashboard"
echo "2. Configurar SSL/HTTPS"
echo "3. Ejecutar ./deploy-prod.sh para desplegar"
echo ""
echo "⚠️  RECORDATORIO: Configura el Webhook Secret en Stripe Dashboard:"
echo "   URL: https://$DOMAIN/metodopago/stripe/webhook"
echo "