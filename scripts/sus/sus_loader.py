"""Carga de datos crudos SUS (Bloque C.3 / tarea K2).

Lee docs/mediciones/sus/sus-raw.csv (matriz cruda de 10 participantes P01..P10)
y la expone como estructuras tipadas. Los consentimientos firmados permanecen
FUERA del repositorio publico (regla de etica del proyecto); aqui solo se
trabaja con codigos de participante.
"""

from __future__ import annotations

import csv
from dataclasses import dataclass
from pathlib import Path
from typing import List

SUS_DIR = Path(__file__).resolve().parents[2] / "docs" / "mediciones" / "sus"

ITEM_PARES = (2, 4, 6, 8, 10)


@dataclass
class RespuestaSUS:
    """Una fila del cuestionario SUS de un participante."""

    codigo: str
    edad: int
    sexo: str
    experiencia_web: str
    dispositivo: str
    consentimiento: str
    respuestas: List[int]
    sus_score_referencia: float

    def validar(self) -> List[str]:
        """Validaciones de dominio: 10 items, escala 1-5, consentimiento."""
        errores: List[str] = []
        if len(self.respuestas) != 10:
            errores.append(f"{self.codigo}: debe tener 10 items")
        if any(not (1 <= v <= 5) for v in self.respuestas):
            errores.append(f"{self.codigo}: items fuera de la escala Likert 1-5")
        if self.consentimiento.lower() not in ("sí", "si", "true", "1"):
            errores.append(f"{self.codigo}: falta consentimiento informado")
        return errores


def cargar_respuestas(ruta: str | Path | None = None) -> List[RespuestaSUS]:
    ruta = Path(ruta) if ruta else SUS_DIR / "sus-raw.csv"
    respuestas: List[RespuestaSUS] = []
    with open(ruta, encoding="utf-8-sig", newline="") as fh:
        for fila in csv.DictReader(fh):
            q = [int(fila[f"q{i}"]) for i in range(1, 11)]
            respuestas.append(
                RespuestaSUS(
                    codigo=fila["codigo"].strip(),
                    edad=int(fila["edad"]),
                    sexo=fila["sexo"].strip(),
                    experiencia_web=fila["experiencia_web"].strip(),
                    dispositivo=fila["dispositivo"].strip(),
                    consentimiento=fila["consentimiento"].strip(),
                    respuestas=q,
                    sus_score_referencia=float(fila["sus_score"]),
                )
            )
    return respuestas
