$ROOT = "C:\Users\PC\Desktop\rest-benchmark"

docker run --rm --network rest-benchmark_default `
    -v "${ROOT}\jmeter:/test" `
    -v "${ROOT}\jmeter-data:/data" `
    -w /test `
    --entrypoint jmeter `
    justb4/jmeter `
    -n -t read-heavy-metrics.jmx `
    -JbaseUrl=http://service-c:8082 `
    -Jthreads=50 -Jramp=60 -Jduration=600 `
    -JcategoriesCsv=/data/categories_ids.csv `
    -JitemsCsv=/data/items_ids.csv `
    -Jinflux.token=C4afzNpgy3VYb2GZ-10TJsi-wRh_Nu30UDzP-2VTC4FaO4qMM6YUo1vx4YciFHHNR_Qwf2N6WB5DwceU7FYQpg==