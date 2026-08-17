# CU-004: Gestión de rutas

| Campo | Valor |
|---|---|
| **ID** | CU-004 |
| **Nombre** | Gestión de rutas de transporte |
| **Actor principal** | Coordinador |
| **Actor secundario** | Administrador |
| **Nivel** | 1 — Objetivo del usuario |
| **Precondiciones** | El usuario está autenticado con permisos de coordinador o administrador |
| **Postcondiciones** | La ruta queda registrada, actualizada o eliminada lógicamente en el sistema |

## Escenario principal de éxito

1. El coordinador accede al módulo de rutas.
2. El sistema muestra la lista de rutas existentes con paginación.
3. El coordinador selecciona la acción (crear, editar, eliminar).
4. Para creación: el coordinador ingresa nombre, origen, destino y distancia.
5. El sistema valida los datos de entrada.
6. El sistema persiste la ruta en la base de datos.
7. El sistema retorna la respuesta con el código HTTP correspondiente.

## Extensiones

| Paso | Condición | Manejo |
|---|---|---|
| 5a | Campos obligatorios vacíos | Sistema responde 422 con ProblemDetails (RFC 7807) |
| 5b | Distancia negativa o cero | Sistema rechaza con mensaje de validación |
| 6a | Nombre de ruta duplicado | Sistema responde 409 Conflict |
| 4a | Ruta inexistente (ID no encontrado) | Sistema responde 404 Not Found |

## Nivel de detalle

| Nivel | Descripción |
|---|---|
| **Cockburn 1** | Administrar las rutas de transporte de la cooperativa |
| **Cockburn 2** | Coordinador gestiona catálogo de rutas |
| **Cockburn 3** | Coordinador crea/edita/elimina rutas desde la interfaz web |
| **Cockburn 4** | (Ver escenario principal paso a paso) |

## Requisitos asociados

REQ-F-015 a REQ-F-019 (CRUD de rutas) — verificar en matriz de trazabilidad.
