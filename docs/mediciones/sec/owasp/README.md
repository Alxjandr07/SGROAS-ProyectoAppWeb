# Evidencia OWASP — controles mínimos (Bloque A.1 / P3)

Los seis controles OWASP mínimos verificados con `curl` reproducible se archivan en `docs/mediciones/sec/`:

- `A01-acceso.sh` + `A01-acceso.txt` / `A01-control-acceso-roto.sh` — A01 Broken Access Control
- `A02-fallo-cripto.sh` + `A02-criptografia.txt` — A02 Cryptographic Failures
- `A03-inyeccion.sh` + `A03-inyeccion.txt` — A03 Injection
- `A05-malconfiguracion.sh` + `A05-headers.txt` + `A05-login-response.json` — A05 Security Misconfiguration
- `A07-rate-limit.sh` + `A07-rate-limit.txt` — A07 Identification & Authentication Failures
- `A09-monitoreo.sh` + `A09-logs.txt` — A09 Logging & Monitoring Failures

Cada `.sh` es reproducible contra la URL pública (Render) y deja su `.txt`/`.json` como evidencia. Este directorio `owasp/` existe por compatibilidad con el Listing 1 (p.17) — el contenido real está en el nivel superior `sec/` por histórico del proyecto.
