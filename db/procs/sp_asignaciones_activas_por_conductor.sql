-- =============================================================================
-- sp_asignaciones_activas_por_conductor
-- Descripcion: Devuelve las asignaciones activas para un conductor,
--              incluyendo datos del vehiculo y la ruta.
-- JOINs: asignacion_rutas + conductores + vehiculos + rutas
-- Invocacion JPA: @Procedure(name = "AsignacionRuta.asignacionesActivasPorConductor")
-- =============================================================================

CREATE OR REPLACE PROCEDURE sp_asignaciones_activas_por_conductor(
    p_conductor_id BIGINT,
    INOUT cur refcursor
)
    LANGUAGE plpgsql
AS $$
BEGIN
    OPEN cur FOR
    SELECT
        ar.id,
        v.placa::VARCHAR,
        v.marca::VARCHAR,
        r.codigo::VARCHAR,
        r.nombre::VARCHAR,
        ar.fecha_inicio,
        ar.fecha_fin
    FROM asignacion_rutas ar
             JOIN conductores c ON ar.conductor_id = c.id
             JOIN vehiculos v ON ar.vehiculo_id = v.id
             JOIN rutas r ON ar.ruta_id = r.id
    WHERE ar.conductor_id = p_conductor_id
      AND ar.estado = 'ACTIVA'
      AND ar.activo = true
    ORDER BY ar.fecha_inicio DESC;
END;
$$;