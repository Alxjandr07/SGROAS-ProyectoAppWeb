#!/usr/bin/env python3
"""
generate-sus-demographics.py
Reads sus-raw.csv and generates the SUS demographics table for cap.5.
Output: tab-separated table suitable for LaTeX inclusion.
Usage: python3 scripts/generate-sus-demographics.py dataset/sus/sus-raw.csv
"""
import csv
import sys
from collections import Counter

if len(sys.argv) != 2:
    print(f"Usage: {sys.argv[0]} <sus-raw.csv>", file=sys.stderr)
    sys.exit(1)

csv_path = sys.argv[1]
try:
    with open(csv_path, encoding="utf-8-sig") as f:
        rows = list(csv.DictReader(f))
except FileNotFoundError:
    print(f"ERROR: file not found: {csv_path}", file=sys.stderr)
    sys.exit(1)

n = len(rows)
codes = sorted(r["codigo"] for r in rows)
genders = Counter(r["sexo"] for r in rows)
ages = [int(r["edad"]) for r in rows]
experience = Counter(r["experiencia_web"] for r in rows)
devices = Counter(r["dispositivo"] for r in rows)
scores = [float(r["sus_score"]) for r in rows]
mean_score = sum(scores) / n

print(f"Total participants: {n}")
print(f"Codes: {', '.join(codes)}")
print(f"Gender: {genders.get('Masculino', 0)} male, {genders.get('Femenino', 0)} female")
print(f"Age range: {min(ages)}-{max(ages)} years (mean {sum(ages)/n:.1f})")
print(f"Web experience: Baja={experience.get('Baja', 0)}, Media={experience.get('Media', 0)}, Alta={experience.get('Alta', 0)}")
print(f"Devices: {dict(devices)}")
print(f"SUS score: mean={mean_score:.1f}, min={min(scores)}, max={max(scores)}")
print()
print("Per-participant data:")
print(f"{'Code':<6} {'Age':<5} {'Gender':<11} {'Experience':<11} {'Device':<14} {'SUS':<6}")
print("-" * 55)
for r in rows:
    print(f"{r['codigo']:<6} {r['edad']:<5} {r['sexo']:<11} {r['experiencia_web']:<11} {r['dispositivo']:<14} {r['sus_score']:<6}")
