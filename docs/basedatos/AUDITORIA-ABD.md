# Mecanismo de auditoría (ABD)

Requisito de ABD: contar con un mecanismo de auditoría funcional sobre la base de datos.
La tabla `auditoria` (definida en `V6__replica_abd_sgroas.sql`) se alimenta
automáticamente mediante disparadores (triggers) creados en la migración Flyway
`V12__auditoria_disparadores.sql`.

## Qué hace

Por cada `INSERT`/`UPDATE`/`DELETE` en las tablas operativas, se registra una fila en
`auditoria` con:

- `accion`: la operación y la tabla (`INSERT en incidente`, `UPDATE en unidad`, ...).
- `fecha_hora`: momento del cambio (`now()`).
- `ip`: dirección de origen (`inet_client_addr()`).
- `id_usuario`: usuario de aplicación cuando la app lo indica; puede ser `NULL` en
  operaciones de carga/administración.

## Tablas auditadas

`incidente`, `alerta`, `unidad`, `ruta`, `conductor` y `usuario`.
Se excluye `programacion` (tabla de hechos masiva) para no duplicar millones de filas
durante la carga masiva; puede activarse si se desea.

## Detalle técnico

- La función `fn_auditoria()` omite el registro cuando la variable de sesión
  `app.bulk_load = 'on'` (usada por el generador de datos masivos, ver
  `CARGA-MASIVA-ABD.md`).
- `auditoria.id_usuario` se declaró previamente `NOT NULL`; el `V12` lo relaja a
  nullable porque el disparador de base de datos no siempre conoce el usuario de
  aplicación. La FK hacia `usuario` se mantiene.
- La aplicación puede indicar el usuario mediante
  `SELECT set_config('app.audit_usuario_id', '<id>', false);` antes de una operación.

## Verificación

Tras cualquier alta/cambio en las tablas auditadas, el conteo crece:

```sql
SELECT COUNT(*) FROM auditoria;
-- y para ver los últimos movimientos:
SELECT accion, fecha_hora, ip, id_usuario
FROM auditoria
ORDER BY fecha_hora DESC
LIMIT 20;
```
