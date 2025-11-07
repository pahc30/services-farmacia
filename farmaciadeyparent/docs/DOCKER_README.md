# 🐳 Docker Deployment Guide - Farmacia DEY

## 📋 Prerequisites

- Docker Desktop installed and running
- Docker Compose (included with Docker Desktop)
- Git (for cloning the repository)

## 🚀 Quick Start

### 1. Clone and Navigate
```bash
git clone https://github.com/pahc30/services-farmacia.git
cd services-farmacia/farmaciadeyparent
git checkout feature/render-deployment
```

### 2. Create External Volume (First Time Only)
```bash
# This ensures data persists across container restarts
docker volume create farmacia_postgres_data_permanent
```

### 3. Build All Services
```bash
./build-docker.sh
```

### 4. Start Full Stack
```bash
docker-compose up -d
```

### 5. Verify Services
```bash
# Check all services are running
docker-compose ps

# Check specific service logs
docker-compose logs -f farmacia-gateway
```

## 💾 Data Persistence

**IMPORTANTE**: Los datos de PostgreSQL ahora persisten automáticamente gracias al volumen externo `farmacia_postgres_data_permanent`.

### ✅ Comandos Seguros (NO borran datos):
```bash
# Parar servicios (mantiene datos)
docker-compose stop

# Reiniciar servicios (mantiene datos)
docker-compose restart

# Recrear contenedores (mantiene datos)
docker-compose up -d --force-recreate

# Redesplegar tras cambios de código (mantiene datos)
./build-docker.sh && docker-compose up -d
```

### ⚠️ Solo si quieres BORRAR todos los datos:
```bash
# Eliminar volumen permanente (BORRA TODOS LOS DATOS)
docker volume rm farmacia_postgres_data_permanent

# Recrear volumen limpio
docker volume create farmacia_postgres_data_permanent
```

## 🌐 Service URLs (Local Docker)

| Service | URL | Health Check |
|---------|-----|--------------|
| **Gateway** | http://localhost:9000 | http://localhost:9000/actuator/health |
| **Auth** | http://localhost:7011/auth | http://localhost:7011/auth/actuator/health |
| **Usuario** | http://localhost:7012/usuario | http://localhost:7012/usuario/actuator/health |
| **Producto** | http://localhost:7013/producto | http://localhost:7013/producto/actuator/health |
| **MetodoPago** | http://localhost:7014/metodopago | http://localhost:7014/metodopago/actuator/health |
| **Compra** | http://localhost:7015/compra | http://localhost:7015/compra/actuator/health |
| **PostgreSQL** | localhost:5432 | Database: `farmaciadb` |

## 🔑 Test Credentials

```json
{
  "username": "test1",
  "password": "test1"
}
```

## 📊 Database Access

```bash
# Connect to PostgreSQL container
docker exec -it farmacia-postgres psql -U farmacia -d farmaciadb

# Or using external client:
# Host: localhost
# Port: 5432
# Database: farmaciadb
# Username: farmacia
# Password: farmacia123
```

## 🛠️ Development Commands

### Build Individual Service
```bash
docker build -t farmacia-auth:latest -f businessdomain/auth/Dockerfile .
```

### View Logs
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f farmacia-auth

# Follow logs in real-time
docker-compose logs -f --tail=100 farmacia-gateway
```

### Scale Services
```bash
# Scale specific service
docker-compose up -d --scale farmacia-auth=2
```

### Restart Services
```bash
# Restart all
docker-compose restart

# Restart specific service
docker-compose restart farmacia-auth
```

## 🧹 Cleanup

### Stop Services
```bash
docker-compose down
```

### Remove Everything (including volumes)
```bash
docker-compose down -v
docker system prune -f
```

### Remove All Farmacia Images
```bash
docker images | grep farmacia | awk '{print $3}' | xargs docker rmi
```

## 🚀 Deploy to Render

Each Dockerfile is optimized for Render deployment:

1. **Multi-stage builds** for smaller images
2. **Non-root user** for security
3. **Health checks** included
4. **Environment variables** configured
5. **Port configuration** via `$PORT` variable

### Render Environment Variables Needed:
```
DATABASE_URL=postgresql://user:pass@host:port/db
JWT_SECRET=your-secure-jwt-secret
CORS_ALLOWED_ORIGINS=https://your-frontend.onrender.com
AUTH_SERVICE_URL=https://farmacia-auth.onrender.com
```

## 🔧 Troubleshooting

### Service Won't Start
```bash
# Check logs
docker-compose logs farmacia-auth

# Check if database is ready
docker-compose logs farmacia-db

# Restart service
docker-compose restart farmacia-auth
```

### Database Connection Issues
```bash
# Check database health
docker exec farmacia-postgres pg_isready -U farmacia

# Reset database
docker-compose down farmacia-db
docker volume rm farmaciadeyparent_postgres_data
docker-compose up -d farmacia-db
```

### Port Conflicts
```bash
# Check what's using the ports
lsof -i :9000
lsof -i :7011

# Stop conflicting services or change ports in docker-compose.yml
```

## 📁 File Structure

```
farmaciadeyparent/
├── docker-compose.yml          # Full stack orchestration
├── build-docker.sh            # Build all services script
├── .dockerignore              # Docker ignore patterns
└── businessdomain/
    ├── auth/Dockerfile        # Auth service
    ├── usuario/Dockerfile     # Usuario service
    ├── producto/Dockerfile    # Producto service
    ├── metodopago/Dockerfile  # MetodoPago service
    ├── compra/Dockerfile      # Compra service
    └── appgw/Dockerfile       # Gateway service
```