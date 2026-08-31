-- V4__seed_nuevas_tablas.sql
-- Datos semilla para vehiculos, rutas, asignaciones e incidentes

-- ============================================================
-- VEHICULOS
-- ============================================================
INSERT INTO vehiculos (placa, marca, modelo, anio, capacidad_pasajeros, numero_motor, numero_chasis, color, estado)
VALUES
    ('ABC-1234', 'Toyota', 'Hiace', 2022, 15, 'MTR-TOY-001', 'CHS-TOY-001', 'Blanco', 'ACTIVO'),
    ('DEF-5678', 'Hyundai', 'County', 2023, 25, 'MTR-HYU-001', 'CHS-HYU-001', 'Azul', 'ACTIVO'),
    ('GHI-9012', 'Mercedes-Benz', 'Sprinter', 2021, 20, 'MTR-MB-001', 'CHS-MB-001', 'Gris', 'ACTIVO'),
    ('JKL-3456', 'Kia', 'Grandbird', 2024, 35, 'MTR-KIA-001', 'CHS-KIA-001', 'Rojo', 'EN_MANTENIMIENTO'),
    ('MNO-7890', 'Volkswagen', 'Crafter', 2020, 18, 'MTR-VW-001', 'CHS-VW-001', 'Blanco', 'FUERA_DE_SERVICIO')
ON CONFLICT (placa) DO NOTHING;

-- ============================================================
-- RUTAS
-- ============================================================
INSERT INTO rutas (codigo, nombre, origen, destino, distancia_km, duracion_estimada_min, estado)
VALUES
    ('R-001', 'Universidad - Centro', 'Campus UTEQ', 'Parque Central', 12.5, 30, 'ACTIVA'),
    ('R-002', 'Norte - Sur', 'Terminal Norte', 'Terminal Sur', 18.3, 45, 'ACTIVA'),
    ('R-003', 'Este - Oeste', 'Mercado Este', 'Mall Oeste', 8.7, 25, 'ACTIVA'),
    ('R-004', 'Periferico', 'Redondel del Cisne', 'Terminal Terrestre', 22.0, 55, 'ACTIVA'),
    ('R-005', 'Ruta Escolar', 'Barrio La Paz', 'Escuela Central', 5.2, 15, 'INACTIVA')
ON CONFLICT (codigo) DO NOTHING;

-- ============================================================
-- ASIGNACIONES
-- ============================================================
INSERT INTO asignacion_rutas (conductor_id, vehiculo_id, ruta_id, fecha_asignacion, fecha_inicio, fecha_fin, estado)
SELECT c.id, v.id, r.id, '2026-07-01'::date, '2026-07-01'::date, NULL::date, 'ACTIVA'
FROM conductores c, vehiculos v, rutas r
WHERE c.cedula = '1234567890'
  AND v.placa = 'ABC-1234'
  AND r.codigo = 'R-001';

INSERT INTO asignacion_rutas (conductor_id, vehiculo_id, ruta_id, fecha_asignacion, fecha_inicio, fecha_fin, estado)
SELECT c.id, v.id, r.id, '2026-07-01'::date, '2026-07-01'::date, '2026-07-30'::date, 'COMPLETADA'
FROM conductores c, vehiculos v, rutas r
WHERE c.cedula = '0987654321'
  AND v.placa = 'DEF-5678'
  AND r.codigo = 'R-002';

INSERT INTO asignacion_rutas (conductor_id, vehiculo_id, ruta_id, fecha_asignacion, fecha_inicio, fecha_fin, estado)
SELECT c.id, v.id, r.id, '2026-07-15'::date, '2026-07-15'::date, NULL::date, 'ACTIVA'
FROM conductores c, vehiculos v, rutas r
WHERE c.cedula = '1234567890'
  AND v.placa = 'GHI-9012'
  AND r.codigo = 'R-003';

-- ============================================================
-- INCIDENTES
-- ============================================================
INSERT INTO incidentes (asignacion_id, reportado_por, tipo, descripcion, fecha_incidente, ubicacion, gravedad, estado)
SELECT ar.id, 'Coordinador Principal', 'AVERIA_MECANICA',
       'Fallo en el sistema de frenos reportado durante la ruta matutina',
       '2026-07-10 08:30:00'::timestamptz, 'Av. Principal km 5', 'ALTA', 'RESUELTO'
FROM asignacion_rutas ar
WHERE ar.estado = 'COMPLETADA'
LIMIT 1;

INSERT INTO incidentes (asignacion_id, reportado_por, tipo, descripcion, fecha_incidente, ubicacion, gravedad, estado)
SELECT ar.id, 'Seguridad General', 'INFRACCION',
       'Exceso de velocidad detectado en el modulo GPS',
       '2026-07-20 14:15:00'::timestamptz, 'Via a Quevedo', 'MEDIA', 'EN_INVESTIGACION'
FROM asignacion_rutas ar
WHERE ar.estado = 'ACTIVA'
LIMIT 1;
