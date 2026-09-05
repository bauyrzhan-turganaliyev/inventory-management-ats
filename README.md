# inventory-management-ats

[![Java CI with Maven](https://github.com/bauyrzhan-turganaliyev/inventory-management-ats/actions/workflows/maven.yml/badge.svg)](https://github.com/bauyrzhan-turganaliyev/inventory-management-ats/actions/workflows/maven.yml)

[![Coverage Status](https://coveralls.io/repos/github/bauyrzhan-turganaliyev/inventory-management-ats/badge.svg)](https://coveralls.io/github/bauyrzhan-turganaliyev/inventory-management-ats)

[![Quality gate status](https://sonarcloud.io/api/project_badges/measure?project=bauyrzhan-turganaliyev_inventory-management-ats&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=bauyrzhan-turganaliyev_inventory-management-ats)

Inventory management desktop application for managing products and categories.

The application is implemented in Java using Swing for the graphical user interface, JPA/Hibernate for persistence, and PostgreSQL as the database. The project includes automated unit, integration, GUI, and end-to-end tests.

## Requirements

- Java 17
- Maven
- Docker
- PostgreSQL

## Build and test

```bash
mvn clean verify
```

Docker must be running because the integration and end-to-end tests use PostgreSQL through Testcontainers.

## Run

Create an empty PostgreSQL database:

```bash
createdb inventory_management
```

Then run the application:

```bash
DB_URL=jdbc:postgresql://localhost:5432/inventory_management \
DB_USER=postgres \
DB_PASSWORD=postgres \
mvn exec:java -Dexec.mainClass="it.unifi.bautur.store.Main"
```

Change `DB_USER` and `DB_PASSWORD` if your PostgreSQL credentials are different.

The database schema is created automatically by Hibernate. If the category table is empty, the application initializes the default categories `Electronics`, `Books`, and `Food`.# inventory-management-ats

[![Java CI with Maven](https://github.com/bauyrzhan-turganaliyev/inventory-management-ats/actions/workflows/maven.yml/badge.svg)](https://github.com/bauyrzhan-turganaliyev/inventory-management-ats/actions/workflows/maven.yml)

[![Coverage Status](https://coveralls.io/repos/github/bauyrzhan-turganaliyev/inventory-management-ats/badge.svg)](https://coveralls.io/github/bauyrzhan-turganaliyev/inventory-management-ats)

[![Quality gate status](https://sonarcloud.io/api/project_badges/measure?project=bauyrzhan-turganaliyev_inventory-management-ats&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=bauyrzhan-turganaliyev_inventory-management-ats)

Inventory management desktop application for managing products and categories.

The application is implemented in Java using Swing for the graphical user interface, JPA/Hibernate for persistence, and PostgreSQL as the database. The project includes automated unit, integration, GUI, and end-to-end tests.

## Requirements

- Java 17
- Maven
- Docker

## Build and test

```bash
mvn clean verify
```

Docker must be running because the integration and end-to-end tests use a PostgreSQL database provided by Testcontainers.

## Run

The application connects to PostgreSQL using the following environment variables:

- `DB_URL` — JDBC URL of the PostgreSQL database
- `DB_USER` — database username
- `DB_PASSWORD` — database password

Example:

```bash
DB_URL=jdbc:postgresql://localhost:5432/inventory \
DB_USER=postgres \
DB_PASSWORD=postgres \
mvn exec:java -Dexec.mainClass="it.unifi.bautur.store.Main"
```