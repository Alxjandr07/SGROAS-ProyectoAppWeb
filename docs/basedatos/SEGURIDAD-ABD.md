# Seguridad nativa de base de datos (ABD)

Implementación de usuarios, roles y privilegios nativos de PostgreSQL sobre el esquema ABD,
junto con Row Level Security (RLS). El script es **manual** (lo ejecuta el DBA como
superusuario **después** de que Flyway crea las tablas) y se encuentra en:

```
db/seguridad/seguridades_bd_sgroas.sql
```

No es una migración de Flyway para no afectar el arranque de la aplicación web.

## Roles creados

| Login (operativo) | Rol de grupo (NOLOGIN) | Contraseña | Alcance |
|-------------------|------------------------|-----------|---------|
| usr_admin_coop | rol_administrador (SUPERUSER) | admin123 | Control total |
| usr_coordinador | rol_coordinador_ruta | coord123 | Catálogos + programación/unidades/rutas |
| usr_seguridad_vial | rol_personal_seguridad | segur123 | Consulta + incidentes/alertas |

La asignación de privilegios sigue RBAC: los permisos se conceden a los roles de grupo y los
logins operativos los heredan (`GRANT rol_... TO usr_...`).

## Row Level Security (RLS)

- **programacion:** el Personal de Seguridad Vial solo ve viajes en estado `Activo`
  (`ver_viajes_activos`). El Coordinador tiene lectura total (`ver_todo_coordinador`) y,
  además, se agregaron políticas de **escritura** (`programacion_coord_insert` /
  `programacion_coord_update`) para que los `INSERT`/`UPDATE` concedidos en la Sección 4 no
  queden bloqueados por RLS.
- **usuario:** RLS habilitado con la política `ver_usuarios_seguridad`, que además limita
  las columnas visibles para Seguridad Vial a `id_usuario, cedula, nombre, estado`
  (sin `contrasena` ni `correo`).

> **Nota de diseño:** se usa `ENABLE ROW LEVEL SECURITY` (no `FORCE`). Así el dueño de las
> tablas —el usuario con el que corre la aplicación web— omite RLS y la app sigue
> funcionando; RLS sólo restringe a los roles operativos nativos creados aquí.

## Correcciones según revisión del docente de ABD

1. En `programacion` sólo existían políticas `SELECT`, por lo que los `INSERT`/`UPDATE`
   concedidos al coordinador quedaban bloqueados. Se agregaron las políticas de escritura
   correspondientes.
2. La política sobre `usuario` no tenía RLS habilitado, por lo que no surtía efecto. Se
   habilitó RLS en esa tabla.

## Cómo ejecutar

```bash
psql -d sgroas_db -f db/seguridad/seguridades_bd_sgroas.sql
```

Para verificar RLS como el rol de seguridad:

```bash
psql -d sgroas_db -U usr_seguridad_vial
-- solo devuelve programaciones en estado 'Activo'
SELECT id_programacion, estado FROM programacion LIMIT 20;
```
