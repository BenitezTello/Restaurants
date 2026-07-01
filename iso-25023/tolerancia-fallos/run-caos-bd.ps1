<#
    RFt1-13 · Prueba de caos: dependencia (Postgres) caída.
    Verifica que, con la BD detenida, la API responde de forma CONTROLADA
    (envelope ApiResponse con 5xx) en vez de colgarse o filtrar un stacktrace.

    ADVERTENCIA: este script DETIENE y luego reinicia el contenedor de Postgres.
    Ejecutar solo en entorno local/dev.

    Uso:
        ./run-caos-bd.ps1
#>
param(
    [string]$BaseUrl   = "http://localhost:8080/api",
    [string]$PgContainer = "restaurants-postgres",
    [string]$Endpoint  = "/v1/restaurants"
)

$ScriptDir  = Split-Path -Parent $MyInvocation.MyCommand.Path
$ResultsDir = Join-Path $ScriptDir "resultados"
if (-not (Test-Path $ResultsDir)) { New-Item -ItemType Directory -Path $ResultsDir | Out-Null }

function Invoke-Case {
    param([string]$Url)
    $res = [ordered]@{ Status = 0; Content = ""; ElapsedMs = 0 }
    $sw = [System.Diagnostics.Stopwatch]::StartNew()
    try {
        $resp = Invoke-WebRequest -Uri $Url -Method GET -UseBasicParsing -TimeoutSec 20
        $res.Status = [int]$resp.StatusCode; $res.Content = $resp.Content
    } catch [System.Net.WebException] {
        $r = $_.Exception.Response
        if ($r -ne $null) {
            $res.Status = [int]$r.StatusCode
            try { $res.Content = (New-Object System.IO.StreamReader($r.GetResponseStream())).ReadToEnd() } catch {}
        } else { $res.Content = $_.Exception.Message }
    } catch { $res.Content = $_.Exception.Message }
    $sw.Stop(); $res.ElapsedMs = $sw.ElapsedMilliseconds
    return $res
}

function Test-Envelope {
    param([string]$Content)
    if ([string]::IsNullOrWhiteSpace($Content)) { return $false }
    try { $o = $Content | ConvertFrom-Json; return ($null -ne $o.PSObject.Properties['success']) } catch { return $false }
}

Write-Host ""
Write-Host "=== RFt1-13 · Caos: Postgres caido ===" -ForegroundColor Cyan

Write-Host "[*] Deteniendo $PgContainer ..." -ForegroundColor Yellow
docker stop $PgContainer | Out-Null
Start-Sleep -Seconds 3

Write-Host "[*] Llamando $Endpoint con la BD caida ..." -ForegroundColor Yellow
$r = Invoke-Case -Url "$BaseUrl$Endpoint"
$env = Test-Envelope -Content $r.Content
$is5xx = ($r.Status -ge 500 -and $r.Status -lt 600)
$noHang = ($r.Status -ne 0)   # respondio (no timeout/crash)
$controlled = $noHang -and ($is5xx -or $r.Status -eq 503) -and $env

$mark = if ($controlled) { "SI" } else { "NO" }
$color = if ($controlled) { "Green" } else { "Red" }
Write-Host ("[{0}] Status={1}  Envelope={2}  Tiempo={3}ms" -f $mark, $r.Status, $env, $r.ElapsedMs) -ForegroundColor $color
if ($r.Content.Length -gt 0) {
    Write-Host ("    Respuesta: {0}" -f ($r.Content.Substring(0, [Math]::Min(200, $r.Content.Length)))) -ForegroundColor DarkGray
}

Write-Host "[*] Reiniciando $PgContainer ..." -ForegroundColor Yellow
docker start $PgContainer | Out-Null
Write-Host "[*] Esperando a que Postgres vuelva a estar healthy..." -ForegroundColor DarkGray
for ($i=0; $i -lt 30; $i++) {
    $h = (docker inspect --format "{{.State.Health.Status}}" $PgContainer 2>$null)
    if ($h -eq "healthy") { break }
    Start-Sleep -Seconds 2
}
Write-Host "[*] Postgres estado: $h" -ForegroundColor DarkGray

$stamp = Get-Date -Format "yyyyMMdd-HHmmss"
$mdPath = Join-Path $ResultsDir "rft1-13-caos-bd-$stamp.md"
@"
# Resultado RFt1-13 - Caos: dependencia (Postgres) caida

- Fecha: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')
- Endpoint probado: $Endpoint
- Status obtenido: $($r.Status)
- Envelope ApiResponse: $env
- Tiempo de respuesta: $($r.ElapsedMs) ms
- Controlado (5xx gestionado, sin colgarse): $mark

> Criterio: con la BD caida el sistema debe responder con un error gestionado
> (envelope ApiResponse, 5xx) y NO colgarse ni exponer un stacktrace crudo.
"@ | Out-File -FilePath $mdPath -Encoding UTF8

Write-Host ""
Write-Host ("Resultado RFt1-13: {0}" -f $mark) -ForegroundColor $(if($controlled){"Green"}else{"Red"})
Write-Host ("Informe: {0}" -f $mdPath) -ForegroundColor DarkGray
