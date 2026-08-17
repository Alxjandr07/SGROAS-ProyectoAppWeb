# Intervalo de confianza bootstrap — rendimiento k6

**Tarea K1** | generado 2026-08-17 | metodo: bootstrap percentil, 10000 remuestras, semilla 20260817.

Medias de duracion por corrida: 14.82, 16.90, 37.32 ms.

| Metrica | Valor |
|---|---|
| Media observada | 23.01 ms |
| IC 95% bootstrap | **[14.82; 37.32]** ms |

## Lectura

El intervalo bootstrap es mas amplio que el t de Student (n = 3), propio de un remuestreo sobre pocas observaciones; coincide en no descartar valores por debajo de cero y en que el p95 maximo (173.13 ms) cumple el umbral de 200 ms. Confirma la conclusion de cumplimiento expresada en ANALISIS-k6.md.

## Reproducibilidad

| Artefacto | Script |
|---|---|
| bootstrap.json | scripts/perf-bootstrap.py |
| Datos fuente | docs/mediciones/perf/k01..k03-run*.json |
| Estadistica t | scripts/perf-analysis.py |
