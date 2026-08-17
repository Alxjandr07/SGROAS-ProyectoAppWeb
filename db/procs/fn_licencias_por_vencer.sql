-- =============================================================================
-- fn_licencias_por_vencer
-- Descripcion: Retorna los conductores cuya licencia vence
--              dentro de los proximos N dias.
-- Uso: Procedimiento con parametro, filtro por fecha
-- Invocacion JPA: @Procedure(name = "Conductor.licenciasPorVencer")
-- =============================================================================

CREATE OR REPLACE PROCEDURE fn_licencias_por_vencer(
    p_dias_umbral INTEGER,
    INOUT cur refcursor
)
    LANGUAGE plpgsql
AS $$
BEGIN
    OPEN cur FOR
    SELECT
        c.id,
        CONCAT(c.nombres, ' ', c.apellidos) AS nombre_completo,
        c.cedula::VARCHAR,
        c.numero_licencia::VARCHAR,
        c.tipo_licencia::VARCHAR,
        c.fecha_vencimiento_licencia,
        EXISTS(
            SELECT 1 FROM asignacion_rutas ar
            WHERE ar.conductor_id = c.id
              AND ar.estado = 'ACTIVA'
              AND ar.activo = true
        )::BOOLEAN AS asignacion_activa
    FROM conductores c
    WHERE c.activo = true
      AND c.fecha_vencimiento_licencia BETWEEN CURRENT_DATE AND (CURRENT_DATE + p_dias_umbral)
    ORDER BY c.fecha_vencimiento_licencia;
END;
$$;