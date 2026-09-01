# RESUMEN — ZAP Baseline (Bloque C.2)

**Estado:** pendiente de URL pública HTTPS (dependencia de la tarea A5 de Alejandro).

## Dependencia

El escaneo de seguridad activo con OWASP ZAP requiere una URL pública estable con
certificado válido y `/actuator/health` UP. Una vez Alejandro despliegue el backend
y notifique la URL, Kevin ejecuta:

```bash
scripts/zap/run-zap.sh https://TU-URL
```

## Información del conjunto de pruebas

| Campo | Valor |
|---|---|
| Herramienta | OWASP ZAP baseline (`ghcr.io/zaproxy/zaproxy`) |
| Escaneo | `zap-baseline.py` con reglas pasivas |
| Salida | `docs/mediciones/sec/zap/zap-baseline-<fecha>.html` |
| Reporte máquina | `zap-baseline-<fecha>.md` |
| Alertas objetivo | 0 High / 0 Medium como criterio de aceptación |

## Interpretación prevista

- Las alertas `High/Medium` se revisan contra los controles OWASP implementados
  (A01, A02, A03, A05, A07, A09; ver `docs/mediciones/sec/`).
- El cumplimiento se reporta en la tabla de seguridad del informe (Cap. 8) y en
  el RESÚMEN del capítulo.

## Reproducibilidad

| Artefacto | Fuente | Script |
|---|---|---|
| Reporte HTML | ejecución de ZAP | `scripts/zap/run-zap.sh` |
| Procedencia | — | `docs/mediciones/DATA-PROVENANCE.md` |