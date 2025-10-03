-- Migración para mejorar el manejo de borrado en cascada
-- Las restricciones de integridad referencial se manejan principalmente
-- desde el lado de la aplicación (JPA) con las anotaciones @OneToMany cascade=ALL
-- que se han agregado en las entidades.

-- Esta migración documenta los cambios realizados en las entidades:
-- 1. Usuario tiene @OneToMany hacia CuentaBancaria con cascade=ALL
-- 2. CuentaBancaria tiene @OneToMany hacia Transaccion con cascade=ALL
-- 3. Se modificó el servicio UsuarioService para usar delete() en lugar de deleteById()
--    para asegurar que las relaciones se carguen y el borrado en cascada funcione

-- No se requieren cambios en el esquema de base de datos
-- Las configuraciones de cascada se manejan a nivel de aplicación