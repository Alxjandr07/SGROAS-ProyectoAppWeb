-- =============================================================================
-- sp_obtener_incidentes_por_rango
-- Descripcion: Obtiene todos los incidentes en un rango de fechas con
--              informacion del conductor, vehiculo y ruta asociados.
-- JOINs: incidentes + asignacion_rutas + conductores + vehiculos + rutas
-- Invocacion JPA: @Procedure(name = "Incidente.obtenerIncidentesPorRango")
-- =============================================================================

CREATE OR REPLACE PROCEDURE sp_obtener_incidentes_por_rango(
    p_fecha_desde TIMESTAMPTZ,
    p_fecha_hasta TIMESTAMPTZ,
    INOUT cur refcursor
)
    LANGUAGE plpgsql
AS $$
BEGIN
    OPEN cur FOR
    SELECT
        i.id,
        i.tipo::VARCHAR,
        i.gravedad::VARCHAR,
        i.estado::VARCHAR,
        i.descripcion,
        i.fecha_incidente,
        i.ubicacion,
        CONCAT(c.nombres, ' ', c.apellidos) AS conductor_nombre,
        v.placa::VARCHAR,
        r.codigo::VARCHAR
    FROM incidentes i
             JOIN asignacion_rutas ar ON i.asignacion_id = ar.id
             JOIN conductores c ON ar.conductor_id = c.id
             JOIN vehiculos v ON ar.vehiculo_id = v.id
             JOIN rutas r ON ar.ruta_id = r.id
    WHERE i.activo = true
      AND i.fecha_incidente >= p_fecha_desde
      AND i.fecha_incidente <= p_fecha_hasta
    ORDER BY i.fecha_incidente DESC;
END;
$$;