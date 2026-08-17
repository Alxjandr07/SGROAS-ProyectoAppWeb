# ADR-007: Despliegue publico en Render (PaaS)

**Estado:** Aceptado

**Fecha:** 2026-08-16

**Contexto:** La entrega final exige una URL publica con HTTPS valido y
health-check `{"status":"UP"}` con componentes `db` y `redis` UP
(P5). El equipo no dispone de servidores propios ni de presupuesto; se
requiere un PaaS con tier gratuito, autodeploy desde GitHub y PostgreSQL
gestionada.

**Decision:** Se despliega en **Render**:

1. **Web Service** `sgroas-backend`: imagen Docker `ghcr.io/alxjandr07/sgroas`
   (tag `v1.0.0`) o build directo del Dockerfile; puerto 8080; cert TLS
   automatico de Render (HTTPS). Contenedor unico (no blue/green: tier free).
2. **PostgreSQL** gestionada de Render (plan free, 1 GB): la URL se inyecta
   como `SPRING_DATASOURCE_URL` (secretos de Render). Flyway aplica
   `V1..V5` al arrancar.
3. **Redis** como servicio externo gratuito (Redis Cloud / Upstash / Render
   Blueprint) inyectado via `SPRING_DATA_REDIS_HOST/PORT/PASSWORD`.
4. **Pipeline:** push a `main` → GitHub Actions CI verde → autodeploy de
   Render desde la rama `main`.
5. **Patron de configuracion:** 12-factor: las variables de entorno del
   `docker-compose.yml` se replican en el dashboard de Render como secretos;
   `application.properties` conserva valores de desarrollo como defaults.
6. **Health:** `GET /actuator/health` (actuador Spring Boot) expone
   `{"status":"UP","components":{"db":{"status":"UP"},"redis":{"status":"UP"}}}`.
   Render lo usa como health check path.
7. **Ciclo de vida de los datos:** backup diario con `pg_dump` (ver
   `docs/despliegue/BACKUP.md`), retencion 30 dias; restauracion probada y
   documentada en `RUNBOOK.md`.

**Consecuencias:**
- **Positivas:** Certificado HTTPS automatico; cero mantenimiento de
  infraestructura; despliegue reproducible desde el git; `render.yaml`
  (Blueprint) permite reconstruir el entorno completo.
- **Negativas:** Tier free puede hibernar (cold start ~50 s); el storage de
  Postgres free tiene limite de 1 GB; sin alarmas integradas (se cubre con el
  RUNBOOK).
- **Riesgos:** Cambio de proveedor requerriria re-emitir secretos y re-apuntar
  el health check; mitigado porque toda la configuracion vive en repos
  (12-factor) y en `render.yaml`.

**Opciones consideradas:**
1. Render (seleccionado): tier free estable, Postgres managed, autodeploy
   GitHub, sin CLI obligatoria.
2. Railway: buen DX, pero el tier free actual exige card y limites de
   horas; descartado por costo/restricciones.
3. Fly.io: requiere `flyctl` y con tarjeta de credito para volumenes;
   descartado.
4. VPS propio (DigitalOcean/Contabo): poder total pero operacion manual
   (TLS, upgrades, backups) fuera del alcance del equipo; descartado.