-- Migración para agregar campo password a usuarios
-- V5__add_password_to_usuarios.sql

-- Agregar columna password a la tabla usuarios
ALTER TABLE usuarios
ADD COLUMN password VARCHAR(100) NOT NULL DEFAULT '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQNguuoZO0XkVuXUYCXgu';
-- Default: "password123" hasheado con BCrypt
-- Este password temporal deberá ser cambiado por los usuarios existentes

-- Comentario:
-- Para usuarios nuevos, el password se establecerá al crearlos
-- Para usuarios existentes, se les asigna password temporal: "password123"
-- Recomendación: Implementar un endpoint para que los usuarios cambien su password
