# Checklist INCOSE — Calidad de requisitos (Guide to Writing Requirements v4)

Verificación de las 15 características C1–C15 del INCOSE para cada requisito del SRS v1.0.0 de SGROAS.

## Leyenda

- **Sí**: Cumple plenamente
- **Parcial**: Cumple parcialmente, requiere refinamiento
- **No**: No cumple
- **N/A**: No aplica al tipo de requisito

---

## Requisitos funcionales

| ID | C1 Necesario | C2 Apropiado | C3 Sin ambigüedad | C4 Completo | C5 Singular | C6 Factible | C7 Verificable | C8 Correcto | C9 Conforme | C10 Consistente | C11 Comprensible | C12 Validable | C13 Trazable | C14 Único | C15 Cerrado |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| REQ-F-001 | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí |
| REQ-F-002 | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí |
| REQ-F-003 | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí |
| REQ-F-004 | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí |
| REQ-F-005 | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí |
| REQ-F-006 | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí |
| REQ-F-007 | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí |
| REQ-F-008 | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí |
| REQ-F-009 | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí |
| REQ-F-010 | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí |
| REQ-F-011 | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí |
| REQ-F-012 | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí |
| REQ-F-013 | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí |
| REQ-F-014 | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí |

## Requisitos no funcionales

| ID | C1 Necesario | C2 Apropiado | C3 Sin ambigüedad | C4 Completo | C5 Singular | C6 Factible | C7 Verificable | C8 Correcto | C9 Conforme | C10 Consistente | C11 Comprensible | C12 Validable | C13 Trazable | C14 Único | C15 Cerrado |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| REQ-NF-001 | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí |
| REQ-NF-002 | Sí | Sí | Parcial | Parcial | Sí | Sí | Parcial | Parcial | Sí | Sí | Sí | Parcial | Sí | Sí | Sí |
| REQ-NF-003 | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí |
| REQ-NF-004 | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí |
| REQ-NF-005 | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí |
| REQ-NF-006 | Sí | Sí | Sí | Parcial | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí | Sí |

## Observaciones por requisito

### REQ-NF-002 — Cifrado en tránsito (Parcial en C3, C4, C7, C8, C12)

- **C3 Sin ambigüedad**: El enunciado declara "TLS v1.3 con suites AEAD" pero el entorno de desarrollo local opera en HTTP plano (evidencia `A02-criptografia.txt`). Se recomienda precisar que TLS aplica exclusivamente a producción detrás de reverse proxy / load balancer con termination TLS.
- **C4 Completo**: Falta especificar el comportamiento cuando el cliente intenta conexión sin TLS (¿rechazo explícito? ¿fallback?).
- **C7 Verificable**: La verificación mediante `curl -v` solo es factible en entorno con TLS configurado; en desarrollo local no es reproducible.
- **C8 Correcto**: La evidencia actual no respalda el enunciado. Se requiere re-ejecutar la medición en entorno con TLS habilitado o ajustar el enunciado para reflejar la realidad del despliegue.
- **C12 Validable**: La validación requiere acceso a un entorno con TLS; actualmente solo se dispone de evidencia en HTTP plano.

### REQ-NF-006 — Cobertura de pruebas (Parcial en C4)

- **C4 Completo**: El umbral declarado en el SRS v0.9.0-rc es ≥ 60 % para Tercera Entrega. La cobertura real alcanzada es 98,8 % (instrucciones) y 85,4 % (ramas). Se recomienda actualizar el umbral a ≥ 70 % para la Entrega Final, alineado con el alcance de las pruebas automatizadas.

## Conclusión

- **17 de 20 requisitos**: Sí en las 15 características (100 %).
- **REQ-NF-002**: Parcial en 5 de 15 características (requiere ajuste de enunciado o evidencia en entorno TLS).
- **REQ-NF-006**: Parcial en 1 de 15 características (umbral obsoleto, actualizable a ≥ 70 %).
- **Cobertura total**: 295 de 300 casillas en Sí (98,3 %).
