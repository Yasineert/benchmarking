$ROOT = "C:\Users\PC\Desktop\rest-benchmark"

$env:JMETER_ARGS = @(
    '-n',
    '-t', 'read-heavy-fixed.jmx',
    '-Jthreads=50',
    '-Jramp=60',
    '-Jduration=600',
    '-Jinflux.token=C4afzNpgy3VYb2GZ-10TJsi-wRh_Nu30UDzP-2VTC4FaO4qMM6YUo1vx4YciFHHNR_Qwf2N6WB5DwceU7FYQpg=='
) -join ' '

docker run --rm --network rest-benchmark_default `
    -v "${ROOT}\jmeter:/test" `
    -v "${ROOT}\jmeter-data:/data" `
    -w /test `
    --entrypoint sh `
    justb4/jmeter `
    -c "jmeter $env:JMETER_ARGS"