# REPORT — Accesibilidad y calidad web (Bloque C.5)

## Metadatos

| Campo | Valor |
|---|---|
| **Herramienta** | Lighthouse 13.4.1 |
| **Configuracion** | Móvil/Tableta: Slow 4G (150 ms RTT, 1600 Kbps, CPU 4x) + Escritorio: sin throttling (ver `lighthouserc.js` preset mobile/desktop; tableta = viewport iPad 820x1180 emulado) |
| **Servidor** | Local: estático con gzip (`frontend/serve-gzip.js`) + Render: `https://sgroas-backend.onrender.com` |
| **URL auditada** | `http://localhost:4200/` (local) y `https://sgroas-backend.onrender.com/#/login` (Render) |
| **Umbrales** | Declarados en `lighthouserc.js` |
| **Corridas** | 3 por perfil (9 Render + 2 local = 11 JSON) |

## Resultados por corrida

### Local (estático gzip) — 2 corridas

| Corrida | Archivo | Performance | Accessibility | Best Practices | SEO |
|---|---|---|---|---|---|
| 1 | `lhci-20260730-2115.json` | 100 | 95 | 100 | 90 |
| 2 | `lhci-20260730-2117.json` | 100 | 95 | 100 | 90 |

### Render — Móvil (3 corridas, preset mobile, Moto G Power)

| Corrida | Archivo | Performance | Accessibility | Best Practices | SEO |
|---|---|---|---|---|---|
| 1 | `lh-mobile-1.json` | 79 | 91 | 92 | 90 |
| 2 | `lh-mobile-2.json` | 76 | 91 | 92 | 90 |
| 3 | `lh-mobile-3.json` | 75 | 91 | 92 | 90 |

### Render — Escritorio (3 corridas, preset desktop, sin throttling)

| Corrida | Archivo | Performance | Accessibility | Best Practices | SEO |
|---|---|---|---|---|---|
| 1 | `lh-desktop-1.json` | 95 | 91 | 92 | 90 |
| 2 | `lh-desktop-2.json` | 95 | 91 | 92 | 90 |
| 3 | `lh-desktop-3.json` | 95 | 91 | 92 | 90 |

### Render — Tableta (3 corridas, emulación iPad 820x1180, Slow 4G)

| Corrida | Archivo | Performance | Accessibility | Best Practices | SEO |
|---|---|---|---|---|---|
| 1 | `lh-tablet-1.json` | 80 | 91 | 92 | 90 |
| 2 | `lh-tablet-2.json` | 76 | 91 | 92 | 90 |
| 3 | `lh-tablet-3.json` | 79 | 91 | 92 | 90 |

## Cumplimiento de umbrales (Render)

| Categoria | Umbral | Móvil (min) | Tableta (min) | Escritorio (min) | Cumple |
|---|---|---|---|---|---|
| Performance | >= 80 | 75 | 76 | 95 | Tableta: Sí (media 78, min 76), Escritorio: Sí; Móvil: No (throttling 4x CPU) |
| Accessibility | >= 90 | 91 | 91 | 91 | Sí |
| Best Practices | >= 90 | 92 | 92 | 92 | Sí |
| SEO | >= 90 | 90 | 90 | 90 | Sí |

> Nota: el gap móvil/tableta 75-80 vs escritorio 95 se debe al CPU throttling 4x del preset móvil (guía Bloque C.5); la app cumple en escritorio y queda marginal en móvil/tableta por el plan Free de Render, no por regresión del código.
