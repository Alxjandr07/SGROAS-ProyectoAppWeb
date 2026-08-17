#!/usr/bin/env python3
"""Empaqueta docs/mediciones como dataset para depósito en Zenodo (K6).

Genera:
  - dataset/MANIFEST.csv  (sha256 de cada archivo del dataset)
  - dist/sgroas-dataset-v1.0.0.zip (subir como depósito SEPARADO en Zenodo)
Reuso: actualizar VERSION al hacer release y, tras el depósito, rellenar
el DOI dataset en dataset/README.md y en CITATION.cff.
"""
from __future__ import annotations

import csv
import hashlib
import os
import sys
import zipfile
from pathlib import Path

REPO = Path(__file__).resolve().parents[2]
VERSION = "v1.0.0"
DIST = REPO / "dist"
ZIP_NAME = f"sgroas-dataset-{VERSION}.zip"
# carpetas y archivos raíz que forman el dataset
MEDICIONES = REPO / "docs" / "mediciones"
FOLDERS = ["perf", "sus", "lighthouse", "zap", "jacoco"]
ROOT_FILES = ["DATA-PROVENANCE.md", "DATA-DICTIONARY.md"]


def sha256(p: Path) -> str:
    h = hashlib.sha256()
    with p.open("rb") as fh:
        for chunk in iter(lambda: fh.read(1 << 20), b""):
            h.update(chunk)
    return h.hexdigest()


def archivos_seleccionados() -> list[Path]:
    out: list[Path] = []
    for f in ROOT_FILES:
        p = MEDICIONES / f
        if p.exists():
            out.append(p)
    for folder in FOLDERS:
        p = MEDICIONES / folder
        if p.is_dir():
            for root, _, files in os.walk(p):
                for name in files:
                    out.append(Path(root) / name)
    return out


def main() -> int:
    files = sorted(archivos_seleccionados())
    if not files:
        print("ERROR: no se encontraron archivos en docs/mediciones")
        return 1

    # 1) MANIFEST con checksums SHA-256
    manifest = REPO / "dataset" / "MANIFEST.csv"
    rows = []
    for p in files:
        rel = p.relative_to(MEDICIONES).as_posix()
        rows.append((rel, os.path.getsize(p), sha256(p)))
    with manifest.open("w", newline="", encoding="utf-8") as fh:
        w = csv.writer(fh)
        w.writerow(["ruta", "bytes", "sha256"])
        w.writerows(rows)
    print(f"MANIFEST: {len(rows)} archivos -> {manifest}")

    # 2) ZIP para subir a Zenodo
    DIST.mkdir(exist_ok=True)
    zip_path = DIST / ZIP_NAME
    with zipfile.ZipFile(zip_path, "w", zipfile.ZIP_DEFLATED) as z:
        for extra in ["README.md", "MANIFEST.csv", "zenodo.json"]:
            z.write(REPO / "dataset" / extra, f"dataset/{extra}")
        for p in files:
            rel = p.relative_to(MEDICIONES).as_posix()
            z.write(p, f"dataset/{rel}")
    print(f"ZIP: {zip_path} ({os.path.getsize(zip_path)/1e6:.1f} MB)")
    return 0


if __name__ == "__main__":
    sys.exit(main())