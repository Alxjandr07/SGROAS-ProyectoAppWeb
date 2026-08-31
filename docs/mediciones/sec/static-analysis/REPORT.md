# Análisis estático — concatenación SQL (SpotBugs + FindSecBugs)

**Fecha:** 2026-08-31  
**Herramienta:** `spotbugs-maven-plugin:4.8.6.6` + `findsecbugs-plugin:1.12.0`  
**Regla:** `SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE` (OWASP SQLi prevention)  
**Filtro:** `scripts/spotbugs-include.xml`  
**Comando:** `./mvnw spotbugs:check -Dspotbugs.failOnError=true` (fase `verify`)  
**Salida:** `docs/mediciones/sec/static-analysis/spotbugsXml.xml`

## Resultado

- **Bugs encontrados:** **0**
- **Hallazgos altos (High):** 0
- **Hallazgos medios/bajos:** 0
- **Estado:** **PASA** — no hay concatenación de entrada de usuario en JPQL/HQL/SQL nativo ni en `@Procedure`/`@NamedStoredProcedureQuery`.

## Evidencia complementaria

- `scripts/audit-sql-dynamic.sh` ejecutado el 2026-08-31: **0 violaciones**
  - Revisa `db/procs/*.sql` contra `EXECUTE IMMEDIATE`, `sp_executesql` y concatenación `||`/`+` en SQL dinámico
  - Revisa `src/main/java/**.java` contra `createNativeQuery("..." +` y `+ " SELECT`/`FROM`/`WHERE`
  - Resultado: sin hallazgos (ver `audit.log` en este directorio si se genera en CI)

## Artefactos

- `spotbugsXml.xml` — reporte XML de SpotBugs (BugCollection vacía)
- Este `REPORT.md` — resumen humano para el criterio P3/A.2.3

> Si se requiere reproducir: `git clone ... && ./mvnw verify` genera este directorio en `docs/mediciones/sec/static-analysis/` y falla el build si aparece un `SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE`.
