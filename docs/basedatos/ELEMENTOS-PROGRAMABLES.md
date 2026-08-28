# Elementos programables en SGROAS

Cobertura de las cuatro categorias de elementos programables exigidas por la
asignatura de Bases de Datos Avanzadas (estructura del enunciado/ejercicios de
libreria, adaptada al esquema operativo de SGROAS). Los objetos se instalan por
las migraciones Flyway y se sincronizan con `db/procs/*.sql`.

| Categoria | Objeto SGROAS | Archivo | Migracion |
|-----------|---------------|---------|-----------|
| Funciones (UDF) | `fn_total_programaciones` | `db/procs/fn_total_programaciones.sql` | V13 |
| Funciones (UDF) | `fn_nivel_atencion_incidente` | `db/procs/fn_nivel_atencion_incidente.sql` | V13 |
| Funciones (UDF) | `fn_auditoria`, `actualizar_fecha_modificacion` | `V12` / `V1` | V12 / V1 |
| Procedimientos | `sp_registrar_incidente` (validacion + DML) | `db/procs/sp_registrar_incidente.sql` | V13 |
| Procedimientos | 7 SPs de reportes/consultas (V5) | `db/procs/sp_*.sql`, `fn_*.sql` | V5 |
| Cursores | `fn_resumen_programaciones_por_unidad` (cursor explicito) | `db/procs/fn_resumen_programaciones_por_unidad.sql` | V13 |
| Cursores | `INOUT cur refcursor` + `OPEN cur FOR` en los 7 SPs | `db/procs/*.sql` | V5 |
| Disparadores | `trg_auditoria_*` (auditoria) , `actualizar_fecha_modificacion` | `V12`, `V1` | V12 / V1 |

## 1. Funciones definidas por el usuario (UDF)

Equivalen a las funciones escalares "recibe un identificador y retorna un valor".
Convencion: nombre con prefijo `fn_`; `RETURNS` tipado; cuerpo con `BEGIN...RETURN`.

- **`fn_total_programaciones(p_estado)`**: cuenta programaciones (total o por
  estado). El filtro por estado usa el indice `idx_prog_estado` (V13).
- **`fn_nivel_atencion_incidente(p_id_incidente)`**: clasifica el nivel de
  atencion (IF/ELSE) segun la cantidad de alertas del incidente.
- Ademas existen las funciones de disparador `fn_auditoria` (V12) y
  `actualizar_fecha_modificacion` (V1).

```sql
SELECT fn_total_programaciones('Programado');
SELECT fn_total_programaciones();
SELECT i.id_incidente, fn_nivel_atencion_incidente(i.id_incidente) AS atencion
FROM incidente i LIMIT 10;
```

## 2. Procedimientos almacenados

- **Con validacion y DML** — `sp_registrar_incidente`: valida existencia de la
  unidad, rango de `nivel_sugerido` y existencia del usuario antes de insertar
  (mismo patron `IF EXISTS` de la PARTE II del enunciado). El alta queda
  registrada automaticamente en `auditoria` por el disparador.
- **De consulta/reporte** — los 7 procedimientos de `V5`
  (`sp_obtener_incidentes_por_rango`, `sp_incidentes_por_gravedad`,
  `sp_asignaciones_activas_por_conductor`, `sp_vehiculos_en_mantenimiento`,
  `sp_reporte_rendimiento_rutas`, `fn_licencias_por_vencer`,
  `fn_estadisticas_generales`); devuelven su result set por cursor
  (`INOUT cur refcursor`) y se invocan desde la capa JPA via `@Procedure`.

```sql
-- Valido (inserta y audita; imprime el id generado)
DO $$
DECLARE nuevo_id INTEGER;
BEGIN
    CALL sp_registrar_incidente('AVERIA_MECANICA', 'Fallo de frenos', 'ALTO', 1, 1, nuevo_id);
    RAISE NOTICE 'Incidente registrado con id=%', nuevo_id;
END $$;

-- Invalido (no inserta, lanza excepcion)
CALL sp_registrar_incidente('AVERIA_MECANICA', 'x', 'ALTO', 999999, NULL, NULL);

-- Result set
CALL fn_estadisticas_generales('cur1');
FETCH ALL FROM cur1;
```

## 3. Cursores

Doble cobertura:

- **Cursor explicito (patron DECLARE/OPEN/FETCH/CLOSE)** — `fn_resumen_programaciones_por_unidad()`
  declara el cursor `cr_unidades`, lo abre, recorre fila a fila con
  `FETCH NEXT INTO`, sale con `EXIT WHEN NOT FOUND` y lo cierra:

```sql
SELECT * FROM fn_resumen_programaciones_por_unidad();
```

- **Cursor (REFCURSOR) de salida** — los 7 procedimientos de V5 exponen el
  result set mediante `OUT cur refcursor` + `OPEN cur FOR SELECT ...`, que es la
  forma en que PostgreSQL devuelve multiples filas a un cliente (JPA lee el
  cursor dentro de la transaccion JDBC).

## 4. Disparadores

- **Auditoria (V12)** — `trg_auditoria_incidente`, `trg_auditoria_alerta`,
  `trg_auditoria_unidad`, `trg_auditoria_ruta`, `trg_auditoria_conductor`,
  `trg_auditoria_usuario` ejecutan `fn_auditoria` en cada INSERT/UPDATE/DELETE
  de las tablas operativas registrando accion, fecha, IP y usuario de aplicacion
  (variable `app.audit_usuario_id`). Mismo patron de "tabla de auditoria + trigger"
  de la PARTE IV del enunciado.
- **Actualizacion de fecha** (V1) — triggers `trg_actualizar_fecha_modificacion`
  sobre las tablas transaccionales del backend, que refrescan la fecha de
  ultima modificacion en cada UPDATE.

```sql
-- Ver la auditoria generada al insertar con el SP
SELECT id_auditoria, accion, fecha_hora, id_usuario
FROM auditoria
ORDER BY id_auditoria DESC
LIMIT 10;
```

## Como reproducir

Todo se aplica automaticamente al levantar el stack (`make up` o
`docker compose up --build -d`): Flyway ejecuta V1→V13. Si se desea probar los
objetos en una BD ya existente, aplicar `V13__funciones_cursores_sgroas.sql`
es idempotente (`CREATE OR REPLACE`, `CREATE INDEX IF NOT EXISTS`).