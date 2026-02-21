# Analytic API

**Author:** Samuel Gbenga Joseph

---

## Overview

A Spring Boot REST API that ingests large merchant activity CSV files into a PostgreSQL database and exposes analytical endpoints over the data. The service is designed for performance and idempotency — CSV loading is asynchronous, batched, and tracked so that restarts never cause duplicate data or crashes.

---

## Assumptions

### 1. CSV Data Loading
- CSV files are placed in a `data/` folder at the **project root** (same level as `pom.xml`) and follow the naming pattern `activities_YYYYMMDD.csv`.
- On every application startup, the service scans this folder and loads any **unprocessed** files into the database.
- To prevent reprocessing on restart, a dedicated `loaded_files` table tracks every successfully loaded file by filename. If a file already exists in this table, it is skipped entirely.
- Each file is processed **asynchronously** in batches of **500 records** for performance. If a file fails midway, it is intentionally **not** marked as loaded — so it will be retried in full on the next restart, ensuring no partial data loss goes undetected.
- Malformed lines within a file are logged and skipped individually without stopping the overall load.

### 2. Database Indexing
Key columns are indexed to ensure fast query performance across all analytical endpoints:

| Index Name | Columns | Purpose |
|---|---|---|
| `idx_status_merchant` | `status, merchant_id` | Top merchant queries |
| `idx_status_timestamp` | `status, event_timestamp` | Monthly active merchant queries |
| `idx_product_status` | `product, status` | Product adoption & failure rate queries |
| `idx_product_status_merchant` | `product, status, merchant_id` | KYC funnel queries |


### 4. Failure Rate Calculation
Failure rate is calculated as `(FAILED / (SUCCESS + FAILED)) × 100`. Records with `PENDING` status are excluded from this calculation.

---

## Architecture

The service follows a traditional **layered architecture**:

```
├── controller/       — Handles HTTP requests and delegates to the service layer
├── service/          — Business logic interfaces and implementations
│   └── impl/
├── repository/       — Spring Data JPA repositories with custom JPQL/native queries
├── entity/           — JPA entities mapped to database tables
├── dto/
│   └── response/     — Response DTOs returned by the API
├── utils/            — CsvLoader utility for startup data ingestion
└── config/           — Application configuration (OpenAPI/Swagger)
```

---

## API Endpoints

All endpoints are prefixed with `/analytics`.

| Method | Endpoint | Description |
|---|---|---|
| GET | `/analytics/top-merchant` | Merchant with highest total successful transaction volume |
| GET | `/analytics/monthly-active-merchants` | Count of unique merchants with at least one successful event per month |
| GET | `/analytics/product-adoption` | Unique merchant count per product, sorted highest first |
| GET | `/analytics/kyc-funnel` | KYC conversion funnel showing unique merchants at each stage |
| GET | `/analytics/failure-rates` | Failure rate per product, sorted by rate descending |

Interactive API documentation is available via Swagger UI at:
```
http://localhost:8080/swagger-ui/index.html
```

---

## Prerequisites

Before running the application, ensure you have the following installed:

- **Java 21** or higher
- **Maven 3.8+**
- **PostgreSQL 13+**

---

## Database Setup

### 1. Create the database

Connect to your PostgreSQL instance and run:

```sql
CREATE DATABASE analytic_api;
```

### 2. Create a user (optional, skip if using default postgres user)

```sql
CREATE USER postgres WITH PASSWORD '1234';
GRANT ALL PRIVILEGES ON DATABASE analytic_api TO postgres;
```

The application will automatically create all required tables on first startup via Hibernate (`ddl-auto=update`).

---

## Configuration

The application uses Spring profiles. The active profile is set in `application.properties`:

```properties
spring.profiles.active=prod
```

Production database settings live in `application-prod.properties` and can be overridden via environment variables:

| Environment Variable | Default | Description |
|---|---|---|
| `DB_HOST` | `localhost` | PostgreSQL host |
| `DB_PORT` | `5432` | PostgreSQL port |
| `DB_NAME` | `analytic_api` | Database name |
| `DB_USERNAME` | `postgres` | Database username |
| `DB_PASSWORD` | `1234` | Database password |

---

## How to Run

### 1. Clone the repository

```bash
git clone <repository-url>
cd analytic_api
```

### 2. Place CSV files in the data folder

Create a `data/` folder at the project root and add your CSV files:

```
analytic_api/
├── pom.xml
├── data/
│   ├── activities_20240101.csv
│   ├── activities_20240102.csv
│   └── ...
└── src/
```

CSV files must follow the naming pattern: `activities_YYYYMMDD.csv`

The expected CSV column order is:
```
event_id, merchant_id, event_timestamp, product, event_type, amount, status, channel, region, merchant_tier
```

### 3. Build the application

```bash
mvn clean install -DskipTests
```

### 4. Run the application

**Using Maven:**
```bash
mvn spring-boot:run
```

### 5. Verify the application started

On successful startup you will see logs similar to:

```
Started AnalyticApiApplication in X seconds
Starting background CSV loading...
Found 31 CSV files
Loading CSV file: .../data/activities_20240101.csv
Finished loading: activities_20240101.csv | Total records: 5000
```

### 6. Test the endpoints

```bash
curl http://localhost:8080/analytics/top-merchant
curl http://localhost:8080/analytics/monthly-active-merchants
curl http://localhost:8080/analytics/product-adoption
curl http://localhost:8080/analytics/kyc-funnel
curl http://localhost:8080/analytics/failure-rates
```

---

## Notes

- On **restart**, already-loaded CSV files are skipped automatically. Only new files in the `data/` folder will be processed.
- If a CSV file fails to load completely, it will be **retried on the next restart**.
- The `data/` folder is listed in `.gitignore` — CSV files should never be committed to version control.