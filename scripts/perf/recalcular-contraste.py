#!/usr/bin/env python3
"""Recalcula el contraste no parametrico cache frio vs caliente (Bloque C).

Lee las corridas crudas versionadas en dataset/perf/kNN-cold.json y
kNN-run1.json y reproduce la U de Mann-Whitney y el d de Cliff usando
scripts/perf/nonparametric.py.

Uso: python scripts/perf/recalcular-contraste.py
"""

from __future__ import annotations

import json
import sys
from pathlib import Path
sys.path.insert(0, str(Path(__file__).resolve().parents[1]))


from perf.nonparametric import cliffs_delta, interpretar_cliffs_delta, mann_whitney_u

RAIZ = Path(__file__).resolve().parents[2]
PERF = RAIZ / "dataset" / "perf"

FRIAS = ["k04-cold.json", "k05-cold.json", "k06-cold.json", "k07-cold.json", "k08-cold.json"]
CALIENTES = ["k04-run1.json", "k05-run1.json", "k06-run1.json", "k07-run1.json", "k08-run1.json"]


def _media_duracion(archivo: Path) -> float:
    with open(archivo, encoding="utf-8") as fh:
        datos = json.load(fh)
    return float(datos["metrics"]["http_req_duration"]["avg"])


def _duracion_frio(archivo: Path) -> float:
    with open(archivo, encoding="utf-8") as fh:
        datos = json.load(fh)
    return float(datos["metrics"]["http_req_duration"]["max"])


def main() -> int:
    frias = [_duracion_frio(PERF / f) for f in FRIAS]
    calientes = [_media_duracion(PERF / c) for c in CALIENTES]

    u, z, p = mann_whitney_u(frias, calientes)
    d = cliffs_delta(frias, calientes)

    print("Contraste no parametrico cache frio vs caliente (n = 5 por condicion)")
    print(f"U (Mann-Whitney)       : {u}")
    print(f"z (aproximacion normal): {z:.2f}")
    print(f"p (bilateral)          : {p:.4f}")
    print(f"d de Cliff             : {d:.2f} -> {interpretar_cliffs_delta(d)}")

    assert p < 0.05, "el contraste deberia ser significativo (p < 0.05)"
    assert d == -1.0, "todas las frias deben ser menores que todas las calientes"
    print("OK: contraste reproducible desde las corridas crudas")
    return 0


if __name__ == "__main__":
    sys.exit(main())