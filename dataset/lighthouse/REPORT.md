# REPORT — Accesibilidad y calidad web (Bloque C.5)

## Metadatos

| Campo | Valor |
|---|---|
| **Herramienta** | Lighthouse 13.4.1 |
| **Configuracion** | Perfil movil, throttling de red Slow 4G (150 ms RTT, 1600 Kbps, CPU 4x) |
| **Servidor** | Estatico con compresion gzip (`frontend/serve-gzip.js`, configuracion equivalente a produccion) |
| **URL auditada** | `http://localhost:4200/` (login del frontend Angular) |
| **Umbrales** | Declarados en `lighthouserc.js` |

## Resultados por corrida

| Corrida | Archivo | Performance | Accessibility | Best Practices | SEO |
|---|---|---|---|---|---|
| 1 | `lhci-20260730-2115.json` | 100 | 95 | 100 | 90 |
| 2 | `lhci-20260730-2117.json` | 100 | 95 | 100 | 90 |

## Cumplimiento de umbrales

| Categoria | Umbral | Resultado | Cumple |
|---|---|---|---|
| Performance | >= 80 | 100 | Si |
| Accessibility | >= 90 | 95 | Si |
| Best Practices | >= 90 | 100 | Si |
| SEO | >= 90 | 90 | Si |
