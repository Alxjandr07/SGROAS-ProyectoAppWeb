#!/usr/bin/env bash
# =============================================================================
# validate-sus-demografia.sh
# Verifica que la demografia declarada en el capitulo 5 del informe (P13)
# cruza 1:1 con los datos crudos SUS (docs/mediciones/sus/sus-raw.csv):
#   - 15 participantes P01..P15 anonimizados
#   - 8 hombres y 7 mujeres
#   - edades entre 19 y 25 anos
#   - experiencia web: baja (3), media (10), alta (2)
#   - todos con computadora de escritorio
#   - puntaje SUS por fila (media 68.5, min 47.5, max 90.0)
# Uso: scripts/validate-sus-demografia.sh   (requiere python3)
# =============================================================================
set -euo pipefail

CSV="docs/mediciones/sus/sus-raw.csv"
if [[ ! -f "$CSV" ]]; then
    echo "ERROR: no existe $CSV" >&2
    exit 1
fi

python3 - "$CSV" <<'PY'
import csv, sys
PATH = sys.argv[1]
rows = list(csv.DictReader(open(PATH, encoding="utf-8-sig")))
errs = []

def fail(msg): errs.append(msg)

n = len(rows)
if n != 15:
    fail(f"n={n} != 15")

codigos = sorted(r["codigo"] for r in rows)
if codigos != [f"P{i:02d}" for i in range(1, 16)]:
    fail(f"codigos no son P01..P15: {codigos}")

if sum(r["sexo"] == "Masculino" for r in rows) != 8:
    fail("hombres != 8")
if sum(r["sexo"] == "Femenino" for r in rows) != 7:
    fail("mujeres != 7")

edades = [int(r["edad"]) for r in rows]
if min(edades) < 19 or max(edades) > 25:
    fail(f"edades fuera de [19,25]: {min(edades)}..{max(edades)}")

from collections import Counter
exp = Counter(r["experiencia_web"] for r in rows)
if exp.get("Baja", 0) != 3 or exp.get("Media", 0) != 10 or exp.get("Alta", 0) != 2:
    fail(f"experiencia web != {exp}")

if any(r["dispositivo"].strip() != "Computadora" for r in rows):
    fail("hay dispositivo != Computadora")

scores = [float(r["sus_score"]) for r in rows]
media = sum(scores) / n
if abs(media - 68.5) > 0.01:
    fail(f"media SUS {media:.2f} != 68.5")
if min(scores) != 47.5 or max(scores) != 90.0:
    fail(f"min/max SUS {min(scores)}/{max(scores)} != 47.5/90.0")

if errs:
    for e in errs:
        print(f"ERROR: {e}", file=sys.stderr)
    print(f"CRUCE DEMOGRAFICO FALLIDO: {len(errs)} error(es)", file=sys.stderr)
    sys.exit(1)

print(f"OK: demografia cap.5 cruza 1:1 con sus-raw.csv (n=15, 8H/7M, 19-25, B3/M10/A2, media SUS 68.5)")
PY