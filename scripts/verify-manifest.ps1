#!/usr/bin/env pwsh
# verify-manifest.ps1 — Equivalente a sha256sum -c MANIFEST.sha256
# Uso: .\scripts\verify-manifest.ps1
# Salida: OK o FAILED por cada archivo, y resumen final.

$ErrorActionPreference = "Stop"
$scriptDir = Split-Path $MyInvocation.MyCommand.Path -Parent
$manifestPath = Join-Path (Join-Path $scriptDir "..") "dataset\MANIFEST.sha256"
$baseDir = Split-Path $manifestPath -Parent

if (-not (Test-Path $manifestPath)) {
    Write-Error "Manifest not found: $manifestPath"
    exit 1
}

$lines = Get-Content $manifestPath
$passed = 0
$failed = 0
$missing = 0

foreach ($line in $lines) {
    $line = $line.Trim()
    if ($line -eq "") { continue }

    # Format: <hash>  <filepath>
    $parts = $line -split '\s+', 2
    if ($parts.Count -lt 2) { continue }

    $expectedHash = $parts[0]
    $filePath = $parts[1]
    $fullPath = Join-Path $baseDir $filePath

    if (-not (Test-Path $fullPath)) {
        Write-Host "FAILED: $filePath (file not found)"
        $missing++
        continue
    }

    $actualHash = (Get-FileHash -Path $fullPath -Algorithm SHA256).Hash.ToLower()

    if ($actualHash -eq $expectedHash.ToLower()) {
        Write-Host "OK: $filePath"
        $passed++
    } else {
        Write-Host "FAILED: $filePath"
        Write-Host "  Expected: $expectedHash"
        Write-Host "  Got:      $actualHash"
        $failed++
    }
}

Write-Host ""
Write-Host "Results: $passed OK, $failed FAILED, $missing MISSING out of $($lines.Count) entries"

if ($failed -gt 0 -or $missing -gt 0) {
    exit 1
}
exit 0
