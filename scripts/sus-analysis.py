#!/usr/bin/env python3
"""Analisis del cuestionario SUS (Bloque C.3 / tarea K2).

Lee la matriz cruda docs/mediciones/sus/sus-raw.csv y produce:

  * docs/mediciones/sus/P01.json .. P10.json  (datos crudos por participante)
  * docs/mediciones/sus/estadisticas-sus.json (datos maquina)
  * docs/mediciones/sus/ANALISIS-SUS.md       (informe reproducible)
  * docs/mediciones/sus/figuras/fig-sus-por-participante.png

Calcula la puntuacion de Brooke por participante, la media, desviacion tipica,
error estandar e IC 95% (t de Student, n = 10) y clasifica segun la escala
adjetiva de Bangor et al. (2009).

Uso: python scripts/sus-analysis.py
"""

from __future__ import annotations

import json
import sys
from datetime import date
from pathlib import Path
from typing import Any, Dict, List

sys.path.insert(0, str(Path(__file__).resolve().parent))

from sus.brooke import interpretar_adjetiva, puntuacion_sus  # noqa: E402
from sus.sus_loader import cargar_respuestas  # noqa: E402
from perf.stats import ic95, media  # noqa: E402

RAIZ = Path(__file__).resolve().parents[1]
SUS_DIR = RAIZ / "docs" / "mediciones" / "sus"
FIGURAS = SUS_DIR / "figuras"

UMBRAL_ACEPTABLE = 70.0


def construir_por_participante(respuestas) -> List[Dict[str, Any]]:
    filas = []
    for r in respuestas:
        puntuacion = puntuacion_sus(r.respuestas)
        if abs(puntuacion - r.sus_score_referencia) > 1e-6:
            raise ValueError(
                f"{r.codigo}: puntuacion recalculada {puntuacion} != referencia "
                f"{r.sus_score_referencia}"
            )
        filas.append(
            {
                "codigo": r.codigo,
                "edad": r.edad,
                "sexo": r.sexo,
                "experiencia_web": r.experiencia_web,
                "dispositivo": r.dispositivo,
                "consentimiento": r.consentimiento,
                "respuestas": r.respuestas,
                "sus_score": puntuacion,
            }
        )
    return filas


def stats_por_item(filas) -> List[Dict[str, Any]]:
    """Media, DT y contribucion promedio de Brooke por ítem (q1..q10)."""
    n_items = 10
    items = []
    for i in range(n_items):
        valores = [f["respuestas"][i] for f in filas]
        contribuciones = [
            v - 1 if i % 2 == 0 else 5 - v for v in valores
        ]
        items.append(
            {
                "item": f"q{i + 1}",
                "media_respuesta": round(sum(valores) / len(valores), 2),
                "dt_respuesta": round(
                    (sum((v - sum(valores) / len(valores)) ** 2 for v in valores)
                     / (len(valores) - 1)) ** 0.5, 2
                ),
                "media_contribucion": round(sum(contribuciones) / len(contribuciones), 2),
            }
        )
    return items


def generar_markdown(filas, stats, puntuaciones, items) -> str:
    m, s, se, inf, sup = stats
    adjetivo, zona = interpretar_adjetiva(m)
    lineas = [
        "# Analisis de usabilidad (SUS) - Bloque C.3",
        "",
        "**Tarea K2** | Fecha de generacion: %s" % date.today().isoformat(),
        "",
        "## Instrumento",
        "",
        "System Usability Scale (Brooke, 1996): 10 items Likert 1-5. Puntuacion por "
        "participante = (suma de contribuciones) * 2.5, donde items impares "
        "contribuyen `respuesta - 1` y pares `5 - respuesta`.",
        "",
        "## Puntuacion por participante",
        "",
        "| Codigo | Edad | Sexo | Exp. web | q1 | q2 | q3 | q4 | q5 | q6 | q7 | q8 | q9 | q10 | SUS |",
        "|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|",
    ]
    for f in filas:
        q = " | ".join(str(v) for v in f["respuestas"])
        lineas.append(
            "| %s | %d | %s | %s | %s | %.1f |"
            % (f["codigo"], f["edad"], f["sexo"], f["experiencia_web"], q, f["sus_score"])
        )

    lineas += [
        "",
        "## Estadisticos descriptivos (n = %d)" % len(puntuaciones),
        "",
        "| Metrica | Valor |",
        "|---|---|",
        "| Media | **%.1f / 100** |" % m,
        "| Desviacion tipica | %.2f |" % s,
        "| Error estandar | %.2f |" % se,
        "| IC 95%% | **[%.1f; %.1f]** |" % (inf, sup),
        "| Minimo | %.1f |" % min(puntuaciones),
        "| Maximo | %.1f |" % max(puntuaciones),
        "| Calificacion adjetiva (Bangor et al., 2009) | %s |" % adjetivo,
        "| Zona de aceptabilidad | %s |" % zona,
        "| Umbral de usabilidad (>= 70) | %s |" % ("CUMPLE" if m >= UMBRAL_ACEPTABLE else "NO CUMPLE"),
        "",
        "## Estadisticos por item",
        "",
        "Contribucion promedio de Brooke por item (mayor = mejor; items pares son "
        "enunciados negativos).",
        "",
        "| Item | Media respuesta | DT respuesta | Contribucion /4 |",
        "|---|---|---|---|",
    ]
    for it in items:
        lineas.append(
            "| %s | %.2f | %.2f | %.2f |"
            % (it["item"], it["media_respuesta"], it["dt_respuesta"], it["media_contribucion"])
        )
    peores = sorted(items, key=lambda x: x["media_contribucion"])[:3]
    mejores = sorted(items, key=lambda x: -x["media_contribucion"])[:3]
    lineas += [
        "",
        "Items con peor contribucion promedio: %s." % ", ".join(p["item"] for p in peores),
        "",
        "Items con mejor contribucion promedio: %s." % ", ".join(m["item"] for m in mejores),
        "",
        "## Interpretacion",
        "",
    ]
    if m >= UMBRAL_ACEPTABLE:
        lineas.append(
            "La media (%.1f) supera el umbral de 70 propuesto por Bangor et al. "
            "(2009); el sistema se percibe como usable, con la salvedad del tamano "
            "muestral (n = 10)." % m
        )
    else:
        lineas.append(
            "La media (%.1f) queda por debajo del umbral de 70 (zona marginal "
            "50-70). Se documenta como area de mejora: los items con peor "
            "contribucion promedio se revisaran en la iteracion de diseno "
            "posterior a la defensa." % m
        )
    lineas += [
        "",
        "## Reproducibilidad",
        "",
        "| Artefacto | Fuente | Script |",
        "|---|---|---|",
        "| Datos crudos | docs/mediciones/sus/sus-raw.csv | — |",
        "| Datos por participante | P01.json..P10.json | scripts/sus-analysis.py |",
        "| Estadisticas | estadisticas-sus.json | scripts/sus-analysis.py |",
        "| Puntuacion Brooke | — | scripts/sus/brooke.py |",
        "| Figura | figuras/fig-sus-por-participante.png | scripts/sus-analysis.py |",
        "",
        "Los consentimientos firmados se custodian fuera del repositorio (regla de "
        "etica); en el repo solo constan codigos P01..P10.",
        "",
    ]
    return "\n".join(lineas)


def main() -> int:
    respuestas = cargar_respuestas()
    errores = [e for r in respuestas for e in r.validar()]
    if errores:
        for e in errores:
            print("ERROR:", e)
        return 1

    filas = construir_por_participante(respuestas)
    puntuaciones = [f["sus_score"] for f in filas]
    stats = ic95(puntuaciones)
    items = stats_por_item(filas)

    SUS_DIR.mkdir(parents=True, exist_ok=True)

    for f in filas:
        ruta = SUS_DIR / f"{f['codigo']}.json"
        with open(ruta, "w", encoding="utf-8") as fh:
            json.dump(f, fh, ensure_ascii=False, indent=2)

    with open(SUS_DIR / "estadisticas-sus.json", "w", encoding="utf-8") as fh:
        json.dump(
            {
                "generado": date.today().isoformat(),
                "n": len(puntuaciones),
                "media": round(stats[0], 3),
                "desviacion_tipica": round(stats[1], 3),
                "error_estandar": round(stats[2], 3),
                "ic95_inf": round(stats[3], 3),
                "ic95_sup": round(stats[4], 3),
                "adjetiva": interpretar_adjetiva(stats[0])[0],
            },
            fh, ensure_ascii=False, indent=2,
        )

    with open(SUS_DIR / "estadisticas-item.json", "w", encoding="utf-8") as fh:
        json.dump(
            {
                "generado": date.today().isoformat(),
                "n": len(filas),
                "items": items,
            },
            fh, ensure_ascii=False, indent=2,
        )

    md = generar_markdown(filas, stats, puntuaciones, items)
    with open(SUS_DIR / "ANALISIS-SUS.md", "w", encoding="utf-8") as fh:
        fh.write(md)

    # Figura: barras por participante + media + banda IC95.
    try:
        import matplotlib

        matplotlib.use("Agg")
        import matplotlib.pyplot as plt

        from perf.colors import FIGH, FIGW, OKABE_ITO

        FIGURAS.mkdir(parents=True, exist_ok=True)
        m, _s, _se, inf, sup = stats
        fig, ax = plt.subplots(figsize=(FIGW, FIGH))
        nombres = [f["codigo"] for f in filas]
        ax.bar(nombres, puntuaciones, color=OKABE_ITO["celeste"], alpha=0.85)
        ax.axhline(UMBRAL_ACEPTABLE, color=OKABE_ITO["granate"], linestyle="--",
                   linewidth=1.3, label=f"umbral 70")
        ax.axhline(m, color="black", linewidth=1.5, label=f"media = {m:.1f}")
        ax.axhspan(inf, sup, color=OKABE_ITO["azul"], alpha=0.18,
                   label=f"IC 95% [{inf:.1f}; {sup:.1f}]")
        ax.set_ylabel("Puntuacion SUS (0-100)")
        ax.set_title("SUS por participante (n = %d)" % len(puntuaciones))
        ax.set_ylim(0, 100)
        ax.legend()
        ax.grid(True, axis="y", linestyle=":", alpha=0.5)
        fig.tight_layout()
        fig.savefig(FIGURAS / "fig-sus-por-participante.png", dpi=150)
        plt.close(fig)

        # Figura: media de respuesta por item (1-5) mostrando enunciados negativos.
        fig2, ax2 = plt.subplots(figsize=(FIGW, FIGH))
        nombres = [it["item"].upper() for it in items]
        medias = [it["media_respuesta"] for it in items]
        colores = [
            OKABE_ITO["azul"] if i % 2 == 0 else OKABE_ITO["naranja"]
            for i in range(len(items))
        ]
        ax2.bar(nombres, medias, color=colores, alpha=0.85)
        ax2.axhline(3.0, color="black", linestyle="--", linewidth=1.2, label="punto medio 3")
        ax2.set_ylabel("Media de respuesta Likert (1-5)")
        ax2.set_title("Media de respuesta por item del SUS (n = %d)" % len(filas))
        ax2.set_ylim(1, 5)
        ax2.legend()
        ax2.grid(True, axis="y", linestyle=":", alpha=0.5)
        fig2.tight_layout()
        fig2.savefig(FIGURAS / "fig-sus-item-respuestas.png", dpi=150)
        plt.close(fig2)
    except ImportError:
        print("AVISO: matplotlib no disponible; no se genero la figura.")

    print(md)
    print("\nGenerados en %s" % SUS_DIR)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
