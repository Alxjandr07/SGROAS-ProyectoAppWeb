#!/usr/bin/env pwsh
# SGROAS Verification Script (EV-2)
# Equivalente a: make verify
# Exit code 0 = all checks passed, non-zero = failure

$ErrorActionPreference = "Continue"
$failed = 0

Write-Host "=== SGROAS Verification ===" -ForegroundColor Cyan
Write-Host ""

# [P1] Hardcoded secrets
Write-Host "[P1] Checking hardcoded secrets..." -ForegroundColor Yellow
$p1a = Get-Content "src\main\resources\application.properties" | Select-String "SPRING_DATASOURCE_PASSWORD=\S+" | Where-Object { $_ -notmatch '\$\{' }
$p1b = Get-Content "src\main\resources\application.properties" | Select-String "JWT_SECRET=\S+" | Where-Object { $_ -notmatch '\$\{' }
if ($p1a -or $p1b) { Write-Host "  FAIL: hardcoded secrets found in application.properties" -ForegroundColor Red; $failed++ } else { Write-Host "  OK" -ForegroundColor Green }

# [P4] Cookie Secure(true)
Write-Host "[P4] Checking cookie Secure(true)..." -ForegroundColor Yellow
$p4bad = Select-String -Path "src\main\java\ec\edu\uteq\sgroas\controller\AuthController.java" -Pattern "\.secure\(cookieSecure\)"
$p4good = (Select-String -Path "src\main\java\ec\edu\uteq\sgroas\controller\AuthController.java" -Pattern "\.secure\(true\)").Count
if ($p4bad) { Write-Host "  FAIL: .secure(cookieSecure) found" -ForegroundColor Red; $failed++ } else { Write-Host "  OK ($p4good .secure(true) calls)" -ForegroundColor Green }

# [P5] Spanish fields in entities
Write-Host "[P5] Checking Spanish field names in entities..." -ForegroundColor Yellow
$p5 = Get-ChildItem "src\main\java\ec\edu\uteq\sgroas\entity\*.java" | Select-String "private String (nombre|apellido|estado|direccion|telefono|placa|marca|modelo)\b"
if ($p5) { Write-Host "  FAIL: Spanish fields found" -ForegroundColor Red; $failed++ } else { Write-Host "  OK - no Spanish fields in entities" -ForegroundColor Green }

# [P7] Spanish captions
Write-Host "[P7] Checking Spanish captions in informe..." -ForegroundColor Yellow
$p7 = Select-String -Path "docs\informe-final\*.md" -Pattern "\b(Tabla|Figura|Listado|cuadro|figura|listado)\b" -ErrorAction SilentlyContinue
if ($p7) { Write-Host "  FAIL: Spanish captions found" -ForegroundColor Red; $failed++ } else { Write-Host "  OK" -ForegroundColor Green }

# [P10] MANIFEST verification
Write-Host "[P10] Verifying MANIFEST.sha256..." -ForegroundColor Yellow
$manifest = Get-Content "dataset\MANIFEST.sha256" | Where-Object { $_ -match '\S' }
$allOk = $true
foreach ($line in $manifest) {
    $parts = $line -split '\s{2,}'
    if ($parts.Count -ge 2) {
        $hash = $parts[0]
        $file = $parts[1]
        if (Test-Path $file) {
            $actual = (Get-FileHash -Algorithm SHA256 $file).Hash.ToLower()
            if ($hash -ne $actual) { Write-Host "  MISMATCH: $file" -ForegroundColor Red; $allOk = $false; $failed++ }
        } else {
            Write-Host "  MISSING: $file" -ForegroundColor Red; $allOk = $false; $failed++
        }
    }
}
if ($allOk) { Write-Host "  OK ($($manifest.Count) files verified)" -ForegroundColor Green }

# [P11] SUS instrument and consent
Write-Host "[P11] Checking SUS instrument and consent..." -ForegroundColor Yellow
$p11files = @("dataset\sus\SUS-INSTRUMENT.md", "dataset\sus\CONSENT-FORM.md", "dataset\sus\CONSENT-REGISTRY.md")
foreach ($f in $p11files) {
    if (Test-Path $f) { Write-Host "  $f exists" -ForegroundColor Green } else { Write-Host "  MISSING: $f" -ForegroundColor Red; $failed++ }
}

# [P9] Postman collection
Write-Host "[P9] Checking Postman collection..." -ForegroundColor Yellow
$p9 = (Select-String -Path "docs\postman\coleccion.json" -Pattern "asignaciones").Count
Write-Host "  $p9 assignment endpoints found" -ForegroundColor Green

Write-Host ""
if ($failed -eq 0) {
    Write-Host "==========================================" -ForegroundColor Green
    Write-Host "ALL CHECKS PASSED" -ForegroundColor Green
    Write-Host "==========================================" -ForegroundColor Green
    exit 0
} else {
    Write-Host "==========================================" -ForegroundColor Red
    Write-Host "FAILED: $failed check(s) failed" -ForegroundColor Red
    Write-Host "==========================================" -ForegroundColor Red
    exit 1
}
