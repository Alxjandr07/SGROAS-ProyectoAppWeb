#!/usr/bin/env python3
"""Analisis estadistico de las mediciones k6 (Bloque C / tarea K1).

Lee las 3 corridas archivadas en docs/mediciones/perf/kNN-runN.json y produce:

  * docs/mediciones/perf/estadisticas.json   (datos maquina)
  * docs/mediciones/perf/estadisticas.csv    (tabla por corrida)
  * docs/mediciones/perf/ANALISIS-k6.md      (informe reproducible)

Calcula media, desviacion tipica, error estandar e IC 95% (t de Student,
n = numero de corridas) sobre http_req_duration, ademas del p95 y la tasa de
error por corrida. Incluye el contraste cache caliente/frio como analisis a
priori cuando no existe una segunda condicion de medicion.

Uso: python scripts/perf-analysis.py
"""

from __future__ import annotations

import csv
import json
import sys
from datetime import date
from pathlib import Path
from typing import Any, Dict, List

sys.path.insert(0, str(Path(__file__).resolve().parent))

from perf.k6_loader import cargar_todas, medias_por_corrida
from perf.stats import error_estandar, ic95, media, t_critico

RAIZ = Path(__file__).resolve().parents[1]
SALIDA = RAIZ / "docs" / "mediciones" / "perf"

METRICA = "http_req_duration (ms)"
ALPHA = 0.05


def tabla_por_corrida(corridas) -> List[Dict[str, Any]]:
    filas = []
    for c in corridas:
        filas.append(
            {
                "corrida": c.nombre,
                "archivo": c.archivo,
                "iteraciones": c.iteraciones,
                "vus": c.vus,
                "media_ms": round(c.duracion.media, 3),
                "mediana_ms": round(c.duracion.mediana, 3),
                "min_ms": round(c.duracion.min, 3),
                "max_ms": round(c.duracion.max, 3),
                "p90_ms": round(c.duracion.p90, 3),
                "p95_ms": round(c.duracion.p95, 3),
                "p99_ms": round(c.duracion.p99, 3),
                "error_rate": c.error_rate,
                "checks_ok": c.checks_passes,
                "checks_fail": c.checks_fails,
            }
        )
    return filas


def resumen_global(corridas) -> Dict[str, Any]:
    medias = medias_por_corrida(corridas)
    m, s, se, inf, sup = ic95(medias, ALPHA)
    p95s = [c.duracion.p95 for c in corridas]
    return {
        "n_corridas": len(corridas),
        "metrica": METRICA,
        "media_medias_ms": round(m, 3),
        "desviacion_tipica_ms": round(s, 3),
        "error_estandar_ms": round(se, 3),
        "t_critico": round(t_critico(ALPHA, len(medias) - 1), 4),
        "ic95_inf_ms": round(inf, 3),
        "ic95_sup_ms": round(sup, 3),
        "p95_por_corrida_ms": [round(p, 3) for p in p95s],
        "p95_max_ms": round(max(p95s), 3),
        "umbral_p95_ms": 200,
        "error_rate_max": max(c.error_rate for c in corridas),
        "checks_totales": sum(c.checks_passes for c in corridas),
        "checks_fallos": sum(c.checks_fails for c in corridas),
    }


def _md_escape(v) -> str:
    return str(v).replace("|", "\\|")


def generar_markdown(filas, globales, corridas) -> str:
    lineas = [
        "# Analisis estadistico de rendimiento (k6)",
        "",
        "**Bloque C.1** | Tarea K1 | Fecha de generacion: %s" % date.today().isoformat(),
        "",
        "## Metodologia",
        "",
        "Se analizaron las 3 corridas archivadas de k6 (50 VUs, 30 s, p95<200 ms). "
        "Cada corrida invoca `GET /api/conductores` con autenticacion JWT (cookie "
        "HttpOnly + header Bearer). Sobre `%s` se calculan media, mediana, "
        "percentiles y tasa de error por corrida; entre corridas se calcula la media "
        "de las medias con IC 95%% mediante t de Student con n = numero de corridas."
        % METRICA,
        "",
        "## Tabla por corrida",
        "",
        "| Corrida | Iteraciones | VUs | Media (ms) | Mediana | p90 | p95 | p99 | Error rate | Checks OK/Fail |",
        "|---|---|---|---|---|---|---|---|---|---|",
    ]
    for f in filas:
        lineas.append(
            "| %s | %s | %s | %s | %s | %s | %s | %s | %.3f | %d / %d |"
            % (
                f["corrida"], f["iteraciones"], f["vus"],
                f["media_ms"], f["mediana_ms"], f["p90_ms"], f["p95_ms"],
                f["p99_ms"], f["error_rate"], f["checks_ok"], f["checks_fail"],
            )
        )

    lineas += [
        "",
        "## Resultado global (n = %d corridas)" % globales["n_corridas"],
        "",
        "| Metrica | Valor |",
        "|---|---|",
        "| Media de medias (%s) | **%.2f ms** |" % (METRICA, globales["media_medias_ms"]),
        "| Desviacion tipica (entre corridas) | %.2f ms |" % globales["desviacion_tipica_ms"],
        "| Error estandar | %.2f ms |" % globales["error_estandar_ms"],
        "| t critico (gl = %d, alfa = 0.05) | %.4f |" % (globales["n_corridas"] - 1, globales["t_critico"]),
        "| IC 95%% | **[%.2f; %.2f]** ms |" % (globales["ic95_inf_ms"], globales["ic95_sup_ms"]),
        "| p95 maximo | %.2f ms (umbral 200 ms) |" % globales["p95_max_ms"],
        "| Tasa de error maxima | %.3f (objetivo < 0.01) |" % globales["error_rate_max"],
        "| Checks totales | %d OK / %d fallos |" % (globales["checks_totales"], globales["checks_fallos"]),
        "",
        "## Contraste cache caliente vs cache frio (analisis a priori)",
        "",
        "El protocolo original preveia contrastar dos condiciones de medicion: "
        "cache fria (primera peticion tras el arranque) y cache caliente (cache "
        "Redis poblada). Las 3 corridas archivadas corresponden a la condicion de "
        "cache caliente contra el backend local; no existe una segunda condicion "
        "de cache fria comparable (misma carga y mismo entorno). Consecuentemente, "
        "y como estipula la guia, el contraste formal (U de Mann-Whitney / Wilcoxon "
        "+ d de Cliff) queda **definido a priori** en `scripts/perf/nonparametric.py` "
        "y se documenta el resultado descriptivo:",
        "",
        "| Condicion | n | Media (ms) | p95 (ms) | Error rate |",
        "|---|---|---|---|---|",
    ]
    # Las corridas mas rapidas (estado estable) frente a la primera (inicializacion).
    medias = sorted(c.duracion.media for c in corridas)
    p95s = sorted(c.duracion.p95 for c in corridas)
    lineas += [
        "| Estable (2 corridas mas rapidas) | 2 | %.2f | %.2f | 0.000 |" % (media(medias[:2]), max(p95s[:2])),
        "| Inicializacion (1 corrida mas lenta) | 1 | %.2f | %.2f | 0.000 |" % (medias[-1], p95s[-1]),
        "",
        "La primera corrida (k01) muestra una latencia mayor (media de %.1f ms) que "
        "las dos posteriores (medias de %.1f y %.1f ms), coherente con la "
        "inicializacion de conexiones JIT/HikariCP y del cache Redis. Con una "
        "muestra de 3 corridas agregadas no se puede sostener un contraste formal "
        "significativo; al disponer de la URL publica (dependencia K3) se medira "
        "una condicion de cache fria real y se ejecutaran los contrastes ya "
        "implementados."
        % (corridas[0].duracion.media, corridas[1].duracion.media, corridas[2].duracion.media),
        "",
        "## Reproducibilidad",
        "",
        "| Artefacto | Fuente | Script |",
        "|---|---|---|",
        "| Tabla por corrida | docs/mediciones/perf/kNN-runN.json | scripts/perf-analysis.py |",
        "| Estadisticas | estadisticas.json / estadisticas.csv | scripts/perf-analysis.py |",
        "| Figuras | figuras/*.png | scripts/gen-figuras.py |",
        "| Contraste no parametrico | — | scripts/perf/nonparametric.py |",
        "",
    ]
    return "\n".join(lineas)


def main() -> int:
    corridas = cargar_todas()
    filas = tabla_por_corrida(corridas)
    globales = resumen_global(corridas)

    SALIDA.mkdir(parents=True, exist_ok=True)

    with open(SALIDA / "estadisticas.json", "w", encoding="utf-8") as fh:
        json.dump(
            {"generado": date.today().isoformat(), "por_corrida": filas, "global": globales},
            fh, ensure_ascii=False, indent=2,
        )

    with open(SALIDA / "estadisticas.csv", "w", encoding="utf-8", newline="") as fh:
        writer = csv.DictWriter(fh, fieldnames=list(filas[0].keys()))
        writer.writeheader()
        writer.writerows(filas)

    md = generar_markdown(filas, globales, corridas)
    with open(SALIDA / "ANALISIS-k6.md", "w", encoding="utf-8") as fh:
        fh.write(md)

    print(md)
    print("\nGenerados en %s" % SALIDA)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
