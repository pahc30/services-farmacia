-- =====================================================
-- Script de inicialización para PostgreSQL
-- Sistema de Farmacia Dey & Parent
-- =====================================================

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

-- =====================================================
-- ESQUEMA: usuario_schema
-- =====================================================

-- Tabla: Usuario
CREATE TABLE IF NOT EXISTS usuario_schema."Usuario" (
    id SERIAL PRIMARY KEY,
    identificacion VARCHAR(20) NOT NULL,
    nombres VARCHAR(45) NOT NULL,
    apellidos VARCHAR(45) NOT NULL,
    telefono VARCHAR(45),
    email VARCHAR(45),
    direccion VARCHAR(100),
    rol VARCHAR(45) NOT NULL,
    username VARCHAR(45) NOT NULL UNIQUE,
    password VARCHAR(200) NOT NULL,
    eliminado INTEGER NOT NULL DEFAULT 0
);

-- Índices para Usuario
CREATE INDEX IF NOT EXISTS idx_usuario_username ON usuario_schema."Usuario"(username);
CREATE INDEX IF NOT EXISTS idx_usuario_eliminado ON usuario_schema."Usuario"(eliminado);

-- =====================================================
-- ESQUEMA: producto_schema
-- =====================================================

-- Tabla: Categoria
CREATE TABLE IF NOT EXISTS producto_schema."Categoria" (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(45) NOT NULL,
    descripcion VARCHAR(200),
    eliminado INTEGER NOT NULL DEFAULT 0
);

-- Índices para Categoria
CREATE INDEX IF NOT EXISTS idx_categoria_eliminado ON producto_schema."Categoria"(eliminado);

-- Tabla: Producto
CREATE TABLE IF NOT EXISTS producto_schema."Producto" (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(45) NOT NULL UNIQUE,
    nombre VARCHAR(70) NOT NULL,
    descripcion VARCHAR(200),
    precio DOUBLE PRECISION,
    stock INTEGER,
    url VARCHAR(200),
    categoria_id INTEGER NOT NULL,
    eliminado INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT fk_producto_categoria FOREIGN KEY (categoria_id) 
        REFERENCES producto_schema."Categoria"(id)
);

-- Índices para Producto
CREATE INDEX IF NOT EXISTS idx_producto_codigo ON producto_schema."Producto"(codigo);
CREATE INDEX IF NOT EXISTS idx_producto_categoria ON producto_schema."Producto"(categoria_id);
CREATE INDEX IF NOT EXISTS idx_producto_eliminado ON producto_schema."Producto"(eliminado);

-- =====================================================
-- ESQUEMA: metodopago_schema
-- =====================================================

-- Tabla: Metodopago
CREATE TABLE IF NOT EXISTS metodopago_schema."Metodopago" (
    id SERIAL PRIMARY KEY,
    tipo VARCHAR(45) NOT NULL,
    descripcion VARCHAR(250),
    eliminado INTEGER NOT NULL DEFAULT 0
);

-- Índices para Metodopago
CREATE INDEX IF NOT EXISTS idx_metodopago_eliminado ON metodopago_schema."Metodopago"(eliminado);

-- Tabla: transaccion_pago
CREATE TABLE IF NOT EXISTS metodopago_schema.transaccion_pago (
    id BIGSERIAL PRIMARY KEY,
    compra_id BIGINT NOT NULL,
    metodo_pago_id BIGINT NOT NULL,
    monto DECIMAL(10,2) NOT NULL,
    moneda VARCHAR(3) NOT NULL DEFAULT 'USD',
    estado VARCHAR(50) NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL,
    fecha_actualizacion TIMESTAMP,
    fecha_pago TIMESTAMP,
    referencia_externa VARCHAR(200),
    client_secret VARCHAR(200),
    descripcion VARCHAR(500),
    detalles_respuesta VARCHAR(1000),
    mensaje_error VARCHAR(500),
    eliminado INTEGER NOT NULL DEFAULT 0
);

-- Índices para transaccion_pago
CREATE INDEX IF NOT EXISTS idx_transaccion_compra_id ON metodopago_schema.transaccion_pago(compra_id);
CREATE INDEX IF NOT EXISTS idx_transaccion_metodo_pago_id ON metodopago_schema.transaccion_pago(metodo_pago_id);
CREATE INDEX IF NOT EXISTS idx_transaccion_estado ON metodopago_schema.transaccion_pago(estado);
CREATE INDEX IF NOT EXISTS idx_transaccion_fecha_creacion ON metodopago_schema.transaccion_pago(fecha_creacion);
CREATE INDEX IF NOT EXISTS idx_transaccion_eliminado ON metodopago_schema.transaccion_pago(eliminado);

-- =====================================================
-- ESQUEMA: compra_schema
-- =====================================================

-- Tabla: CarritoCompra
CREATE TABLE IF NOT EXISTS compra_schema.carrito_compra (
    id SERIAL PRIMARY KEY,
    cantidad INTEGER,
    usuario_id INTEGER,
    producto_id INTEGER,
    eliminado INTEGER NOT NULL DEFAULT 0
);

-- Índices para CarritoCompra
CREATE INDEX IF NOT EXISTS idx_carrito_usuario_id ON compra_schema.carrito_compra(usuario_id);
CREATE INDEX IF NOT EXISTS idx_carrito_producto_id ON compra_schema.carrito_compra(producto_id);
CREATE INDEX IF NOT EXISTS idx_carrito_eliminado ON compra_schema.carrito_compra(eliminado);

-- Tabla: Compra
CREATE TABLE IF NOT EXISTS compra_schema."Compra" (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(200) NOT NULL UNIQUE,
    fecha TIMESTAMP,
    usuario_id INTEGER,
    metodo_pago_id INTEGER,
    igv DOUBLE PRECISION DEFAULT 0.0,
    subtotal DOUBLE PRECISION,
    total DOUBLE PRECISION,
    eliminado INTEGER NOT NULL DEFAULT 0
);

-- Índices para Compra
CREATE INDEX IF NOT EXISTS idx_compra_codigo ON compra_schema."Compra"(codigo);
CREATE INDEX IF NOT EXISTS idx_compra_usuario_id ON compra_schema."Compra"(usuario_id);
CREATE INDEX IF NOT EXISTS idx_compra_metodo_pago_id ON compra_schema."Compra"(metodo_pago_id);
CREATE INDEX IF NOT EXISTS idx_compra_fecha ON compra_schema."Compra"(fecha);
CREATE INDEX IF NOT EXISTS idx_compra_eliminado ON compra_schema."Compra"(eliminado);

-- Tabla: DetalleCompra
CREATE TABLE IF NOT EXISTS compra_schema."DetalleCompra" (
    id SERIAL PRIMARY KEY,
    compra_id INTEGER NOT NULL,
    producto_id INTEGER,
    carrito_compra_id INTEGER,
    cantidad INTEGER,
    precio DOUBLE PRECISION,
    subtotal DOUBLE PRECISION,
    eliminado INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT fk_detalle_compra FOREIGN KEY (compra_id) 
        REFERENCES compra_schema."Compra"(id) ON DELETE CASCADE
);

-- Índices para DetalleCompra
CREATE INDEX IF NOT EXISTS idx_detalle_compra_id ON compra_schema."DetalleCompra"(compra_id);
CREATE INDEX IF NOT EXISTS idx_detalle_producto_id ON compra_schema."DetalleCompra"(producto_id);
CREATE INDEX IF NOT EXISTS idx_detalle_eliminado ON compra_schema."DetalleCompra"(eliminado);


-- =====================================================
-- Fin del script
-- =====================================================