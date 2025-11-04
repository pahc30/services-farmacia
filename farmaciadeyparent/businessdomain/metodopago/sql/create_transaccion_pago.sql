-- Script SQL para crear la tabla transaccion_pago en MySQL
-- Base de datos: farmaciadey

USE farmaciadey;

-- Crear tabla transaccion_pago
CREATE TABLE IF NOT EXISTS transaccion_pago (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    compra_id BIGINT NOT NULL,
    metodo_pago_id BIGINT NOT NULL,
    monto DECIMAL(10,2) NOT NULL,
    moneda VARCHAR(3) NOT NULL DEFAULT 'PEN',
    estado VARCHAR(50) NOT NULL,
    fecha_creacion DATETIME NOT NULL,
    fecha_actualizacion DATETIME,
    fecha_pago DATETIME,
    referencia_externa VARCHAR(200),
    client_secret VARCHAR(200),
    descripcion VARCHAR(500),
    detalles_respuesta VARCHAR(1000),
    mensaje_error VARCHAR(500),
    eliminado INT NOT NULL DEFAULT 0,
    
    INDEX idx_compra_id (compra_id),
    INDEX idx_metodo_pago_id (metodo_pago_id),
    INDEX idx_estado (estado),
    INDEX idx_fecha_creacion (fecha_creacion),
    INDEX idx_eliminado (eliminado)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insertar algunos datos de prueba (opcional)
INSERT IGNORE INTO transaccion_pago 
(id, compra_id, metodo_pago_id, monto, moneda, estado, fecha_creacion, fecha_actualizacion, descripcion, eliminado)
VALUES 
(1762201936234, 1, 2, 100.00, 'PEN', 'PENDIENTE', NOW(), NOW(), 'Pago Yape/Plin simulado', 0);

-- Mostrar estructura de la tabla
DESCRIBE transaccion_pago;

-- Mostrar datos insertados
SELECT * FROM transaccion_pago;