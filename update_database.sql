-- Script para actualizar la base de datos y permitir TRANSFERENCIA
-- Ejecutar este script en tu base de datos

-- Para H2 Database (si estás usando H2)
ALTER TABLE transacciones ALTER COLUMN tipo SET DATA TYPE VARCHAR(20);
ALTER TABLE transacciones ADD CONSTRAINT check_tipo CHECK (tipo IN ('DEPOSITO', 'RETIRO', 'TRANSFERENCIA'));

-- Si la restricción anterior ya existe, primero elimínala y luego crea la nueva:
-- ALTER TABLE transacciones DROP CONSTRAINT IF EXISTS check_tipo;
-- ALTER TABLE transacciones ADD CONSTRAINT check_tipo CHECK (tipo IN ('DEPOSITO', 'RETIRO', 'TRANSFERENCIA'));

-- Para MySQL (si estás usando MySQL)
-- ALTER TABLE transacciones MODIFY COLUMN tipo ENUM('DEPOSITO', 'RETIRO', 'TRANSFERENCIA') NOT NULL;

-- Para PostgreSQL (si estás usando PostgreSQL)
-- ALTER TYPE tipo_transaccion ADD VALUE 'TRANSFERENCIA';
-- O si no tienes un tipo custom:
-- ALTER TABLE transacciones DROP CONSTRAINT IF EXISTS tipo_check;
-- ALTER TABLE transacciones ADD CONSTRAINT tipo_check CHECK (tipo IN ('DEPOSITO', 'RETIRO', 'TRANSFERENCIA'));