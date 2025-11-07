$ROOT = "C:\Users\PC\Desktop\rest-benchmark"

$env:JMETER_ARGS = @(
    '-n',
    '-t', 'read-heavy-no-influx.jmx',
    '-JbaseUrl=http://service-c:8082',
    '-Jthreads=50',
    '-Jramp=60',
    '-Jduration=600',
    '-JcategoriesCsv=/jmeter-data/categories_ids.csv',
    '-JitemsCsv=/jmeter-data/items_ids.csv'
) -join ' '

docker run --rm --network rest-benchmark_default `
    -v "${ROOT}\jmeter:/test" `
    -v "${ROOT}\jmeter-data:/jmeter-data" `
    -w /test `
    justb4/jmeter `
    /bin/sh -c "jmeter $env:JMETER_ARGS"