# Security Policy

## Reporting a vulnerability

Please do not open a public issue for security problems. Use GitHub's
[private vulnerability reporting](../../security/advisories/new) instead.
You will receive an answer within 7 days.

## Configuration

- Credentials are read from a local `.env` file (see `.env.example`), which is ignored by Git.
- Default values in `docker-compose.yml` are for local benchmarking only. Change them before exposing any service.
- Monitoring tools (Grafana, Prometheus, InfluxDB) and PostgreSQL are bound to `127.0.0.1`.
- Every push is scanned for committed secrets with Gitleaks.
