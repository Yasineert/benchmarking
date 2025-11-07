param(
    [Parameter(Mandatory=$true)]
    [string]$TestFile,
    
    [Parameter(Mandatory=$true)]
    [int]$Threads,
    
    [Parameter(Mandatory=$true)]
    [int]$Duration,
    
    [Parameter(Mandatory=$true)]
    [int]$Ramp,

    [Parameter(Mandatory=$false)]
    [string]$BaseUrl = "http://service-c:8082"
)

$ROOT = $PSScriptRoot
$TestFileName = Split-Path $TestFile -Leaf
$TestName = [System.IO.Path]::GetFileNameWithoutExtension($TestFileName)

Write-Host "Running test: $TestName with $Threads threads, $Duration seconds duration, $Ramp seconds ramp-up"

$args = @(
    "--rm",
    "--network", "rest-benchmark_default",
    "-v", "${ROOT}/jmeter:/test",
    "-v", "${ROOT}/jmeter-data:/jmeter-data",
    "-w", "/test",
    "justb4/jmeter",
    "-n",
    "-t", $TestFileName,
    "-JbaseUrl=$BaseUrl",
    "-Jthreads=$Threads",
    "-Jramp=$Ramp",
    "-Jduration=$Duration",
    "-JcategoriesCsv=/jmeter-data/categories_ids.csv",
    "-JitemsCsv=/jmeter-data/items_ids.csv",
    "-Jinflux.url=http://influxdb:8086",
    "-Jinflux.org=perf",
    "-Jinflux.bucket=jmeter",
    "-Jinflux.token=C4afzNpgy3VYb2GZ-10TJsi-wRh_Nu30UDzP-2VTC4FaO4qMM6YUo1vx4YciFHHNR_Qwf2N6WB5DwceU7FYQpg==",
    "-JtestTitle=$TestName",
    "-JtagTestTitle=$TestName"
)

docker run $args