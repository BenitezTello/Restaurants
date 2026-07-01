<#
    RFt-1-G · Evitación de fallos (ISO/IEC 25023)
    Ejecuta la batería de casos de fallo 01–12 contra la API y calcula X = A/B.

    Uso:
        ./run-rft1.ps1
        ./run-rft1.ps1 -BaseUrl "http://localhost:8080/api"

    Requisitos: stack levantado (docker compose up -d). PowerShell 5.1+.
#>
param(
    [string]$BaseUrl   = "http://localhost:8080/api",
    [string]$AdminEmail = "admin@tingo-restaurants.com",
    [string]$AdminPass  = "Admin@1234!"
)

$ErrorActionPreference = "Stop"
$ScriptDir  = Split-Path -Parent $MyInvocation.MyCommand.Path
$ResultsDir = Join-Path $ScriptDir "resultados"
if (-not (Test-Path $ResultsDir)) { New-Item -ItemType Directory -Path $ResultsDir | Out-Null }

# ------------------------------------------------------------------
# Helper: ejecuta una petición y devuelve status + cuerpo, sin lanzar
# excepción en errores HTTP (compatible con PowerShell 5.1).
# ------------------------------------------------------------------
function Invoke-Case {
    param(
        [string]$Method,
        [string]$Url,
        [hashtable]$Headers = @{},
        [string]$Body = $null,
        [switch]$RawBody   # si se pasa, no se fuerza Content-Type JSON válido
    )
    $result = [ordered]@{ Status = 0; Content = "" }
    try {
        $params = @{
            Uri             = $Url
            Method          = $Method
            Headers         = $Headers
            UseBasicParsing = $true
            TimeoutSec      = 15
        }
        if (-not [string]::IsNullOrEmpty($Body)) {
            $params.Body        = $Body
            $params.ContentType = "application/json"
        }
        $resp = Invoke-WebRequest @params
        $result.Status  = [int]$resp.StatusCode
        $result.Content = $resp.Content
    }
    catch [System.Net.WebException] {
        $r = $_.Exception.Response
        if ($null -ne $r) {
            $result.Status = [int]$r.StatusCode
            try {
                $sr = New-Object System.IO.StreamReader($r.GetResponseStream())
                $result.Content = $sr.ReadToEnd()
            } catch { $result.Content = "" }
        } else {
            $result.Status  = 0
            $result.Content = $_.Exception.Message   # timeout / conexión rechazada
        }
    }
    catch {
        $result.Status  = 0
        $result.Content = $_.Exception.Message
    }
    return $result
}

# ¿El cuerpo es un envelope ApiResponse gestionado? (success:false + errorCode/message)
function Test-Envelope {
    param([string]$Content)
    if ([string]::IsNullOrWhiteSpace($Content)) { return $false }
    try {
        $o = $Content | ConvertFrom-Json
        return ($null -ne $o.PSObject.Properties['success']) -and
               (($null -ne $o.PSObject.Properties['errorCode']) -or
                ($null -ne $o.PSObject.Properties['message']))
    } catch { return $false }
}

function New-Uuid { return [guid]::NewGuid().ToString() }

Write-Host ""
Write-Host "=== RFt-1-G · Evitacion de fallos ===" -ForegroundColor Cyan
Write-Host "API: $BaseUrl" -ForegroundColor DarkGray

# ------------------------------------------------------------------
# 0) Sanity check + login ADMIN
# ------------------------------------------------------------------
$ping = Invoke-Case -Method GET -Url "$BaseUrl/v1/restaurants"
if ($ping.Status -eq 0) {
    Write-Host "[X] No se pudo conectar a la API. Levanta el stack: docker compose up -d" -ForegroundColor Red
    exit 1
}

$adminBody = @{ email = $AdminEmail; password = $AdminPass } | ConvertTo-Json
$adminLogin = Invoke-Case -Method POST -Url "$BaseUrl/v1/auth/login" -Body $adminBody
$adminToken = $null
if ($adminLogin.Status -eq 200) {
    try { $adminToken = ($adminLogin.Content | ConvertFrom-Json).data.accessToken } catch {}
}
if (-not $adminToken) {
    Write-Host "[!] No se obtuvo token ADMIN (los casos que lo requieren se omitiran)." -ForegroundColor Yellow
}

# Crear/loguear un CLIENTE para el caso de rol insuficiente (RFt1-08)
$clientToken = $null
$clientEmail = "rft1.cliente@example.com"
$clientPass  = "Cliente1@2026"
$regBody = @{ fullName = "RFt1 Cliente"; email = $clientEmail; password = $clientPass } | ConvertTo-Json
Invoke-Case -Method POST -Url "$BaseUrl/v1/auth/register" -Body $regBody | Out-Null  # 201 o 409 si ya existe
$clientLogin = Invoke-Case -Method POST -Url "$BaseUrl/v1/auth/login" -Body (@{ email = $clientEmail; password = $clientPass } | ConvertTo-Json)
if ($clientLogin.Status -eq 200) {
    try { $clientToken = ($clientLogin.Content | ConvertFrom-Json).data.accessToken } catch {}
}

# ------------------------------------------------------------------
# Definición de casos
# ------------------------------------------------------------------
$cases = New-Object System.Collections.ArrayList

function Add-Case { param($Id,$Desc,$Method,$Url,$Expected,$Headers=@{},$Body=$null,$Skip=$false)
    [void]$cases.Add([ordered]@{
        Id=$Id; Desc=$Desc; Method=$Method; Url=$Url; Expected=$Expected;
        Headers=$Headers; Body=$Body; Skip=$Skip
    })
}

$authClient = if ($clientToken) { @{ Authorization = "Bearer $clientToken" } } else { @{} }

Add-Case "RFt1-01" "Recurso inexistente"          GET  "$BaseUrl/v1/restaurants/$(New-Uuid)" @(404)
Add-Case "RFt1-02" "Identificador malformado"      GET  "$BaseUrl/v1/restaurants/no-es-uuid"  @(400)
Add-Case "RFt1-03" "Cuerpo vacio"                  POST "$BaseUrl/v1/auth/login" @(400) @{} "{}"
Add-Case "RFt1-04" "Formato invalido (email)"      POST "$BaseUrl/v1/auth/login" @(400) @{} (@{ email="abc"; password="x" } | ConvertTo-Json)
Add-Case "RFt1-05" "Credenciales incorrectas"      POST "$BaseUrl/v1/auth/login" @(401) @{} (@{ email=$AdminEmail; password="ClaveMala99!" } | ConvertTo-Json)
Add-Case "RFt1-06" "Acceso sin autenticacion"      POST "$BaseUrl/v1/restaurants" @(401,403) @{} '{"name":"X"}'
Add-Case "RFt1-07" "Token invalido/corrupto"       POST "$BaseUrl/v1/restaurants" @(401,403) @{ Authorization="Bearer basura.invalida.123" } '{"name":"X"}'
Add-Case "RFt1-08" "Autorizacion insuficiente (rol)" DELETE "$BaseUrl/v1/restaurants/$(New-Uuid)" @(403) $authClient $null (-not $clientToken)
Add-Case "RFt1-09" "JSON malformado"               POST "$BaseUrl/v1/auth/login" @(400) @{} '{"email":'
Add-Case "RFt1-10" "Metodo HTTP no permitido"      DELETE "$BaseUrl/v1/auth/login" @(405)
Add-Case "RFt1-11" "Ruta inexistente"              GET  "$BaseUrl/v1/ruta-que-no-existe" @(404)

# ------------------------------------------------------------------
# Ejecución
# ------------------------------------------------------------------
$rows = @()
$A = 0; $B = 0

foreach ($c in $cases) {
    if ($c.Skip) {
        Write-Host ("[-] {0} {1} -> OMITIDO (sin prerequisito)" -f $c.Id, $c.Desc) -ForegroundColor DarkYellow
        $rows += [pscustomobject]@{ Id=$c.Id; Descripcion=$c.Desc; HttpEsperado=($c.Expected -join "/"); HttpObtenido="-"; Envelope="-"; Semantica="-"; Controlado="OMITIDO" }
        continue
    }
    $r = Invoke-Case -Method $c.Method -Url $c.Url -Headers $c.Headers -Body $c.Body
    $env = Test-Envelope -Content $r.Content
    $semanticaOk = $c.Expected -contains $r.Status
    # Metrica RFt-1: un fallo esta CONTROLADO si el sistema respondio sin un fallo
    # serio/critico, es decir sin 5xx y sin colgarse (status != 0). El status exacto
    # esperado y el envelope se reportan aparte como indicadores de calidad.
    $controlled = ($r.Status -ne 0) -and ($r.Status -lt 500)
    $B++
    if ($controlled) { $A++ }

    $mark  = if ($controlled) { "SI" } else { "NO" }
    $color = if ($controlled) { "Green" } elseif ($r.Status -eq 0) { "Red" } else { "DarkYellow" }
    Write-Host ("[{0}] {1} {2} -> {3} (esperado {4}, envelope={5}, semantica={6})" -f $mark, $c.Id, $c.Desc, $r.Status, ($c.Expected -join "/"), $env, $(if($semanticaOk){"OK"}else{"NO"})) -ForegroundColor $color

    $rows += [pscustomobject]@{
        Id=$c.Id; Descripcion=$c.Desc; HttpEsperado=($c.Expected -join "/");
        HttpObtenido=$r.Status; Envelope=$env; Semantica=$(if($semanticaOk){"OK"}else{"NO"}); Controlado=$mark
    }
}

# ------------------------------------------------------------------
# RFt1-12 · Fuerza bruta / rate limiting (usuario falso para no bloquear al admin)
# ------------------------------------------------------------------
$B++
$bfBody = @{ email = "rft1.bruteforce@example.com"; password = "ClaveMala99!" } | ConvertTo-Json
$got429 = $false; $lastStatus = 0
for ($i = 1; $i -le 10; $i++) {
    $rr = Invoke-Case -Method POST -Url "$BaseUrl/v1/auth/login" -Body $bfBody
    $lastStatus = $rr.Status
    if ($rr.Status -eq 429) { $got429 = $true; break }
}
if ($got429) { $A++ }
$bfMark = if ($got429) { "SI" } else { "NO" }
$bfColor = if ($got429) { "Green" } else { "Red" }
Write-Host ("[{0}] RFt1-12 Fuerza bruta -> {1} tras varios intentos (esperado 429)" -f $bfMark, $(if($got429){"429 detectado"}else{"ultimo $lastStatus, sin 429"})) -ForegroundColor $bfColor
$rows += [pscustomobject]@{ Id="RFt1-12"; Descripcion="Fuerza bruta / rate limiting"; HttpEsperado="429"; HttpObtenido=$(if($got429){429}else{$lastStatus}); Envelope="-"; Semantica=$(if($got429){"OK"}else{"NO"}); Controlado=$bfMark }

# ------------------------------------------------------------------
# Resultado
# ------------------------------------------------------------------
$X = if ($B -gt 0) { [math]::Round($A / $B, 4) } else { 0 }
$stamp = Get-Date -Format "yyyyMMdd-HHmmss"
$csvPath = Join-Path $ResultsDir "rft1-$stamp.csv"
$mdPath  = Join-Path $ResultsDir "rft1-$stamp.md"

$rows | Export-Csv -Path $csvPath -NoTypeInformation -Encoding UTF8

$md = @()
$md += "# Resultado RFt-1-G - Evitacion de fallos"
$md += ""
$md += "- Fecha: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')"
$md += "- API: $BaseUrl"
$md += ""
$md += "| ID | Descripcion | HTTP esperado | HTTP obtenido | Envelope | Semantica | Controlado |"
$md += "|----|-------------|---------------|---------------|----------|-----------|------------|"
foreach ($row in $rows) {
    $md += "| $($row.Id) | $($row.Descripcion) | $($row.HttpEsperado) | $($row.HttpObtenido) | $($row.Envelope) | $($row.Semantica) | $($row.Controlado) |"
}
$md += ""
$md += "**A (controlados) = $A**"
$md += ""
$md += "**B (ejecutados) = $B**"
$md += ""
$md += "**RFt-1-G = A/B = $X**"
$md -join "`r`n" | Out-File -FilePath $mdPath -Encoding UTF8

Write-Host ""
Write-Host ("========================================") -ForegroundColor Cyan
Write-Host ("  A (controlados) = {0}" -f $A) -ForegroundColor White
Write-Host ("  B (ejecutados)  = {0}" -f $B) -ForegroundColor White
Write-Host ("  RFt-1-G = A/B   = {0}" -f $X) -ForegroundColor Green
Write-Host ("========================================") -ForegroundColor Cyan
Write-Host ("Informe: {0}" -f $mdPath) -ForegroundColor DarkGray
Write-Host ("CSV:     {0}" -f $csvPath) -ForegroundColor DarkGray
