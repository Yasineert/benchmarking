# REST Benchmark: Jersey vs Spring MVC vs Spring Data REST

[![CI](https://github.com/Yasineert/benchmarking/actions/workflows/ci.yml/badge.svg)](https://github.com/Yasineert/benchmarking/actions/workflows/ci.yml)
![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-6DB33F?logo=springboot&logoColor=white)
![License](https://img.shields.io/badge/license-MIT-blue)

Performance comparison of three ways to expose the same REST API with Spring Boot, under identical data, load profiles and monitoring.

| Variant | Port | Stack | Approach |
|---|---|---|---|
| **A** | 8081 | Spring Boot + Jersey (JAX-RS) + Hibernate | Hand-written JAX-RS resources |
| **C** | 8082 | Spring Boot + `@RestController` | Hand-written Spring MVC controllers, same service layer as A |
| **D** | 8083 | Spring Boot + Spring Data REST | Repositories exposed automatically (HAL) |

## Architecture

```
                 ┌──────────────┐
  JMeter ───────▶│  service-a   │──┐
  (Docker)       │  service-c   │──┼──▶ PostgreSQL
                 │  service-d   │──┘
                 └──────┬───────┘
                        │ /actuator/prometheus
                        ▼
  InfluxDB ◀── JMeter backend listener      Prometheus
        └──────────────┬──────────────────────┘
                       ▼
                    Grafana
```

## API

All variants expose the same resources, paginated with `page` and `size` (max 200):

| Method | Path | Description |
|---|---|---|
| `GET` | `/categories`, `/categories/{id}` | List / read categories |
| `POST`, `PUT`, `DELETE` | `/categories[/{id}]` | Create / update / delete a category |
| `GET` | `/items`, `/items/{id}`, `/items?categoryId=` | List / read / filter items |
| `POST`, `PUT`, `DELETE` | `/items[/{id}]` | Create / update / delete an item |
| `GET` | `/categories/{id}/items` | Items of a category |

Dataset: 2,000 categories and 100,000 items (about 50 per category). Write payloads are about 1 KB (light) or 5 KB (heavy).

## Getting started

**Requirements:** Docker with Compose, Python 3.10+ (data generator), JDK 21 and Maven (only to build outside Docker).

```bash
git clone https://github.com/Yasineert/benchmarking.git
cd benchmarking
cp .env.example .env            # set passwords and an InfluxDB token
docker compose up --build -d    # services, PostgreSQL, Prometheus, Grafana, InfluxDB
```

Seed the database and generate the JMeter CSV files:

```bash
cd data-generator
pip install -r requirements.txt
python generate_data.py         # reads DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD
```

## Running a load test

```bash
# Linux / macOS
scripts/run-jmeter.sh read-heavy.jmx service-c 50 60 600

# Windows PowerShell
.\scripts\run-jmeter.ps1 -Plan read-heavy.jmx -Service service-c -Threads 50 -Ramp 60 -Duration 600
```

Each run is tagged with a unique `testTitle` so runs can be compared side by side in Grafana.

| Plan | Workload |
|---|---|
| `read-heavy.jmx` | 50% `GET /items`, 20% filter by category, 20% `GET /categories/{id}/items`, 10% `GET /categories` |
| `join-filter.jmx` | 70% `GET /items?categoryId=`, 30% `GET /items/{id}` |
| `heavy-body.jmx` | 50% `POST /items`, 50% `PUT /items/{id}` with 5 KB bodies |
| `mixed.jmx` | Reads and writes on categories and items |

## Monitoring

| Tool | URL | Content |
|---|---|---|
| Grafana | http://localhost:3000 | Dashboards in `monitoring/` (JVM, HTTP latency, JMeter results) |
| Prometheus | http://localhost:9090 | Scrapes each service's metrics endpoint |
| InfluxDB | http://localhost:8086 | JMeter results (bucket `jmeter`) |

Monitoring ports are bound to `127.0.0.1` only.

## Tests and CI

```bash
cd service-a && mvn verify      # same for service-c and service-d
```

Unit tests cover the service layer (paging limits, not-found handling, category changes, default values) with JUnit 5 and Mockito. GitHub Actions builds and tests the three services, builds the Docker images and runs a secret scan with Gitleaks on every push.

## Project structure

```
service-a/        Jersey (JAX-RS) variant
service-c/        Spring MVC variant
service-d/        Spring Data REST variant
data-generator/   Dataset seeding and JMeter CSV generation (Python)
jmeter/           JMeter test plans
monitoring/       Prometheus configuration and Grafana dashboards
scripts/          Load-test runners (Bash and PowerShell)
```

## License

[MIT](LICENSE)
