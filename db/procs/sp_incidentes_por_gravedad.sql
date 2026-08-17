-- =============================================================================
-- sp_incidentes_por_gravedad
-- Descripcion: Agrupa y cuenta incidentes por nivel de gravedad,
--              filtrados opcionalmente por tipo.
-- Uso: Agregacion con COUNT, GROUP BY
-- Invocacion JPA: @Procedure(name = "Incidente.incidentesPorGravedad")
-- =============================================================================

CREATE OR REPLACE PROCEDURE sp_incidentes_por_gravedad(
    p_tipo VARCHAR DEFAULT NULL,
    INOUT cur refcursor
)
    LANGUAGE plpgsql
AS $$
BEGIN
    OPEN cur FOR
    SELECT
        i.gravedad::VARCHAR,
        COUNT(*)::BIGINT AS total_incidentes,
        MAX(i.fecha_incidente)::TIMESTAMPTZ AS ultimo_incidente
    FROM incidentes i
    WHERE i.activo = true
      AND (p_tipo IS NULL OR i.tipo::TEXT = p_tipo)
    GROUP BY i.gravedad
    ORDER BY total_incidentes DESC;
END;
$$;