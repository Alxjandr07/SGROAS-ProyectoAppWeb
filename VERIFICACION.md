# VERIFICACION — SGROAS Supletorio v1.1.0

Fecha: 2026-09-14
Commit: c09f981
Tag: v1.1.0

---

## P1 — Passwords y secrets movidos a variables de entorno (1.2)

**Orden de verificación:**
```bash
# 1. application.properties NO contiene valores literales
grep -n "SPRING_DATASOURCE_PASSWORD\|JWT_SECRET" src/main/resources/application.properties
# Salida esperada: solo variables ${...}, sin valores literales

# 2. docker-compose.yml usa env vars, no secrets hardcodeados
grep -n "SPRING_DATASOURCE_PASSWORD\|JWT_SECRET" docker-compose.yml
# Salida esperada: solo referencias ${VARIABLE}

# 3. JwtServiceTest compila sin secret hardcodeado
grep -n "JWT_SECRET" src/test/java/ec/edu/uteq/sgroas/security/JwtServiceTest.java
# Salida esperada: System.getenv("JWT_SECRET") con fallback de test

# 4. .env.example tiene placeholders
grep -n "CHANGE_ME\|SPRING_DATASOURCE_PASSWORD\|JWT_SECRET" .env.example
# Salida esperada: 3 líneas con valores CHANGE_ME...
```

**Archivos modificados:**
- `src/main/resources/application.properties` — DB password y JWT secret ahora usan `${VARIABLE}`
- `docker-compose.yml` — secrets referencian variables de entorno
- `.env.example` — placeholders CHANGE_ME para cada secreto
- `src/test/java/ec/edu/uteq/sgroas/security/JwtServiceTest.java` — JWT_SECRET desde env var con test fallback

---

## P2 — k6 corridas crudas versionadas (1.2)

**Orden de verificación:**
```bash
# Verificar que existen al menos 3 corridas crudas por escenario
ls dataset/perf/k*.json | wc -l
# Salida esperada: 13+

# Verificar que cada archivo contiene iteraciones y métricas
head -5 dataset/perf/k6-smoke.json
# Salida esperada: JSON con iteraciones, http_req_duration, etc.
```

**Archivos:** `dataset/perf/k6-*.json` (13 archivos)

---

## P3 — Lighthouse corridas versionadas (1.0)

**Orden de verificación:**
```bash
# Verificar que existen al menos 3 corridas por perfil
ls dataset/lighthouse/lh-*.json | wc -l
# Salida esperada: 9+

# Verificar que contiene scores
grep -l "performance.*[0-9]" dataset/lighthouse/lh-*.json | wc -l
# Salida esperada: 9
```

**Archivos:** `dataset/lighthouse/lh-*.json` (9 archivos)

---

## P4 — Cookie Secure() en todas las respuestas (1.0)

**Orden de verificación:**
```bash
# Verificar que NO existe .secure(cookieSecure) en AuthController
grep -n "\.secure(cookieSecure)" src/main/java/ec/edu/uteq/sgroas/controller/AuthController.java
# Salida esperada: (sin resultados)

# Verificar que SÍ existe .secure(true)
grep -n "\.secure(true)" src/main/java/ec/edu/uteq/sgroas/controller/AuthController.java
# Salida esperada: 4 líneas (4 cookies)
```

**Archivos:** `src/main/java/ec/edu/uteq/sgroas/controller/AuthController.java`

---

## P5 — Métodos en español ≤ 5% (1.4)

**Orden de verificación:**
```bash
# Contar campos privados en español en entidades (0 esperado)
grep -rn "private String nombre\|private String apellido\|private String estado\|private String direccion\|private String telefono\|private String placa\|private String marca\|private String modelo" src/main/java/ec/edu/uteq/sgroas/entity/
# Salida esperada: 0 resultados

# Contar campos en español en DTOs (0 esperado)
grep -rn "getNombre()\|setNombre(\|getApellido()\|setApellido(" src/main/java/ec/edu/uteq/sgroas/dto/
# Salida esperada: 0 resultados

# Verificar que los campos usan @Column(name="...") para preservar columnas DB
grep -n "@Column(name=" src/main/java/ec/edu/uteq/sgroas/entity/User.java | head -5
# Salida esperada: @Column(name="nombre"), @Column(name="rol"), etc.
```

# Contar métodos públicos con nombre en español (0 esperado)
# Métodos de producción (src/main/java): 226, nombre en español: 0
powershell -ExecutionPolicy Bypass -File scripts/check-spanish-methods.ps1
# Salida esperada: Total methods (main + tests): 496 / OK: 0 Spanish method names (0%)

# Verificar que los campos usan @Column(name="...") para preservar columnas DB
grep -n "@Column(name=" src/main/java/ec/edu/uteq/sgroas/entity/User.java | head -5
# Salida esperada: @Column(name="nombre"), @Column(name="rol"), etc.
```

**Medición real (2026-09-15):** 496 métodos (226 de producción + 270 de test), 0 en español (0% ≤ 5%).

**Archivos modificados:**
- `src/main/java/ec/edu/uteq/sgroas/entity/Driver.java` — 11 campos renombrados
- `src/main/java/ec/edu/uteq/sgroas/entity/User.java` — 6 campos renombrados
- `src/main/java/ec/edu/uteq/sgroas/entity/Vehicle.java` — 9 campos renombrados
- `src/main/java/ec/edu/uteq/sgroas/entity/Route.java` — 7 campos renombrados
- `src/main/java/ec/edu/uteq/sgroas/entity/Incident.java` — 8 campos renombrados
- `src/main/java/ec/edu/uteq/sgroas/entity/RouteAssignment.java` — 9 campos renombrados
- `src/main/java/ec/edu/uteq/sgroas/entity/VerificationCode.java` — 6 campos renombrados
- `src/main/java/ec/edu/uteq/sgroas/service/{Driver,Incident,RouteAssignment,Route,Vehicle,User}Service.java` y controladores — método `desactivar` → `deactivate`
- `src/test/java/**` — 178 nombres de métodos de test traducidos al inglés (32 archivos)
- Todos los DTOs, servicios, controladores, repositorios y tests actualizados

---

## P6 — Javadoc ≥ 90% (0.6)

**Orden de verificación:**
```bash
# Número de métodos públicos/protected en src/main (226)
grep -rnE "^\s*(public|protected)\s+" src/main/java/ec/edu/uteq/sgroas/ | wc -l

# Métodos cuya línea anterior es una anotación o nada antes del /** (con Javadoc)
grep -B1 -E "^\s*(public|protected)\s+" src/main/java/ec/edu/uteq/sgroas/ | grep -cE "^\s*\*/\s*$"
# Salida esperada: 226 (100%)
```

**Medición real (2026-09-15):** 226/226 métodos públicos documentados (100%), incluidos los 10 records DTO (Driver*, Incident*, Route*, RouteAssignment*, Vehicle*) y los métodos de servicio `{list,listCached,findById,create,update,deactivate}`, `VerificationCodeService.{generate,canResend,validate}`.

---

## P7 — Títulos de figuras/tablas en inglés (0.9)

**Orden de verificación:**
```bash
# Verificar que NO existen captions en español en el informe final
grep -rn "caption{" docs/informe-final/ | grep -E "Tabla|Figura|Listado|Resumen|Resultados|Distribución|Síntesis|Desglose|trazados|comparación|puntaje|prioridad"
# Salida esperada: 0 resultados

# Verificar que SÍ existen captions en inglés
grep -rn "caption{" docs/informe-final/ | grep -E "Table|Figure|Listing"
# Salida esperada: 6+ resultados
```

**Archivos:** `docs/informe-final/` — captions traducidos al inglés

---

## P8 — Demografía SUS con trazabilidad CSV (0.7)

**Orden de verificación:**
```bash
# Verificar que el script existe y tiene permisos
ls -la scripts/generate-sus-demographics.py
# Salida esperada: archivo existe, ejecutable

# Verificar que genera tabla desde CSV
python3 scripts/generate-sus-demographics.py 2>&1 | head -5
# Salida esperada: tabla con 15 participantes, edades, género
```

**Archivos:**
- `scripts/generate-sus-demographics.py` — genera tabla desde `dataset/sus/sus-raw.csv`
- `dataset/sus/sus-raw.csv` — datos crudos (15 participantes)

---

## P9 — Endpoint asignaciones CRUD en Postman (0.7)

**Orden de verificación:**
```bash
# Verificar que la colección tiene requests para asignaciones
grep -c "asignaciones" docs/postman/coleccion.json
# Salida esperada: 6+

# Verificar que incluye GET, POST, PUT, DELETE
grep -o '"method": "[A-Z]*"' docs/postman/coleccion.json | sort | uniq -c
# Salida esperada: DELETE, GET, POST, PUT presentes
```

**Archivos:** `docs/postman/coleccion.json` — carpeta "Asignaciones" con 6 requests CRUD

---

## P10 — Manifest SHA-256 verificable (0.5)

**Orden de verificación:**
```bash
# Verificar que el manifest tiene archivos
wc -l dataset/MANIFEST.sha256
# Salida esperada: 280+ líneas

# Verificar que el script de verificación existe
ls -la scripts/verify-manifest.ps1
# Salida esperada: archivo existe

# Ejecutar verificación
powershell -ExecutionPolicy Bypass -File scripts/verify-manifest.ps1
# Salida esperada: "Verification passed" o "Todos los archivos verificados correctamente"
```

**Archivos:**
- `dataset/MANIFEST.sha256` — 280 entradas SHA-256
- `scripts/verify-manifest.ps1` — script de verificación
- `scripts/regenerate-manifest.ps1` — script de regeneración

---

## P11 — Instrumento Brooke + consentimientos (0.8)

**Orden de verificación:**
```bash
# Verificar que el instrumento Brooke existe
ls -la dataset/sus/SUS-INSTRUMENT.md
# Salida esperada: archivo existe

# Verificar que tiene 10 ítems
grep -c "^[0-9]\." dataset/sus/SUS-INSTRUMENT.md
# Salida esperada: 10

# Verificar consentimiento informado
ls -la dataset/sus/CONSENT-FORM.md
# Salida esperada: archivo existe

# Verificar registro de consentimiento
grep -c "Confirmo" dataset/sus/CONSENT-REGISTRY.md
# Salida esperada: 15 (1 participante por línea)
```

**Archivos:**
- `dataset/sus/SUS-INSTRUMENT.md` — Cuestionario System Usability Scale (Brooke 1996)
- `dataset/sus/CONSENT-FORM.md` — Consentimiento informado según LOPDP
- `dataset/sus/CONSENT-REGISTRY.md` — Registro de consentimiento de 15 participantes

---

## make verify (EV-2)

**Orden de verificación:**
```bash
make verify
# Salida esperada: "All checks passed" o similar, exit code 0
```

---

## CONTRIBUCIONES.md (EV-4)

Ver archivo `CONTRIBUCIONES.md` en la raíz del repositorio.

---

## Tag v1.1.0 (EV-3)

```bash
git log -1 --format="%H %s (%cs)" v1.1.0
# Salida esperada: c09f981 refactor: update security classes for P5 field renames (2026-09-xx)
```
