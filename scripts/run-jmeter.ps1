# Run a JMeter plan against one service variant inside the Compose network.
# Usage: .\scripts\run-jmeter.ps1 -Plan read-heavy.jmx -Service service-c [-Threads 50] [-Ramp 60] [-Duration 600]
param(
    [Parameter(Mandatory)] [string] $Plan,
    [Parameter(Mandatory)] [ValidateSet("service-a", "service-c", "service-d")] [string] $Service,
    [int] $Threads = 50,
    [int] $Ramp = 60,
    [int] $Duration = 600
)
$ErrorActionPreference = "Stop"

$Ports = @{ "service-a" = 8081; "service-c" = 8082; "service-d" = 8083 }
$Root = Resolve-Path (Join-Path $PSScriptRoot "..")

$EnvFile = Join-Path $Root ".env"
if (Test-Path $EnvFile) {
    Get-Content $EnvFile | Where-Object { $_ -match '^\s*[^#].*=' } | ForEach-Object {
        $k, $v = $_ -split '=', 2
        [Environment]::SetEnvironmentVariable($k.Trim(), $v.Trim())
    }
}
if (-not $env:INFLUXDB_TOKEN) { throw "Set INFLUXDB_TOKEN in .env" }

$Network = if ($env:COMPOSE_NETWORK) { $env:COMPOSE_NETWORK } else { "$((Split-Path $Root -Leaf).ToLower())_default" }
$Org = if ($env:INFLUXDB_ORG) { $env:INFLUXDB_ORG } else { "perf" }
$Bucket = if ($env:INFLUXDB_BUCKET) { $env:INFLUXDB_BUCKET } else { "jmeter" }
$Title = "$([IO.Path]::GetFileNameWithoutExtension($Plan))-$Service-$(Get-Date -Format yyyyMMdd-HHmmss)"

docker run --rm --network $Network `
  -v "${Root}\jmeter:/test" -v "${Root}\jmeter-data:/jmeter-data" -w /test justb4/jmeter `
  -n -t $Plan `
  "-JbaseUrl=http://${Service}:$($Ports[$Service])" `
  "-Jthreads=$Threads" "-Jramp=$Ramp" "-Jduration=$Duration" `
  -JcategoriesCsv=/jmeter-data/categories_ids.csv `
  -JitemsCsv=/jmeter-data/items_ids.csv `
  -Jinflux.url=http://influxdb:8086 `
  "-Jinflux.org=$Org" "-Jinflux.bucket=$Bucket" `
  "-Jinflux.token=$($env:INFLUXDB_TOKEN)" `
  "-JtestTitle=$Title" "-JtagTestTitle=$Title"
