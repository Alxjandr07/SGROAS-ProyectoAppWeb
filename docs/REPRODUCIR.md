# Reproducibilidad — SGROAS

Todo número incluido en el informe de evaluación se regenera desde el clon
limpio. Guía rápida (Windows / Linux):

## 1. Clon y entorno

```sh
git clone https://github.com/Alxjandr07/SGROAS-ProyectoAppWeb.git
cd SGROAS-ProyectoAppWeb
git checkout v1.0.0
```

Python 3.14+ y dependencias de análisis:

```sh
pip install numpy scipy matplotlib   # solo para análisis (k6/SUS/figuras)
```

## 2. Análisis de rendimiento k6 (K1)

```sh
# datos crudos ya archivados en docs/mediciones/perf/
python scripts/perf-analysis.py      # -> ANALISIS-k6.md, estadisticas.json
python scripts/gen-figuras.py        # -> figuras/ (paleta Okabe-Ito)
```

Para re-ejecutar la carga contra una URL:

```sh
k6 run --summary-export k6/tmp.json k6/script.js   # 50 VUs / 30 s (opts.js)
```

## 3. Análisis de usabilidad SUS (K2)

```sh
python scripts/sus-analysis.py   # -> ANALISIS-SUS.md, estadisticas-sus.json,
                                 #    estadisticas-item.json, figuras/
```

## 4. Auditorías (K3)

```sh
bash scripts/lighthouse/run-lighthouse.sh <URL>   # LHCI
bash scripts/zap/run-zap.sh <URL>                 # OWASP ZAP baseline
```

## 5. Dataset (K6)

```sh
python scripts/zenodo/package-dataset.py   # -> dist/sgroas-dataset-v1.0.0.zip
                                           #    dataset/MANIFEST.csv
```

Verificación de integridad contra Zenodo:

```sh
sha256sum -c dataset/MANIFEST.csv    # Linux
certutil -hashfile <archivo> SHA256   # Windows
```

## 6. Informe (K5)

`docs/informe-final/main-evaluacion.tex` compila con `pdflatex` + `biber`
(pdflatex x2). Las referencias fueron verificadas contra Crossref (ver
`docs/informe-final/VERIFICACION-REFERENCIAS.md`).

## Regla

Ningún número del informe se escribe a mano: siempre sale de un script o de un
archivo crudo archivado. Los datos y su origen están en
`docs/mediciones/DATA-PROVENANCE.md`.