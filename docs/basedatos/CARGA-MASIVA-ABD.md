# Carga masiva de datos (ABD)

El esquema ABD debe contener **más de 1 millón de registros** verificables. El generador
reproducible se encuentra en:

```
db/data/generar_datos_masivos.sql
```

## Cómo ejecutar

```bash
psql -d sgroas_db -v escala=1000000 -f db/data/generar_datos_masivos.sql
```

El parámetro `:escala` es la cantidad de `programacion` a generar. Con `escala=1000000`
el total supera 1 millón de filas en el esquema ABD (programacion = 1.000.000, más
incidentes, alertas, auditoría, etc.).

> El script omite las tablas de hechos si ya tienen datos (`WHERE NOT EXISTS ... LIMIT 1`).
> Para regenerar desde cero, truncar primero:
> `TRUNCATE programacion, incidente, alerta, auditoria;`

## Verificación del volumen

```sql
SELECT
    (SELECT COUNT(*) FROM programacion) AS programacion,
    (SELECT COUNT(*) FROM incidente)    AS incidente,
    (SELECT COUNT(*) FROM alerta)       AS alerta,
    (SELECT COUNT(*) FROM auditoria)    AS auditoria,
    (SELECT COUNT(*) FROM usuario)      AS usuario,
    (SELECT COUNT(*) FROM conductor)    AS conductor,
    (SELECT COUNT(*) FROM unidad)       AS unidad;
-- La suma debe superar 1.000.000.
```

## Notas

- Al inicio el script fija `app.bulk_load = 'on'` para que los disparadores de auditoría
  (`V12`) no registren millones de filas durante la carga; al final lo restablece a `'off'`.
- Los nombres de rol se alinean con el esquema semilla (`ROLE_ADMIN`, `ROLE_COORDINADOR`,
  `ROLE_SEGURIDAD`, `ROLE_OPERADOR`).
- Los catálogos (provincia, ciudad, terminal, rol) usan `ON CONFLICT DO NOTHING`, por lo que
  es seguro ejecutarlo después de las migraciones Flyway (`V9` ya siembra datos base).
