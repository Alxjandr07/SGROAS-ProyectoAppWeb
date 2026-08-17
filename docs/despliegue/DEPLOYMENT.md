# DEPLOYMENT.md — Despliegue publico de SGROAS en Render

**Version:** v1.0.0 · **Fecha:** 2026-08-17 · **Estado:** OPERATIVO

## 1. Proveedor y topologia

| Servicio | Proveedor | Plan | Artefacto |
|---|---|---|---|
| `sgroas-backend` (Web) | Render | Free | `ghcr.io/alxjandr07/sgroas:v1.0.0` / build Dockerfile |
| PostgreSQL `sgroas-db` | Render (managed) | Free 1 GB | migraciones Flyway `V1..V5` al arrancar |
| Redis | Render Blueprint / externo | Free | dependencia de cache y refresh tokens |

```
Internet → Render TLS (HTTPS)
             │
             └─ sgroas-backend :8080  (Web Service, autodeploy desde main)
                        ├── PostgreSQL (managed)  → SPRING_DATASOURCE_URL
                        └── Redis                 → SPRING_DATA_REDIS_HOST/PORT
```

## 2. Variables de entorno (secretos en dashboard de Render)

| Variable | Valor de referencia | Nota |
|---|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://<host>:5432/sgroas_db` | secretos de Render Postgres |
| `SPRING_DATASOURCE_USERNAME` | `<user>` | idem |
| `SPRING_DATASOURCE_PASSWORD` | `<password>` | idem |
| `SPRING_DATA_REDIS_HOST` | `<redis-host>` | idem |
| `SPRING_DATA_REDIS_PORT` | `6379` | idem |
| `SPRING_DATA_REDIS_PASSWORD` | `<redis-pass>` | si aplica |
| `APP_JWT_SECRET` | generar con `openssl rand -base64 48` | **nunca** el default de desarrollo |
| `APP_JWT_EXPIRATION_MS` | `3600000` | 1 h |
| `APP_JWT_REFRESH_EXPIRATION_MS` | `604800000` | 7 dias |
| `SPRING_PROFILES_ACTIVE` | (vacío) | usa application.properties |

> El default de `application.properties` (localhost:5433) es SOLO desarrollo:
> el despliegue inyecta secretos que tienen prioridad.

## 3. Pasos de reproduccion

1. **Imagen contenedor** (repositorio GHCR alternativo al build inline):
   ```
   docker build -t ghcr.io/alxjandr07/sgroas:v1.0.0 .
   docker push ghcr.io/alxjandr07/sgroas:v1.0.0
   docker inspect --format='{{index .RepoDigests 0}}' ghcr.io/alxjandr07/sgroas:v1.0.0
   ```
2. **Blueprint Render:** subir `render.yaml` del repo → "New → Blueprint"
   → seleccionar `SGROAS-ProyectoAppWeb` → Render crea Postgres + Redis +
   Web Service con el health check `/actuator/health`.
3. **Secretos:** en el dashboard del Web Service → Environment, pegar la
   tabla de la seccion 2 (generar `APP_JWT_SECRET` nuevo).
4. **Autodeploy:** cada push a `main` con CI verde reconstruye el servicio.
   Deploy manual: "Manual Deploy → Deploy latest commit".
5. **Verificacion:**
   ```
   curl -sS https://<url>/actuator/health
   # → {"status":"UP","components":{"db":{"status":"UP"},"redis":{"status":"UP"}}}
   curl -sS -o /dev/null -w "%{http_code}\n" https://<url>/api/docs
   # → 200
   ```
6. **Primera carga de datos (opcional):** la BD gestionada arranca vacia
   (Flyway crea esquema); la informacion semilla vive en la BD local
   (restaurar con `docs/despliegue/BACKUP.md`, seccion Restauracion, o
   poblaria la app en uso).

## 4. URL publica y evidencias

| Item | Valor |
|---|---|
| URL base | `https://sgroas-backend.onrender.com` |
| Health | `https://sgroas-backend.onrender.com/actuator/health` → `{"status":"UP"}` (db, redis UP) |
| OpenAPI | `https://sgroas-backend.onrender.com/api/docs` |
| Capturas CI | `.github/workflows/ci.yml` (corridas verdes en Actions) |

## 5. Frontend

El frontend Angular se sirve desde su propio despliegue (ver plan de Maria);
el backend expone CORS para el origen del frontend. La URL base del API del
frontend se configura en `frontend/src/environments/environment.prod.ts`.