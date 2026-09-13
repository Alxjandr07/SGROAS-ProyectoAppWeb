#!/usr/bin/env bash
# =============================================================================
# validate-listings.sh
# Valida que los Listings del informe LaTeX existen en el código (P17).
# Verifica los 3 listados señalados en cap7-implementacion.tex contra src/.
# Uso: scripts/validate-listings.sh
# =============================================================================
set -euo pipefail

LISTINGS=(
  "lst:conductor-repo|ConductorRepository|src/main/java"
  "lst:incidente-sp|@NamedStoredProcedureQuery|src/main/java/ec/edu/uteq/sgroas/entity"
  "lst:incidente-repo|IncidenteRepository|src/main/java"
  "lst:fn-programaciones|ProgramacionRepository|src/main/java"
)

ERRS=0
for entry in "${LISTINGS[@]}"; do
  IFS='|' read -r label pattern dir <<<"$entry"
  if ! grep -rq "$pattern" "$dir" 2>/dev/null; then
    echo "ERROR [$label]: patrón '$pattern' no encontrado en $dir" >&2
    ERRS=$((ERRS+1))
  else
    echo "OK [$label]: $dir contiene '$pattern'"
  fi
done

if [[ "$ERRS" -gt 0 ]]; then
  echo "VALIDACION LISTINGS FALLIDA: $ERRS error(es)" >&2
  exit 1
fi

echo "OK: los 4 listados de cap7 verificados contra el código."
exit 0
