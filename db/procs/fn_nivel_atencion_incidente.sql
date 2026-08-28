-- =============================================================================
-- fn_nivel_atencion_incidente
-- Descripcion: Funcion escalar que clasifica el nivel de atencion que requiere
--              un incidente segun la cantidad de alertas asociadas
--              (estructura IF/ELSE de la PARTE I del enunciado):
--                  3+ alertas -> CRITICO
--                  2  alertas -> MEDIO
--                  1  alerta  -> BAJO
--                  0  alertas -> SIN_ALERTA
-- Uso:
--   SELECT i.id_incidente, fn_nivel_atencion_incidente(i.id_incidente)
--   FROM incidente i;
-- Tabla relacionada: alerta (id_incidente).
-- Instalada por V13__funciones_cursores_sgroas.sql (sincronizada con V13).
-- =============================================================================

CREATE OR REPLACE FUNCTION fn_nivel_atencion_incidente(
    p_id_incidente INTEGER
)
    RETURNS VARCHAR
    LANGUAGE plpgsql
AS $$
DECLARE
    v_total_alertas INTEGER;
    v_nivel VARCHAR(20);
BEGIN
    SELECT COUNT(*) INTO v_total_alertas
    FROM alerta
    WHERE id_incidente = p_id_incidente;

    IF v_total_alertas >= 3 THEN
        v_nivel := 'CRITICO';
    ELSIF v_total_alertas = 2 THEN
        v_nivel := 'MEDIO';
    ELSIF v_total_alertas = 1 THEN
        v_nivel := 'BAJO';
    ELSE
        v_nivel := 'SIN_ALERTA';
    END IF;

    RETURN v_nivel;
END;
$$;