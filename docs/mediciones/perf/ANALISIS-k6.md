# Analisis estadistico de rendimiento (k6)

**Bloque C.1** | Tarea K1 | Fecha de generacion: 2026-08-16

## Metodologia

Se analizaron las 3 corridas archivadas de k6 (50 VUs, 30 s, p95<200 ms). Cada corrida invoca `GET /api/conductores` con autenticacion JWT (cookie HttpOnly + header Bearer). Sobre `http_req_duration (ms)` se calculan media, mediana, percentiles y tasa de error por corrida; entre corridas se calcula la media de las medias con IC 95% mediante t de Student con n = numero de corridas.

## Tabla por corrida

| Corrida | Iteraciones | VUs | Media (ms) | Mediana | p90 | p95 | p99 | Error rate | Checks OK/Fail |
|---|---|---|---|---|---|---|---|---|---|
| k01-run1 | 1465 | 50 | 37.324 | 17.885 | 82.062 | 173.13 | 251.443 | 0.000 | 2930 / 0 |
| k02-run2 | 1500 | 50 | 16.897 | 10.477 | 23.64 | 35.3 | 155.98 | 0.000 | 3000 / 0 |
| k03-run3 | 1500 | 50 | 14.818 | 9.126 | 26.881 | 37.843 | 159.622 | 0.000 | 3000 / 0 |

## Resultado global (n = 3 corridas)

| Metrica | Valor |
|---|---|
| Media de medias (http_req_duration (ms)) | **23.01 ms** |
| Desviacion tipica (entre corridas) | 12.44 ms |
| Error estandar | 7.18 ms |
| t critico (gl = 2, alfa = 0.05) | 4.3027 |
| IC 95% | **[-7.88; 53.91]** ms |
| p95 maximo | 173.13 ms (umbral 200 ms) |
| Tasa de error maxima | 0.000 (objetivo < 0.01) |
| Checks totales | 8930 OK / 0 fallos |

## Contraste cache caliente vs cache frio (analisis a priori)

El protocolo original preveia contrastar dos condiciones de medicion: cache fria (primera peticion tras el arranque) y cache caliente (cache Redis poblada). Las 3 corridas archivadas corresponden a la condicion de cache caliente contra el backend local; no existe una segunda condicion de cache fria comparable (misma carga y mismo entorno). Consecuentemente, y como estipula la guia, el contraste formal (U de Mann-Whitney / Wilcoxon + d de Cliff) queda **definido a priori** en `scripts/perf/nonparametric.py` y se documenta el resultado descriptivo:

| Condicion | n | Media (ms) | p95 (ms) | Error rate |
|---|---|---|---|---|
| Estable (2 corridas mas rapidas) | 2 | 15.86 | 37.84 | 0.000 |
| Inicializacion (1 corrida mas lenta) | 1 | 37.32 | 173.13 | 0.000 |

La primera corrida (k01) muestra una latencia mayor (media de 37.3 ms) que las dos posteriores (medias de 16.9 y 14.8 ms), coherente con la inicializacion de conexiones JIT/HikariCP y del cache Redis. Con una muestra de 3 corridas agregadas no se puede sostener un contraste formal significativo; al disponer de la URL publica (dependencia K3) se medira una condicion de cache fria real y se ejecutaran los contrastes ya implementados.

## Reproducibilidad

| Artefacto | Fuente | Script |
|---|---|---|
| Tabla por corrida | docs/mediciones/perf/kNN-runN.json | scripts/perf-analysis.py |
| Estadisticas | estadisticas.json / estadisticas.csv | scripts/perf-analysis.py |
| Figuras | figuras/*.png | scripts/gen-figuras.py |
| Contraste no parametrico | — | scripts/perf/nonparametric.py |
