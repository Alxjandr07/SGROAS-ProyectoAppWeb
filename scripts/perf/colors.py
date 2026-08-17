"""Paleta Okabe-Ito, accesible para daltonismo (regla K1).

La guia exige figuras con paletas perceptualmente uniformes y accesibles.
Okabe-Ito (2008) es la paleta por defecto para figuras cientificas.
"""

from __future__ import annotations

OKABE_ITO = {
    "naranja": "#E69F00",
    "azul": "#56B4E9",
    "verde": "#009E73",
    "amarillo": "#F0E442",
    "celeste": "#0072B2",
    "granate": "#D55E00",
    "rosa": "#CC79A7",
    "gris": "#999999",
}

# Orden de uso recomendado (alto contraste para daltonicos).
PALETA_PRINCIPAL = [
    OKABE_ITO["celeste"],
    OKABE_ITO["naranja"],
    OKABE_ITO["verde"],
    OKABE_ITO["granate"],
    OKABE_ITO["rosa"],
]

GRIS_SUAVE = OKABE_ITO["gris"]

# Variables (estilo scientific notation para ejes y textos).
FONT_FAMILY = "DejaVu Sans"
FIGURE_DPI = 150
FIGW = 8
FIGH = 5
