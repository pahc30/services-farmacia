# 🏥 FARMACIA DEY PARENT - SISTEMA COMPLETO

## 📱 PROYECTOS DISPONIBLES

### 1. **BACKEND MICROSERVICIOS** ✅ FUNCIONANDO
Arquitectura de 6 microservicios con Spring Boot:

| Servicio | Puerto | Estado | URL | Descripción |
|----------|--------|--------|-----|-------------|
| **Gateway** | 9000 | ✅ | http://localhost:9000 | API Gateway principal |
| **Auth** | 7011 | ✅ | http://localhost:7011/auth | Autenticación y JWT |
| **Usuario** | 7012 | ✅ | http://localhost:7012/usuario | Gestión de usuarios |
| **Producto** | 7013 | ✅ | http://localhost:7013/producto | Catálogo de productos |
| **MetodoPago** | 7014 | ✅ | http://localhost:7014/metodopago | Métodos de pago |
| **Compra** | 7015 | ✅ | http://localhost:7015/compra | Gestión de compras |

### 2. **APLICACIÓN ANDROID** ✅ COMPLETADA
Proyecto Android completo en: `farmacia-android/`

- **Arquitectura**: MVVM con StateFlow
- **Lenguaje**: Kotlin 1.9.22
- **SDK**: Android 34
- **Características**:
  - Login con JWT
  - Lista de productos con grid
  - Navegación entre pantallas
  - API Client integrado
  - Material Design 3

## 🔐 CREDENCIALES DE PRUEBA

### Backend (Base de Datos H2)
```
Usuario: test1
Contraseña: test1

Usuario Admin: admin
Contraseña: admin123
```

### Android (Para testing)
```
Usuario: test1
Contraseña: test1
```

## 🚀 COMANDOS DISPONIBLES

### Iniciar Todos los Servicios
```bash
./start-all-services.sh
```

### Detener Todos los Servicios
```bash
./stop-all-services.sh
```

### Iniciar Servicios Manualmente
```bash
# Gateway
./mvnw spring-boot:run -pl businessdomain/appgw &

# Auth
./mvnw spring-boot:run -pl businessdomain/auth &

# Usuario
./mvnw spring-boot:run -pl businessdomain/usuario &

# Producto
./mvnw spring-boot:run -pl businessdomain/producto &

# MetodoPago
./mvnw spring-boot:run -pl businessdomain/metodopago &

# Compra
./mvnw spring-boot:run -pl businessdomain/compra &
```

## 🔧 API ENDPOINTS PRINCIPALES

### A través del Gateway (Puerto 9000)
```
POST http://localhost:9000/auth/login
GET  http://localhost:9000/usuario/
GET  http://localhost:9000/producto/
GET  http://localhost:9000/metodopago/
GET  http://localhost:9000/compra/
```

### Directos (Para debug)
```
POST http://localhost:7011/auth/login
GET  http://localhost:7012/usuario/actuator/health
GET  http://localhost:7013/producto/actuator/health
GET  http://localhost:7014/metodopago/actuator/health
GET  http://localhost:7015/compra/actuator/health
```

## 📂 ESTRUCTURA DEL PROYECTO

```
farmaciadeyparent/
├── businessdomain/           # Microservicios Backend
│   ├── appgw/               # API Gateway
│   ├── auth/                # Autenticación
│   ├── usuario/             # Gestión usuarios
│   ├── producto/            # Catálogo productos
│   ├── metodopago/          # Métodos de pago
│   └── compra/              # Gestión compras
│
├── farmacia-android/        # Aplicación Android
│   ├── app/src/main/java/   # Código Kotlin
│   ├── app/src/main/res/    # Recursos Android
│   └── build.gradle.kts     # Configuración Gradle
│
├── start-all-services.sh    # Script inicio servicios
├── stop-all-services.sh     # Script parada servicios
└── logs/                    # Logs de servicios
```

## 🧪 TESTING

### Backend
```bash
# Verificar todos los servicios
curl http://localhost:9000/actuator/health

# Test de login
curl -X POST http://localhost:9000/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test1","password":"test1"}'
```

### Android
1. Abrir proyecto en Android Studio
2. Ejecutar en emulador o dispositivo
3. Login con credenciales: test1/test1
4. Navegar por la app

## 💾 BASE DE DATOS

Cada servicio usa H2 in-memory:
- **Consola H2**: http://localhost:[PORT]/h2-console
- **Usuario**: sa
- **Contraseña**: (vacía)

## 🔄 ESTADO ACTUAL

✅ **COMPLETADO**: 
- 6 microservicios funcionando
- Android app completamente funcional
- Autenticación JWT implementada
- API Gateway configurado
- Scripts de gestión creados

🎯 **LISTO PARA**:
- Testing end-to-end
- Desarrollo de nuevas features
- Integración con frontend web
- Deploy en producción

---
**Última actualización**: 29 Oct 2025, 14:05 PM
**Servicios activos**: 6/6 ✅