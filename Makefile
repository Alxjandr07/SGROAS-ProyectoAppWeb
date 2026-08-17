.PHONY: up down test bench audit jacoco versions docs all clean

# =============================================================================
# SGROAS — Makefile
# =============================================================================
# Uso: make up    -> levantar el sistema completo
#      make down  -> detener contenedores
#      make test  -> ejecutar pruebas
#      make bench -> ejecutar benchmarks k6
#      make audit -> auditoria SQL estatico + trazabilidad
#      make jacoco-> regenerar reporte de cobertura
#      make versions -> generar docs/entorno/versions.txt
#      make docs  -> generar artefactos de documentacion
#      make all   -> pipeline completo (R1: reproduccion end-to-end)
#      make clean -> limpieza total
# =============================================================================

up:
	docker compose up --build -d
	@echo "Esperando a que el backend esté listo..."
	@sleep 15
	@echo "Sistema disponible en http://localhost:8080"

down:
	docker compose down -v

test:
	./mvnw test
	@echo "Reporte JaCoCo generado en docs/mediciones/jacoco/"

bench:
	@echo "Ejecutando benchmarks k6 (3 corridas)..."
	k6 run k6/script.js --summary-export docs/mediciones/perf/k01-run1.json
	k6 run k6/script.js --summary-export docs/mediciones/perf/k02-run2.json
	k6 run k6/script.js --summary-export docs/mediciones/perf/k03-run3.json
	@echo "Benchmarks completos. Resultados en docs/mediciones/perf/"

audit:
	@echo "Auditoria: SQL dinamico prohibido..."
	scripts/audit-sql-dynamic.sh
	@echo "Auditoria: trazabilidad end-to-end..."
	scripts/validate-traceability.sh
	@echo "Auditorias completas (exit 0 = OK)."

jacoco:
	./mvnw clean verify
	@echo "Reporte JaCoCo regenerado en docs/mediciones/jacoco/"

versions:
	python scripts/gen-versions.py > docs/entorno/versions.txt
	@echo "Versiones registradas en docs/entorno/versions.txt"

docs: versions
	@echo "Artefactos de documentacion generados."

all: up test bench audit jacoco versions
	@echo "=========================================="
	@echo "PIPELINE COMPLETO (make all) FINALIZADO OK"
	@echo "=========================================="

clean:
	docker compose down -v --rmi all
	@echo "Limpieza completada."
