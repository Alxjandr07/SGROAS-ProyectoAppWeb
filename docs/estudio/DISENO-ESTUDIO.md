# Diseño del estudio empírico — SGROAS

**Documento de referencia:** `docs/informe-final/capitulos/cap8-evaluacion.tex`
y `cap9-discusion.tex`.
**Declarado:** a priori, antes de ejecutar las mediciones (transparencia
empírica). Los umbrales y RQ no se ajustaron después de ver los datos.

## Objetivo y metas (GQM)

| Foco | pregunta | Métrica | Umbral a priori | Método |
|---|---|---|---|---|
| Rendimiento | RQ1: ¿El sistema cumple la latencia aceptable bajo carga? | p95 de respuesta, error rate | p95 < 200 ms; error < 1 % | k6, 50 VUs / 30 s, 3 corridas |
| Usabilidad | RQ2: ¿La usabilidad percibida alcanza el umbral? | SUS (Brooke, 1996) | media >= 70 (Bangor et al., 2009) | 10 participantes, consentimiento informado |
| Calidad web | RQ3: ¿La interfaz web cumple estándares? | Lighthouse: performance/accessibility/best-practices/SEO | 80/90/90/90 | Lighthouse CI, móvil, Slow 4G |
| Seguridad | RQ4: ¿Hay vulnerabilidades OWASP Top 10 críticas? | OWASP ZAP baseline + auditoría manual | sin alertas de severidad Alta/Crítica | ZAP baseline + scripts de prueba A01–A09 |

## Preguntas de investigación (RQ)

1. **RQ1 (Rendimiento):** ¿SGROAS mantiene latencias aceptables (p95 < 200 ms)
   en un escenario con 50 usuarios virtuales concurrentes?
2. **RQ2 (Usabilidad):** ¿La usabilidad percibida por usuarios finales alcanza
   un nivel aceptable (SUS >= 70)?
3. **RQ3 (Calidad web):** ¿La aplicación web cumple estándares de rendimiento,
   accesibilidad, mejores prácticas y SEO?
4. **RQ4 (Seguridad/robustez):** ¿El sistema está libre de vulnerabilidades
   críticas del OWASP Top 10 y mantiene cobertura de código suficiente?

## Diseño

- **Tipo:** estudio de caso único (un sistema, producción-like), replicación de
  carga en 3 corridas (decisión recomendada por baselines empíricos).
- **Muestra SUS:** n = 10 participantes (conveniencia, estudiantes/egresados de
  Ingeniería de Software), muestra voluntaria, sin compensación.
- **Test de carga:** k6 0.57, 50 VUs, duración 30 s, escenario de lectura
  (listados y detalle), endpoints autenticados contra entorno de staging.
- **Análisis inferencial:** IC 95 % con t de Student (n pequeño); se reporta
  media, DT, EE, mínimo y máximo por corrida.
- **Medición del tamaño del efecto:** no aplica (comparación contra umbral, no
  entre grupos).

## Umbrales y justificación

- p95 < 200 ms: estándar web (percepción de respuesta < 200 ms).
- SUS >= 70: punto de corte "aceptable" según Bangor et al. (2009).
- Lighthouse 80/90/90/90: defaults de la herramienta para performance y
  accesibilidad estricta (público amplio).
- JaCoCo >= 90 % líneas: objetivo interno del equipo (decisión de go/no-go
  documentada en ADR).

## Material experimental

- Cuestionario SUS (10 ítems Likert), presentado en `docs/mediciones/sus/`.
- Script de carga: `k6/script.js` + `k6/opts.js`.
- Auditorías automáticas: `scripts/lighthouse/`, `scripts/zap/`.

## Limitaciones declaradas

- n = 10 limita la precisión del SUS (IC 95 % ancho).
- El escenario de carga cubre rutas de lectura, no el flujo transaccional
  completo (escritura de incidentes).
- ZAP depende de URL pública estable (dependencia de despliegue, bloque A).