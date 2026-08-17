# Checklist — Principios FAIR

**Documento:** Paquete de datos de mediciones SGROAS (k6, SUS, JaCoCo, Lighthouse, ZAP)
**Verificación:** 16 de agosto de 2026
**Equipo:** Kevin (Castro Espinoza) con revisión del equipo

Referencia: Wilkinson, M. D., et al. (2016). *The FAIR Guiding Principles for scientific
data management and stewardship*. Scientific Data, 3, 160018.

## F — Findable (Localizable)

- [x] F1. Los datos reciben identificadores persistentes: DOI del dataset en Zenodo (depósito separado del software).
- [x] F2. Los datos se describen con metadatos ricos (DATA-DICTIONARY.md, DATA-PROVENANCE.md, README).
- [x] F3. Los metadatos incluyen de forma clara y explícita el identificador del dataset (DOI en CITATION.cff y README).
- [x] F4. Los metadatos se registran en una infraestructura de búsqueda (Zenodo, con licencia CC BY 4.0).

## A — Accessible (Accesible)

- [x] A1. Los datos se recuperan por su identificador mediante protocolo estándar (HTTPS en Zenodo).
- [x] A1.1. El protocolo de acceso es abierto, gratuito y universalmente implementable.
- [x] A2. Los metadatos son accesibles incluso cuando los datos no lo son (en el repo GitHub).

## I — Interoperable (Interoperable)

- [x] I1. Los datos usan formatos abiertos y estándar: JSON, CSV, PNG, HTML (JaCoCo), sin propietarios.
- [x] I2. Los metadatos usan vocabularios estándar: IS0 8601 (fechas), códigos de variables definidos en el diccionario.
- [x] I3. Los metadatos referencian otros datos de forma cruzada (migraciones, scripts, figuras).

## R — Reusable (Reutilizable)

- [x] R1. Los datos se describen con atributos múltiples y precisos (origen, condiciones, instrumento).
- [x] R1.1. Se publica una licencia de uso clara y accesible (CC BY 4.0 para el dataset; MIT para el código).
- [x] R1.2. Se documenta la procedencia de los datos (DATA-PROVENANCE.md con scripts y commits).
- [x] R1.3. Los datos cumplen estándares de dominio (métricas SUS, percentiles k6, contadores JaCoCo).
- [x] R1.4. Los datos se etiquetan en el idioma del informe con descripciones en español/inglés donde aplica.

## Evidencia

| Principio | Evidencia en el repositorio |
|---|---|
| F1 | DOI dataset Zenodo **10.5281/zenodo.21973297** (ver CITATION.cff / README) |
| F2–F3 | docs/mediciones/DATA-DICTIONARY.md, DATA-PROVENANCE.md |
| A1 | Enlace de depósito Zenodo público (https://doi.org/10.5281/zenodo.21973297) |
| I1 | Formatos JSON/CSV/PNG/HTML |
| R1.1 | docs/etica/ETHICS.md y metadatos del depósito |
| R1.2 | docs/mediciones/DATA-PROVENANCE.md |

## Estado

- [x] Checklist revisado y aceptado por el equipo el 16 de agosto de 2026.
