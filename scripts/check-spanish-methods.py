#!/usr/bin/env python3
"""Equivalent to scripts/check-spanish-methods.ps1.

Checks that public/protected method names in src/main/java and @Test methods
in src/test/java are not Spanish (< 5%). Ports the PowerShell regex logic.
Exit code 0 = OK, 1 = Spanish names present.
"""
import os
import re
import sys

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))

SPANISH_ROOTS = (
    r"^(listar|crear|actualizar|eliminar|desactivar|activar|obtener|buscar|guardar|borrar|"
    r"editar|registrar|cambiar|generar|validar|enviar|resetear|marcar|verificar|consultar|"
    r"devolver|cargar|descargar|inyectar|manejar|usuario|conductor|vehiculo|ruta|incidente|"
    r"asignacion|clave|correo|telefono|direccion|estado|nombre|apellido|placa|marca|modelo|"
    r"anio|capacidad|codigo|descripcion|gravedad|origen|destino|distancia|duracion|color|"
    r"motor|chasis)\b"
)

SPANISH_TEST_ROOTS = (
    r"^(debe|fecha|hora|nivel|tipo|evidencia|disco|terminal|unidad|programa|alerta|email|"
    r"password|token|host|smtp|credencial|intento|codigo|rango|filtro|gravedad|parametro|"
    r"mantenimiento|autenticacion|protegido|respuesta|encontrado|inexistente|nulo|blanco|"
    r"duplicado|inactivo|correcto|refresh|configurado|genera|devuelve|mapea|normaliza|"
    r"conserva|desbloquea|permite|mantiene|cambia|marca|borra|activa|correctamente)\b"
)

HAS_ACCENT = re.compile(r"[áéíóúñÁÉÍÓÚÑ]", re.UNICODE)
SPANISH_RE = re.compile(SPANISH_ROOTS, re.IGNORECASE)

# Same as PowerShell defaults: case-insensitive.
METHOD_PATTERN = re.compile(
    r"^\s*(public|protected)\s+((static|final|synchronized|abstract)\s+)*[\w<>, \.\?\[\]]+\s+(\w+)\s*\(",
    re.IGNORECASE,
)
TEST_ANN_PATTERN = re.compile(r"^\s*@Test")
TEST_METHOD_PATTERN = re.compile(
    r"^\s*(public\s+)?(?:void|boolean|int|String|long|List\S*|ResponseEntity\S*|Map\S*|Optional\S*|[A-Z]\w*)\s+(\w+)\s*\("
)


def is_spanish(name):
    if HAS_ACCENT.search(name):
        return True
    if SPANISH_RE.search(name):
        return True
    return False


def main():
    total = 0
    bad = []

    main_dir = os.path.join(ROOT, "src", "main", "java")
    for dirpath, _dirs, files in os.walk(main_dir):
        for fname in files:
            if not fname.endswith(".java"):
                continue
            path = os.path.join(dirpath, fname)
            with open(path, "r", encoding="latin-1") as fh:
                lines = fh.readlines()
            for i, line in enumerate(lines):
                m = METHOD_PATTERN.match(line)
                if not m:
                    continue
                total += 1
                name = m.group(4)
                if is_spanish(name):
                    rel = os.path.relpath(path, ROOT)
                    bad.append(f"{rel}:{i + 1}: {name}")

    test_dir = os.path.join(ROOT, "src", "test", "java")
    if os.path.isdir(test_dir):
        for dirpath, _dirs, files in os.walk(test_dir):
            for fname in files:
                if not fname.endswith(".java"):
                    continue
                path = os.path.join(dirpath, fname)
                with open(path, "r", encoding="latin-1") as fh:
                    lines = fh.readlines()
                for i, line in enumerate(lines):
                    if not TEST_ANN_PATTERN.match(line):
                        continue
                    for k in range(i, min(i + 6, len(lines))):
                        m = TEST_METHOD_PATTERN.match(lines[k])
                        if m:
                            total += 1
                            name = m.group(2)
                            if is_spanish(name):
                                rel = os.path.relpath(path, ROOT)
                                bad.append(f"{rel}:{i + 1}: {name}")
                            break

    print(f"Total methods (main + tests): {total}")
    if bad:
        print(f"Spanish method names found ({len(bad)}):")
        for item in bad:
            print(f"  {item}")
        print("FAIL: Spanish method names present")
        return 1
    print("OK: 0 Spanish method names (0%)")
    return 0


if __name__ == "__main__":
    sys.exit(main())