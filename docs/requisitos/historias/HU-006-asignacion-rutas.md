# HU-006: Asignación de rutas

- **Rol:** Coordinador
- **Objetivo:** Asignar conductores y vehículos a rutas específicas
- **Beneficio:** Organizar la operación diaria de transporte asignando recursos por ruta

## Criterios de aceptación (Gherkin)

```gherkin
Feature: Asignación de rutas
  Scenario: Crear asignación exitosa
    Given un coordinador autenticado en el sistema
    And existen una ruta, un conductor y un vehículo disponibles
    When envía POST /api/asignaciones con ruta, conductor, vehículo y fecha
    Then el sistema crea la asignación y responde 201 Created

  Scenario: Consultar asignaciones activas de un conductor
    Given el conductor con ID 1 tiene asignaciones activas
    When se ejecuta el SP sp_asignaciones_activas_por_conductor con ID 1
    Then el sistema retorna las asignaciones activas con datos de ruta y vehículo

  Scenario: Asignar conductor sin disponibilidad
    Given el conductor con ID 1 ya tiene una asignación para la misma fecha
    When el coordinador intenta crear otra asignación para el mismo conductor y fecha
    Then el sistema responde con error indicando conflicto de horario

  Scenario: Listar todas las asignaciones
    Given existen asignaciones registradas
    When el coordinador solicita GET /api/asignaciones
    Then el sistema retorna la lista completa con código 200
```
