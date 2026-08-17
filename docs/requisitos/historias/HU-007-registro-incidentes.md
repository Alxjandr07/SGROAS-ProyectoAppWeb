# HU-007: Registro de incidentes

- **Rol:** Seguridad / Coordinador
- **Objetivo:** Registrar y consultar incidentes ocurridos durante las rutas
- **Beneficio:** Mantener un registro estructurado para análisis de seguridad y reportes

## Criterios de aceptación (Gherkin)

```gherkin
Feature: Registro de incidentes
  Scenario: Registrar incidente nuevo
    Given un usuario con rol SEGURIDAD autenticado
    When envía POST /api/incidentes con fecha, ubicación, gravedad y descripción
    Then el sistema crea el incidente y responde 201 Created

  Scenario: Consultar incidentes por rango de fechas
    Given existen incidentes registrados en diferentes fechas
    When se ejecuta el SP sp_obtener_incidentes_por_rango con fecha inicio y fin
    Then el sistema retorna los incidentes dentro del rango especificado

  Scenario: Consultar incidentes por gravedad
    Given existen incidentes con gravedad ALTA, MEDIA y BAJA
    When se ejecuta el SP sp_incidentes_por_gravedad
    Then el sistema retorna el conteo de incidentes agrupados por nivel de gravedad

  Scenario: Listar incidentes con paginación
    Given existen incidentes registrados
    When el usuario solicita GET /api/incidentes?page=0&size=10
    Then el sistema retorna la lista paginada con código 200

  Scenario: Consultar incidente inexistente
    Given no existe un incidente con ID 9999
    When el usuario solicita GET /api/incidentes/9999
    Then el sistema responde 404 Not Found
```
