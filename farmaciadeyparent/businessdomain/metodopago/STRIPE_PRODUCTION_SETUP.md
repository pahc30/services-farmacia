# 🚀 Configuración de Stripe para Producción

## 1. Cuenta de Stripe

### Crear Cuenta de Stripe
1. Ve a https://stripe.com/
2. Crea una cuenta empresarial
3. Completa la verificación de identidad
4. Configura tu información bancaria

### Obtener Claves de Producción
1. En el Dashboard de Stripe, ve a "Developers" > "API keys"
2. Obtén las claves de **PRODUCCIÓN**:
   - `Publishable key` (pk_live_xxx) - Para el frontend/Android
   - `Secret key` (sk_live_xxx) - Para el backend (NUNCA exponer)

## 2. Configuración del Backend

### Variables de Entorno para Producción
```bash
# Archivo .env o variables del sistema
STRIPE_SECRET_KEY=sk_live_tu_clave_secreta_real
STRIPE_PUBLISHABLE_KEY=pk_live_tu_clave_publica_real
STRIPE_WEBHOOK_SECRET=whsec_tu_webhook_secret
```

### application-prod.properties
```properties
# Configuración de producción
stripe.secret.key=${STRIPE_SECRET_KEY}
stripe.publishable.key=${STRIPE_PUBLISHABLE_KEY}
stripe.webhook.secret=${STRIPE_WEBHOOK_SECRET}

# Base de datos de producción
spring.datasource.url=jdbc:postgresql://tu-servidor:5432/farmacia_prod
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# SSL obligatorio en producción
server.ssl.enabled=true
server.port=8443

# Logging
logging.level.pe.com.farmaciadey=INFO
logging.level.com.stripe=WARN
```

## 3. Webhooks de Stripe

### Configurar Webhooks en Stripe Dashboard
1. Ve a "Developers" > "Webhooks"
2. Crea un endpoint: `https://tudominio.com/metodopago/stripe/webhook`
3. Selecciona eventos:
   - `payment_intent.succeeded`
   - `payment_intent.payment_failed`
   - `charge.dispute.created`

### Implementar Webhook Handler
El webhook ya está implementado en el código, solo necesitas:
- Configurar la URL pública
- Agregar el webhook secret

## 4. Seguridad para Producción

### HTTPS Obligatorio
```properties
# Forzar HTTPS
server.ssl.enabled=true
security.require-ssl=true
```

### Rate Limiting
```java
// Agregar en el controller
@RateLimiter(name = "pagos", fallbackMethod = "fallbackPago")
```

### Validaciones Adicionales
- Validar montos máximos/mínimos
- Implementar 3D Secure para pagos europeos
- Logs de auditoría

## 5. Monitoreo

### Métricas de Stripe
- Dashboard de Stripe para transacciones
- Alertas por correo
- Webhooks para eventos críticos

### Logging
```properties
# Logs estructurados para producción
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n
logging.file.name=logs/metodopago.log
```

## 6. Testing en Producción

### Pagos de Prueba en Vivo
- Usar montos pequeños (ej: $0.50)
- Probar con diferentes tarjetas
- Verificar webhooks funcionan

### Rollback Plan
- Mantener versión anterior lista
- Script de rollback de base de datos
- Monitoreo de errores en tiempo real