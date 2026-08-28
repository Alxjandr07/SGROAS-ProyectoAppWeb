-- =============================================================================
-- fn_total_programaciones
-- Descripcion: Funcion escalar que retorna la cantidad de programaciones,
--              total o filtrada por estado. Equivalente SGROAS de la funcion
--              escalar "count" (PARTE I del enunciado de ABD).
-- Uso:
--   SELECT fn_total_programaciones('Programado');
--   SELECT fn_total_programaciones();
-- El filtro por estado aprovecha el indice idx_prog_estado.
-- Instalada por V13__funciones_cursores_sgroas.sql (sincronizada con V13).
-- =============================================================================

CREATE OR REPLACE FUNCTION fn_total_programaciones(
    p_estado VARCHAR DEFAULT NULL
)
    RETURNS BIGINT
    LANGUAGE plpgsql
AS $$
DECLARE
    v_total BIGINT;
BEGIN
    IF p_estado IS NULL THEN
        SELECT COUNT(*) INTO v_total FROM programacion;
    ELSE
        SELECT COUNT(*) INTO v_total FROM programacion WHERE estado = p_estado;
    END IF;

    RETURN v_total;
END;
$$;