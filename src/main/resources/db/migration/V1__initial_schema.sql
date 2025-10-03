-- Migración inicial para crear la estructura base
-- V1__initial_schema.sql

CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cedula VARCHAR(10) UNIQUE NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    apellido VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    telefono VARCHAR(15) NOT NULL
);

CREATE TABLE IF NOT EXISTS cuentas_bancarias (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_cuenta VARCHAR(20) UNIQUE NOT NULL,
    saldo_actual DECIMAL(15,2) NOT NULL,
    usuario_id BIGINT NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE TABLE IF NOT EXISTS transacciones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    monto DECIMAL(15,2) NOT NULL,
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('DEPOSITO', 'RETIRO', 'TRANSFERENCIA')),
    fecha TIMESTAMP NOT NULL,
    cuenta_bancaria_id BIGINT NOT NULL,
    cuenta_destino_id BIGINT,
    FOREIGN KEY (cuenta_bancaria_id) REFERENCES cuentas_bancarias(id),
    FOREIGN KEY (cuenta_destino_id) REFERENCES cuentas_bancarias(id)
);