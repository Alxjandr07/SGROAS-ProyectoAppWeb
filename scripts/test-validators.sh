#!/usr/bin/env bash
# =============================================================================
# test-validators.sh
# Self-test de los validadores del pipeline `make audit` (P11).
# Demuestra que los scripts de auditoria:
#   1. devuelven exit 0 cuando la evidencia es valida, y
#   2. devuelven exit != 0 cuando la evidencia esta rota.
# Uso: scripts/test-validators.sh   (requiere Git Bash / sh; no toca el repo)
# =============================================================================
set -u

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
TMP="$(mktemp -d)"
trap 'rm -rf "$TMP"' EXIT
FAILED=0

pass() { echo "PASS: $1"; }
fail() { echo "FAIL: $1"; FAILED=1; }

# --- Caso A: matriz valida -> exit 0 -------------------------------------
if [[ ! -f "$ROOT/docs/trazabilidad/matriz.csv" ]]; then
    fail "caso A: no existe docs/trazabilidad/matriz.csv"
else
    ( cd "$ROOT" && scripts/validate-traceability.sh >/dev/null 2>&1 )
    [[ $? -eq 0 ]] && pass "validate-traceability.sh (matriz valida) exit=0" \
                   || fail "validate-traceability.sh (matriz valida) esperaba exit=0"
fi

# --- Caso B: matriz rota -> exit != 0 -------------------------------------
cp "$ROOT/docs/trazabilidad/matriz.csv" "$TMP/matriz_backup.csv"
printf 'id,tipo,prioridad\n' > "$ROOT/docs/trazabilidad/matriz.csv"
( cd "$ROOT" && scripts/validate-traceability.sh >/dev/null 2>&1 )
RC=$?
mv -f "$TMP/matriz_backup.csv" "$ROOT/docs/trazabilidad/matriz.csv"
if [[ $RC -ne 0 ]]; then
    pass "validate-traceability.sh (matriz rota) exit=$RC (esperado != 0)"
else
    fail "validate-traceability.sh (matriz rota) esperaba exit != 0"
fi

# --- Caso C: listings compensados -> exit 0 --------------------------------
if [[ ! -f "$ROOT/scripts/validate-listings.sh" ]]; then
    fail "caso C: no existe scripts/validate-listings.sh"
else
    ( cd "$ROOT" && scripts/validate-listings.sh >/dev/null 2>&1 )
    [[ $? -eq 0 ]] && pass "validate-listings.sh (listings ok) exit=0" \
                   || fail "validate-listings.sh (listings ok) esperaba exit=0"
fi

# --- Caso D: listing roto -> exit != 0 -------------------------------------
sed 's/DriverRepository/PatronQueNoExisteEnElCodigo/' "$ROOT/scripts/validate-listings.sh" > "$TMP/validate-listings-broken.sh"
chmod +x "$TMP/validate-listings-broken.sh"
( cd "$ROOT" && "$TMP/validate-listings-broken.sh" >/dev/null 2>&1 )
RC=$?
if [[ $RC -ne 0 ]]; then
    pass "validate-listings.sh (listing roto) exit=$RC (esperado != 0)"
else
    fail "validate-listings.sh (listing roto) esperaba exit != 0"
fi

echo "----------------------------------------"
if [[ $FAILED -eq 0 ]]; then
    echo "SELF-TEST OK: los validadores fallan con exit != 0 ante evidencia rota (P11)"
    exit 0
else
    echo "SELF-TEST FALLIDO"
    exit 1
fi