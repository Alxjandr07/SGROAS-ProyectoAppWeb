#!/usr/bin/env pwsh
# regenerate-manifest.ps1 — Regenera dataset/MANIFEST.sha256 con rutas relativas a la raiz.
$scriptDir = Split-Path $MyInvocation.MyCommand.Path -Parent
$repoRoot = Resolve-Path (Join-Path $scriptDir "..")
$baseDir = Join-Path $repoRoot "dataset"
$output = @()
$files = Get-ChildItem -Path $baseDir -Recurse -File | Where-Object { $_.Name -ne 'MANIFEST.sha256' -and $_.Name -ne 'MANIFEST.csv' } | Sort-Object FullName
foreach ($f in $files) {
    $relative = $f.FullName.Substring($repoRoot.Path.Length + 1).Replace('\', '/')
    $hash = (Get-FileHash -Path $f.FullName -Algorithm SHA256).Hash.ToLower()
    $output += "$hash  $relative"
}
[System.IO.File]::WriteAllLines((Join-Path $baseDir "MANIFEST.sha256"), $output)
Write-Host "Manifest regenerated with $($output.Count) entries"
