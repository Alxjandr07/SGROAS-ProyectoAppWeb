#!/usr/bin/env pwsh
$scriptDir = Split-Path $MyInvocation.MyCommand.Path -Parent
$baseDir = Resolve-Path (Join-Path (Join-Path $scriptDir "..") "dataset")
$output = @()
$files = Get-ChildItem -Path $baseDir -Recurse -File | Where-Object { $_.Name -ne 'MANIFEST.sha256' -and $_.Name -ne 'MANIFEST.csv' } | Sort-Object FullName
foreach ($f in $files) {
    $relative = $f.FullName.Substring($baseDir.Path.Length + 1).Replace('\', '/')
    $hash = (Get-FileHash -Path $f.FullName -Algorithm SHA256).Hash.ToLower()
    $output += "$hash  $relative"
}
$manifestPath = Join-Path $baseDir.Path "MANIFEST.sha256"
[System.IO.File]::WriteAllLines($manifestPath, $output)
Write-Host "Manifest regenerated with $($output.Count) entries at $manifestPath"
