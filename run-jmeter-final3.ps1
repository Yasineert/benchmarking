$ROOT = "C:\Users\PC\Desktop\rest-benchmark"

docker run --rm --network rest-benchmark_default `
    -v "${ROOT}\jmeter:/test" `
    -v "${ROOT}\jmeter-data:/jmeter-data" `
    -w /test `
    --entrypoint jmeter `
    justb4/jmeter `
    -n -t read-heavy-no-influx.jmx `
    -JbaseUrl=http://service-c:8082 `
    -Jthreads=50 -Jramp=60 -Jduration=600 `
    -JcategoriesCsv=/jmeter-data/categories_ids.csv `
    -JitemsCsv=/jmeter-data/items_ids.csv