# REPORT — Rendimiento del endpoint GET /api/conductores (Bloque C.1)

## Metadatos de la corrida

| Campo | Valor |
|---|---|
| **Fecha** | 2026-07-29 (corridas) |
| **Commit** | `62bf8fa` |
| **Herramienta** | k6 (config en `k6/opts.js`) |
| **Configuracion** | 50 VUs, duracion 30 s, ramp-up en `k6/opts.js` |
| **Endpoint** | `GET /api/conductores` (protegido, cache Redis caliente) |
| **Semilla** | No aplica (benchmark HTTP, sin generacion aleatoria) |
| **Archivos crudos** | `docs/mediciones/perf/k01-run1.json`, `k02-run2.json`, `k03-run3.json` |

## Resultados por corrida (tiempo de respuesta en ms)

| Corrida | Archivo | Reqs | avg | med | p50 | p90 | p95 | p99 | Errores |
|---|---|---|---|---|---|---|---|---|---|
| 1 | `k01-run1.json` | 1466 | 37,26 | 17,88 | 17,88 | 81,30 | 173,15 | 251,54 | 0 |
| 2 | `k02-run2.json` | 1501 | 16,82 | 10,48 | 10,48 | 23,55 | 34,82 | 155,99 | 0 |
| 3 | `k03-run3.json` | 1501 | 14,70 | 9,13 | 9,13 | 26,88 | 36,32 | 156,97 | 0 |

## Estadistica agregada (n = 3 corridas independientes)

| Metrica | Media | DT | IC 95 % |
|---|---|---|---|
| Tiempo de respuesta (avg) | 22,93 ms | 12,46 | [-8,02; 53,87] |
| Percentil p95 | 81,43 ms | 79,44 | [-115,90; 278,77] |
| Percentil p99 | 188,16 ms | — | — |
| Throughput | 49,64 rps | — | 48,87–50,03 rps por corrida |

- **Total de peticiones HTTP:** 4468 (1466 + 1501 + 1501)
- **Verificaciones (checks):** 8930 exitosas (4465 por cada uno de los 2 checks × 3 corridas), **0 fallidas**
- **Tasa de error HTTP ≥ 500:** 0,00 % (`http_req_failed` = 0 en las tres corridas)
- **Checks configurados:** `status es 200` y `tiempo respuesta < 500ms` — 100 % de cumplimiento

## Umbrales

| Umbral objetivo | Resultado |
|---|---|
| p95 < 200 ms con cache caliente | Cumplido (p95 medio 81,43 ms; maximo 173,15 ms) |
| p95 < 500 ms con cache frio | Aplica en corridas frias; la primera peticion tras expiracion TTL excede el p95 caliente |
| Tasa de error 0 % | Cumplido |

## Interpretacion

La corrida 1 presenta p95 mayor (173,15 ms) atribuible al arranque del cache
caliente y a la primera consulta tras el TTL; las corridas 2 y 3 estabilizan el
p95 por debajo de 40 ms. Las tres corridas cumplen el umbral de 200 ms
declarado en `k6/opts.js`.
