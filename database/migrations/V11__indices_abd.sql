-- V11: Índices para optimización de consultas del esquema ABD (V6)
-- Las sentencias fueron extraídas del análisis de rendimiento con
-- EXPLAIN / ANALYZE (ver docs/basedatos/INDICES-ABD.md).
-- Idempotente: se usa CREATE INDEX IF NOT EXISTS.

CREATE INDEX IF NOT EXISTS idx_prog_conductor       ON programacion (id_conductor);
CREATE INDEX IF NOT EXISTS idx_prog_unidad_estado   ON programacion (id_unidad, estado);
CREATE INDEX IF NOT EXISTS idx_prog_fecha           ON programacion (fecha);
CREATE INDEX IF NOT EXISTS idx_audit_fecha          ON auditoria (fecha_hora DESC);
CREATE INDEX IF NOT EXISTS idx_audit_usuario_fecha  ON auditoria (id_usuario, fecha_hora);
CREATE INDEX IF NOT EXISTS idx_prog_ruta            ON programacion (id_ruta);
CREATE INDEX IF NOT EXISTS idx_incidente_nivel      ON incidente (nivel_sugerido);
CREATE INDEX IF NOT EXISTS idx_audit_usuario        ON auditoria (id_usuario);
CREATE INDEX IF NOT EXISTS idx_incidente_unidad     ON incidente (id_unidad);
CREATE INDEX IF NOT EXISTS idx_incidente_fecha      ON incidente (fecha_incidente);

-- Actualiza las estadísticas del planner tras crear los índices.
ANALYZE programacion;
ANALYZE incidente;
ANALYZE auditoria;
