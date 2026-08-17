-- V5__stored_procedures.sql
-- Migracion que instala los stored procedures y funciones de SGROAS
-- Contenido sincronizado con db/procs/*.sql (CATALOGO-SP.md)
-- Estrategia hibrida de acceso a datos: invocacion via @Procedure (JPA 2.1)
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
-- =============================================================================
-- sp_incidentes_por_gravedad
-- Descripcion: Agrupa y cuenta incidentes por nivel de gravedad,
--              filtrados opcionalmente por tipo (NULL = sin filtro).
-- Uso: Agregacion con COUNT, GROUP BY
-- Invocacion JPA: @Procedure(name = "Incidente.incidentesPorGravedad")
-- =============================================================================

CREATE OR REPLACE PROCEDURE sp_incidentes_por_gravedad(
    p_tipo VARCHAR,
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
        ROUND(AVG(r.distancia_km)::NUMERIC, 2) AS promedio_distancia_km
    FROM rutas r
             LEFT JOIN asignacion_rutas ar ON ar.ruta_id = r.id AND ar.activo = true
             LEFT JOIN incidentes i ON i.asignacion_id = ar.id AND i.activo = true
    WHERE r.activo = true
    GROUP BY r.id, r.codigo, r.nombre
    ORDER BY total_incidentes DESC;
END;
$$;
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
