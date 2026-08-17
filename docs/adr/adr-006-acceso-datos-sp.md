# ADR-006: Estrategia hibrida de acceso a datos (CRUD-ORM + Stored Procedures)

**Estado:** Aceptado

**Fecha:** 2026-08-16

**Contexto:** El plan de la asignatura exige que parte de la logica de
agregaciones y reportes viva en stored procedures de PostgreSQL
(`db/procs/*.sql`), pero prohíbe construccion de SQL dinamico por
concatenacion (P0, Bloque A.2.3). Ademas, el 100% del CRUD se implementa con
Spring Data JPA. Se observo en la Entrega 2 que invocar cursors REFCURSOR de
PostgreSQL desde la capa Java es fragile si no se respeta el ciclo de vida de
la transaccion JDBC.

**Decision:** Se adopta una estrategia hibrida documentada:

1. **CRUD:** Spring Data JPA (`JpaRepository`) — consultas derivadas de
   nombres de metodos. No se usa `@Query` ni `createNativeQuery`.
2. **Agregaciones/reportes:** stored procedures de PostgreSQL, instalados por
   la migracion Flyway `V5__stored_procedures.sql` (contenido generado a
   partir de `db/procs/*.sql`).
3. **Invocacion:** exclusivamente `@NamedStoredProcedureQuery` declarada en la
   entidad + `@Procedure(name=...)` en el repositorio (mecanismo JPA 2.1).
   El cursor de salida se declara con
   `@StoredProcedureParameter(mode = ParameterMode.REF_CURSOR, type = Class.class)`
   — es el unico modo que registra `Types.REF_CURSOR` para pgjdbc y evita el
   error de firma "procedure does not exist".
4. **Transaccionalidad requerida:** los REFCURSOR de PostgreSQL solo son
   legibles dentro de la misma transaccion JDBC (portal con nombre). Por eso
   `ReporteService` y los tests de integracion estan anotados con
   `@Transactional`. Sin esto, pgjdbc devuelve `cursor "<unnamed portal N>"
   does not exist`.
5. **Auditoria:** el script `scripts/audit-sql-dynamic.sh` (CI) rechaza
   EXECUTE IMMEDIATE, sp_executesql, concatenacion `||` en `db/procs`,
   `@Query`/`createNativeQuery` en `src/main` y `ddl-auto=update`.

**Consecuencias:**
- **Positivas:** Demostrable a la asignatura (stored procedures reales con
  agregaciones), cero SQL dinamico (P1), los reportes se prueban contra
  PostgreSQL real en CI (service `postgres:18`).
- **Negativas:** Cada procedimiento requiere 3 piezas sincronizadas
  (archivo SQL → V5 → declaracion JPA). ReporteService debe ser
  transaccional.
- **Riesgos:** Si un futuro desarrollador invoca un REFCURSOR sin
  `@Transactional`, fallara en runtime; mitigado por la documentacion y el
  test de integracion.

**Opciones consideradas:**
1. `FUNCTION RETURNS TABLE` + `@Procedure` (se probo: Hibernate genera
   `CALL`, que solo existe para PROCEDURE en PostgreSQL → "procedure does
   not exist"). Descartada.
2. `PROCEDURE` + `outputParameterName="cur"` en `@Procedure` (Spring Data
   omite el parametro de salida de la llamada → firma incompleta →
   "procedure does not exist"). Descartada.
3. `PROCEDURE` + `@NamedStoredProcedureQuery` + `mode = OUT` + `void.class`
   (Hibernate no adivina el tipo → "Could not determine bind type") o
   `Class.class` (registra VARCHAR → pgjdbc rechaza REF_CURSOR).
   Descartadas.
4. `PROCEDURE` + `@NamedStoredProcedureQuery` + `mode = REF_CURSOR` +
   `type = Class.class` + `@Transactional` (**seleccionada**): 7/7
   integration tests contra PostgreSQL real.