#!/usr/bin/env bash
# Run a JMeter plan against one service variant inside the Compose network.
# Usage: scripts/run-jmeter.sh <plan.jmx> <service-a|service-c|service-d> [threads] [ramp] [duration]
set -euo pipefail

PLAN="${1:?plan file, e.g. read-heavy.jmx}"
SERVICE="${2:?service-a | service-c | service-d}"
THREADS="${3:-50}"
RAMP="${4:-60}"
DURATION="${5:-600}"

case "$SERVICE" in
  service-a) PORT=8081 ;;
  service-c) PORT=8082 ;;
  service-d) PORT=8083 ;;
  *) echo "Unknown service: $SERVICE" >&2; exit 1 ;;
esac

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
[ -f "$ROOT/.env" ] && set -a && . "$ROOT/.env" && set +a
NETWORK="${COMPOSE_NETWORK:-$(basename "$ROOT")_default}"
TITLE="${PLAN%.jmx}-${SERVICE}-$(date +%Y%m%d-%H%M%S)"

docker run --rm --network "$NETWORK" \
  -v "$ROOT/jmeter:/test" -v "$ROOT/jmeter-data:/jmeter-data" -w /test justb4/jmeter \
  -n -t "$PLAN" \
  -JbaseUrl="http://${SERVICE}:${PORT}" \
  -Jthreads="$THREADS" -Jramp="$RAMP" -Jduration="$DURATION" \
  -JcategoriesCsv=/jmeter-data/categories_ids.csv \
  -JitemsCsv=/jmeter-data/items_ids.csv \
  -Jinflux.url=http://influxdb:8086 \
  -Jinflux.org="${INFLUXDB_ORG:-perf}" -Jinflux.bucket="${INFLUXDB_BUCKET:-jmeter}" \
  -Jinflux.token="${INFLUXDB_TOKEN:?set INFLUXDB_TOKEN in .env}" \
  -JtestTitle="$TITLE" -JtagTestTitle="$TITLE"
