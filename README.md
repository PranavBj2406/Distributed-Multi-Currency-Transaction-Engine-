Multi-Currency Banking API

A production-style REST API built with Java 17 + Spring Boot that enables cross-border fund transfers with live exchange rate conversion, simulating real-world fintech backend systems.

Problem Statement

Traditional banking APIs treat money as a single-currency value.

In reality:

users hold wallets in multiple currencies
transfers happen across borders
exchange rate snapshots must be auditable

This API solves that by supporting multi-wallet users + real-time currency conversion + immutable transaction logs.

Architecture
Client Request
      │
      ▼
REST Controller Layer
(Handles HTTP + validation)
      │
      ▼
Service Layer
(Business logic + FX Strategy Pattern)
      │
      ▼
Repository Layer
(Spring Data JPA + H2 DB)
      │
      ▼
Transaction Audit Log
(Immutable conversion history)
Tech Stack
Technology	Purpose
Java 17	Core language
Spring Boot 3	REST framework
Spring Data JPA	Data access layer
H2 Database	In-memory DB
JUnit 5 + Mockito	Testing
Docker	Containerization
GitHub Actions	CI/CD
ExchangeRate API	Live FX conversion
Design Patterns Used
Strategy Pattern

ExchangeRateStrategy

Allows switching between:

live FX rates
mock FX rates (testing)

without changing business logic.

Factory Pattern

Wallet creation logic abstracted from controller layer.

Repository Pattern

Clean separation between:

Controller → Service → Repository

Each layer has a single responsibility.

API Endpoints
Users
Method	Endpoint	Description
POST	/api/users	Create user
GET	/api/users/{id}	Get user
GET	/api/users	Get all users
Wallets
Method	Endpoint	Description
POST	/api/wallets	Create wallet
GET	/api/wallets/user/{userId}	User wallets
GET	/api/wallets/{walletId}	Wallet by ID
POST	/api/wallets/{walletId}/deposit	Deposit money
Transfers
Method	Endpoint	Description
POST	/api/transfers	Cross-currency transfer
GET	/api/transfers/history/{walletId}	Transaction history
Quick Start
Run Locally
git clone https://github.com/PranavBj2406/multi-currency-banking-api.git
cd multi-currency-banking-api
mvn spring-boot:run
Run Using Docker
docker build -t multi-currency-api .
docker run -p 8080:8080 multi-currency-api
Example Flow
Create User
curl -X POST http://localhost:8080/api/users \
-H "Content-Type: application/json" \
-d '{"username":"pranav","email":"pranav@gmail.com"}'
Create INR + USD Wallets
curl -X POST http://localhost:8080/api/wallets \
-H "Content-Type: application/json" \
-d '{"userId":1,"currency":"INR"}'
curl -X POST http://localhost:8080/api/wallets \
-H "Content-Type: application/json" \
-d '{"userId":1,"currency":"USD"}'
Deposit Money
curl -X POST http://localhost:8080/api/wallets/1/deposit \
-H "Content-Type: application/json" \
-d '{"amount":10000}'
Transfer INR → USD
curl -X POST http://localhost:8080/api/transfers \
-H "Content-Type: application/json" \
-d '{"fromWalletId":1,"toWalletId":2,"amount":5000}'

Example Response:

{
  "id": 1,
  "fromCurrency": "INR",
  "toCurrency": "USD",
  "amount": 5000,
  "exchangeRate": 0.012,
  "convertedAmount": 60.00,
  "timestamp": "2026-04-11T21:13:02"
}
View Transaction History
curl http://localhost:8080/api/transfers/history/1
Running Tests
mvn test
