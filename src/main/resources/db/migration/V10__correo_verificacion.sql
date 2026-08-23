-- V10__correo_verificacion.sql
-- Registro con confirmacion por correo y restablecimiento de contrasena.
-- 1) Columna 'verificado' en usuarios: las cuentas existentes quedan verificadas;
--    las nuevas nacen verificado = false hasta confirmar el codigo enviado por correo.
-- 2) Tabla codigos_verificacion: guarda el hash del codigo de 6 digitos,
--    su tipo (verificacion de cuenta o restablecimiento de contrasena),
--    expiracion (10 min), intentos consumidos y estado de uso.

ALTER TABLE usuarios
    ADD COLUMN IF NOT EXISTS verificado BOOLEAN NOT NULL DEFAULT TRUE;

CREATE TABLE codigos_verificacion (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    codigo_hash VARCHAR(64) NOT NULL,
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('VERIFICACION', 'RESET_PASSWORD')),
    expira_en TIMESTAMPTZ NOT NULL,
    intentos INTEGER NOT NULL DEFAULT 0,
    usado BOOLEAN NOT NULL DEFAULT FALSE,
    creado_en TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_codigos_verificacion_email_tipo
    ON codigos_verificacion(email, tipo);
