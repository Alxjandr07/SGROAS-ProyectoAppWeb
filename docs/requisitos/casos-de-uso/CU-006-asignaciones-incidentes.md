# CU-006: Asignación de rutas y registro de incidentes

| Campo | Valor |
|---|---|
| **ID** | CU-006 |
| **Nombre** | Asignación de rutas y registro de incidentes |
| **Actor principal** | Coordinador (asignaciones) / Seguridad (incidentes) |
| **Actor secundario** | Administrador |
| **Nivel** | 1 — Objetivo del usuario |
| **Precondiciones** | El usuario está autenticado; existen rutas, conductores y vehículos registrados |
| **Postcondiciones** | La asignación queda registrada o el incidente queda registrado en el sistema |

## Escenario principal de éxito — Asignación

1. El coordinador accede al módulo de asignaciones.
2. El coordinador selecciona ruta, conductor, vehículo y fecha.
3. El sistema valida disponibilidad del conductor y vehículo para la fecha.
4. El sistema persiste la asignación.
5. El sistema retorna 201 Created con el recurso creado.

## Escenario principal de éxito — Incidente

1. El usuario de seguridad accede al módulo de incidentes.
2. El usuario registra fecha, ubicación, gravedad (ALTA/MEDIA/BAJA) y descripción.
3. El sistema valida los campos obligatorios.
4. El sistema persiste el incidente.
5. El sistema retorna 201 Created.

## Extensiones — Asignación

| Paso | Condición | Manejo |
|---|---|---|
| 3a | Conductor ya asignado para esa fecha | Sistema responde 409 Conflict |
| 3b | Vehículo en mantenimiento | Sistema rechaza con advertencia |
| 2a | Ruta, conductor o vehículo inexistente | Sistema responde 404 Not Found |

## Extensiones — Incidente

| Paso | Condición | Manejo |
|---|---|---|
| 3a | Gravedad inválida | Sistema responde 422 con ProblemDetails |
| 3b | Fecha futura | Sistema rechaza con mensaje de validación |

## Nivel de detalle

| Nivel | Descripción |
|---|---|
| **Cockburn 1** | Asignar recursos a rutas y registrar incidentes operativos |
| **Cockburn 2** | Coordinador asigna conductores/vehículos; Seguridad registra incidentes |
| **Cockburn 3** | Asignación desde interfaz web; registro de incidentes con formulario |
| **Cockburn 4** | (Ver escenarios principales paso a paso) |

## Requisitos asociados

- Asignaciones: REQ-F-025 a REQ-F-029 (CRUD de asignaciones, SP `sp_asignaciones_activas_por_conductor`).
- Incidentes: REQ-F-030 a REQ-F-034 (CRUD de incidentes, SPs `sp_incidentes_por_gravedad`, `sp_obtener_incidentes_por_rango`).
- Reportes: REQ-F-035 a REQ-F-036 (funciones `fn_licencias_por_vencer`, `fn_estadisticas_generales`).
