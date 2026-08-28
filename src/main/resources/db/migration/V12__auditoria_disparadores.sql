-- V12: Mecanismo de auditoría automática (disparadores sobre tablas ABD)
-- Requisito de ABD: contar con un mecanismo de auditoría funcional.
-- Cada alta/baja/cambio en las tablas operativas registra una fila en
-- "auditoria" con la operación, la tabla, la IP de origen y el usuario
-- (cuando la aplicación lo indica mediante la variable de sesión
-- app.audit_usuario_id).
--
-- Se excluye "programacion" (tabla de hechos masiva) para no duplicar
-- millones de filas durante la carga masiva; puede activarse si se desea.

-- El disparador no siempre conoce el usuario de aplicación, se permite nulo.
ALTER TABLE auditoria ALTER COLUMN id_usuario DROP NOT NULL;

CREATE OR REPLACE FUNCTION fn_auditoria() RETURNS trigger AS $$
DECLARE
    v_usuario integer;
    v_ip text;
BEGIN
    -- Omitir durante cargas masivas (ver db/data/generar_datos_masivos.sql).
    IF current_setting('app.bulk_load', true) = 'on' THEN
        RETURN NULL;
    END IF;

    BEGIN
        v_usuario := NULLIF(current_setting('app.audit_usuario_id', true), '')::integer;
    EXCEPTION WHEN others THEN
        v_usuario := NULL;
    END;

    v_ip := COALESCE(inet_client_addr()::text, '0.0.0.0');

    INSERT INTO auditoria (accion, fecha_hora, ip, id_usuario)
    VALUES (TG_OP || ' en ' || TG_TABLE_NAME, now(), v_ip, v_usuario);

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_auditoria_incidente ON incidente;
CREATE TRIGGER trg_auditoria_incidente
    AFTER INSERT OR UPDATE OR DELETE ON incidente
    FOR EACH ROW EXECUTE FUNCTION fn_auditoria();

DROP TRIGGER IF EXISTS trg_auditoria_alerta ON alerta;
CREATE TRIGGER trg_auditoria_alerta
    AFTER INSERT OR UPDATE OR DELETE ON alerta
    FOR EACH ROW EXECUTE FUNCTION fn_auditoria();

DROP TRIGGER IF EXISTS trg_auditoria_unidad ON unidad;
CREATE TRIGGER trg_auditoria_unidad
    AFTER INSERT OR UPDATE OR DELETE ON unidad
    FOR EACH ROW EXECUTE FUNCTION fn_auditoria();

DROP TRIGGER IF EXISTS trg_auditoria_ruta ON ruta;
CREATE TRIGGER trg_auditoria_ruta
    AFTER INSERT OR UPDATE OR DELETE ON ruta
    FOR EACH ROW EXECUTE FUNCTION fn_auditoria();

DROP TRIGGER IF EXISTS trg_auditoria_conductor ON conductor;
CREATE TRIGGER trg_auditoria_conductor
    AFTER INSERT OR UPDATE OR DELETE ON conductor
    FOR EACH ROW EXECUTE FUNCTION fn_auditoria();

DROP TRIGGER IF EXISTS trg_auditoria_usuario ON usuario;
CREATE TRIGGER trg_auditoria_usuario
    AFTER INSERT OR UPDATE OR DELETE ON usuario
    FOR EACH ROW EXECUTE FUNCTION fn_auditoria();
