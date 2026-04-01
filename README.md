# Transaction Aggregation API

A Spring Boot application that aggregates financial transactions from multiple sources, normalizes, categorizes and save them to the database.

## Tech Stack
### Backend
- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA

### Database
- PostgreSQL

### Build Tool
- Maven

### Other Tools
- Docker
- Lombok

## Setup

### Build
```
mvn clean package
```

### Run
```
mvn spring-boot:run
```

App runs at: 
http://localhost:8081
---
## API Endpoints
### 1. Aggregate Transactions
Fetches transactions from multiple sources, categorizes them, and stores them in the database.
```
POST /api/transactions/aggregate?customerId=123
```
### 2.  Get Transactions
Retrieve stored transactions for a specific customer.
```
GET /api/transactions?customerId=123
```


