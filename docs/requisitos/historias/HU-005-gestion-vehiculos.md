# HU-005: Gestión de vehículos

- **Rol:** Coordinador / Administrador
- **Objetivo:** Registrar y administrar los vehículos de la cooperativa
- **Beneficio:** Contar con un inventario actualizado de la flota vehicular

## Criterios de aceptación (Gherkin)

```gherkin
Feature: Gestión de vehículos
  Scenario: Registrar vehículo nuevo
    Given un coordinador autenticado en el sistema
    When envía POST /api/vehiculos con placa, marca, modelo, año y capacidad
    Then el sistema crea el vehículo y responde 201 Created

  Scenario: Listar vehículos con paginación
    Given existen vehículos registrados en el sistema
    When el coordinador solicita GET /api/vehiculos?page=0&size=10
    Then el sistema retorna la lista paginada con código 200

  Scenario: Consultar vehículos en mantenimiento
    Given existen vehículos con estado EN_MANTENIMIENTO
    When se ejecuta el SP sp_vehiculos_en_mantenimiento
    Then el sistema retorna la lista de vehículos en mantenimiento

  Scenario: Actualizar vehículo
    Given un vehículo con ID 1 registrado en el sistema
    When el coordinador envía PUT /api/vehiculos/1 con la capacidad actualizada
    Then el sistema actualiza y responde 200 OK

  Scenario: Desactivar vehículo
    Given un vehículo con ID 1 registrado en el sistema
    When el coordinador envía DELETE /api/vehiculos/1
    Then el sistema marca el vehículo como inactivo y responde 204 No Content
```
