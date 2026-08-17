# Evidencia de elicitación de requisitos — SGROAS

## 1. Técnicas de elicitación aplicadas

| # | Técnica | Fecha | Participantes | Resultado |
|---|---------|-------|---------------|-----------|
| 1 | Entrevista semiestructurada | 2026-05-10 | Coordinador de operaciones, Administrador | Requisitos iniciales: autenticación, CRUD conductores, reportes básicos |
| 2 | Observación directa | 2026-05-15 | Personal de seguridad, Conductores | Flujo operativo de asignación de rutas y registro de incidentes |
| 3 | Cuestionario online (Google Forms) | 2026-05-20 | 12 empleados de la cooperativa | Priorización MoSCoW de funcionalidades; SUS baseline |
| 4 | Revisión de documentación existente | 2026-05-25 | — | Políticas de seguridad, formatos de incidentes, organigrama |
| 5 | Taller de validación (JAD) | 2026-06-01 | Coordinador, Seguridad, Administrador | Validación de historias de usuario y casos de uso |

## 2. Stakeholders identificados

| Stakeholder | Rol en el proyecto | Necesidades principales |
|-------------|-------------------|------------------------|
| Administrador del sistema | Usuario final, decisor | Gestión de usuarios, control de acceso, reportes |
| Coordinador de operaciones | Usuario final | Gestión de conductores, rutas, vehículos, asignaciones |
| Personal de seguridad | Usuario final | Registro y consulta de incidentes |
| Conductores | Usuarios indirectos | Información de rutas asignadas |
| Director de la cooperativa | Patrocinador | Reportes ejecutivos, estadísticas |
| Equipo de desarrollo (PFC) | Desarrolladores | Requisitos claros, trazabilidad, calidad |

## 3. Documentos de entrada analizados

1. **Organigrama institucional**: Estructura jerárquica de la cooperativa de transporte.
2. **Formatos de incidentes en papel**: Formato physical actual para registro de incidentes (migrado a digital).
3. **Política de seguridad informática**: Directrices de control de acceso y protección de datos.
4. **Flujos operativos de asignación**: Proceso manual de asignación de conductores a rutas.
5. **Requisitos previos del proyecto (Entrega 1A)**: Documento de requisitos iniciales con 15 requisitos.

## 4. Decisiones de diseño derivadas

| Decisión | Origen | ADR |
|----------|--------|-----|
| JWT en cookie HttpOnly (no localStorage) | Entrevista: seguridad de sesiones | ADR-003 |
| Procedimientos almacenados para reportes | Observación: complejidad de consultas | ADR-004 |
| Redis como caché distribuida | Cuestionario: rendimiento percibido | ADR-005 |
| Angular en frontend (no Vue.js) | Revisión técnica: familiaridad del equipo | ADR-006 |

## 5. Cambios derivados de la elicitación

| Iteración | Cambio | Requisito afectado | Fuente |
|-----------|--------|-------------------|--------|
| Entrega 1A → 1B | Agregado rate limiting en login | REQ-NF-005 | Observación de vulnerabilidad |
| Entrega 1B → 2 | Migrado de Bearer token a cookie HttpOnly | REQ-F-001 | Entrevista de seguridad |
| Entrega 2 → 3 | Agregado caché Redis con TTL configurable | REQ-NF-003 | Cuestionario de rendimiento |
| Entrega 3 → Final | Agregado CRUD de rutas, vehículos, asignaciones, incidentes | REQ-F-015 a REQ-F-034 | Taller JAD |

## 6. Criterios de aceptación verificados

- Todas las historias de usuario (HU-001 a HU-008) tienen criterios Gherkin verificables.
- Todos los casos de uso (CU-001 a CU-006) están en formato Cockburn niveles 1–4.
- La matriz de trazabilidad (`docs/trazabilidad/matriz.csv`) cubre el 100 % de los requisitos.
