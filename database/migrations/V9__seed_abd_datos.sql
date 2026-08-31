-- V9__seed_abd_datos.sql
-- Datos de ejemplo para el esquema ABD (replica sgroas_db) para que los
-- modulos de Rutas, Seguridad, Administracion y Reportes muestren contenido.
-- Valores alineados a lo que consulta ReporteAbdService:
--   programacion.estado = 'Programado' | incidente.nivel_sugerido = 'ALTO'
--   unidad.estado = 'Activo' | 'En Mantenimiento' | 'Inactivo'

-- ============================================================
-- Geografia
-- ============================================================
INSERT INTO provincia (id_provincia, nombre) VALUES
    (1, 'Pichincha'), (2, 'Guayas'), (3, 'Azuay');

INSERT INTO ciudad (id_ciudad, nombre, id_provincia) VALUES
    (1, 'Quito', 1),
    (2, 'Guayaquil', 2),
    (3, 'Cuenca', 3),
    (4, 'Santo Domingo', 1);

INSERT INTO terminal (id_terminal, nombre, id_ciudad) VALUES
    (1, 'Terminal Terrestre Quito', 1),
    (2, 'Terminal Terrestre Guayaquil', 2),
    (3, 'Terminal Terrestre Cuenca', 3),
    (4, 'Terminal Terrestre Santo Domingo', 4);

-- ============================================================
-- Roles y usuarios ABD
-- ============================================================
INSERT INTO rol (id_rol, nombre, descripcion) VALUES
    (1, 'ROLE_ADMIN', 'Administrador del sistema'),
    (2, 'ROLE_COORDINADOR', 'Gestiona programaciones y flota'),
    (3, 'ROLE_SEGURIDAD', 'Gestiona incidentes y alertas');

INSERT INTO usuario (id_usuario, cedula, nombre, correo, contrasena, estado, id_rol) VALUES
    (1, '1701234567', 'Kevin Castro',   'kcastro@sgroas.com',  '$2b$10$4Xg/hLS584uT0/7GaKFuWOp2hMi9ym15xrCjRhZmVM5KPzGkQLYZW', 'Activo', 1),
    (2, '0912345678', 'Maria Escudero', 'mescudero@sgroas.com','$2b$10$4Xg/hLS584uT0/7GaKFuWOp2hMi9ym15xrCjRhZmVM5KPzGkQLYZW', 'Activo', 2),
    (3, '0101234567', 'Alejandro Tejada','atejada@sgroas.com', '$2b$10$4Xg/hLS584uT0/7GaKFuWOp2hMi9ym15xrCjRhZmVM5KPzGkQLYZW', 'Activo', 3);

-- ============================================================
-- Conductores
-- ============================================================
INSERT INTO conductor (id_conductor, cedula, nombres, licencia, fecha_vencimiento, telefono) VALUES
    (1, '1709876543', 'Ana Salazar Torres',     'LIC-EC-0003', DATE '2028-02-10', '0991000003'),
    (2, '0912345670', 'Luis Ramos Perez',       'LIC-EC-0002', DATE '2027-11-20', '0991000002'),
    (3, '1314567890', 'Pedro Zambrano Mendoza', 'LIC-EC-0005', DATE '2026-12-01', '0945678901'),
    (4, '1719988776', 'Gabriela Cabezas Pincay','LIC-EC-0006', DATE '2028-03-25', '0936789012'),
    (5, '1803344556', 'Sonia Quiroz Alava',    'LIC-EC-0008', DATE '2029-09-05', '0918901234'),
    (6, '0912233445', 'Diego Briones Macias',   'LIC-EC-0009', DATE '2027-04-18', '0909012345');

-- ============================================================
-- Unidades
-- ============================================================
INSERT INTO unidad (id_unidad, placa, numero_disco, modelo, capacidad, anio_fabricacion, estado) VALUES
    (1, 'PCC-1234', 'D-01', 'Volvo 9700',              45, 2021, 'Activo'),
    (2, 'GIB-5678', 'D-02', 'Mercedes-Benz O500',      40, 2020, 'Activo'),
    (3, 'PCU-9012', 'D-03', 'Marcopolo Paradiso G7',   45, 2022, 'Activo'),
    (4, 'GIX-3456', 'D-04', 'Hino RG1J',               35, 2019, 'En Mantenimiento'),
    (5, 'PCM-7890', 'D-05', 'Volvo 9800',              50, 2023, 'Activo'),
    (6, 'PXY-2468', 'D-06', 'Scania K410',             44, 2018, 'Inactivo');

-- ============================================================
-- Rutas (precio de pasaje USD)
-- ============================================================
INSERT INTO ruta (id_ruta, id_terminal_origen, id_terminal_destino, precio_pasaje) VALUES
    (1, 1, 2, 12.50),
    (2, 2, 1, 12.50),
    (3, 1, 3, 14.00),
    (4, 2, 3, 9.75),
    (5, 1, 4, 5.25),
    (6, 3, 1, 14.00);

-- ============================================================
-- Programaciones (varios meses y estados para reportes)
-- ============================================================
INSERT INTO programacion (id_programacion, fecha, hora_salida, hora_estimada_llegada, estado, id_ruta, id_unidad, id_conductor, id_usuario) VALUES
    (1, DATE '2026-06-15', TIME '06:00', TIME '13:30', 'Realizado',  1, 1, 1, 1),
    (2, DATE '2026-07-02', TIME '08:00', TIME '15:30', 'Realizado',  3, 3, 2, NULL),
    (3, DATE '2026-07-20', TIME '10:00', TIME '17:30', 'Cancelado',  2, 2, 3, NULL),
    (4, DATE '2026-08-05', TIME '06:00', TIME '11:00', 'Programado', 5, 5, 4, NULL),
    (5, DATE '2026-08-22', TIME '07:00', TIME '14:30', 'Programado', 1, 1, 1, NULL),
    (6, DATE '2026-08-23', TIME '09:30', TIME '14:45', 'Programado', 4, 3, 5, NULL),
    (7, DATE '2026-08-24', TIME '05:45', TIME '13:15', 'Programado', 3, 2, 6, NULL),
    (8, DATE '2026-08-25', TIME '16:00', TIME '21:15', 'Programado', 6, 5, 2, NULL);

-- ============================================================
-- Incidentes
-- ============================================================
INSERT INTO incidente (id_incidente, tipo, descripcion, nivel_sugerido, fecha_incidente, evidencia, estado, id_unidad) VALUES
    (1, 'AVERIA_MECANICA', 'Falla en el sistema de frenos detectada en revision previa al viaje', 'ALTO',
        TIMESTAMP '2026-08-20 07:15:00', 'evidencias/frenos-gix3456.jpg', 'Reportado', 4),
    (2, 'INFRACCION', 'Exceso de velocidad registrado por GPS en zona escolar', 'MEDIO',
        TIMESTAMP '2026-08-12 16:45:00', NULL, 'En investigacion', 2),
    (3, 'ACCIDENTE', 'Colision leve contra barrera de contencion, sin heridos de gravedad', 'ALTO',
        TIMESTAMP '2026-07-28 09:10:00', 'evidencias/accidente-pcc1234.jpg', 'Resuelto', 1),
    (4, 'QUEJA', 'Pasajeros reportan demoras reiteradas en la parada de Parque Central', 'BAJO',
        TIMESTAMP '2026-07-05 11:30:00', NULL, 'Resuelto', 5);

-- ============================================================
-- Alertas
-- ============================================================
INSERT INTO alerta (id_alerta, nivel_riesgo, descripcion, fecha, id_incidente) VALUES
    (1, 'ALTO',  'Incidente critico: unidad retirada de servicio hasta revision tecnica', TIMESTAMP '2026-08-20 07:40:00', 1),
    (2, 'MEDIO', 'Seguimiento de infraccion pendiente de descargo del conductor',          TIMESTAMP '2026-08-12 17:00:00', 2),
    (3, 'BAJO',  'Queja de usuarios derivada al area comercial',                           TIMESTAMP '2026-07-05 12:00:00', 4);

-- ============================================================
-- Auditoria
-- ============================================================
INSERT INTO auditoria (id_auditoria, accion, fecha_hora, ip, id_usuario) VALUES
    (1, 'Creacion de rutas iniciales del sistema',      TIMESTAMP '2026-06-01 08:15:00', '186.3.52.10', 1),
    (2, 'Registro de unidades de la cooperativa',       TIMESTAMP '2026-06-01 09:00:00', '186.3.52.10', 1),
    (3, 'Programacion de viajes de la semana',          TIMESTAMP '2026-08-03 07:30:00', '190.15.200.4', 2),
    (4, 'Revision de incidentes y cierre de alertas',   TIMESTAMP '2026-08-21 15:20:00', '190.15.200.7', 3);

-- ============================================================
-- Sincronizar secuencias (los formularios Crear usan nextval)
-- ============================================================
SELECT setval('provincia_id_provincia_seq',      (SELECT MAX(id_provincia)   FROM provincia));
SELECT setval('ciudad_id_ciudad_seq',            (SELECT MAX(id_ciudad)      FROM ciudad));
SELECT setval('terminal_id_terminal_seq',        (SELECT MAX(id_terminal)    FROM terminal));
SELECT setval('rol_id_rol_seq',                  (SELECT MAX(id_rol)         FROM rol));
SELECT setval('usuario_id_usuario_seq',          (SELECT MAX(id_usuario)     FROM usuario));
SELECT setval('conductor_id_conductor_seq',      (SELECT MAX(id_conductor)   FROM conductor));
SELECT setval('unidad_id_unidad_seq',            (SELECT MAX(id_unidad)      FROM unidad));
SELECT setval('ruta_id_ruta_seq',                (SELECT MAX(id_ruta)        FROM ruta));
SELECT setval('programacion_id_programacion_seq',(SELECT MAX(id_programacion) FROM programacion));
SELECT setval('incidente_id_incidente_seq',      (SELECT MAX(id_incidente)   FROM incidente));
SELECT setval('alerta_id_alerta_seq',            (SELECT MAX(id_alerta)      FROM alerta));
SELECT setval('auditoria_id_auditoria_seq',      (SELECT MAX(id_auditoria)   FROM auditoria));
