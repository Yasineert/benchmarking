$ROOT = "C:\Users\PC\Desktop\rest-benchmark"

docker run --rm --network rest-benchmark_default `
    -v "${ROOT}\jmeter:/test" `
    -v "${ROOT}\jmeter-data:/data" `
    -w /test `
    --entrypoint jmeter `
    justb4/jmeter `
    -n -t read-heavy-simple.jmx `
    -l test-results.jtl `
    -JbaseUrl=service-c `
    -Jthreads=50 -Jramp=60 -Jduration=600