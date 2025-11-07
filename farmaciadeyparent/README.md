# 🏥 FARMACIA DEY - SISTEMA COMPLETO

## 📋 DESCRIPCIÓN
Sistema de farmacia completo con microservicios backend, frontend Angular y aplicación Android. Incluye gestión de productos, usuarios, compras, métodos de pago y generación de boletas PDF.

## 🚀 INICIO RÁPIDO

### Prerequisitos
- Docker & Docker Compose
- Java 21+ (para desarrollo)
- Node.js 18+ (para frontend)

### Ejecutar el Sistema
```bash
# 1. Clonar repositorio
git clone <repo-url>
cd farmaciadeyparent

# 2. Iniciar todos los servicios
./start-all-services.sh

# 3. Acceder a la aplicación
# Frontend: http://localhost:4200
# Gateway API: http://localhost:9000
# Usuario de prueba: test.user / 123456
```

## 🏗️ ARQUITECTURA

### Microservicios Backend
| Servicio | Puerto | Descripción |
|----------|--------|-------------|
| **Gateway** | 9000 | API Gateway y enrutamiento |
| **Auth** | 7011 | Autenticación JWT |
| **Usuario** | 7012 | Gestión de usuarios |
| **Producto** | 7013 | Catálogo de productos |
| **Metodopago** | 7014 | Pagos y boletas PDF |
| **Compra** | 7015 | Gestión de compras |

### Características Principales
- ✅ **Autenticación JWT** - Login seguro
- ✅ **Gestión de Productos** - CRUD completo con imágenes
- ✅ **Carrito de Compras** - Funcionalidad completa
- ✅ **Métodos de Pago** - Yape/Plin simulado
- ✅ **Boletas PDF** - Generación automática con IGV correcto
- ✅ **Base de datos persistente** - PostgreSQL con volúmenes Docker

## 🔧 DESARROLLO

### Estructura del Proyecto
```
farmaciadeyparent/
├── businessdomain/          # Microservicios Spring Boot
│   ├── appgw/              # API Gateway
│   ├── auth/               # Servicio de autenticación
│   ├── usuario/            # Gestión de usuarios
│   ├── producto/           # Catálogo de productos
│   ├── metodopago/         # Pagos y boletas PDF
│   └── compra/             # Gestión de compras
├── docs/                   # Documentación técnica
├── logs/                   # Logs de servicios (en desarrollo)
├── docker-compose.yml      # Configuración Docker
├── start-all-services.sh   # Script de inicio
└── stop-all-services.sh    # Script de parada
```

### Scripts Útiles
```bash
# Iniciar todos los servicios
./start-all-services.sh

# Detener todos los servicios
./stop-all-services.sh

# Reconstruir servicios específicos
docker-compose build <servicio> --no-cache
docker-compose restart <servicio>
```

### Base de Datos
- **PostgreSQL 15** en puerto 5432
- **Volumen persistente**: `farmacia_postgres_data_permanent`
- **Usuario**: farmacia / farmacia123

## 📱 APLICACIONES

### Frontend Angular (Puerto 4200)
- Catálogo de productos con búsqueda
- Carrito de compras
- Historial de compras ("Mis Compras")
- Descarga de boletas PDF

### Aplicación Android
- Código en: `farmacia-android/`
- Arquitectura MVVM con Kotlin
- Integración completa con backend

## 🧪 TESTING
Las carpetas `test/` contienen:
- **Pruebas Unitarias** (`*Test.java`)
- **Pruebas de Integración** (`*IT.java`) 
- **Pruebas de Caja Negra** (`*BlackBoxTest.java`)

```bash
# Ejecutar tests
./mvnw test -pl businessdomain/<servicio>
```

## 📄 FUNCIONALIDADES DESTACADAS

### Sistema de IGV Corregido
- Precios de productos **incluyen IGV** (18%)
- Boletas PDF muestran desglose correcto:
  - Subtotal: Precio sin IGV
  - IGV: Monto del impuesto
  - Total: Precio final del producto

### Boletas PDF Profesionales
- Generación automática tras cada compra
- Datos reales del usuario
- Timezone UTC-5 (Lima)
- Formato profesional con logo y datos de empresa

## 🔐 USUARIOS DE PRUEBA
| Usuario | Contraseña | Rol |
|---------|------------|-----|
| test.user | 123456 | Cliente |
| admin | admin123 | Administrador |

## 📞 SOPORTE
Para más información, consultar la documentación en `docs/`

---
**Desarrollado por**: UTP - Integrador II  
**Fecha**: Noviembre 2025