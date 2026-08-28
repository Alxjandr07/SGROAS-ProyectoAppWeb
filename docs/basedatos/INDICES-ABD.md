# Índices de optimización (ABD)

Análisis e implementación de índices sobre el esquema ABD (tablas de `V6__replica_abd_sgroas.sql`),
con medición de rendimiento mediante `EXPLAIN ANALYZE`. Las sentencias se aplican de forma
automatizada en la migración Flyway `V11__indices_abd.sql` (idempotente: `CREATE INDEX IF NOT EXISTS`).

## Sentencias aplicadas

```sql
CREATE INDEX IF NOT EXISTS idx_prog_conductor     ON programacion (id_conductor);
CREATE INDEX IF NOT EXISTS idx_prog_unidad_estado ON programacion (id_unidad, estado);
CREATE INDEX IF NOT EXISTS idx_prog_fecha         ON programacion (fecha);
CREATE INDEX IF NOT EXISTS idx_audit_fecha        ON auditoria (fecha_hora DESC);
CREATE INDEX IF NOT EXISTS idx_audit_usuario_fecha ON auditoria (id_usuario, fecha_hora);
CREATE INDEX IF NOT EXISTS idx_prog_ruta          ON programacion (id_ruta);
CREATE INDEX IF NOT EXISTS idx_incidente_nivel    ON incidente (nivel_sugerido);
CREATE INDEX IF NOT EXISTS idx_audit_usuario      ON auditoria (id_usuario);
CREATE INDEX IF NOT EXISTS idx_incidente_unidad   ON incidente (id_unidad);
CREATE INDEX IF NOT EXISTS idx_incidente_fecha    ON incidente (fecha_incidente);

ANALYZE programacion;
ANALYZE incidente;
ANALYZE auditoria;
```

> `idx_prog_estado` (V13) se agrego posteriormente para dar soporte a la funcion
> escalar `fn_total_programaciones(p_estado)` (ver `ELEMENTOS-PROGRAMABLES.md`).
> Su medicion con `EXPLAIN ANALYZE` sobre `WHERE estado = 'Programado'` reduce la
> cantidad de filas leidas sobre el millon de programaciones respecto a un `Seq Scan`.

## Evidencia de rendimiento (promedio de 5 ejecuciones)

| # | Índice | Tabla (columnas) | Antes (ms) | Después (ms) | Mejora |
|---|--------|------------------|-----------:|-------------:|-------:|
| 1 | idx_prog_conductor | programacion (id_conductor) | 86,3166 | 6,2504 | 92,76 % |
| 2 | idx_prog_unidad_estado | programacion (id_unidad, estado) | 114,5742 | 4,5016 | 96,07 % |
| 3 | idx_prog_fecha | programacion (fecha) | 84,7042 | 0,0102 | 99,99 % |
| 4 | idx_audit_fecha | auditoria (fecha_hora DESC) | 110,7708 | 0,1656 | 99,85 % |
| 5 | idx_audit_usuario_fecha | auditoria (id_usuario, fecha_hora) | 153,005 | 125,4668 | 18,00 % |
| 6 | idx_prog_ruta | programacion (id_ruta) | 112,8774 | 11,6824 | 89,65 % |
| 7 | idx_incidente_nivel | incidente (nivel_sugerido) | 2,1302 | 1,6904 | 20,65 % |
| 8 | idx_audit_usuario | auditoria (id_usuario) | 64,101 | 57,3044 | 10,60 % |
| 9 | idx_incidente_unidad | incidente (id_unidad) | 420,072 | 393,8404 | 6,24 % |
| 10 | idx_incidente_fecha | incidente (fecha_incidente) | 5,4404 | 4,492 | 17,43 % |

## Interpretación técnica

- **Consultas 1–4 (mayor impacto):** el planner pasaba de `Seq Scan` + `Sort`/`Gather` a
  `Bitmap Index Scan` / `Index Scan`. En los casos 3 y 4 el plan colapsó a un único nodo
  `Index Scan` porque el índice cubre a la vez el `WHERE` y el `ORDER BY`, eliminando el
  paso de ordenamiento.
- **Consulta 5 y 8 (mejora moderada):** el índice ayuda, pero el filtro es poco selectivo
  (el usuario coordinador concentra muchos registros) o el costo dominante es la agregación
  (`GROUP BY`), no el acceso a los datos. Se mantienen porque siguen siendo una mejora real.
- **Consultas 7, 9 y 10 (mejora menor):** las tablas `incidente`/`unidad` son pequeñas o el
  costo dominante viene de la lógica de la consulta (por ejemplo, un `EXISTS` correlacionado
  en la consulta 9). El índice reduce trabajo, pero no cambia el tipo de nodo dominante. Son
  ejemplos válidos para explicar que un índice no siempre resuelve el cuello de botella si
  este está en el procesamiento, no en el acceso.

## Cómo reproducir

Con la base poblada (ver `CARGA-MASIVA-ABD.md`), ejecutar por ejemplo:

```sql
EXPLAIN (ANALYZE, BUFFERS)
SELECT id_programacion, fecha, estado
FROM programacion
WHERE id_conductor = 75;
```

La salida debe mostrar `Bitmap Index Scan on idx_prog_conductor`.
