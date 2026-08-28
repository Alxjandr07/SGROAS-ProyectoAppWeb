#!/usr/bin/env bash
# =============================================================================
# audit-sql-dynamic.sh
# Auditoria de prohibiciones de la estrategia de acceso a datos (Bloque A.2.3).
# Rechaza (exit != 0) si encuentra:
#   - SQL dinamico construido por concatenacion (EXECUTE IMMEDIATE, sp_executesql)
#   - Concatenacion de entrada de usuario en JPQL/HQL/SQL nativo
#   - dependencia de spring.jpa.hibernate.ddl-auto=update
# Uso: scripts/audit-sql-dynamic.sh
# =============================================================================
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
VIOLATIONS=0

check_grep() {
    local label="$1"
    local pattern="$2"
    local dir="$3"
    local matches
    matches="$(grep -rniE "$pattern" "$dir" 2>/dev/null || true)"
    if [[ -n "$matches" ]]; then
        echo "VIOLACION [$label]:" >&2
        echo "$matches" >&2
        VIOLATIONS=$((VIOLATIONS+1))
    else
        echo "OK: $label"
    fi
}

# 1) SQL dinamico por concatenacion en procedimientos almacenados
check_grep "SQL dinamico (EXECUTE IMMEDIATE)" \
    "EXECUTE[[:space:]]+IMMEDIATE" \
    "$ROOT/db/procs"

check_grep "SQL dinamico (sp_executesql)" \
    "sp_executesql" \
    "$ROOT/db/procs"

# 2) Concatenacion de parametros en SQL dentro de procs/funciones (operador || o +)
check_grep "Concatenacion de texto en SQL" \
    "\|[[:space:]]*'|'[[:space:]]*\|" \
    "$ROOT/db/procs"

# 3) Concatenacion de strings (operador +) en consultas JPA/Hibernate del codigo
#    Java. Permite consultas estaticas y parametrizadas (text blocks o nativeQuery
#    con :param); falla solo si se concatena texto para construir la consulta.
check_grep "JPQL/HQL/SQL nativo con concatenacion Java (+)" \
    "(createQuery|createNativeQuery|createSQLQuery|@Query)[^;]*\+" \
    "$ROOT/src/main"

# 3b) createNativeQuery invocado con un String construido dinamicamente
#     (no literal), sinonimo de SQL construido en runtime.
check_grep "createNativeQuery con argumento dinamico" \
    "createNativeQuery[[:space:]]*\([[:space:]]*[A-Za-z_$]" \
    "$ROOT/src/main"

# 4) Dependencia de ddl-auto=update en configuracion
check_grep "spring.jpa.hibernate.ddl-auto=update" \
    "ddl-auto[[:space:]]*=[[:space:]]*update" \
    "$ROOT/src/main/resources"

# 5) Declaraciones EXECUTE IMMEDIATE en migraciones
check_grep "EXECUTE IMMEDIATE en migraciones" \
    "EXECUTE[[:space:]]+IMMEDIATE" \
    "$ROOT/src/main/resources/db/migration"

if [[ "$VIOLATIONS" -gt 0 ]]; then
    echo "AUDITORIA FALLIDA: $VIOLATIONS categoria(s) con violaciones." >&2
    exit 1
fi

echo "AUDITORIA OK: sin SQL dinamico ni concatenacion de entrada de usuario."
exit 0
