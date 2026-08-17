"""Carga y extraccion de datos desde los resumenes JSON de k6.

Los archivos `k01-run1.json`, `k02-run2.json` y `k03-run3.json` son la salida
`--out json --summary-export` de k6. Este modulo los convierte en una
estructura plana con las metricas que alimentan el analisis (Bloque C.1).
"""

from __future__ import annotations

import json
from dataclasses import dataclass, field
from pathlib import Path
from typing import Any, Dict, List

PERF_DIR = Path(__file__).resolve().parents[2] / "docs" / "mediciones" / "perf"

CORRIDAS = {
    "k01-run1": "k01-run1.json",
    "k02-run2": "k02-run2.json",
    "k03-run3": "k03-run3.json",
}


@dataclass
class PerfilMetrica:
    """Resumen agregado de una metrica de duracion (ms)."""

    min: float
    max: float
    mediana: float
    p50: float
    p90: float
    p95: float
    p99: float
    media: float

    @classmethod
    def desde_json(cls, m: Dict[str, Any]) -> "PerfilMetrica":
        return cls(
            min=m["min"],
            max=m["max"],
            mediana=m["med"],
            p50=m["p(50)"],
            p90=m["p(90)"],
            p95=m["p(95)"],
            p99=m["p(99)"],
            media=m["avg"],
        )


@dataclass
class Corrida:
    """Toda la informacion de una corrida k6 que interesa al analisis."""

    nombre: str
    archivo: str
    duracion: PerfilMetrica
    error_rate: float
    checks_passes: int
    checks_fails: int
    iteraciones: int
    vus: int
    metadatos: Dict[str, Any] = field(default_factory=dict)


def cargar_corrida(ruta: str | Path) -> Dict[str, Any]:
    """Lee un archivo JSON de resumen k6 y devuelve el diccionario completo."""
    with open(ruta, encoding="utf-8") as fh:
        return json.load(fh)


def extraer_corrida(nombre: str, datos: Dict[str, Any]) -> Corrida:
    """Convierte el JSON crudo de una corrida en un objeto `Corrida`."""
    metricas = datos["metrics"]
    dur = PerfilMetrica.desde_json(metricas["http_req_duration"])
    err = metricas["http_req_failed"]
    checks = metricas["checks"]
    iters = metricas["iterations"]
    vus = metricas["vus_max"]

    return Corrida(
        nombre=nombre,
        archivo=str(PERF_DIR / f"{nombre}.json"),
        duracion=dur,
        error_rate=float(err["value"]),
        checks_passes=int(checks["passes"]),
        checks_fails=int(checks["fails"]),
        iteraciones=int(iters["count"]),
        vus=int(vus.get("value", 0)),
    )


def cargar_todas() -> List[Corrida]:
    """Carga las 3 corridas archivadas de la carpeta de mediciones."""
    corridas: List[Corrida] = []
    for nombre, nombre_archivo in CORRIDAS.items():
        ruta = PERF_DIR / nombre_archivo
        if not ruta.exists():
            raise FileNotFoundError(
                f"No se encontro {ruta}. Ejecute primero k6 con summary-export."
            )
        corridas.append(extraer_corrida(nombre, cargar_corrida(ruta)))
    return corridas


def medias_por_corrida(corridas: List[Corrida]) -> List[float]:
    """Lista de medias (avg) de `http_req_duration` por corrida, en ms."""
    return [c.duracion.media for c in corridas]
