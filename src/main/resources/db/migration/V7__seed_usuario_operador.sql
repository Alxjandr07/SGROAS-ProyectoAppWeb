-- V7__seed_usuario_operador.sql
-- Usuario OPERADOR para demostrar el control de acceso por roles.
-- Misma contrasena de demo: admin123

-- Ampliar el CHECK de rol para permitir ROLE_OPERADOR
ALTER TABLE usuarios DROP CONSTRAINT IF EXISTS chk_usuarios_rol;
ALTER TABLE usuarios ADD CONSTRAINT chk_usuarios_rol
  CHECK (rol IN ('ROLE_ADMIN', 'ROLE_COORDINADOR', 'ROLE_SEGURIDAD', 'ROLE_OPERADOR'));

INSERT INTO usuarios (nombre, email, password_hash, rol, activo)
VALUES
  ('Operador Terminal', 'operador@sgroas.com', '$2b$10$4Xg/hLS584uT0/7GaKFuWOp2hMi9ym15xrCjRhZmVM5KPzGkQLYZW', 'ROLE_OPERADOR', true)
ON CONFLICT DO NOTHING;
