-- =============================================================================
-- fn_resumen_programaciones_por_unidad
-- Descripcion: Funcion que recorre todas las unidades con un CURSOR explicito
--              (DECLARE CURSOR / OPEN / FETCH NEXT / EXIT WHEN NOT FOUND /
--              CLOSE, equivalente de la PARTE III del enunciado) y agrega por
--              cada una el total de programaciones, realizadas y canceladas.
-- Uso:
--   SELECT * FROM fn_resumen_programaciones_por_unidad();
-- El conteo por unidad aprovecha idx_prog_unidad_estado (id_unidad, estado).
-- Instalada por V13__funciones_cursores_sgroas.sql (sincronizada con V13).
-- =============================================================================

CREATE OR REPLACE FUNCTION fn_resumen_programaciones_por_unidad()
    RETURNS TABLE (
        placa VARCHAR,
        modelo VARCHAR,
        capacidad INTEGER,
        total_programaciones BIGINT,
        programaciones_realizadas BIGINT,
        programaciones_canceladas BIGINT
    )
    LANGUAGE plpgsql
AS $$
DECLARE
    v_id_unidad INTEGER;
    v_placa VARCHAR(15);
    v_modelo VARCHAR(50);
    v_capacidad INTEGER;
    v_total BIGINT;
    v_realizadas BIGINT;
    v_canceladas BIGINT;

    cr_unidades CURSOR FOR
        SELECT u.id_unidad, u.placa, u.modelo, u.capacidad
        FROM unidad u
        ORDER BY u.placa;
BEGIN
    OPEN cr_unidades;

    LOOP
        FETCH NEXT FROM cr_unidades
            INTO v_id_unidad, v_placa, v_modelo, v_capacidad;
        EXIT WHEN NOT FOUND;

        SELECT COUNT(*),
               COUNT(*) FILTER (WHERE estado = 'Realizado'),
               COUNT(*) FILTER (WHERE estado = 'Cancelado')
        INTO v_total, v_realizadas, v_canceladas
        FROM programacion
        WHERE id_unidad = v_id_unidad;

        placa                    := v_placa;
        modelo                   := v_modelo;
        capacidad                := v_capacidad;
        total_programaciones     := v_total;
        programaciones_realizadas := v_realizadas;
        programaciones_canceladas := v_canceladas;
        RETURN NEXT;
    END LOOP;

    CLOSE cr_unidades;
    RETURN;
END;
$$;