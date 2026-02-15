# Scientific Calculator (Java) with Jenkins + Docker CI/CD

This project is a simple Java scientific calculator web app built with Spring Boot.
It includes:

- A browser UI (`/`) and REST API (`/api/calculate`)
- Unit and controller tests
- `Dockerfile` for containerization
- `Jenkinsfile` for CI/CD pipeline
- ngrok webhook setup guide in `GUIDE.md`

## Quick Start

### Run locally (Maven)

```bash
mvn spring-boot:run
```

Open: `http://localhost:8080`

### Run with Docker Compose

```bash
docker compose up --build
```

Open: `http://localhost:8080`

### Run tests

```bash
mvn test
```

For complete CI/CD + ngrok webhook setup, follow `GUIDE.md` and try.
