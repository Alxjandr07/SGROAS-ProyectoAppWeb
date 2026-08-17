-- =============================================================================
-- fn_estadisticas_generales
-- Descripcion: Retorna estadisticas resumidas de todo el sistema:
--              totales de conductores, vehiculos, rutas, asignaciones e
--              incidentes. Util para dashboards.
-- Uso: Procedimiento sin parametros, multiples agregaciones
-- Invocacion JPA: @Procedure(name = "Incidente.estadisticasGenerales")
-- =============================================================================

CREATE OR REPLACE PROCEDURE fn_estadisticas_generales(
    INOUT cur refcursor
)
    LANGUAGE plpgsql
AS $$
BEGIN
    OPEN cur FOR
    SELECT
        (SELECT COUNT(*) FROM conductores)::BIGINT,
        (SELECT COUNT(*) FROM conductores WHERE activo = true)::BIGINT,
        (SELECT COUNT(*) FROM vehiculos)::BIGINT,
        (SELECT COUNT(*) FROM vehiculos WHERE activo = true)::BIGINT,
        (SELECT COUNT(*) FROM rutas)::BIGINT,
        (SELECT COUNT(*) FROM rutas WHERE activo = true)::BIGINT,
        (SELECT COUNT(*) FROM asignacion_rutas)::BIGINT,
        (SELECT COUNT(*) FROM asignacion_rutas WHERE estado = 'ACTIVA' AND activo = true)::BIGINT,
        (SELECT COUNT(*) FROM incidentes)::BIGINT,
        (SELECT COUNT(*) FROM incidentes WHERE estado IN ('REPORTADO', 'EN_INVESTIGACION') AND activo = true)::BIGINT;
END;
$$;