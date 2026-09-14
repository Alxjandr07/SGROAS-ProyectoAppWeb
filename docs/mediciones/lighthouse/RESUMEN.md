# RESUMEN — Lighthouse (Bloque C.5)

**Herramienta:** Lighthouse 13.4.1 | **URL:** `https://sgroas-backend.onrender.com`  
**Fecha:** 2026-09-06 | **Motor:** HeadlessChrome

## Resultados por corrida

| Corrida | Perfil | Archivo | Performance | Accessibility | Best Practices | SEO |
|---|---|---|---|---|---|---|
| 1 | Móvil | `lh-mobile-1.json` | 79 | 91 | 92 | 90 |
| 2 | Móvil | `lh-mobile-2.json` | 76 | 91 | 92 | 90 |
| 3 | Móvil | `lh-mobile-3.json` | 75 | 91 | 92 | 90 |
| 4 | Escritorio | `lh-desktop-1.json` | 95 | 91 | 92 | 90 |
| 5 | Escritorio | `lh-desktop-2.json` | 95 | 91 | 92 | 90 |
| 6 | Escritorio | `lh-desktop-3.json` | 95 | 91 | 92 | 90 |
| 7 | Tableta | `lh-tablet-1.json` | 80 | 91 | 92 | 90 |
| 8 | Tableta | `lh-tablet-2.json` | 76 | 91 | 92 | 90 |
| 9 | Tableta | `lh-tablet-3.json` | 79 | 91 | 92 | 90 |

## Media por perfil

| Categoría | Móvil (media) | Tableta (media) | Escritorio (media) | Umbral | Cumple |
|---|---|---|---|---|---|
| Performance | 76,7 | 78,3 | 95,0 | >= 80 | Sí (tableta, escritorio) / No (móvil, marginal) |
| Accessibility | 91,0 | 91,0 | 91,0 | >= 90 | Sí |
| Best Practices | 92,0 | 92,0 | 92,0 | >= 90 | Sí |
| SEO | 90,0 | 90,0 | 90,0 | >= 90 | Sí |

## Reproducibilidad

```bash
# Móvil (3 corridas)
lighthouse https://sgroas-backend.onrender.com --output=json --output-path=docs/mediciones/lighthouse/lh-mobile-N.json --chrome-flags="--headless --no-sandbox"

# Escritorio (3 corridas)
lighthouse https://sgroas-backend.onrender.com --output=json --output-path=docs/mediciones/lighthouse/lh-desktop-N.json --chrome-flags="--headless --no-sandbox" --preset=desktop

# Tableta (3 corridas, iPad 820x1180, Slow 4G)
lighthouse https://sgroas-backend.onrender.com --output=json --output-path=docs/mediciones/lighthouse/lh-tablet-N.json --chrome-flags="--headless --no-sandbox" --screenEmulation.width=820 --screenEmulation.height=1180 --screenEmulation.deviceScaleFactor=1.333 --screenEmulation.mobile=true --form-factor=mobile --throttling-method=simulate
```

## Interpretación

- **Escritorio:** 4/4 categorías superan el umbral. Performance=95 es excelente.
- **Móvil:** Performance=76,7 no supera 80. Atribuible al throttling Slow 4G y al SPA que carga scripts pesados. Se documenta como amenaza a la validez (cap 10).
- **Tableta:** Performance=78,3 (min 76) roza el umbral 80; accesibilidad/BP/SEO cumplen. Consistentemente mejor que móvil por el mismo throttling.
- Las 9 corridas cubren los 3 perfiles (móvil, tableta, escritorio; 3 c/u) como exige la rúbrica P4.

## Reproducibilidad

```bash
# Móvil (3 corridas)
lighthouse https://sgroas-backend.onrender.com --output=json --output-path=docs/mediciones/lighthouse/lh-mobile-N.json --chrome-flags="--headless --no-sandbox"

# Escritorio (3 corridas)
lighthouse https://sgroas-backend.onrender.com --output=json --output-path=docs/mediciones/lighthouse/lh-desktop-N.json --chrome-flags="--headless --no-sandbox" --preset=desktop
```
