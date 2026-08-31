-- ============================================================
-- SGROAS · Consultas de demostración ABD
-- Uso en terminal (Windows/PowerShell):
--   docker cp db\consultas_demo.sql sgroas-postgres:/tmp/consultas_demo.sql
--   docker exec sgroas-postgres psql -U postgres -d sgroas_db -f /tmp/consultas_demo.sql
-- Alternativa visual: abrir este archivo en DBeaver y ejecutarlo.
-- Nota: las secciones 5B/5C insertan y modifican datos (demo).
-- ============================================================

\pset pager off
\pset footer off
\set QUIET on
\set VERBOSITY terse

-- ------------------------------------------------------------
\echo ============================================================
\echo  [1] BASE DE DATOS CON 1 MLLON DE REGISTROS (minimo)
\echo ============================================================
-- Distribucion de filas por tabla (coherente con el dominio):
SELECT 'programacion' AS tabla, count(*) AS filas FROM programacion
UNION ALL SELECT 'incidente', count(*) FROM incidente
UNION ALL SELECT 'alerta', count(*) FROM alerta
UNION ALL SELECT 'auditoria', count(*) FROM auditoria
UNION ALL SELECT 'conductor', count(*) FROM conductor
UNION ALL SELECT 'unidad', count(*) FROM unidad
ORDER BY filas DESC;

-- Total acumulado:
WITH t AS (
  SELECT count(*) AS filas FROM programacion
  UNION ALL SELECT count(*) FROM incidente
  UNION ALL SELECT count(*) FROM alerta
  UNION ALL SELECT count(*) FROM auditoria
  UNION ALL SELECT count(*) FROM conductor
  UNION ALL SELECT count(*) FROM unidad
)
SELECT sum(filas) AS total_registros FROM t;

-- Muestra del mayor volumen (la tabla operativa central):
SELECT estado, count(*) AS filas
FROM programacion
GROUP BY estado
ORDER BY filas DESC;

-- ------------------------------------------------------------
\echo ============================================================
\echo  [2] USUARIOS, ROLES Y PRIVILEGIOS (RLS)
\echo ============================================================
-- Usuarios (LOGIN) y roles (plantillas NOLOGIN):
SELECT rolname, rolcanlogin AS login, rolsuper AS superuser
FROM pg_roles
WHERE rolname IN ('usr_admin_coop','usr_coordinador','usr_seguridad_vial',
                  'rol_administrador','rol_coordinador_ruta','rol_personal_seguridad')
ORDER BY rolname;

-- Politicas Row Level Security instaladas:
SELECT tablename, policyname, cmd, permissive, roles
FROM pg_policies
WHERE schemaname = 'public'
ORDER BY tablename, policyname;

-- Mismo SELECT, distinto rol -> distintas filas visibles:
SET ROLE rol_personal_seguridad;
SELECT current_user AS sesion, count(*) AS programaciones_visibles FROM programacion;
RESET ROLE;

SET ROLE rol_administrador;
SELECT current_user AS sesion, count(*) AS programaciones_visibles FROM programacion;
RESET ROLE;

-- ------------------------------------------------------------
\echo ============================================================
\echo  [3] OPTIMIZACION DE CONSULTAS: EXPLAIN ANALYZE + INDICES
\echo ============================================================
-- Plan de ejecucion con 1M de filas (debe usar Index/Bitmap Scan):
EXPLAIN (ANALYZE, BUFFERS)
SELECT count(*) FROM programacion WHERE estado = 'Programado';

-- Uso real de los indices por parte de la app:
SELECT relname, seq_scan, idx_scan
FROM pg_stat_user_tables
WHERE relname IN ('programacion','incidente',
                  'conductor','unidad','auditoria');

-- ------------------------------------------------------------
\echo ============================================================
\echo  [4] AUDITORIA DE LA BASE DE DATOS
\echo ============================================================
-- Registros recientes de auditoria:
\pset format wrapped
SELECT * FROM auditoria ORDER BY id_auditoria DESC LIMIT 5;
\pset format aligned

-- Disparadores instalados (los que generan la auditoria):
SELECT event_object_table AS tabla, trigger_name,
       action_timing AS momento, event_manipulation AS operacion
FROM information_schema.triggers
WHERE trigger_schema = 'public'
ORDER BY event_object_table;

-- Demo: modificar un incidente y ver la trazabilidad automatica:
UPDATE incidente SET descripcion = 'Actualizado en demo'
WHERE id_incidente = 3;

SELECT id_auditoria, tabla, operacion, id_usuario, fecha
FROM auditoria
ORDER BY id_auditoria DESC LIMIT 3;

-- ------------------------------------------------------------
\echo ============================================================
\echo  [5] ELEMENTOS PROGRAMABLES
\echo ============================================================
\echo  ----- 5A. FUNCIONES (UDF escalares)
-- Total (sin filtro) y por estado:
SELECT fn_total_programaciones() AS total_programaciones;
SELECT fn_total_programaciones('Programado') AS programadas;

-- Nivel de atencion por incidente (clasificacion IF/ELSE):
SELECT id_incidente,
       fn_nivel_atencion_incidente(id_incidente) AS nivel_atencion
FROM incidente
ORDER BY id_incidente
LIMIT 5;

\echo  ----- 5B. PROCEDIMIENTO CON VALIDACION  (incluye auditoria)
\echo          Caso VALIDO: unidad 1, nivel ALTO, usuario 1
CALL sp_registrar_incidente('Mecanico','Incidente demo defensa','ALTO',1,1,NULL);
SELECT id_incidente, tipo_incidente, descripcion
FROM incidente
WHERE descripcion LIKE 'Incidente demo%'
ORDER BY id_incidente DESC;

\echo  ----- 5B. Caso INVALIDO: nivel CRITICO no permitido
CALL sp_registrar_incidente('Mecanico','x','CRITICO',1,1,NULL);

\echo  ----- 5B. Caso INVALIDO: unidad 999999 inexistente
CALL sp_registrar_incidente('Mecanico','x','ALTO',999999,1,NULL);

\echo  ----- 5C. CURSOR EXPLICITO (DECLARE/OPEN/FETCH/CLOSE)
SELECT * FROM fn_resumen_programaciones_por_unidad() ORDER BY 2 DESC LIMIT 5;

-- ------------------------------------------------------------
\echo ============================================================
\echo  [6] RESPALDO Y RECUPERACION (referencia rapida)
\echo ============================================================
\echo  Respaldo:     docker exec sgroas-postgres pg_dump -U postgres -d sgroas_db
\echo                -Fc -f /tmp/sgroas_abd.dump
\echo  Restauracion: docker exec sgroas-postgres createdb -U postgres sgroas_rec
\echo                docker exec sgroas-postgres pg_restore -U postgres
\echo                -d sgroas_rec /tmp/sgroas_abd.dump
\echo  Verificacion: ... -d sgroas_rec -c "SELECT count(*) FROM programacion;"