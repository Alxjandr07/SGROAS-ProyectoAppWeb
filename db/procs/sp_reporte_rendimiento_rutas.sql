-- =============================================================================
-- sp_reporte_rendimiento_rutas
-- Descripcion: Reporte de rendimiento por ruta: total de asignaciones,
--              incidentes reportados, y distribucion por gravedad.
-- Uso: Agregaciones multiples, subqueries
-- Invocacion JPA: @Procedure(name = "Ruta.reporteRendimientoRutas")
-- =============================================================================

CREATE OR REPLACE PROCEDURE sp_reporte_rendimiento_rutas(
    INOUT cur refcursor
)
    LANGUAGE plpgsql
AS $$
BEGIN
    OPEN cur FOR
    SELECT
        r.id,
        r.codigo::VARCHAR,
        r.nombre::VARCHAR,
        COUNT(DISTINCT ar.id)::BIGINT AS total_asignaciones,
        COUNT(DISTINCT i.id)::BIGINT AS total_incidentes,
        COUNT(DISTINCT CASE WHEN i.gravedad = 'CRITICA' THEN i.id END)::BIGINT AS incidentes_criticos,
        COUNT(DISTINCT CASE WHEN i.gravedad = 'ALTA' THEN i.id END)::BIGINT AS incidentes_altos,
        COUNT(DISTINCT CASE WHEN i.gravedad = 'MEDIA' THEN i.id END)::BIGINT AS incidentes_medios,
        COUNT(DISTINCT CASE WHEN i.gravedad = 'BAJA' THEN i.id END)::BIGINT AS incidentes_bajos,
        ROUND(AVG(r.distancia_km), 2)::NUMERIC AS promedio_distancia_km
    FROM rutas r
             LEFT JOIN asignacion_rutas ar ON ar.ruta_id = r.id AND ar.activo = true
             LEFT JOIN incidentes i ON i.asignacion_id = ar.id AND i.activo = true
    WHERE r.activo = true
    GROUP BY r.id, r.codigo, r.nombre
    ORDER BY total_incidentes DESC;
END;
$$;