# Transaction Aggregator

A production-grade Spring Boot service that aggregates customer financial transaction data from multiple external data sources, categorises each transaction automatically, and exposes a comprehensive REST API for querying and summarising the data.

---

## Table of Contents

- [Architecture Overview](#architecture-overview)
- [Prerequisites](#prerequisites)
- [Configuration](#configuration)
- [Running the Project](#running-the-project)
  - [Option A: Docker Compose (recommended)](#option-a-docker-compose-recommended)
  - [Option B: Local with existing PostgreSQL](#option-b-local-with-existing-postgresql)
- [Running Tests](#running-tests)
- [API Reference](#api-reference)
- [Adding a New Data Source](#adding-a-new-data-source)
- [Project Structure](#project-structure)

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────┐
│                  REST API Layer                      │
│   TransactionController   AggregationController     │
└───────────────────┬─────────────────────────────────┘
                    │
┌───────────────────▼─────────────────────────────────┐
│                 Service Layer                        │
│   TransactionServiceImpl   AggregationServiceImpl   │
└───────────────────┬─────────────────────────────────┘
                    │
┌───────────────────▼─────────────────────────────────┐
│              Repository Layer (JPA)                  │
│   TransactionRepository (PostgreSQL via Flyway)      │
└─────────────────────────────────────────────────────┘
                    │
┌───────────────────▼─────────────────────────────────┐
│            External Data Sources                     │
│   ExternalDataSource (interface)                     │
│   └── StitchDataSource (stub — replace with real)   │
└─────────────────────────────────────────────────────┘
```

**Key design decisions:**

- **ExternalDataSource interface** — each external provider (Stitch, Plaid, Mono, etc.) implements this interface. The aggregation engine auto-discovers all registered sources via Spring's DI. Adding a new provider requires only a new `@Component` class.
- **Idempotent ingestion** — every transaction carries an `externalId` from the source. Duplicate ingestion calls are safely skipped.
- **Database-side aggregation** — category totals are computed with `GROUP BY` in SQL, not in Java, keeping memory usage flat regardless of transaction volume.
- **Flyway migrations** — schema changes are versioned and applied automatically on startup. No manual SQL scripts to run.

---

## Prerequisites

| Tool | Version |
|------|---------|
| Java | 17+ |
| Maven | 3.8+ (or use `./mvnw`) |
| Docker | 24+ |
| Docker Compose | 2.x |

---

## Configuration

All configuration is supplied via environment variables. **No secrets are hardcoded.**

Copy the example file and fill in your values:

```bash
cp .env.example .env
```

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_URL` | JDBC URL for PostgreSQL | `jdbc:postgresql://localhost:5432/transactions_db` |
| `DB_USERNAME` | Database username | `postgres` |
| `DB_PASSWORD` | Database password | *(required — no default)* |
| `SERVER_PORT` | Port the app listens on | `8081` |
| `SPRING_PROFILES_ACTIVE` | Spring profile (`dev` enables verbose SQL logging) | *(none)* |

---

## Running the Project

### Option A: Docker Compose (recommended)

Starts both PostgreSQL and the application. No local Java or database installation needed.

```bash
# 1. Set your database password
cp .env.example .env
# Edit .env and set DB_PASSWORD

# 2. Build and start
docker compose up --build

# App will be available at http://localhost:8081
```

To stop:

```bash
docker compose down
```

To stop and remove the database volume:

```bash
docker compose down -v
```

---

### Option B: Local with existing PostgreSQL

No need to create the database manually. The app creates it automatically on first startup.

```bash
# 1. Export your PostgreSQL credentials
export DB_USERNAME=postgres
export DB_PASSWORD=your_postgres_password

# Optional overrides (these are the defaults):
# export DB_HOST=localhost
# export DB_PORT=5432
# export DB_NAME=transactions_db

# 2. Run the application
./mvnw spring-boot:run

# Or with dev profile for verbose SQL logging:
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

On startup the app will:
1. Connect to PostgreSQL's built-in `postgres` maintenance database
2. Check whether `transactions_db` exists — create it if not
3. Run Flyway to apply `V1__create_transactions_table.sql`
4. Start serving requests on port 8081

**Windows Command Prompt:**
```cmd
set DB_USERNAME=postgres
set DB_PASSWORD=your_postgres_password
mvnw spring-boot:run
```

**Windows PowerShell:**
```powershell
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="your_postgres_password"
./mvnw spring-boot:run
```

---

## Running Tests

Tests use an H2 in-memory database — no running PostgreSQL instance required.

```bash
# Run all tests
./mvnw test

# Run a specific test class
./mvnw test -Dtest=TransactionCategorizerTest

# Run with coverage report (target/site/jacoco/index.html)
./mvnw test jacoco:report
```

### Test coverage includes:

| Test Class | Type | What's tested |
|-----------|------|---------------|
| `TransactionCategorizerTest` | Unit | Null/blank input, all category keywords, case-insensitivity, real bank feed formats (e.g. `UBER* EATS`) |
| `TransactionServiceImplTest` | Unit (mocked) | Create transaction, duplicate skipping, summary totals, invalid date ranges, 404 behaviour |
| `TransactionControllerTest` | `@WebMvcTest` | HTTP status codes, request validation, 400/404 responses, JSON structure |

---

## API Reference

Base URL: `http://localhost:8081/api/v1`

### Aggregation

#### Trigger ingestion from all external sources

```
POST /aggregation/{customerId}
```

Fetches transactions from all registered data sources for the customer, categorises them, and persists any that haven't been seen before. Safe to call multiple times — duplicates are skipped.

**Response 200:**
```json
{
  "customerId": "cust-123",
  "transactionsIngested": 10,
  "duplicatesSkipped": 0,
  "source": "STITCH"
}
```

---

### Transactions

#### Create a transaction manually

```
POST /transactions
Content-Type: application/json
```

**Request body:**
```json
{
  "customerId": "cust-123",
  "merchant": "Checkers Hyper",
  "amount": 1250.00,
  "source": "BANK_ACCOUNT",
  "date": "2024-03-15T10:30:00",
  "currency": "ZAR"
}
```

**Response 201:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "customerId": "cust-123",
  "merchant": "Checkers Hyper",
  "amount": 1250.00,
  "currency": "ZAR",
  "category": "GROCERIES",
  "source": "BANK_ACCOUNT",
  "date": "2024-03-15T10:30:00",
  "createdAt": "2024-03-15T10:30:01"
}
```

---

#### Get all transactions (paginated)

```
GET /transactions?customerId=cust-123&page=0&size=20&sortBy=date&sortDir=desc
```

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `customerId` | string | required | Customer identifier |
| `page` | int | 0 | Page number (0-based) |
| `size` | int | 20 | Page size (max 100) |
| `sortBy` | string | `date` | Field to sort by |
| `sortDir` | string | `desc` | `asc` or `desc` |

---

#### Get transactions by category

```
GET /transactions/category?customerId=cust-123&category=GROCERIES&page=0&size=20
```

Valid categories: `GROCERIES`, `TRANSPORT`, `ENTERTAINMENT`, `DINING`, `UTILITIES`, `HEALTHCARE`, `SHOPPING`, `INCOME`, `TRANSFERS`, `OTHER`

---

#### Get transactions by date range

```
GET /transactions/range?customerId=cust-123&from=2024-01-01T00:00:00&to=2024-03-31T23:59:59
```

---

#### Get category summary (all time)

```
GET /transactions/summary?customerId=cust-123
```

**Response 200:**
```json
{
  "customerId": "cust-123",
  "totalsByCategory": {
    "GROCERIES": 1928.30,
    "TRANSPORT": 154.50,
    "ENTERTAINMENT": 199.00,
    "INCOME": 35000.00
  },
  "grandTotal": 37281.80,
  "from": null,
  "to": null
}
```

---

#### Get category summary by date range

```
GET /transactions/summary/range?customerId=cust-123&from=2024-01-01T00:00:00&to=2024-03-31T23:59:59
```

---

### Health Check

```
GET /actuator/health
```

---

## Adding a New Data Source

To connect a new external provider (e.g. Plaid, Mono, a bank's open banking API):

1. Create a new class in `service/datasource/` that implements `ExternalDataSource`
2. Annotate it with `@Component`
3. Implement `sourceName()` and `fetchTransactions(String customerId)`

```java
@Slf4j
@Component
public class PlaidDataSource implements ExternalDataSource {

    @Override
    public String sourceName() {
        return "PLAID";
    }

    @Override
    public List<CreateTransactionRequest> fetchTransactions(String customerId) {
        // Call Plaid API, map response to CreateTransactionRequest
    }
}
```

The aggregation engine will automatically pick it up — no other changes required.

---

## Project Structure

```
src/
├── main/
│   ├── java/com/jessica/transactions/
│   │   ├── TransactionAggregatorApplication.java
│   │   ├── controller/
│   │   │   ├── TransactionController.java
│   │   │   └── AggregationController.java
│   │   ├── dto/
│   │   │   └── TransactionDtos.java
│   │   ├── exception/
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   └── ResourceNotFoundException.java
│   │   ├── model/
│   │   │   ├── Transaction.java
│   │   │   ├── Category.java
│   │   │   └── TransactionSource.java
│   │   ├── repository/
│   │   │   └── TransactionRepository.java
│   │   ├── service/
│   │   │   ├── TransactionService.java        (interface)
│   │   │   ├── AggregationService.java        (interface)
│   │   │   ├── datasource/
│   │   │   │   ├── ExternalDataSource.java    (interface)
│   │   │   │   └── StitchDataSource.java      (stub)
│   │   │   └── impl/
│   │   │       ├── TransactionServiceImpl.java
│   │   │       └── AggregationServiceImpl.java
│   │   └── util/
│   │       ├── TransactionCategorizer.java
│   │       └── TransactionMapper.java
│   └── resources/
│       ├── application.properties
│       ├── application-dev.properties
│       └── db/migration/
│           └── V1__create_transactions_table.sql
└── test/
    ├── java/com/jessica/transactions/
    │   ├── controller/TransactionControllerTest.java
    │   ├── service/TransactionServiceImplTest.java
    │   └── util/TransactionCategorizerTest.java
    └── resources/
        └── application-test.properties
```
