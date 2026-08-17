-- =============================================================================
-- sp_vehiculos_en_mantenimiento
-- Descripcion: Lista los vehiculos en estado EN_MANTENIMIENTO con
--              el conteo de incidentes asociados a sus asignaciones.
-- Uso: LEFT JOIN + agregacion
-- Invocacion JPA: @Procedure(name = "Vehiculo.vehiculosEnMantenimiento")
-- =============================================================================

CREATE OR REPLACE PROCEDURE sp_vehiculos_en_mantenimiento(
    INOUT cur refcursor
)
    LANGUAGE plpgsql
AS $$
BEGIN
    OPEN cur FOR
    SELECT
        v.id,
        v.placa::VARCHAR,
        v.marca::VARCHAR,
        v.modelo::VARCHAR,
        v.anio,
        COUNT(DISTINCT ar.id)::BIGINT AS total_asignaciones,
        COUNT(DISTINCT i.id)::BIGINT AS total_incidentes
    FROM vehiculos v
             LEFT JOIN asignacion_rutas ar ON ar.vehiculo_id = v.id AND ar.activo = true
             LEFT JOIN incidentes i ON i.asignacion_id = ar.id AND i.activo = true
    WHERE v.estado = 'EN_MANTENIMIENTO'
      AND v.activo = true
    GROUP BY v.id, v.placa, v.marca, v.modelo, v.anio
    ORDER BY v.placa;
END;
$$;