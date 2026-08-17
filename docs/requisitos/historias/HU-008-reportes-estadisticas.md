# HU-008: Reportes y estadísticas

- **Rol:** Administrador / Coordinador
- **Objetivo:** Generar reportes de rendimiento de rutas y estadísticas generales del sistema
- **Beneficio:** Tomar decisiones basadas en datos operativos de la cooperativa

## Criterios de aceptación (Gherkin)

```gherkin
Feature: Reportes y estadísticas
  Scenario: Consultar reporte de rendimiento de rutas
    Given existen datos de asignaciones completadas
    When se ejecuta el SP sp_reporte_rendimiento_rutas
    Then el sistema retorna métricas de rendimiento por ruta

  Scenario: Consultar estadísticas generales
    Given existen registros en todas las tablas del sistema
    When se ejecuta la función fn_estadisticas_generales
    Then el sistema retorna conteos totales de conductores, vehículos, rutas, asignaciones e incidentes

  Scenario: Consultar licencias por vencer
    Given existen conductores con licencias próximas a vencer
    When se ejecuta la función fn_licencias_por_vencer con los próximos 30 días
    Then el sistema retorna la lista de conductores cuya licencia vence en el período

  Scenario: Acceso no autorizado a reportes
    Given un usuario sin token de autenticación
    When solicita GET /api/reportes/rendimiento-rutas
    Then el sistema responde 401 Unauthorized
```
