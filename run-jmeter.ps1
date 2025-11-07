$ROOT = "C:\Users\PC\Desktop\rest-benchmark"

docker run --rm --network rest-benchmark_default `
  -v "$ROOT\jmeter:/test" `
  -v "$ROOT\jmeter-data:/jmeter-data" `
  -w /test justb4/jmeter `
  -n -t read-heavy.jmx `
  -JbaseUrl=http://service-c:8082 `
  -Jthreads=50 -Jramp=60 -Jduration=600 `
  -JcategoriesCsv=/jmeter-data/categories_ids.csv `
  -JitemsCsv=/jmeter-data/items_ids.csv `
  -Jinflux.url=http://influxdb:8086 `
  -Jinflux.org=perf -Jinflux.bucket=jmeter `
  -Jinflux.token=C4afzNpgy3VYb2GZ-10TJsi-wRh_Nu30UDzP-2VTC4FaO4qMM6YUo1vx4YciFHHNR_Qwf2N6WB5DwceU7FYQpg== `
  -JtestTitle=read-heavy-bootC `
  -JtagTestTitle=read-heavy-bootC