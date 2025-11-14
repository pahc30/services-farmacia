#!/bin/bash

# Script para inicializar la base de datos PostgreSQL en Render
# Este script debe ejecutarse después de crear la base de datos en Render

echo "🔄 Configurando esquemas de la base de datos..."

# Obtener la URL de conexión de Render (variable de entorno DATABASE_URL)
# Formato: postgresql://username:password@hostname:port/database

if [ -z "$DATABASE_URL" ]; then
    echo "❌ ERROR: DATABASE_URL no está configurada"
    exit 1
fi

# Ejecutar el script de inicialización
psql $DATABASE_URL << 'EOF'

-- Script de inicialización para PostgreSQL en Render
-- Este script crea los esquemas separados para cada microservicio

-- Crear esquemas para cada microservicio
CREATE SCHEMA IF NOT EXISTS auth_schema;
CREATE SCHEMA IF NOT EXISTS usuario_schema;
CREATE SCHEMA IF NOT EXISTS producto_schema;
CREATE SCHEMA IF NOT EXISTS metodopago_schema;
CREATE SCHEMA IF NOT EXISTS compra_schema;

-- Dar permisos al usuario actual en todos los esquemas
GRANT ALL PRIVILEGES ON SCHEMA auth_schema TO CURRENT_USER;
GRANT ALL PRIVILEGES ON SCHEMA usuario_schema TO CURRENT_USER;
GRANT ALL PRIVILEGES ON SCHEMA producto_schema TO CURRENT_USER;
GRANT ALL PRIVILEGES ON SCHEMA metodopago_schema TO CURRENT_USER;
GRANT ALL PRIVILEGES ON SCHEMA compra_schema TO CURRENT_USER;

-- Dar permisos de creación de tablas
GRANT CREATE ON SCHEMA auth_schema TO CURRENT_USER;
GRANT CREATE ON SCHEMA usuario_schema TO CURRENT_USER;
GRANT CREATE ON SCHEMA producto_schema TO CURRENT_USER;
GRANT CREATE ON SCHEMA metodopago_schema TO CURRENT_USER;
GRANT CREATE ON SCHEMA compra_schema TO CURRENT_USER;

-- Configurar el search_path por defecto
ALTER DATABASE CURRENT_DATABASE() SET search_path = auth_schema, usuario_schema, producto_schema, metodopago_schema, compra_schema, public;

EOF

if [ $? -eq 0 ]; then
    echo "✅ Esquemas de base de datos configurados correctamente"
else
    echo "❌ ERROR: Falló la configuración de los esquemas"
    exit 1
fi