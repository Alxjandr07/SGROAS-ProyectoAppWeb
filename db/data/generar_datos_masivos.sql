-- ============================================================
-- Generador de datos masivos SGROAS (esquema ABD - V6)
-- Reproduce la carga de datos de sgroas_db de forma reproducible.
-- Uso (pgAdmin4 Query Tool o psql):
--   psql -d sgroas_db -v escala=1000000 -f db/data/generar_datos_masivos.sql
-- Parametro :escala = total aproximado de registros a generar (default 1M).
-- ============================================================

-- ---------- Catalogos base ----------
INSERT INTO provincia (id_provincia, nombre) VALUES
    (1,'Pichincha'),(2,'Guayas'),(3,'Los Rios'),(4,'Azuay'),(5,'Manabi'),
    (6,'Esmeraldas'),(7,'Imbabura'),(8,'Tungurahua'),(9,'El Oro'),(10,'Loja')
ON CONFLICT (id_provincia) DO NOTHING;

INSERT INTO ciudad (id_ciudad, nombre, id_provincia) VALUES
    (1,'Quito',1),(2,'Sangolqui',1),(3,'Cayambe',1),
    (4,'Guayaquil',2),(5,'Duran',2),(6,'Milagro',2),
    (7,'Quevedo',3),(8,'Babahoyo',3),(9,'Mocache',3),(10,'Buena Fe',3),
    (11,'Cuenca',4),(12,'Gualaceo',4),
    (13,'Portoviejo',5),(14,'Manta',5),
    (15,'Esmeraldas',6),(16,'Atacames',6),
    (17,'Ibarra',7),(18,'Otavalo',7),
    (19,'Ambato',8),(20,'Banios',8),
    (21,'Machala',9),(22,'Pasaje',9),
    (23,'Loja',10),(24,'Catamayo',10)
ON CONFLICT (id_ciudad) DO NOTHING;

INSERT INTO terminal (id_terminal, nombre, id_ciudad) VALUES
    (1,'Terminal Terrestre Quitumbe',1),(2,'Terminal Carcelen',1),
    (3,'Terminal Terrestre Guayaquil',4),(4,'Terminal Duran',5),
    (5,'Terminal Quevedo',7),(6,'Terminal Babahoyo',8),
    (7,'Terminal Mocache',9),(8,'Terminal Buena Fe',10),
    (9,'Terminal Cuenca',11),(10,'Terminal Portoviejo',13),
    (11,'Terminal Manta',14),(12,'Terminal Esmeraldas',15),
    (13,'Terminal Ibarra',17),(14,'Terminal Ambato',19),
    (15,'Terminal Machala',21),(16,'Terminal Loja',23)
ON CONFLICT (id_terminal) DO NOTHING;

INSERT INTO rol (id_rol, nombre, descripcion) VALUES
    (1,'ADMIN','Acceso total al sistema'),
    (2,'COORDINADOR','Gestion operativa de flota y rutas'),
    (3,'SEGURIDAD','Consulta y registro de incidentes'),
    (4,'OPERADOR','Registro de programaciones diarias')
ON CONFLICT (id_rol) DO NOTHING;

-- ---------- Usuarios ----------
INSERT INTO usuario (cedula, nombre, correo, contrasena, estado, id_rol)
SELECT
    lpad((1700000000 + g)::text, 10, '0'),
    'Usuario ' || g,
    'usuario' || g || '@sgroas.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    CASE WHEN g % 50 = 0 THEN 'Inactivo' ELSE 'Activo' END,
    1 + (g % 4)
FROM generate_series(1, GREATEST(:escala / 20, 10)) AS g
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE cedula = lpad((1700000000 + g)::text, 10, '0'));

-- ---------- Conductores ----------
INSERT INTO conductor (cedula, nombres, licencia, fecha_vencimiento, telefono)
SELECT
    lpad((0910000000 + g)::text, 10, '0'),
    'Conductor ' || g,
    'LIC-' || lpad(g::text, 6, '0') || '-CAT' || (1 + g % 4)::text,
    CURRENT_DATE + ((g % 900) - 300) * INTERVAL '1 day',
    '+593' || (90 + g % 9) || lpad(((g * 7919) % 10000000)::text, 7, '0')
FROM generate_series(1, GREATEST(:escala / 25, 10)) AS g
WHERE NOT EXISTS (SELECT 1 FROM conductor WHERE cedula = lpad((0910000000 + g)::text, 10, '0'));

-- ---------- Unidades ----------
INSERT INTO unidad (placa, numero_disco, modelo, capacidad, anio_fabricacion, estado)
SELECT
    substr('ABCDEGHJKLMNPQRSTUVWXY', 1 + g % 22, 1) || '-' || lpad(g::text, 4, '0'),
    'D' || lpad(g::text, 5, '0'),
    (ARRAY['Hino FG','Mercedes OH-1621','Chevrolet NQR','Yutong ZK6122','Toyota Coaster','Hyundai County'])[1 + g % 6],
    (ARRAY[40,45,50,55,60])[1 + g % 5],
    2005 + (g % 20),
    CASE WHEN g % 30 = 0 THEN 'En Mantenimiento' WHEN g % 40 = 0 THEN 'Inactivo' ELSE 'Activo' END
FROM generate_series(1, GREATEST(:escala / 500, 10)) AS g
WHERE NOT EXISTS (SELECT 1 FROM unidad WHERE numero_disco = 'D' || lpad(g::text, 5, '0'));

-- ---------- Rutas entre terminales ----------
INSERT INTO ruta (id_terminal_origen, id_terminal_destino, precio_pasaje)
SELECT o.id_terminal, d.id_terminal,
       round((1 + (o.id_terminal * 13 + d.id_terminal * 7) % 25 + random())::numeric, 2)
FROM terminal o
CROSS JOIN terminal d
WHERE o.id_terminal <> d.id_terminal
  AND (o.id_terminal * 100 + d.id_terminal) % 3 <> 0
ON CONFLICT DO NOTHING;

-- ---------- Programaciones ----------
-- Los rangos de FK se derivan de MIN(id)/COUNT(*) reales (no se asume que
-- los ids comienzan en 1: las secuencias no son transaccionales).
-- Las tablas de hechos solo se llenan si estan vacias; para regenerar,
-- trunquelas primero (TRUNCATE programacion, incidente, alerta, auditoria;).
INSERT INTO programacion (fecha, hora_salida, hora_estimada_llegada, estado, id_ruta, id_unidad, id_conductor, id_usuario)
SELECT
    CURRENT_DATE - ((g % 365)) * INTERVAL '1 day',
    TIME '05:00' + ((g % 60)) * INTERVAL '10 minutes',
    TIME '08:00' + ((g % 90)) * INTERVAL '10 minutes',
    CASE WHEN g % 10 = 0 THEN 'Completado' WHEN g % 17 = 0 THEN 'Cancelado' WHEN g % 23 = 0 THEN 'En Curso' ELSE 'Programado' END,
    (SELECT MIN(id_ruta) FROM ruta) + (g % GREATEST((SELECT COUNT(*) FROM ruta), 1)),
    (SELECT MIN(id_unidad) FROM unidad) + (g % GREATEST((SELECT COUNT(*) FROM unidad), 1)),
    (SELECT MIN(id_conductor) FROM conductor) + (g % GREATEST((SELECT COUNT(*) FROM conductor), 1)),
    NULLIF((SELECT MIN(id_usuario) FROM usuario) + (g % GREATEST((SELECT COUNT(*) FROM usuario), 1)), 0)
FROM generate_series(1, :escala / 2) AS g
WHERE NOT EXISTS (SELECT 1 FROM programacion LIMIT 1);

-- ---------- Incidentes ----------
INSERT INTO incidente (tipo, descripcion, nivel_sugerido, fecha_incidente, evidencia, estado, id_unidad)
SELECT
    (ARRAY['Falla Mecanica','Choque','Mal Manejo','Exceso Velocidad','Robo','Vandalismo','Retraso'])[1 + g % 7],
    'Incidente generado automaticamente No. ' || g,
    (ARRAY['BAJO','MEDIO','ALTO'])[1 + g % 3],
    CURRENT_TIMESTAMP - ((g % 180)) * INTERVAL '1 day',
    CASE WHEN g % 4 = 0 THEN '/evidencias/inc_' || g || '.jpg' ELSE NULL END,
    CASE WHEN g % 8 = 0 THEN 'En Revision' WHEN g % 15 = 0 THEN 'Cerrado' ELSE 'Reportado' END,
    (SELECT MIN(id_unidad) FROM unidad) + (g % GREATEST((SELECT COUNT(*) FROM unidad), 1))
FROM generate_series(1, GREATEST(:escala / 100, 10)) AS g
WHERE NOT EXISTS (SELECT 1 FROM incidente LIMIT 1);

-- ---------- Alertas (derivadas de incidentes ALTO) ----------
INSERT INTO alerta (nivel_riesgo, descripcion, fecha, id_incidente)
SELECT i.nivel_sugerido,
       'Alerta por incidente ' || i.id_incidente || ': ' || i.tipo,
       i.fecha_incidente,
       i.id_incidente
FROM incidente i
WHERE i.nivel_sugerido = 'ALTO'
  AND NOT EXISTS (SELECT 1 FROM alerta a WHERE a.id_incidente = i.id_incidente);

-- ---------- Auditoria ----------
INSERT INTO auditoria (accion, fecha_hora, ip, id_usuario)
SELECT
    (ARRAY['LOGIN','LOGOUT','CREATE_PROGRAMACION','UPDATE_UNIDAD','DELETE_INCIDENTE','VIEW_REPORTE'])[1 + g % 6],
    CURRENT_TIMESTAMP - ((g % 120)) * INTERVAL '1 hour',
    '192.168.' || (g % 255) || '.' || ((g * 31) % 255),
    (SELECT MIN(id_usuario) FROM usuario) + (g % GREATEST((SELECT COUNT(*) FROM usuario), 1))
FROM generate_series(1, GREATEST(:escala / 10, 10)) AS g
WHERE NOT EXISTS (SELECT 1 FROM auditoria LIMIT 1);
