# RUNBOOK.md — Operacion del despliegue SGROAS

**Version:** v1.0.0 · **Fecha:** 2026-08-17

## 1. Arranque / apagado

| Tarea | Como |
|---|---|
| Arrancar / reiniciar | Dashboard Render → sgroas-backend → "Manual Deploy → Deploy latest commit" (o restart del Web Service). Cold start del tier free ≈ 30-60 s |
| Apagar | Dashboard → sgroas-backend → "Suspend" (solo suspende el Web Service; la BD gestionada sigue activa) |
| Comprobar estado | `curl -sS https://<url>/actuator/health` → status UP con db/redis UP |
| Despliegue manual | `HTTP GET https://api.render.com/v1/services/{id}/deploys` con token de API (opcional) |

## 2. Rotacion de secretos

### 2.1 JWT (`APP_JWT_SECRET`)
1. Generar: `openssl rand -base64 48` (64 bytes → 86 chars base64).
2. Dashboard Render → Environment → actualizar `APP_JWT_SECRET` → guardar
   (Render redepliega automaticamente).
3. **Efecto:** todos los tokens JWT emitidos quedan invalidos (firma nueva);
   los refresh tokens almacenados en Redis tambien dejan de validar.
   Impacto: sesiones activas finalizadas (esperado en rotacion).
4. Verificar: login de prueba → nuevo JWT → `GET /api/...` 200.

### 2.2 Credenciales de BD (Postgres managed)
1. Render → Postgres → "Reset database credentials" (genera user/password
   nuevos y revoca los viejos).
2. Actualizar `SPRING_DATASOURCE_USERNAME` / `SPRING_DATASOURCE_PASSWORD`
   en el Web Service y redeploy.
3. Verificar: `curl /actuator/health` muestra `db: UP`; `make up` local no
   se afecta (usa credenciales locales).

### 2.3 Redis
1. Rotar password en el proveedor de Redis.
2. Actualizar `SPRING_DATA_REDIS_PASSWORD` en el Web Service → redeploy.
3. Verificar: health `redis: UP`; sesiones/refresh tokens nuevos.

## 3. Rotacion de contenedores (deploy de nueva version)

1. CI verde en `main` → Render autodeploy (o deploy manual).
2. Durante el deploy Render lanza el nuevo contenedor y, si el health check
   `/actuator/health` pasa, drena el anterior.
3. Si el health falla 3 veces consecutivas, Render revierte al deploy
   anterior (rollback automatico).
4. Rollback manual: Dashboard → "Deploys" → previa imagen/sha → "Deploy".

## 4. Restauracion de datos

1. Ver `docs/despliegue/BACKUP.md` para obtener el backup mas reciente.
2. Restaurar:
   ```
   psql "$DATABASE_URL_RENDER" -c "DROP SCHEMA public CASCADE; CREATE SCHEMA public;"
   cat backup-prod-YYYY-MM-DD.sql.gz | gunzip | psql "$DATABASE_URL_RENDER"
   ```
   (o restaurar en una BD temporal primero y validar conteos.)
3. Al reiniciar el backend, Flyway valida el esquema (las migraciones V1-V5
   ya estan aplicadas; solo reordena el historial)
4. Verificar: `curl /actuator/health` → UP; consultar `/api/reportes/*` con
   datos restaurados.

## 5. Incidentes frecuentes

| Sintoma | Causa probable | Accion |
|---|---|---|
| health rojo `db` | secretos de BD desactualizados o BD suspendida | rotar secretos (2.2) |
| health rojo `redis` | password de Redis rotada sin actualizar | rotar secretos (2.3) |
| cold start lento | tier free hiberna | aceptar ~50 s; opcional: plan paided |
| 401 en login | JWT secret cambiado | sesion nueva (esperado) |
| deploy falla en V5 | firma SP desincronizada con entidades | verificar CATALOGO-SP.md y vuelta atras del commit