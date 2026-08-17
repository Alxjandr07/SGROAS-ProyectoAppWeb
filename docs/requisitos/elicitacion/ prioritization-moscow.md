# Matriz de priorización MoSCoW — SGROAS v1.0.0

## Definición

- **Must (M)**: Requisito obligatorio; el sistema no funciona sin él.
- **Should (S)**: Requisito importante; se incluye si hay tiempo, pero no bloquea la entrega.
- **Could (C)**: Requisito deseable; se incluye si sobra tiempo y no hay costo adicional significativo.
- **Won't (W)**: No se implementa en esta entrega; se pospone a futuras versiones.

## Requisitos funcionales

| ID | Requisito | MoSCoW | Justificación |
|----|-----------|--------|---------------|
| REQ-F-001 | Inicio de sesión con JWT | M | Acceso obligatorio al sistema |
| REQ-F-002 | Registro de usuario | M | Administrador debe crear cuentas |
| REQ-F-003 | Renovación de token | M | Evitar reautenticación frecuente |
| REQ-F-004 | Cierre de sesión | M | Revocar sesiones activas |
| REQ-F-005 | Listado paginado de conductores | M | Consulta frecuente del coordinador |
| REQ-F-006 | Consulta de conductor por ID | M | Edición y eliminación requieren consulta |
| REQ-F-007 | Alta de conductor | M | Mantenimiento del catálogo |
| REQ-F-008 | Actualización de conductor | M | Corrección de datos |
| REQ-F-009 | Eliminación lógica de conductor | M | Baja sin pérdida de historial |
| REQ-F-010 | Listado paginado de usuarios | M | Gestión de cuentas |
| REQ-F-011 | Consulta de usuario por ID | M | Edición requiere consulta |
| REQ-F-012 | Alta de usuario | M | Creación de cuentas |
| REQ-F-013 | Actualización de usuario | M | Perfil y roles |
| REQ-F-014 | Eliminación lógica de usuario | M | Baja sin pérdida de historial |
| REQ-F-015 | Listado de rutas | M | Planificación de asignaciones |
| REQ-F-016 | Consulta de ruta por ID | M | Edición requiere consulta |
| REQ-F-017 | Alta de ruta | M | Mantenimiento del catálogo |
| REQ-F-018 | Actualización de ruta | M | Corrección de datos |
| REQ-F-019 | Eliminación lógica de ruta | M | Baja sin pérdida de historial |
| REQ-F-020 | Listado de vehículos | M | Inventario de flota |
| REQ-F-021 | Consulta de vehículo por ID | M | Edición requiere consulta |
| REQ-F-022 | Alta de vehículo | M | Registro de nueva flota |
| REQ-F-023 | Actualización de vehículo | M | Datos de mantenimiento |
| REQ-F-024 | Desactivación de vehículo | M | Baja sin pérdida de historial |
| REQ-F-025 | Listado de asignaciones | M | Control operativo |
| REQ-F-026 | Consulta de asignación por ID | M | Edición requiere consulta |
| REQ-F-027 | Alta de asignación | M | Asignación de recursos |
| REQ-F-028 | Actualización de asignación | M | Cambios de última hora |
| REQ-F-029 | Eliminación de asignación | M | Cancelación de ruta |
| REQ-F-030 | Listado de incidentes | M | Consulta de seguridad |
| REQ-F-031 | Consulta de incidente por ID | M | Detalle para investigación |
| REQ-F-032 | Alta de incidente | M | Registro obligatorio |
| REQ-F-033 | Actualización de incidente | M | Corrección de datos |
| REQ-F-034 | Eliminación de incidente | M | Baja lógica |
| REQ-F-035 | Reporte de rendimiento de rutas (SP) | S | Análisis operativo |
| REQ-F-036 | Estadísticas generales (FN) | S | Métricas ejecutivas |
| REQ-F-037 | Licencias por vencer (FN) | S | Alerta preventiva |
| REQ-F-038 | Reporte de incidentes por gravedad (SP) | S | Análisis de seguridad |
| REQ-F-039 | Reporte de incidentes por rango (SP) | S | Análisis temporal |
| REQ-F-040 | Asignaciones activas por conductor (SP) | S | Consulta operativa |

## Requisitos no funcionales

| ID | Requisito | MoSCoW | Justificación |
|----|-----------|--------|---------------|
| REQ-NF-001 | Cabeceras de seguridad HTTP | M | OWASP A05 |
| REQ-NF-002 | Cifrado TLS en producción | M | OWASP A02, confidencialidad |
| REQ-NF-003 | p95 < 200 ms en listado con caché | M | ISO 25010 eficiencia |
| REQ-NF-004 | Protección contra inyección SQL | M | OWASP A03 |
| REQ-NF-005 | Rate limiting en login (6 intentos) | M | OWASP A07 |
| REQ-NF-006 | Cobertura JaCoCo ≥ 70 % | S | Calidad de código |
| REQ-NF-007 | Documentación OpenAPI 3.0 | S | Interoperabilidad |
| REQ-NF-008 | Error handling con ProblemDetails RFC 7807 | M | Estándar de APIs |
| REQ-NF-009 | CORS configurado por origen | M | Seguridad de navegador |
| REQ-NF-010 | CORS configurado por rol | M | Control de acceso por rol |

## Resumen

| Prioridad | Cantidad | Porcentaje |
|-----------|----------|------------|
| Must | 44 | 88 % |
| Should | 6 | 12 % |
| Could | 0 | 0 % |
| Won't | 0 | 0 % |
| **Total** | **50** | **100 %** |
