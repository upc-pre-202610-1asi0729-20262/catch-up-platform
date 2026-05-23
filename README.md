# Catch-Up Platform (catch-up-platform)

## Overview

Catch-Up Platform is a Spring Boot service that provides a REST API to manage users' favorite news sources. The project is structured around Domain-Driven Design (DDD), organized into bounded contexts (`news` and `shared`), using application-layer command/query contracts, a shared `Result` abstraction for command outcomes, and JPA persistence.

This service is designed to work with [NewsAPI.org](https://newsapi.org/):
- `newsApiKey` refers to an API key issued by NewsAPI.org
- `sourceId` refers to a NewsAPI.org source identifier (for example, `bbc-news`)
- This service stores favorite source mappings; it does not proxy NewsAPI.org content endpoints

## Features

- List favorite sources scoped to a News API key
- Retrieve a favorite source by its identifier
- Retrieve a favorite source by News API key and source id
- Create (persist) a new favorite source
- Map command outcomes to HTTP responses through an interface-layer response translator
- Custom Hibernate naming strategy to convert identifiers to snake_case and plural table names

## Technologies

- OpenJDK 26 and Spring Boot 4.0.6
- Spring Web (REST controllers)
- Spring Data JPA (Hibernate) with MySQL
- Spring Validation (Jakarta Bean Validation)
- SpringDoc OpenAPI 3 / Swagger UI
- Lombok (compile-time helpers)
- PlantUML (architecture diagrams in `docs/`)

## Prerequisites

- OpenJDK 26
- MySQL 8+ (database server)
- Docker (optional for local testing, recommended for deployment packaging)

## Environment variables

The application resolves credentials and connection details from environment variables. Set the following before running locally or in a container:

| Variable            | Description                                      |
|---------------------|--------------------------------------------------|
| `DATABASE_USER`     | MySQL username                                   |
| `DATABASE_PASSWORD` | MySQL password                                   |
| `DATABASE_URL`      | JDBC URL (default profile uses `localhost:3306`) |
| `DATABASE_NAME`     | Database name (default: `catch-up-os`)           |
| `DATABASE_PORT`     | Database port (production profile)               |
| `PORT`              | Application port (default: `8080`)               |

## Spring profiles

| Profile     | Usage                                                      |
|-------------|------------------------------------------------------------|
| *(default)* | Uses `localhost:3306` with SSL enabled; SQL logging off    |
| `dev`       | Uses `localhost:3306` with SSL disabled; SQL logging on    |
| `prod`      | Reads full connection URL from env vars; used in Docker/CI |

Set the active profile with:

```bash
export SPRING_PROFILES_ACTIVE=dev
```

## Technical stories

The API-focused technical stories for frontend integration are in [`docs/user-stories.md`](docs/user-stories.md).

## Class diagram

A PlantUML class diagram that reflects the code structure and bounded contexts is available at [`docs/class-diagram.puml`](docs/class-diagram.puml).

## Architecture notes

- `news.application.commandservices` and `news.application.queryservices` define the application-layer service contracts
- `news.application.internal.commandservices` and `news.application.internal.queryservices` contain the implementations
- `shared.application.result.Result` models command outcomes without introducing HTTP or domain concepts
- `news.interfaces.rest.transform.ResponseEntityFromFavoriteSourceCommandResultAssembler` maps command results to `ResponseEntity` values for the REST boundary

## API documentation (Swagger UI)

When the application is running, interactive API documentation is available at:

```
http://localhost:8080/swagger-ui/index.html
```

## Getting started (quick)

Export the required environment variables, then run with the `dev` profile:

```bash
export DATABASE_USER=your_db_user
export DATABASE_PASSWORD=your_db_password
export SPRING_PROFILES_ACTIVE=dev
./mvnw spring-boot:run
```

Or build and run the jar:

```bash
./mvnw clean package
java -jar target/*.jar
```

## Run with Docker

The `Dockerfile` is intended for deployment-style packaging:
- it builds the application in a multi-stage image
- it runs the application with `SPRING_PROFILES_ACTIVE=prod` by default
- it expects database connection settings to be provided via environment variables at runtime

Build the container image:

```bash
docker build -t catch-up-platform:local .
```

Run the container (deployment-style execution; supply required runtime env vars):

```bash
docker run --rm -p 8080:8080 \
  -e DATABASE_USER=your_db_user \
  -e DATABASE_PASSWORD=your_db_password \
  -e DATABASE_URL=jdbc:mysql://your_db_host:3306/catch-up-os \
  -e DATABASE_PORT=3306 \
  -e DATABASE_NAME=catch-up-os \
  -e SPRING_PROFILES_ACTIVE=prod \
  --name catch-up-platform \
  catch-up-platform:local
```

For cloud/container platforms, inject the same variables using the platform's secret/config mechanism instead of hardcoding values.

## Notes

- This repository intentionally reflects a focused subset of functionality (favorite management); delete operations are not currently implemented.
