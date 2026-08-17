# RESUMEN — Lighthouse (Bloque C.5)

**Herramienta:** Lighthouse 13.4.1 | **Configuración:** perfil móvil, throttling Slow 4G (lighthouserc.js)

## Resultados por corrida

| Corrida | Archivo | Performance | Accessibility | Best Practices | SEO |
|---|---|---|---|---|---|
| 1 (móvil) | `lhci-20260730-2115.json` | 100 | 95 | 100 | 90 |
| 2 (móvil) | `lhci-20260730-2117.json` | 100 | 95 | 100 | 90 |

> Las corridas adicionales planificadas (m1–m3 y d1–d3, dependencia K3) se
> ejecutarán contra la URL pública HTTPS una vez disponible (tarea A5 de
> Alejandro) con `scripts/lighthouse/run-lighthouse.sh`.

## Cumplimiento de umbrales

| Categoría | Umbral | Resultado | Cumple |
|---|---|---|---|
| Performance | >= 80 | 100 | Sí |
| Accessibility | >= 90 | 95 | Sí |
| Best Practices | >= 90 | 100 | Sí |
| SEO | >= 90 | 90 | Sí |

## Interpretación

El frontend Angular cumple los cuatro umbrales. El punto más cercano al límite es
SEO (90), atribuible a metadatos básicos del SPA; se documenta como mejora menor.

## Reproducibilidad

| Artefacto | Fuente | Script |
|---|---|---|
| Corridas 1–2 | `lhci-*.json` | `npx lighthouse` (ver `lighthouserc.js`) |
| Corridas 3–6 (pendientes) | — | `scripts/lighthouse/run-lighthouse.sh` |
| Procedencia | — | `docs/mediciones/DATA-PROVENANCE.md` |