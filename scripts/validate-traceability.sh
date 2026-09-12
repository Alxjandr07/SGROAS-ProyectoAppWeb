#!/usr/bin/env bash
# =============================================================================
# validate-traceability.sh
# Valida la matriz de trazabilidad end-to-end (Bloque A.3.3 de la guia).
# 1. Verifica la estructura de columnas obligatorias de docs/trazabilidad/matriz.csv
# 2. Rechaza si un requisito no tiene trazabilidad minima
# 3. Verifica coherencia SRS <-> matriz (identificadores y estados)
# Uso: scripts/validate-traceability.sh
# =============================================================================
set -euo pipefail

MATRIZ="docs/trazabilidad/matriz.csv"
SRS="docs/requisitos/SRS-v1.1.0.tex"

if [[ ! -f "$MATRIZ" ]]; then
    echo "ERROR: no existe $MATRIZ" >&2
    exit 1
fi

HEADER="id_requisito,tipo,prioridad_moscow,historia_usuario,caso_de_uso,modulo_codigo,endpoint_api,prueba_automatizada,tipo_acceso,evidencia_empirica,estado"

FIRST_LINE="$(head -n 1 "$MATRIZ" | tr -d '\r')"
if [[ "$FIRST_LINE" != "$HEADER" ]]; then
    echo "ERROR: cabecera de matriz.csv incorrecta." >&2
    echo "  Esperado: $HEADER" >&2
    echo "  Recibido: $FIRST_LINE" >&2
    exit 1
fi

ERRS=0

# --- FASE 1: Validar estructura y trazabilidad minima ---
while IFS= read -r line; do
    line="$(echo "$line" | tr -d '\r')"
    [[ -z "$line" ]] && continue

    IFS=',' read -r id tipo prioridad historia caso modulo endpoint prueba tipo_acceso evidencia estado <<<"$line"

    if [[ -z "$id" || -z "$tipo" || -z "$prioridad" ]]; then
        echo "ERROR: fila sin id/tipo/prioridad: $line" >&2
        ERRS=$((ERRS+1))
        continue
    fi

    if [[ "$tipo" != "Funcional" && "$tipo" != "No funcional" ]]; then
        echo "ERROR: tipo invalido en $id ('$tipo'). Debe ser 'Funcional' o 'No funcional'." >&2
        exit 1
    fi

    if [[ "$tipo" == "Funcional" ]]; then
        if [[ -z "$historia" && -z "$caso" && -z "$prueba" ]]; then
            echo "ERROR: $id (Funcional) sin correspondencia en historia, caso de uso o prueba." >&2
            ERRS=$((ERRS+1))
        fi
    else
        if [[ -z "$prueba" && -z "$evidencia" ]]; then
            echo "ERROR: $id (No funcional) sin prueba automatizada ni evidencia empirica." >&2
            ERRS=$((ERRS+1))
        fi
    fi
done < <(tail -n +2 "$MATRIZ")

# --- FASE 2: Validar columna estado (vocabulario controlado) ---
ESTADOS_VALIDOS="verificado pendiente parcialmente_verificado"
while IFS= read -r line; do
    line="$(echo "$line" | tr -d '\r')"
    [[ -z "$line" ]] && continue

    IFS=',' read -r id tipo prioridad historia caso modulo endpoint prueba tipo_acceso evidencia estado <<<"$line"

    if [[ -n "$estado" ]]; then
        if ! echo "$ESTADOS_VALIDOS" | grep -qw "$estado"; then
            echo "ERROR: estado invalido en $id ('$estado'). Valores permitidos: $ESTADOS_VALIDOS" >&2
            ERRS=$((ERRS+1))
        fi
    fi
done < <(tail -n +2 "$MATRIZ")

# --- FASE 3: Cross-check SRS <-> matriz (identificadores) ---
if [[ -f "$SRS" ]]; then
    # Extraer REQ-XXX-NNN del SRS
    SRS_IDS=$(grep -oP 'REQ-[FN]+-\d+' "$SRS" | sort -u)
    # Extraer REQ-XXX-NNN de la matriz
    MATRIZ_IDS=$(tail -n +2 "$MATRIZ" | cut -d',' -f1 | tr -d '\r' | sort -u)

    # Requisitos en SRS pero no en matriz
    MISSING_IN_MATRIZ=$(comm -23 <(echo "$SRS_IDS") <(echo "$MATRIZ_IDS"))
    if [[ -n "$MISSING_IN_MATRIZ" ]]; then
        echo "ERROR: requisitos en SRS sin fila en matriz.csv:" >&2
        echo "$MISSING_IN_MATRIZ" | while read -r rid; do echo "  - $rid" >&2; done
        ERRS=$((ERRS+1))
    fi

    # Requisitos en matriz pero no en SRS
    MISSING_IN_SRS=$(comm -13 <(echo "$SRS_IDS") <(echo "$MATRIZ_IDS"))
    if [[ -n "$MISSING_IN_SRS" ]]; then
        echo "ERROR: requisitos en matriz.csv sin definicion en SRS:" >&2
        echo "$MISSING_IN_SRS" | while read -r rid; do echo "  - $rid" >&2; done
        ERRS=$((ERRS+1))
    fi
else
    echo "ADVERTENCIA: no existe $SRS, se omite cross-check SRS <-> matriz." >&2
fi

# --- Resultado ---
if [[ "$ERRS" -gt 0 ]]; then
    echo "ERROR: la matriz de trazabilidad tiene $ERRS error(es)." >&2
    exit 1
fi

echo "OK: matriz de trazabilidad valida ($(($(wc -l < "$MATRIZ") - 1)) requisitos, SRS <-> matriz consistente)."
exit 0
