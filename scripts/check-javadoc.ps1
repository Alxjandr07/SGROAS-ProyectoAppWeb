$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
$src = Join-Path $root "src\main\java"

$pattern = '^\s*(public|protected)\s+((static|final|synchronized|abstract)\s+)*[\w<>, \.\?\[\]]+\s+(\w+)\s*\('

$total = 0
$doc = 0
$missing = @()
Get-ChildItem -Path $src -Recurse -Filter *.java | ForEach-Object {
    $raw = [System.IO.File]::ReadAllBytes($_.FullName)
    $txt = [System.Text.Encoding]::GetEncoding(28591).GetString($raw)
    $lines = $txt -split "`r?`n"
    for ($i = 0; $i -lt $lines.Count; $i++) {
        $line = $lines[$i]
        $m = [regex]::Match($line, $pattern)
        if (-not $m.Success) { continue }
        $total++
        $j = $i - 1
        while ($j -ge 0 -and $lines[$j].TrimStart().StartsWith("@")) { $j-- }
        if ($j -ge 0 -and $lines[$j].TrimEnd() -match '\*/\s*$') { $doc++ }
        else { $missing += "$($_.FullName.Substring($root.Length)):$($i+1): $($m.Groups[4].Value)" }
    }
}

$pct = if ($total -gt 0) { 100.0 * $doc / $total } else { 0 }
Write-Output "Javadoc coverage: $doc/$total ($([math]::Round($pct,1))%)"
if ($pct -lt 90) {
    Write-Output ("Missing Javadoc ({0}):" -f $missing.Count)
    $missing | ForEach-Object { Write-Output "  $_" }
    Write-Output "FAIL: Javadoc below 90%"
    exit 1
}
Write-Output "OK: Javadoc >= 90%"
