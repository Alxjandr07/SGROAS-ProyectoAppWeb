"""Estadistica descriptiva e intervalos de confianza (Bloque C / K1).

Calcula media, desviacion tipica muestral, error estandar e intervalo de
confianza al 95% con la distribucion t de Student. La correccion se aplica
sobre las medias de cada corrida k6 (n = numero de corridas), como exige el
protocolo del proyecto.
"""

from __future__ import annotations

import math
from statistics import fmean, pstdev, stdev
from typing import List, Sequence, Tuple

import scipy.stats as stats

# Tabla de referencia para df pequenos si scipy no estuviera disponible.
T_TABLE_95 = {
    1: 12.706, 2: 4.303, 3: 3.182, 4: 2.776, 5: 2.571, 6: 2.447, 7: 2.365,
    8: 2.306, 9: 2.262, 10: 2.228, 11: 2.201, 12: 2.179, 13: 2.160, 14: 2.145,
    15: 2.131, 16: 2.120, 17: 2.110, 18: 2.101, 19: 2.093, 20: 2.086, 21: 2.080,
    22: 2.074, 23: 2.069, 24: 2.064, 25: 2.060, 26: 2.056, 27: 2.052, 28: 2.048,
    29: 2.045, 30: 2.042,
}


def media(xs: Sequence[float]) -> float:
    """Media aritmetica."""
    return fmean(xs)


def desviacion_tipica(xs: Sequence[float]) -> float:
    """Desviacion tipica muestral (n-1)."""
    if len(xs) < 2:
        return 0.0
    return stdev(xs)


def desviacion_poblacional(xs: Sequence[float]) -> float:
    """Desviacion tipica poblacional (n), util para informar por corrida."""
    return pstdev(xs)


def error_estandar(xs: Sequence[float]) -> float:
    """Error estandar de la media: s / sqrt(n)."""
    n = len(xs)
    if n == 0:
        return 0.0
    return desviacion_tipica(xs) / math.sqrt(n)


def t_critico(alpha: float = 0.05, gl: int = 1) -> float:
    """Valor critico de t para el nivel alpha (bilateral) con `gl` grados."""
    if gl < 1:
        return float("nan")
    try:
        return float(stats.t.ppf(1.0 - alpha / 2.0, df=gl))
    except Exception:
        return T_TABLE_95.get(gl, T_TABLE_95[30])


def ic95(xs: Sequence[float], alpha: float = 0.05) -> Tuple[float, float, float, float, float]:
    """IC 95% de la media.

    Devuelve (media, desviacion tipica muestral, error estandar, limite
    inferior, limite superior). Si n < 2, el IC no es calculable y ambos
    limites valen la propia media.
    """
    n = len(xs)
    m = media(xs)
    if n < 2:
        return m, 0.0, 0.0, m, m
    s = desviacion_tipica(xs)
    se = s / math.sqrt(n)
    t = t_critico(alpha, n - 1)
    margen = t * se
    return m, s, se, m - margen, m + margen


def percentiles_estimados(xs: Sequence[float]) -> List[float]:
    """Percentiles p10, p25, p50, p75, p90 de una muestra (para figuras)."""
    if not xs:
        return []
    ordenado = sorted(xs)
    n = len(ordenado)
    def _p(q: float) -> float:
        idx = (n - 1) * q
        base = math.floor(idx)
        resto = idx - base
        if base + 1 < n:
            return ordenado[base] * (1 - resto) + ordenado[base + 1] * resto
        return ordenado[base]
    return [_p(q) for q in (0.10, 0.25, 0.50, 0.75, 0.90)]
