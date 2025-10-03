-- Script de migración Flyway para agregar TRANSFERENCIA
-- Nombre del archivo: V2__add_transferencia_tipo.sql
-- Coloca este archivo en src/main/resources/db/migration/

-- Para H2 Database
ALTER TABLE transacciones DROP CONSTRAINT IF EXISTS check_tipo;
ALTER TABLE transacciones ADD CONSTRAINT check_tipo CHECK (tipo IN ('DEPOSITO', 'RETIRO', 'TRANSFERENCIA'));