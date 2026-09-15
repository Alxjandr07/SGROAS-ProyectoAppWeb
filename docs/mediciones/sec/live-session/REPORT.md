# Verificación en vivo: sesión con cookie segura y endpoint de asignaciones (P6/P16)

**Fecha:** 2026-09-14
**Entorno:** deploy público en Render (`https://sgroas-backend.onrender.com`, TLS + Cloudflare).

Este reporte documenta la evidencia en vivo (commit evaluado) de dos puntos de la
rúbrica: el acceso al endpoint de asignaciones con sesión iniciada (P6) y la cookie
segura de sesión `Secure + HttpOnly + SameSite=Strict` contra el deploy (P16).

## Resumen de resultados

| Prueba | Endpoint | Resultado |
|---|---|---|
| Health público | `GET /actuator/health` | **200 OK** (db UP, redis UP, liveness/readiness UP) |
| Login (sesión) | `POST /api/auth/login` | **200 OK** + `Set-Cookie` segura (detalle abajo) |
| Sesión activa | `GET /api/auth/me` | **200 OK** → `Admin SGROAS / ROLE_ADMIN` |
| Asignaciones con sesión | `GET /api/asignaciones?page=0&size=10` | **200 OK**, 8 asignaciones (5 en página 0) |
| Asignaciones sin sesión | `GET /api/asignaciones` | **403** (rechazado por Cloudflare/proxy antes de Spring) |

## Cookie de sesión (P16)

El login devuelve dos cookies con las tres banderas de seguridad:

```text
Set-Cookie: access_token=<JWT>; Path=/; Max-Age=3600; Expires=...; Secure; HttpOnly; SameSite=Strict
Set-Cookie: refresh_token=<UUID>; Path=/api/auth; Max-Age=604800; Expires=...; Secure; HttpOnly; SameSite=Strict
```

- `HttpOnly`: el JWT no es accesible desde JavaScript (mitiga XSS, OWASP A03).
- `Secure`: solo se transmite por HTTPS (el deploy usa TLS automático de Render).
- `SameSite=Strict`: no se envía en peticiones entre sitios (mitiga CSRF).
- El token `access_token` vence en 1 hora (claim `exp` = `iat` + 3600) y el
  `refresh_token` en 7 días.
- El body de login (`SessionResponse`) **no** contiene el JWT: solo
  `{nombre, email, rol, expiresIn}`; el token se transporta exclusivamente por cookie.

Este flujo coincide con `AuthController.java` (métodos `login`/`cookies`), la
`application.properties` del deploy (`COOKIE_SECURE=true` vía `render.yaml`) y el
filtro `JwtAuthenticationFilter`.

## Endpoint de asignaciones con sesión (P6)

`GET /api/asignaciones?page=0&size=10` con la cookie `access_token` responde
`200 OK` y un `Page<RouteAssignmentResponse>` con `totalElements: 8` (5 en la
primera página): cada item incluye `conductorNombre`, `vehiculoPlaca`, `rutaNombre`,
`estado` y `fechaAsignacion` (id 1–5, estados ACTIVA/COMPLETADA/CANCELADA).

El endpoint pertenece al `RouteAssignmentController` (`@RequestMapping("/api/asignaciones")`)
y **no** está en las rutas `permitAll` de `SecurityConfig`, por lo que exige sesión
autenticada (cualquier petición sin cookie válida es rechazada; en el deploy el
bloqueo lo aplica primero el proxy/Cloudflare con `403` y `Content-Length: 0`).

## Evidencia cruda (artefactos versionados)

| Artefacto | Contenido |
|---|---|
| `login-response.txt` | Respuesta completa del login (headers `Set-Cookie` + body) |
| `auth-me.json` | Body de `GET /api/auth/me` con la cookie (sesión activa) |
| `asignaciones.json` | Body de `GET /api/asignaciones?page=0&size=10` con la cookie |
| `sin-sesion-403.txt` | Respuesta de `GET /api/asignaciones` sin cookie (403) |

## Reproducibilidad

```sh
# 1. Login (guarda las cookies de sesión)
curl -s -i -c cookies.txt -H 'Content-Type: application/json' \
  -d '{"email":"admin@sgroas.com","password":"admin123"}' \
  https://sgroas-backend.onrender.com/api/auth/login

# 2. Sesión activa
curl -s -b cookies.txt https://sgroas-backend.onrender.com/api/auth/me

# 3. Asignaciones con sesión
curl -s -b cookies.txt "https://sgroas-backend.onrender.com/api/asignaciones?page=0&size=10"

# 4. Sin sesión (debe rechazarse)
curl -s -o /dev/null -w '%{http_code}\n' \
  "https://sgroas-backend.onrender.com/api/asignaciones?page=0&size=10"
```

(En la consola del evaluador, reemplace las credenciales de demostración por las que
prefiera; están documentadas en `README.md` y en `V2__seed.sql`.)