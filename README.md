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
http://localhost:8080
---
## API Endpoints
Fetch transactions from external systems.
```
POST /api/transactions/aggregate?customerId=123
```
## Get Transactions
Retrieve stored transactions for a customer.
```
GET /api/transactions?customerId=123
```
## Mock APIs
The project includes mock endpoints to stimulate external financial systems.

### Bank API
```
GET /mock/bank/transactions/{customerId}
```
### Credit Card API
```
GET /mock/creditcard/transactions/{customerId}
```
### Payment App API
```
GET /mock/payment/transactions/{customerId}
```
