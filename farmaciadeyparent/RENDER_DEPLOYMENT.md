# 🚀 Despliegue en Render - Farmacia Microservicios

Este documento te guiará paso a paso para desplegar los 6 microservicios de la farmacia en Render.

## 📋 Prerequisitos

1. Cuenta en [Render](https://render.com) (gratuita)
2. Repositorio en GitHub con el código
3. Git configurado localmente

## 🏗️ Arquitectura de Servicios

| Servicio | Puerto | Función | URL en Render |
|----------|--------|---------|---------------|
| **Auth** | 7011 | Autenticación y JWT | `https://farmacia-auth.onrender.com` |
| **Usuario** | 7012 | Gestión de usuarios | `https://farmacia-usuario.onrender.com` |
| **Producto** | 7013 | Catálogo de productos | `https://farmacia-producto.onrender.com` |
| **MetodoPago** | 7014 | Pagos y transacciones | `https://farmacia-metodopago.onrender.com` |
| **Compra** | 7015 | Órdenes y compras | `https://farmacia-compra.onrender.com` |
| **Gateway** | 9000 | API Gateway | `https://farmacia-gateway.onrender.com` |

## 🗄️ Base de Datos

- **PostgreSQL 15** con esquemas separados:
  - `auth_schema`
  - `usuario_schema` 
  - `producto_schema`
  - `metodopago_schema`
  - `compra_schema`

## 📝 Pasos para el Despliegue

### 1. Preparar el Repositorio

```bash
# Asegúrate de estar en la rama feature/render-deployment
git checkout feature/render-deployment

# Subir los archivos de configuración
git add render.yaml
git add businessdomain/appgw/src/main/resources/application-render.yml
git add scripts/init-render-db.sh
git add RENDER_DEPLOYMENT.md
git commit -m "feat: configuración para despliegue en Render"
git push origin feature/render-deployment
```

### 2. Configurar la Base de Datos en Render

1. **Crear la Base de Datos:**
   - Ve a [Render Dashboard](https://dashboard.render.com)
   - Click en "New +" → "PostgreSQL"
   - Configura:
     - **Name:** `farmacia-postgresql`
     - **Database:** `farmaciadb`
     - **User:** `farmacia`
     - **Region:** `Oregon (US West)`
     - **Plan:** `Free`

2. **Inicializar Esquemas:**
   - Una vez creada la DB, ve a "Connect"
   - Copia la **External Database URL**
   - En tu terminal local, ejecuta:
   ```bash
   export DATABASE_URL="postgresql://farmacia:password@host:port/farmaciadb"
   ./scripts/init-render-db.sh
   ```

### 3. Desplegar los Servicios

#### Opción A: Despliegue Automático con render.yaml

1. **Importar desde GitHub:**
   - En Render Dashboard → "New +" → "Blueprint"
   - Conecta tu repositorio GitHub
   - Selecciona la rama `feature/render-deployment`
   - Render detectará automáticamente el `render.yaml`

2. **Configurar Variables:**
   - Render creará todos los servicios automáticamente
   - Revisa que las variables de entorno estén correctas

#### Opción B: Despliegue Manual (uno por uno)

1. **Auth Service:**
   ```
   Name: farmacia-auth
   Environment: Docker
   Build Command: (automático)
   Start Command: (automático)
   Dockerfile Path: ./businessdomain/auth/Dockerfile
   Docker Context: .
   ```

2. **Usuario Service:**
   ```
   Name: farmacia-usuario
   Environment: Docker
   Dockerfile Path: ./businessdomain/usuario/Dockerfile
   Docker Context: .
   ```

3. **Producto Service:**
   ```
   Name: farmacia-producto
   Environment: Docker
   Dockerfile Path: ./businessdomain/producto/Dockerfile
   Docker Context: .
   ```

4. **MetodoPago Service:**
   ```
   Name: farmacia-metodopago
   Environment: Docker
   Dockerfile Path: ./businessdomain/metodopago/Dockerfile
   Docker Context: .
   ```

5. **Compra Service:**
   ```
   Name: farmacia-compra
   Environment: Docker
   Dockerfile Path: ./businessdomain/compra/Dockerfile
   Docker Context: .
   ```

6. **Gateway Service:**
   ```
   Name: farmacia-gateway
   Environment: Docker
   Dockerfile Path: ./businessdomain/appgw/Dockerfile
   Docker Context: .
   ```

### 4. Variables de Entorno Críticas

Para cada servicio, configura estas variables:

#### Variables Comunes (todos los servicios excepto Gateway):
```env
PORT=7011  # (cambiar según el servicio: 7012, 7013, 7014, 7015)
SPRING_PROFILES_ACTIVE=render
DATABASE_URL=(conectar a farmacia-postgresql)
DB_USERNAME=(conectar a farmacia-postgresql)
DB_PASSWORD=(conectar a farmacia-postgresql)
JWT_SECRET=(generar secreto seguro - 64 caracteres)
CORS_ORIGINS=https://farmacia-frontend.onrender.com
```

#### Variables Específicas:

**MetodoPago Service:**
```env
STRIPE_API_KEY=sk_test_tu_clave_stripe
PAYPAL_CLIENT_ID=tu_paypal_client_id
PAYPAL_CLIENT_SECRET=tu_paypal_secret
TZ=America/Lima
```

**Producto Service:**
```env
UPLOAD_DIR=/app/uploads
```

**Gateway Service:**
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

### 5. Orden de Despliegue Recomendado

1. **Base de Datos** (farmacia-postgresql)
2. **Auth Service** (farmacia-auth)
3. **Usuario Service** (farmacia-usuario)
4. **Producto Service** (farmacia-producto)
5. **MetodoPago Service** (farmacia-metodopago)
6. **Compra Service** (farmacia-compra)
7. **Gateway Service** (farmacia-gateway) - ⚠️ **ÚLTIMO**

## 🔧 Configuración Post-Despliegue

### 1. Verificar Servicios Individuales

Prueba cada servicio por separado:

```bash
# Auth Service
curl https://farmacia-auth.onrender.com/auth/health

# Usuario Service
curl https://farmacia-usuario.onrender.com/usuario/health

# Producto Service
curl https://farmacia-producto.onrender.com/producto/health

# MetodoPago Service
curl https://farmacia-metodopago.onrender.com/metodopago/health

# Compra Service
curl https://farmacia-compra.onrender.com/compra/health
```

### 2. Verificar Gateway

```bash
# Health check del gateway
curl https://farmacia-gateway.onrender.com/actuator/health

# Probar rutas a través del gateway
curl https://farmacia-gateway.onrender.com/auth/health
curl https://farmacia-gateway.onrender.com/usuario/health
```

### 3. Configurar CORS para Frontend

Si tienes un frontend, actualiza las URLs en las variables `CORS_ORIGINS`:

```env
CORS_ORIGINS=https://tu-frontend.onrender.com,http://localhost:4200
```

## 🚨 Solución de Problemas Comunes

### 1. Servicio no inicia (Build failed)
- Verifica que los Dockerfiles estén en las rutas correctas
- Revisa los logs de build en Render
- Asegúrate de que `Docker Context` esté configurado como `.` (raíz del proyecto)

### 2. Base de Datos no conecta
- Verifica que las variables `DATABASE_URL`, `DB_USERNAME`, `DB_PASSWORD` estén conectadas correctamente
- Ejecuta el script de inicialización de esquemas
- Revisa que el perfil `render` esté activado

### 3. Gateway no puede contactar servicios
- Verifica que las URLs de los servicios en el Gateway sean correctas
- Asegúrate de que todos los servicios estén desplegados y funcionando
- Revisa las variables `*_SERVICE_URL` en el Gateway

### 4. CORS Errors
- Actualiza las variables `CORS_ORIGINS` con las URLs correctas de tu frontend
- Reinicia los servicios después de cambiar variables de entorno

### 5. Servicios se "duermen" (Cold Start)
- Los servicios gratuitos se duermen después de 15 minutos de inactividad
- Considera implementar un servicio de "keep-alive" o usar webhooks

## 📊 Monitoreo y Logs

### Ver Logs en Render:
1. Ve a cada servicio en el Dashboard
2. Click en la pestaña "Logs"
3. Monitorea errores de startup y conexión a BD

### Endpoints de Monitoreo:
```
https://farmacia-gateway.onrender.com/actuator/health
https://farmacia-auth.onrender.com/actuator/health
https://farmacia-usuario.onrender.com/actuator/health
https://farmacia-producto.onrender.com/actuator/health
https://farmacia-metodopago.onrender.com/actuator/health
https://farmacia-compra.onrender.com/actuator/health
```

## 🎯 URLs Finales

Una vez desplegado, tu API estará disponible en:

- **API Gateway Principal:** `https://farmacia-gateway.onrender.com`
- **Swagger UI (si habilitado):** `https://farmacia-gateway.onrender.com/swagger-ui.html`

### Endpoints de ejemplo:
```
POST https://farmacia-gateway.onrender.com/auth/login
GET  https://farmacia-gateway.onrender.com/usuario/profile
GET  https://farmacia-gateway.onrender.com/producto/list
POST https://farmacia-gateway.onrender.com/compra/create
```

## 💡 Consejos de Optimización

1. **Usar la misma región** para todos los servicios (Oregon US West)
2. **Configurar Health Checks** apropiados en cada servicio
3. **Implementar retry logic** en el Gateway para manejar cold starts
4. **Usar Redis/Cache** para sesiones compartidas (upgrade plan)
5. **Monitorear límites** del plan gratuito (750 horas/mes por servicio)

## 🆘 Soporte

Si tienes problemas:
1. Revisa los logs en Render Dashboard
2. Verifica las variables de entorno
3. Prueba cada servicio individualmente
4. Contacta al equipo de desarrollo

---

**⚡ ¡Listo!** Tus 6 microservicios deberían estar funcionando en Render. El endpoint principal será el Gateway en `https://farmacia-gateway.onrender.com`