# CU-005: Gestión de vehículos

| Campo | Valor |
|---|---|
| **ID** | CU-005 |
| **Nombre** | Gestión de vehículos |
| **Actor principal** | Coordinador |
| **Actor secundario** | Administrador |
| **Nivel** | 1 — Objetivo del usuario |
| **Precondiciones** | El usuario está autenticado con permisos de coordinador o administrador |
| **Postcondiciones** | El vehículo queda registrado, actualizado o desactivado en el sistema |

## Escenario principal de éxito

1. El coordinador accede al módulo de vehículos.
2. El sistema muestra la lista de vehículos con paginación.
3. El coordinador selecciona la acción (crear, editar, desactivar).
4. Para creación: el coordinador ingresa placa, marca, modelo, año y capacidad.
5. El sistema valida unicidad de placa y rangos de año.
6. El sistema persiste el vehículo.
7. El sistema retorna la respuesta con el código HTTP correspondiente.

## Extensiones

| Paso | Condición | Manejo |
|---|---|---|
| 5a | Placa duplicada | Sistema responde 409 Conflict |
| 5b | Año fuera de rango válido | Sistema responde 422 con ProblemDetails |
| 4a | Vehículo no encontrado | Sistema responde 404 Not Found |
| 3a | Vehículo con asignaciones activas | Sistema impide desactivación con advertencia |

## Nivel de detalle

| Nivel | Descripción |
|---|---|
| **Cockburn 1** | Administrar la flota vehicular de la cooperativa |
| **Cockburn 2** | Coordinador gestiona inventario de vehículos |
| **Cockburn 3** | Coordinador crea/edita/desactiva vehículos desde la interfaz web |
| **Cockburn 4** | (Ver escenario principal paso a paso) |

## Requisitos asociados

REQ-F-020 a REQ-F-024 (CRUD de vehículos) — verificar en matriz de trazabilidad.
