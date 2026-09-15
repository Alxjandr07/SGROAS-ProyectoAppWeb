$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot

$spanishRoots = '^(listar|crear|actualizar|eliminar|desactivar|activar|obtener|buscar|guardar|borrar|editar|registrar|cambiar|generar|validar|enviar|resetear|marcar|verificar|consultar|devolver|cargar|descargar|inyectar|manejar|usuario|conductor|vehiculo|ruta|incidente|asignacion|clave|correo|telefono|direccion|estado|nombre|apellido|placa|marca|modelo|anio|capacidad|codigo|descripcion|gravedad|origen|destino|distancia|duracion|color|motor|chasis)\b'
$spanishTestRoots = '^(debe|fecha|hora|nivel|tipo|evidencia|disco|terminal|unidad|programa|alerta|email|password|token|host|smtp|credencial|intento|codigo|rango|filtro|gravedad|parametro|mantenimiento|autenticacion|protegido|respuesta|encontrado|inexistente|nulo|blanco|duplicado|inactivo|correcto|refresh|configurado|genera|devuelve|mapea|normaliza|conserva|desbloquea|permite|mantiene|cambia|marca|borra|activa|correctamente)\b'
$accents = [char]0xE1,[char]0xE9,[char]0xED,[char]0xF3,[char]0xFA,[char]0xF1,[char]0xC1,[char]0xC9,[char]0xCD,[char]0xD3,[char]0xDA,[char]0xD1
$hasAccent = '[' + ($accents -join '') + ']'

$methodPattern = '^\s*(public|protected)\s+((static|final|synchronized|abstract)\s+)*[\w<>, \.\?\[\]]+\s+(\w+)\s*\('
$testAnnPattern = '^\s*@Test'
$testMethodPattern = '^\s*(public\s+)?(?:void|boolean|int|String|long|List\S*|ResponseEntity\S*|Map\S*|Optional\S*|[A-Z]\w*)\s+(\w+)\s*\('

function Test-Name([string]$name) {
    if ($name -match $hasAccent) { return $true }
    if ($name -match $spanishRoots) { return $true }
    return $false
}

$total = 0
$bad = @()

# src/main/java: metodos public/protected
Get-ChildItem -Path (Join-Path $root "src\main\java") -Recurse -Filter *.java | ForEach-Object {
    $lines = Get-Content -LiteralPath $_.FullName
    for ($i = 0; $i -lt $lines.Count; $i++) {
        $m = [regex]::Match($lines[$i], $methodPattern)
        if (-not $m.Success) { continue }
        $total++
        $name = $m.Groups[4].Value
        if (Test-Name $name) { $bad += "$($_.FullName.Substring($root.Length)):$($i+1): $name" }
    }
}

# src/test/java: metodos bajo @Test
Get-ChildItem -Path (Join-Path $root "src\test\java") -Recurse -Filter *.java | ForEach-Object {
    $lines = Get-Content -LiteralPath $_.FullName
    for ($i = 0; $i -lt $lines.Count; $i++) {
        if (-not ($lines[$i] -match $testAnnPattern)) { continue }
        for ($k = $i; $k -lt $lines.Count -and $k -lt $i + 6; $k++) {
            $m = [regex]::Match($lines[$k], $testMethodPattern)
            if ($m.Success) {
                $total++
                $name = $m.Groups[2].Value
                if (Test-Name $name) { $bad += "$($_.FullName.Substring($root.Length)):$($i+1): $name" }
                break
            }
        }
    }
}

Write-Output "Total methods (main + tests): $total"
if ($bad.Count -gt 0) {
    Write-Output "Spanish method names found ($($bad.Count)):"
    $bad | ForEach-Object { Write-Output "  $_" }
    Write-Output "FAIL: Spanish method names present"
    exit 1
}
Write-Output "OK: 0 Spanish method names (0%)"
