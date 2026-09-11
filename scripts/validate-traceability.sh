#!/usr/bin/env bash
# =============================================================================
# validate-traceability.sh
# Valida la matriz de trazabilidad end-to-end (Bloque A.3.3 de la guia).
# - Verifica la estructura de columnas obligatorias de docs/trazabilidad/matriz.csv
# - Rechaza (exit != 0) si un requisito se agrega sin correspondencia en al menos
#   una historia de usuario, un caso de uso o una prueba automatizada.
# - (M4) Unicidad de identificadores: un id no puede repetirse en la matriz
#   (p. ej. el duplicado REQ-F-050 de SRS §3.4.21/3.4.22).
# - (M4) Vocabulario cerrado de estado: solo 'verificado' | 'pendiente'
#   (valores atestiguados hoy en la matriz; el tercer valor de SRS §3.1 aun
#   no consta en el repositorio: cuando se confirme, agregarlo aqui).
# - (M4) Cruce SRS <-> matriz en ambas direcciones contra la fuente LaTeX
#   (docs/requisitos/SRS-v1.1.0.tex si existe; si no, docs/requisitos/SRS.tex;
#   sobreescribible con SRS_TEX=...):
#     a) todo identificador del SRS existe en la matriz;
#     b) todo identificador de la matriz existe en el SRS.
# - Informa la composicion derivada del archivo (M3: N funcionales + M no
#   funcionales = total; la §5 del SRS debe citar estas cifras).
# Uso: scripts/validate-traceability.sh
#      SRS_TEX=docs/requisitos/SRS-v1.1.0.tex scripts/validate-traceability.sh
# =============================================================================
set -euo pipefail

MATRIZ="docs/trazabilidad/matriz.csv"
SRS_TEX="${SRS_TEX:-}"
if [[ -z "$SRS_TEX" ]]; then
    if [[ -f "docs/requisitos/SRS-v1.1.0.tex" ]]; then
        SRS_TEX="docs/requisitos/SRS-v1.1.0.tex"
    else
        SRS_TEX="docs/requisitos/SRS.tex"
    fi
fi

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
NF=0
NFF=0
declare -A SEEN=()
# Salta la cabecera
while IFS= read -r line; do
    line="$(echo "$line" | tr -d '\r')"
    if [[ -z "$line" ]]; then
        continue
    fi

    IFS=',' read -r id tipo prioridad historia caso modulo endpoint prueba tipo_acceso evidencia estado <<<"$line"

    if [[ -z "$id" || -z "$tipo" || -z "$prioridad" ]]; then
        echo "ERROR: fila sin id/tipo/prioridad: $line" >&2
        ERRS=$((ERRS+1))
        continue
    fi

    # (M4/M5) Unicidad de identificadores.
    if [[ -n "${SEEN[$id]:-}" ]]; then
        echo "ERROR: identificador duplicado en la matriz: $id" >&2
        ERRS=$((ERRS+1))
    else
        SEEN[$id]=1
    fi

    if [[ "$tipo" != "Funcional" && "$tipo" != "No funcional" ]]; then
        echo "ERROR: tipo invalido en $id ('$tipo'). Debe ser 'Funcional' o 'No funcional'." >&2
        exit 1
    fi

    if [[ "$tipo" == "Funcional" ]]; then
        NFF=$((NFF+1))
    else
        NF=$((NF+1))
    fi

    # (M4) Vocabulario cerrado de estado (SRS §3.1; tercer valor pendiente).
    if [[ "$estado" != "verificado" && "$estado" != "pendiente" ]]; then
        echo "ERROR: estado invalido en $id ('$estado'). Debe ser 'verificado' o 'pendiente'." >&2
        ERRS=$((ERRS+1))
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

TOTAL=$((NFF+NF))
echo "Composicion (derivada de $MATRIZ): $NFF funcionales + $NF no funcionales = $TOTAL identificadores."

# (M4) Cruce SRS <-> matriz en ambas direcciones.
if [[ ! -f "$SRS_TEX" ]]; then
    echo "ERROR: fuente SRS ausente: $SRS_TEX (M1: el SRS sometido a firma debe estar en el repositorio)." >&2
    ERRS=$((ERRS+1))
else
    # Corpus v1.1.0: solo espacios F y NF (77+10). Los REQ-IF-* de SRS.tex
    # pertenecen al SRS v0.9.0-rc heredado y no forman parte de este corpus.
    mapfile -t SRS_IDS < <(grep -o -E 'REQ-(F|NF)-[0-9]{3}' "$SRS_TEX" | sort -u || true)
    declare -A SRS_SEEN=()
    for sid in ${SRS_IDS[@]+"${SRS_IDS[@]}"}; do
        SRS_SEEN[$sid]=1
        # a) Todo identificador del SRS existe en la matriz.
        if [[ -z "${SEEN[$sid]:-}" ]]; then
            echo "ERROR: $sid esta en el SRS ($SRS_TEX) pero no en la matriz." >&2
            ERRS=$((ERRS+1))
        fi
    done
    # b) Todo identificador de la matriz existe en el SRS.
    for mid in "${!SEEN[@]}"; do
        if [[ -z "${SRS_SEEN[$mid]:-}" ]]; then
            echo "ERROR: $mid esta en la matriz pero no en el SRS ($SRS_TEX)." >&2
            ERRS=$((ERRS+1))
        fi
    done
fi

if [[ "$ERRS" -gt 0 ]]; then
    echo "ERROR: la matriz de trazabilidad tiene $ERRS problema(s)." >&2
    exit 1
fi

echo "OK: matriz de trazabilidad valida ($TOTAL requisitos)."
exit 0
