# Changelog de Requisitos — SGROAS

Formato basado en [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## [v1.0.0] — 2026-08-17

### Added

- REQ-F-015 a REQ-F-019: CRUD de rutas de transporte (listado, consulta, alta, actualizacion, eliminacion logica)
- REQ-F-020 a REQ-F-024: CRUD de vehiculos (listado, consulta, alta, actualizacion, desactivacion)
- REQ-F-025 a REQ-F-029: CRUD de asignaciones ruta-conductor-vehiculo (listado, consulta, alta, actualizacion, eliminacion)
- REQ-F-030 a REQ-F-034: CRUD de incidentes (listado, consulta, alta, actualizacion, eliminacion logica)
- REQ-F-035: Reporte de rendimiento de rutas (sp_reporte_rendimiento_rutas)
- REQ-F-036: Estadisticas generales del sistema (fn_estadisticas_generales)
- REQ-F-037: Alerta de licencias por vencer (fn_licencias_por_vencer)
- REQ-F-038: Reporte de incidentes por gravedad (sp_incidentes_por_gravedad)
- REQ-F-039: Reporte de incidentes por rango de fechas (sp_obtener_incidentes_por_rango)
- REQ-F-040: Consulta de asignaciones activas por conductor (sp_asignaciones_activas_por_conductor)
- REQ-NF-007: Documentacion OpenAPI 3.0 en /api/docs
- REQ-NF-008: Error handling con ProblemDetails (RFC 7807)
- REQ-NF-009: CORS configurado por origen
- REQ-NF-010: CORS configurado por rol
- 8 historias de usuario (HU-001 a HU-008) con criterios Gherkin
- 6 casos de uso (CU-001 a CU-006) en formato Cockburn niveles 1-4
- Checklist INCOSE C1-C15 para los 20 requisitos del SRS
- Evidencia de elicitation: entrevistas, observaciones, cuestionario, taller JAD
- Matriz de priorizacion MoSCoW para 50 requisitos

### Modified

- REQ-NF-002: Aclarado que TLS aplica exclusivamente a produccion (entorno local opera en HTTP plano)
- REQ-NF-006: Umbral de cobertura actualizado de >= 60% a >= 70% para Entrega Final
- Matriz de trazabilidad: REQ-F-002 a REQ-F-014 actualizados de "implementado" a "verificado"
- SRS consolidado a v1.0.0 con 44 requisitos funcionales + 6 no funcionales

### Removed

- Ninguno

## [v0.9.0-rc] — 2026-07-24

### Added

- REQ-F-010 a REQ-F-014: CRUD de usuarios (gestion de cuentas del sistema)
- REQ-F-005 a REQ-F-009: CRUD de conductores (gestion de flota vehicular)
- REQ-F-001 a REQ-F-004: Autenticacion JWT (login, register, refresh, logout)
- REQ-NF-001: Cabeceras de seguridad HTTP (HSTS, CSP, X-Frame-Options)
- REQ-NF-002: Cifrado TLS v1.3 con suites AEAD
- REQ-NF-003: Rendimiento con p95 < 200ms (cache caliente)
- REQ-NF-004: Proteccion contra inyeccion SQL
- REQ-NF-005: Rate limiting en login (6 intentos, 429)
- REQ-NF-006: Cobertura JaCoCo >= 60%

### Modified

- REQ-F-001: Migrado de Bearer token a cookie HttpOnly + Secure + SameSite=Strict
- REQ-NF-004: Reforzado con validacion Jakarta + ProblemDetails RFC 7807

### Removed

- Ninguno

## [v0.7.0] — 2026-06-14

### Added

- REQ-F-001 a REQ-F-004: Autenticacion JWT basica (Bearer token)
- REQ-F-005 a REQ-F-009: CRUD de conductores
- REQ-NF-003: Cache Redis en listado de conductores

## [v0.3.0] — 2026-06-04

### Added

- Requisitos iniciales del sistema (14 RF + 6 RNF)
- Definicion de actores y modulos del sistema
- Criterios de aceptacion iniciales
# CHANGELOG de Requisitos — SGROAS

## Entrega 2 — Nuevas entidades, reportes y documentacion

### Agregado

- **REQ-006:** CRUD completo de vehiculos (VehiculoController, VehiculoService, VehiculoRepository)
- **REQ-007:** CRUD completo de rutas (RutaController, RutaService, RutaRepository)
- **REQ-008:** CRUD completo de asignaciones ruta-conductor-vehiculo (AsignacionRutaController, AsignacionRutaService, AsignacionRutaRepository)
- **REQ-009:** CRUD completo de incidentes (IncidenteController, IncidenteService, IncidenteRepository)
- **REQ-010:** Stored Procedure `sp_obtener_incidentes_por_rango` — reporte de incidentes por rango de fechas
- **REQ-011:** Stored Procedure `sp_incidentes_por_gravedad` — agregacion de incidentes por gravedad
- **REQ-012:** Stored Procedure `sp_asignaciones_activas_por_conductor` — asignaciones activas por conductor
- **REQ-013:** Stored Procedure `sp_vehiculos_en_mantenimiento` — listado de vehiculos en mantenimiento
- **REQ-014:** Stored Procedure `sp_reporte_rendimiento_rutas` — rendimiento por ruta con metricas
- **REQ-015:** Stored Procedure `fn_licencias_por_vencer` — alerta de licencias proximas a vencer
- **REQ-016:** Stored Procedure `fn_estadisticas_generales` — dashboard de KPIs del sistema
- **REQ-019:** Funcion de licencias por vencer (integrada con fn_licencias_por_vencer)
- **REQ-020:** Migraciones Flyway V3 (nuevas tablas) y V4 (datos semilla)

### Documentacion

- `docs/basedatos/CATALOGO-SP.md` — Catalogo completo de stored procedures
- `docs/adr/adr-001.md` al `adr-006.md` — 6 ADRs formato Nygard
- `docs/mediciones/DATA-DICTIONARY.md` — Actualizado con nuevas entidades y endpoints
- `docs/trazabilidad/matriz.csv` — Matriz de trazabilidad requisitos vs implementacion
- `docs/requisitos/CHANGELOG-REQ.md` — Este archivo

### Cambios en Base de Datos

- Nueva tabla: `vehiculos` con indices, constraints y trigger
- Nueva tabla: `rutas` con indices, constraints y trigger
- Nueva tabla: `asignacion_rutas` con FK a conductores, vehiculos y rutas
- Nueva tabla: `incidentes` con FK a asignacion_rutas y tipos enumerados
- 7 stored procedures/functions para reportes y agregaciones
- Datos semilla para todas las nuevas tablas

### Cambios en API

- `POST/GET /api/vehiculos` — CRUD vehiculos
- `POST/GET /api/rutas` — CRUD rutas
- `POST/GET /api/asignaciones` — CRUD asignaciones
- `POST/GET /api/incidentes` — CRUD incidentes
