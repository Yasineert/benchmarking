# REST Web Service Benchmark Project

This repository contains a complete setup for benchmarking three different REST
service implementations built with Java and Spring Boot. The goal is to
compare the performance of manual controllers versus automatic exposure of
repositories under realistic load. The included components handle data
generation, load testing, monitoring and visualization.

## Variants

Three independent services implement identical endpoints:

| Variant | Tech Stack | Notes |
|--------:|:-----------|:------|
| **A**   | Spring Boot + Jersey (JAX‑RS) + Hibernate | Manual JAX‑RS resources registered via `ResourceConfig`. |
| **C**   | Spring Boot + `@RestController` | Manual Spring MVC controllers using the same service layer as A. |
| **D**   | Spring Boot + Spring Data REST | Automatic exposure of repositories with optional HAL support. |

All variants expose the following routes (with standard pagination using `page`
and `size` query parameters):

* **Categories**: `GET /categories`, `GET /categories/{id}`, `POST /categories`, `PUT /categories/{id}`, `DELETE /categories/{id}`
* **Items**: `GET /items`, `GET /items/{id}`, `GET /items?categoryId=…`, `POST /items`, `PUT /items/{id}`, `DELETE /items/{id}`
* **Relations**: `GET /categories/{id}/items` (inverse association)

The data model consists of 2 000 categories and 100 000 items (≈50 per
category). POST/PUT payloads are either ~1 KB (light) or ~5 KB (heavy).

## Getting Started

### Requirements

* Docker and Docker Compose
* Python 3 (for data generator)

### Build and run the stack

1. **Clone this repository** and navigate into `rest-benchmark`.
2. **Build and start all services** using Docker Compose:

   ```bash
   docker-compose up --build
   ```

   This command builds each service via a multi‑stage Dockerfile and brings up
   PostgreSQL, Prometheus, Grafana and InfluxDB. The services listen on
   ports 8081 (variant A), 8082 (variant C) and 8083 (variant D). Prometheus
   scrapes the services’ metrics from `/prometheus` or `/actuator/prometheus` as
   recommended by the Spring Boot documentation【459617082511492†L1225-L1241】.

3. **Generate the data set**. Once the stack is running, populate the
   PostgreSQL database and produce CSV payloads for JMeter:

   ```bash
   cd data-generator
   pip install -r requirements.txt
   python generate_data.py
   ```

   This script truncates existing tables, inserts 2 000 categories and
   100 000 items, and writes CSV files with existing `id`s into
   `jmeter-data/`. It also generates example JSON payloads for 1 KB and 5 KB
   bodies.

### JMeter load tests

Example JMeter test plans are provided in the `jmeter/` directory. The
`read-heavy.jmx` plan uses a `ThroughputController` to allocate 50 % of
requests to `GET /items?page=&size=50`, 20 % to `GET /items?categoryId=…`,
20 % to `GET /categories/{id}/items` and 10 % to `GET /categories?page=&size=`.

To run this plan from the command line against variant C on port 8082 and
report metrics to InfluxDB v2, execute:

```bash
docker run --rm -v "$PWD/jmeter:/test" -w /test justb4/jmeter \
  -n -t read-heavy.jmx \
  -JbaseUrl=http://localhost:8082 \
  -JcategoriesCsv=../jmeter-data/categories_ids.csv \
  -JitemsCsv=../jmeter-data/items_ids.csv \
  -Jthreads=50 -Jramp=60 -Jduration=600 \
  -Jinflux.url=http://localhost:8086 \
  -Jinflux.org=perf -Jinflux.bucket=jmeter -Jinflux.token=adminadmin
```

The test plan references the Backend Listener client. When using the
InfluxDB 2 backend it is important to supply a unique `TAG_testTitle` so that
Grafana can distinguish multiple runs【459016757048461†L498-L536】. You can also
adjust the queue size and send interval for high request rates as shown in
the referenced Grafana Labs guidance【459016757048461†L535-L545】.

You can create additional JMX plans following the provided examples:

* **join-filter.jmx** – 70 % `GET /items?categoryId=...`, 30 % `GET /items/{id}`
* **mixed.jmx** – combine reads and writes on categories and items (not
  included here, create as needed)
* **heavy-body.jmx** – 50 % `POST /items` and 50 % `PUT /items/{id}` with a 5 KB
  body

### Monitoring

Prometheus scrapes application metrics from each variant. Spring Boot
exposes a dedicated `/actuator/prometheus` endpoint when enabled. The
Prometheus configuration included in `monitoring/prometheus.yml` defines
separate scrape jobs for each service. A sample `scrape_config` from the
official documentation shows how to point Prometheus at the actuator
endpoint【459617082511492†L1225-L1241】.

Grafana is available at <http://localhost:3000> with the default login
(`admin`/`admin`). You can add both Prometheus and InfluxDB data sources and
import dashboards such as the *JMeter test results (InfluxDB2)* dashboard
from Grafana Labs. When configuring the JMeter backend listener, the
recommended settings include specifying the `InfluxdbBackendListenerClient` and
unique test identifiers【459016757048461†L498-L525】.

## Customization

* **Ports** – adjust `server.port` properties or the port mappings in
  `docker-compose.yml` to avoid conflicts.
* **Data volume** – tweak `NUM_CATEGORIES` and `NUM_ITEMS_TOTAL` constants in
  `data-generator/generate_data.py`.
* **Pagination limits** – the repositories in variant D restrict the maximum
  page size to 200 via a `RepositoryRestConfigurer`. Modify the value in
  `RestRepositoryConfig` to suit your tests.

## Sources

Key references used for this setup:

* Spring Boot actuator exposes a Prometheus scraping endpoint at
  `/actuator/prometheus`【459617082511492†L1225-L1241】. Prometheus should be
  configured to scrape this path for each service instance.
* Grafana Labs recommend configuring the JMeter backend listener for
  InfluxDB v2 with explicit parameters such as `influxdbUrl`, `application`
  and `testTitle`【459016757048461†L498-L525】. Queue sizes and send intervals
  should be tuned for high‑throughput runs【459016757048461†L535-L545】.

Use these guidelines as a starting point for your own performance tests and
feel free to extend the project for more complex scenarios.