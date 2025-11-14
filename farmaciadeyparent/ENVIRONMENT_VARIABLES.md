# 🔐 Variables de Entorno para Render

## ⚠️ IMPORTANTE: 
- Reemplaza `TU_SECRETO_AQUI` con un secreto real de 64 caracteres
- Actualiza las claves de Stripe y PayPal con tus credenciales reales
- Cambia la URL del frontend por la tuya

## 🔑 JWT Secret Generator
Ejecuta este comando para generar un secreto seguro:
```bash
openssl rand -base64 64
```

## 📝 Variables por Servicio

### 1. farmacia-auth
```env
PORT=7011
SPRING_PROFILES_ACTIVE=render
DATABASE_URL=[Conectar a farmacia-postgresql]
DB_USERNAME=[Conectar a farmacia-postgresql] 
DB_PASSWORD=[Conectar a farmacia-postgresql]
JWT_SECRET=TU_SECRETO_AQUI_64_CARACTERES
CORS_ORIGINS=https://farmacia-frontend.onrender.com
```

### 2. farmacia-usuario
```env
PORT=7012
SPRING_PROFILES_ACTIVE=render
DATABASE_URL=[Conectar a farmacia-postgresql]
DB_USERNAME=[Conectar a farmacia-postgresql]
DB_PASSWORD=[Conectar a farmacia-postgresql]
JWT_SECRET=[Sincronizar con farmacia-auth]
CORS_ORIGINS=https://farmacia-frontend.onrender.com
```

### 3. farmacia-producto
```env
PORT=7013
SPRING_PROFILES_ACTIVE=render
DATABASE_URL=[Conectar a farmacia-postgresql]
DB_USERNAME=[Conectar a farmacia-postgresql]
DB_PASSWORD=[Conectar a farmacia-postgresql]
JWT_SECRET=[Sincronizar con farmacia-auth]
CORS_ORIGINS=https://farmacia-frontend.onrender.com
UPLOAD_DIR=/app/uploads
```

### 4. farmacia-metodopago
```env
PORT=7014
SPRING_PROFILES_ACTIVE=render
DATABASE_URL=[Conectar a farmacia-postgresql]
DB_USERNAME=[Conectar a farmacia-postgresql]
DB_PASSWORD=[Conectar a farmacia-postgresql]
JWT_SECRET=[Sincronizar con farmacia-auth]
CORS_ORIGINS=https://farmacia-frontend.onrender.com
STRIPE_API_KEY=sk_test_TU_CLAVE_STRIPE_REAL
TZ=America/Lima
PAYPAL_CLIENT_ID=TU_PAYPAL_CLIENT_ID
PAYPAL_CLIENT_SECRET=TU_PAYPAL_CLIENT_SECRET
```

### 5. farmacia-compra
```env
PORT=7015
SPRING_PROFILES_ACTIVE=render
DATABASE_URL=[Conectar a farmacia-postgresql]
DB_USERNAME=[Conectar a farmacia-postgresql]
DB_PASSWORD=[Conectar a farmacia-postgresql]
JWT_SECRET=[Sincronizar con farmacia-auth]
CORS_ORIGINS=https://farmacia-frontend.onrender.com
```

### 6. farmacia-gateway
```env
PORT=9000
SPRING_PROFILES_ACTIVE=render
CORS_ORIGINS=https://farmacia-frontend.onrender.com
AUTH_SERVICE_URL=https://farmacia-auth.onrender.com
USUARIO_SERVICE_URL=https://farmacia-usuario.onrender.com
PRODUCTO_SERVICE_URL=https://farmacia-producto.onrender.com
METODOPAGO_SERVICE_URL=https://farmacia-metodopago.onrender.com
COMPRA_SERVICE_URL=https://farmacia-compra.onrender.com
```

## 📋 Lista de Verificación

- [ ] Base de datos PostgreSQL creada: `farmacia-postgresql`
- [ ] Esquemas inicializados con `init-render-db.sh`
- [ ] JWT_SECRET generado y compartido entre servicios
- [ ] Claves de Stripe/PayPal configuradas (si usas pagos reales)
- [ ] URLs de frontend actualizadas en CORS_ORIGINS
- [ ] Todos los servicios usando perfil `render`
- [ ] Gateway configurado con URLs correctas de servicios

## 🔄 Sincronización de JWT_SECRET

**MUY IMPORTANTE:** El mismo `JWT_SECRET` debe usarse en todos los servicios que manejan autenticación (auth, usuario, producto, metodopago, compra).

### Forma fácil en Render:
1. Genera el secreto una vez
2. En el servicio `farmacia-auth`, agrégalo como variable de entorno
3. En los otros servicios, usa "Sync from another service" y selecciona `farmacia-auth`

## 🌐 URLs de Servicios Finales

Una vez desplegados, estos serán los endpoints:

```
Auth:       https://farmacia-auth.onrender.com
Usuario:    https://farmacia-usuario.onrender.com  
Producto:   https://farmacia-producto.onrender.com
MetodoPago: https://farmacia-metodopago.onrender.com
Compra:     https://farmacia-compra.onrender.com
Gateway:    https://farmacia-gateway.onrender.com (PRINCIPAL)
```

## 🧪 Testing Rápido

Después del despliegue, prueba:

```bash
# Verificar que todos los servicios están arriba
curl https://farmacia-gateway.onrender.com/actuator/health

# Probar ruta de autenticación
curl https://farmacia-gateway.onrender.com/auth/actuator/health

# Probar ruta de productos  
curl https://farmacia-gateway.onrender.com/producto/actuator/health
```