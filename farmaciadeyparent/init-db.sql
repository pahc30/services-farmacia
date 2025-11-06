-- Script de inicialización para PostgreSQL
-- Este script crea los esquemas separados para cada microservicio

-- Crear esquemas para cada microservicio
CREATE SCHEMA IF NOT EXISTS auth_schema;
CREATE SCHEMA IF NOT EXISTS usuario_schema;
CREATE SCHEMA IF NOT EXISTS producto_schema;
CREATE SCHEMA IF NOT EXISTS metodopago_schema;
CREATE SCHEMA IF NOT EXISTS compra_schema;

-- Dar permisos al usuario farmacia en todos los esquemas
GRANT ALL PRIVILEGES ON SCHEMA auth_schema TO farmacia;
GRANT ALL PRIVILEGES ON SCHEMA usuario_schema TO farmacia;
GRANT ALL PRIVILEGES ON SCHEMA producto_schema TO farmacia;
GRANT ALL PRIVILEGES ON SCHEMA metodopago_schema TO farmacia;
GRANT ALL PRIVILEGES ON SCHEMA compra_schema TO farmacia;

-- Configurar el search_path por defecto para incluir todos los esquemas
ALTER USER farmacia SET search_path = auth_schema, usuario_schema, producto_schema, metodopago_schema, compra_schema, public;