# HU-004: Gestión de rutas

- **Rol:** Coordinador / Administrador
- **Objetivo:** Crear, consultar, actualizar y eliminar rutas de transporte
- **Beneficio:** Mantener el catálogo de rutas actualizado para planificar asignaciones

## Criterios de aceptación (Gherkin)

```gherkin
Feature: Gestión de rutas
  Scenario: Crear ruta exitosamente
    Given un coordinador autenticado en el sistema
    When envía los datos de una nueva ruta con nombre, origen, destino y distancia
    Then el sistema crea la ruta y responde 201 Created
    And la ruta aparece en el listado GET /api/rutas

  Scenario: Consultar ruta por ID
    Given una ruta con ID 1 registrada en el sistema
    When el coordinador solicita GET /api/rutas/1
    Then el sistema retorna los datos completos de la ruta con código 200

  Scenario: Actualizar ruta existente
    Given una ruta con ID 1 registrada en el sistema
    When el coordinador envía PUT /api/rutas/1 con la distancia actualizada
    Then el sistema actualiza el registro y responde 200 OK

  Scenario: Eliminar ruta
    Given una ruta con ID 1 registrada en el sistema
    When el coordinador envía DELETE /api/rutas/1
    Then el sistema marca la ruta como inactiva y responde 204 No Content

  Scenario: Ruta no encontrada
    Given una ruta con ID 9999 que no existe
    When el coordinador solicita GET /api/rutas/9999
    Then el sistema responde 404 Not Found

  Scenario: Crear ruta sin campos obligatorios
    Given un coordinador autenticado en el sistema
    When envía POST /api/rutas con nombre vacío
    Then el sistema responde 422 Unprocessable Entity con errores de validación
```
