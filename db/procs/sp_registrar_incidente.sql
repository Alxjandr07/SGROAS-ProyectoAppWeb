-- =============================================================================
-- sp_registrar_incidente
-- Descripcion: Procedimiento que registra un incidente validando previamente
--              que la unidad y el usuario existan y que el nivel sugerido sea
--              valido (misma estructura IF EXISTS / validacion de la PARTE II
--              del enunciado). Tras la validacion inserta la fila y entrega el
--              id generado via parametro OUT.
-- Uso (caso valido, capturando el id):
--   DO $$
--   DECLARE nuevo_id INTEGER;
--   BEGIN
--       CALL sp_registrar_incidente('AVERIA_MECANICA', 'Fallo de frenos',
--                                    'ALTO', 1, 1, nuevo_id);
--       RAISE NOTICE 'Incidente registrado con id=%', nuevo_id;
--   END $$;
-- Uso (caso invalido, debe lanzar EXCEPTION):
--   CALL sp_registrar_incidente('AVERIA_MECANICA', 'x', 'ALTO', 999, NULL, NULL);
-- La insercion dispara el trigger de auditoria (V12).
-- Instalado por V13__funciones_cursores_sgroas.sql (sincronizado con V13).
-- =============================================================================

CREATE OR REPLACE PROCEDURE sp_registrar_incidente(
    p_tipo VARCHAR,
    p_descripcion TEXT,
    p_nivel_sugerido VARCHAR,
    p_id_unidad INTEGER,
    p_id_usuario INTEGER,
    OUT p_id_incidente INTEGER
)
    LANGUAGE plpgsql
AS $$
DECLARE
    v_existe INTEGER;
BEGIN
    -- Validacion 1: la unidad debe existir.
    SELECT COUNT(*) INTO v_existe FROM unidad WHERE id_unidad = p_id_unidad;
    IF v_existe = 0 THEN
        RAISE EXCEPTION 'sp_registrar_incidente: la unidad % no existe.', p_id_unidad;
    END IF;

    -- Validacion 2: el nivel sugerido debe ser permitido.
    IF p_nivel_sugerido IS NULL OR p_nivel_sugerido NOT IN ('ALTO', 'MEDIO', 'BAJO') THEN
        RAISE EXCEPTION 'sp_registrar_incidente: nivel sugerido invalido (%). Permitidos: ALTO, MEDIO, BAJO.', p_nivel_sugerido;
    END IF;

    -- Validacion 3: si se indica el usuario, debe existir (lo registra la auditoria).
    IF p_id_usuario IS NOT NULL THEN
        SELECT COUNT(*) INTO v_existe FROM usuario WHERE id_usuario = p_id_usuario;
        IF v_existe = 0 THEN
            RAISE EXCEPTION 'sp_registrar_incidente: el usuario % no existe.', p_id_usuario;
        END IF;
        PERFORM set_config('app.audit_usuario_id', p_id_usuario::TEXT, false);
    END IF;

    INSERT INTO incidente (tipo, descripcion, nivel_sugerido, fecha_incidente, estado, id_unidad)
    VALUES (p_tipo, p_descripcion, p_nivel_sugerido, CURRENT_TIMESTAMP, 'Reportado', p_id_unidad)
    RETURNING id_incidente INTO p_id_incidente;
END;
$$;