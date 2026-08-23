-- V8__seed_conductores.sql
-- Datos semilla de conductores, asignaciones e incidentes.
-- V4 sembraba asignaciones/incidentes solo si existian conductores,
-- pero nadie insertaba conductores: los modulos quedaban vacios.

-- ============================================================
-- 1. CONDUCTORES
-- ============================================================
INSERT INTO conductores (nombres, apellidos, cedula, numero_licencia, tipo_licencia, fecha_vencimiento_licencia, telefono, email, estado)
VALUES
    ('Carlos Alberto',   'Perez Gomez',       '1712345678', 'LIC-EC-0001', 'E', '2028-05-15', '0991234567', 'cperez@sgroas.com',      'ACTIVO'),
    ('Maria Fernanda',   'Sanchez Lopez',     '1723456789', 'LIC-EC-0002', 'D', '2027-11-20', '0982345678', 'msanchez@sgroas.com',    'ACTIVO'),
    ('Jorge Luis',       'Congcona Vega',     '1804456783', 'LIC-EC-0003', 'E', '2029-02-10', '0963456789', 'jcongcona@sgroas.com',   'ACTIVO'),
    ('Ana Lucia',        'Teran Villamar',    '0912345678', 'LIC-EC-0004', 'C', '2027-08-30', '0954567890', 'ateran@sgroas.com',      'ACTIVO'),
    ('Pedro Antonio',    'Zambrano Mendoza',  '1314567890', 'LIC-EC-0005', 'E', '2026-12-01', '0945678901', 'pzambrano@sgroas.com',   'SUSPENDIDO'),
    ('Gabriela Elizabeth','Cabezas Pincay',   '1719988776', 'LIC-EC-0006', 'D', '2028-03-25', '0936789012', 'gcabezas@sgroas.com',    'ACTIVO'),
    ('Ricardo Manuel',   'Estupinan Galeas',  '1722334455', 'LIC-EC-0007', 'C', '2027-06-14', '0927890123', 'restupinan@sgroas.com',  'INACTIVO'),
    ('Sonia Maribel',    'Quiroz Alava',      '1803344556', 'LIC-EC-0008', 'E', '2029-09-05', '0918901234', 'squiroz@sgroas.com',     'ACTIVO'),
    ('Diego Fernando',   'Briones Macias',    '0912233445', 'LIC-EC-0009', 'D', '2027-04-18', '0909012345', 'dbriones@sgroas.com',    'ACTIVO'),
    ('Veronica Beatriz', 'Mendoza Barreiro',  '1345566778', 'LIC-EC-0010', 'C', '2028-10-22', '0990123456', 'vmendoza@sgroas.com',    'ACTIVO')
ON CONFLICT (cedula) DO NOTHING;

-- ============================================================
-- 2. ASIGNACIONES DE RUTAS
-- ============================================================
INSERT INTO asignacion_rutas (conductor_id, vehiculo_id, ruta_id, fecha_asignacion, fecha_inicio, fecha_fin, estado)
SELECT c.id, v.id, r.id, DATE '2026-07-01', DATE '2026-07-01', NULL, 'ACTIVA'
FROM conductores c, vehiculos v, rutas r
WHERE c.cedula = '1712345678' AND v.placa = 'ABC-1234' AND r.codigo = 'R-001';

INSERT INTO asignacion_rutas (conductor_id, vehiculo_id, ruta_id, fecha_asignacion, fecha_inicio, fecha_fin, estado)
SELECT c.id, v.id, r.id, DATE '2026-07-01', DATE '2026-07-01', DATE '2026-07-30', 'COMPLETADA'
FROM conductores c, vehiculos v, rutas r
WHERE c.cedula = '1723456789' AND v.placa = 'DEF-5678' AND r.codigo = 'R-002';

INSERT INTO asignacion_rutas (conductor_id, vehiculo_id, ruta_id, fecha_asignacion, fecha_inicio, fecha_fin, estado)
SELECT c.id, v.id, r.id, DATE '2026-07-15', DATE '2026-07-15', NULL, 'ACTIVA'
FROM conductores c, vehiculos v, rutas r
WHERE c.cedula = '1804456783' AND v.placa = 'GHI-9012' AND r.codigo = 'R-003';

INSERT INTO asignacion_rutas (conductor_id, vehiculo_id, ruta_id, fecha_asignacion, fecha_inicio, fecha_fin, estado)
SELECT c.id, v.id, r.id, DATE '2026-08-01', DATE '2026-08-01', NULL, 'ACTIVA'
FROM conductores c, vehiculos v, rutas r
WHERE c.cedula = '0912345678' AND v.placa = 'JKL-3456' AND r.codigo = 'R-004';

INSERT INTO asignacion_rutas (conductor_id, vehiculo_id, ruta_id, fecha_asignacion, fecha_inicio, fecha_fin, estado)
SELECT c.id, v.id, r.id, DATE '2026-06-01', DATE '2026-06-01', DATE '2026-06-28', 'CANCELADA'
FROM conductores c, vehiculos v, rutas r
WHERE c.cedula = '1314567890' AND v.placa = 'MNO-7890' AND r.codigo = 'R-001';

INSERT INTO asignacion_rutas (conductor_id, vehiculo_id, ruta_id, fecha_asignacion, fecha_inicio, fecha_fin, estado)
SELECT c.id, v.id, r.id, DATE '2026-08-10', DATE '2026-08-10', NULL, 'ACTIVA'
FROM conductores c, vehiculos v, rutas r
WHERE c.cedula = '1719988776' AND v.placa = 'ABC-1234' AND r.codigo = 'R-002';

INSERT INTO asignacion_rutas (conductor_id, vehiculo_id, ruta_id, fecha_asignacion, fecha_inicio, fecha_fin, estado)
SELECT c.id, v.id, r.id, DATE '2026-07-05', DATE '2026-07-05', DATE '2026-08-15', 'COMPLETADA'
FROM conductores c, vehiculos v, rutas r
WHERE c.cedula = '1722334455' AND v.placa = 'DEF-5678' AND r.codigo = 'R-005';

INSERT INTO asignacion_rutas (conductor_id, vehiculo_id, ruta_id, fecha_asignacion, fecha_inicio, fecha_fin, estado)
SELECT c.id, v.id, r.id, DATE '2026-08-18', DATE '2026-08-18', NULL, 'ACTIVA'
FROM conductores c, vehiculos v, rutas r
WHERE c.cedula = '1803344556' AND v.placa = 'GHI-9012' AND r.codigo = 'R-004';

-- ============================================================
-- 3. INCIDENTES
-- ============================================================
INSERT INTO incidentes (asignacion_id, reportado_por, tipo, descripcion, fecha_incidente, ubicacion, gravedad, estado)
SELECT ar.id, 'Coordinador de Flota', 'AVERIA_MECANICA',
       'Fallo en el sistema de frenos detectado durante el recorrido matutino',
       TIMESTAMPTZ '2026-07-18 08:30:00-05', 'Km 12 via Terminal Norte', 'ALTA', 'RESUELTO'
FROM asignacion_rutas ar JOIN conductores c ON c.id = ar.conductor_id
WHERE c.cedula = '1723456789';

INSERT INTO incidentes (asignacion_id, reportado_por, tipo, descripcion, fecha_incidente, ubicacion, gravedad, estado)
SELECT ar.id, 'Seguridad General', 'INFRACCION',
       'Exceso de velocidad registrado por el modulo GPS en zona escolar',
       TIMESTAMPTZ '2026-08-12 16:45:00-05', 'Av. Simon Bolivar y Av. 6 de Diciembre', 'MEDIA', 'EN_INVESTIGACION'
FROM asignacion_rutas ar JOIN conductores c ON c.id = ar.conductor_id
WHERE c.cedula = '1712345678';

INSERT INTO incidentes (asignacion_id, reportado_por, tipo, descripcion, fecha_incidente, ubicacion, gravedad, estado)
SELECT ar.id, 'Usuario via app movil', 'QUEJA',
       'Pasajeros reportan demoras reiteradas en la parada de Parque Central',
       TIMESTAMPTZ '2026-07-28 09:10:00-05', 'Parada Parque Central', 'BAJA', 'RESUELTO'
FROM asignacion_rutas ar JOIN conductores c ON c.id = ar.conductor_id
WHERE c.cedula = '1804456783';

INSERT INTO incidentes (asignacion_id, reportado_por, tipo, descripcion, fecha_incidente, ubicacion, gravedad, estado)
SELECT ar.id, 'Coordinador de Flota', 'ACCIDENTE',
       'Colision leve contra barrera de contencion, sin heridos de gravedad',
       TIMESTAMPTZ '2026-08-20 07:15:00-05', 'Redondel del Cisne', 'CRITICA', 'REPORTADO'
FROM asignacion_rutas ar JOIN conductores c ON c.id = ar.conductor_id
WHERE c.cedula = '0912345678';

INSERT INTO incidentes (asignacion_id, reportado_por, tipo, descripcion, fecha_incidente, ubicacion, gravedad, estado)
SELECT ar.id, 'Seguridad General', 'OTRO',
       'Puerta de emergencia abierta durante el trayecto por falla del seguro',
       TIMESTAMPTZ '2026-08-01 11:00:00-05', 'Barrio La Paz', 'BAJA', 'CERRADO'
FROM asignacion_rutas ar JOIN conductores c ON c.id = ar.conductor_id
WHERE c.cedula = '1722334455';

INSERT INTO incidentes (asignacion_id, reportado_por, tipo, descripcion, fecha_incidente, ubicacion, gravedad, estado)
SELECT ar.id, 'Conductor', 'AVERIA_MECANICA',
       'Recalentamiento del motor, unidad retirada de servicio para revision',
       TIMESTAMPTZ '2026-08-21 14:30:00-05', 'Km 8 via Quevedo', 'MEDIA', 'REPORTADO'
FROM asignacion_rutas ar JOIN conductores c ON c.id = ar.conductor_id
WHERE c.cedula = '1803344556';
