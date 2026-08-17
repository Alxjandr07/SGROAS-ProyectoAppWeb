# Checklist — Estándares Empíricos en Ingeniería de Software (Ralph et al., 2021)

**Documento:** Evaluación empírica del sistema SGROAS (mediciones k6, SUS, JaCoCo, Lighthouse, ZAP)
**Verificación:** 16 de agosto de 2026
**Equipo:** Kevin (Castro Espinoza) con revisión del equipo SGROAS

Referencia: Ralph, P., et al. (2021). *Empirical Standards for Software Engineering Research*.
arXiv:2010.03525v2. Adaptado al tipo de estudio: *benchmarking / evaluación de sistema*.

## 1. Introducción

- [x] El estudio se sitúa en el contexto del sistema SGROAS y su protocolo experimental (Bloque C).
- [x] Se declaran los objetivos de evaluación: rendimiento (C.1), seguridad (C.2), usabilidad (C.3), cobertura (C.4) y calidad web (C.5).
- [x] Se enuncia que la evaluación compara el sistema contra umbrales definidos (p95 < 200 ms, error < 1 %, SUS >= 70, cobertura >= 70 %, Lighthouse >= 80/90/90/90).

## 2. Metodología

### 2.1 Diseño

- [x] El diseño es de *benchmarking observacional* con corridas repetidas.
- [x] Se definen métricas y umbrales a priori (documentados en `docs/mediciones/`).
- [x] Se documenta el entorno de ejecución (k6 0.57, 50 VUs, 30 s; ver `k6/opts.js`).

### 2.2 Sujetos / participantes

- [x] El estudio SUS recluta 10 participantes (P01–P10) con datos demográficos anonimizados.
- [x] Se obtiene consentimiento informado y se respeta el anonimato.
- [x] Se documenta el instrumento (SUS de Brooke, 1996) y el procedimiento.

### 2.3 Tareas y procedimiento

- [x] Las tareas evaluadas se enumeran (login, listado de conductores, CRUDs).
- [x] El protocolo de medición es reproducible con scripts versionados.

## 3. Análisis de datos

- [x] Se usan estadísticos descriptivos: media, mediana, percentiles, desviación típica.
- [x] Se calculan intervalos de confianza al 95 % con t de Student (n = corridas / participantes).
- [x] Se definen a priori los contrastes no paramétricos (U de Mann-Whitney, Wilcoxon, d de Cliff) para el contraste cache caliente/frío.
- [x] Se reportan las limitaciones del tamaño muestral.

## 4. Hallazgos

- [x] Los resultados se reportan con sus métricas y valores (tablas regenerables).
- [x] Las figuras se generan con scripts y paleta accesible (Okabe-Ito).
- [x] No se omiten resultados desfavorables (p. ej., SUS 63 < 70 se documenta como área de mejora).

## 5. Discusión

- [x] Los hallazgos se interpretan contra las preguntas de investigación (RQ1–RQ4).
- [x] Se comparan con la literatura y con los umbrales del proyecto.
- [x] Se discuten hallazgos inesperados (p. ej., primera corrida k6 más lenta).

## 6. Amenazas a la validez

- [x] Validez de constructo, interna, externa y conclusión, con mitigaciones (ver capítulo 10 del informe).
- [x] Se declara que los datos provienen de mediciones reales y reproducibles.

## 7. Reproducibilidad

- [x] Todos los scripts y datos crudos están versionados en el repositorio.
- [x] El mapeo dato → script → figura consta en `docs/mediciones/DATA-PROVENANCE.md`.

## Estado

- [x] Checklist revisado y aceptado por el equipo el 16 de agosto de 2026.
