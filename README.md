# Job Board REST API

A production-style RESTful microservice built with Spring Boot featuring JWT authentication, PostgreSQL, comprehensive testing, and automated CI/CD.

![CI/CD](https://github.com/DeekshithaKalluri/jobboard-api/actions/workflows/ci.yml/badge.svg)

## What It Does

This is the backend engine of a job board platform (think Indeed or LinkedIn Jobs). It handles:

- User registration and login with secure JWT tokens
- Posting, editing, and deleting job listings (only if you own them)
- Browsing and searching jobs without needing to log in
- Input validation and error handling on all endpoints

A React or mobile frontend could connect to this API to build a complete job board product.

## Tech Stack

- **Java 17** + **Spring Boot 3** (Spring MVC, Spring Security 6)
- **PostgreSQL 15** with Hibernate / Spring Data JPA
- **JWT authentication** via jjwt 0.12
- **JUnit 5** + Mockito + MockMvc (15 tests, 0 failures)
- **Docker** + Docker Compose
- **GitHub Actions** CI/CD pipeline

## Project Structure

    src/main/java/com/jobboard/api/
        controller/   → REST endpoints (AuthController, JobController)
        service/      → Business logic (JobService)
        repository/   → Database queries (UserRepository, JobRepository)
        model/        → Database entities (User, Job)
        security/     → JWT filter, SecurityConfig, UserDetailsService
        dto/          → Request bodies (LoginRequest, RegisterRequest)

    src/test/java/com/jobboard/api/
        JobServiceTest.java                  → Unit tests with Mockito
        AuthControllerIntegrationTest.java   → Integration tests with MockMvc

## API Endpoints

**Auth** (no login required)

    POST /api/auth/register   Register a new user
    POST /api/auth/login      Login and get a JWT token

**Jobs** (GET endpoints are public, POST/PUT/DELETE require login)

    GET    /api/jobs                        Get all jobs
    GET    /api/jobs/{id}                   Get one job
    GET    /api/jobs/search?title=X         Search by title
    GET    /api/jobs/search?location=X      Search by location
    POST   /api/jobs                        Create a job listing
    PUT    /api/jobs/{id}                   Update your listing
    DELETE /api/jobs/{id}                   Delete your listing

## Running Locally

**Option 1 — Docker (easiest, no setup needed)**

    docker-compose up

App runs at http://localhost:8080 with PostgreSQL included.

**Option 2 — Run directly**

    brew services start postgresql@15
    ./mvnw spring-boot:run

## Example Walkthrough

**1. Register**

    curl -X POST http://localhost:8080/api/auth/register \
      -H "Content-Type: application/json" \
      -d '{"username":"jane","email":"jane@example.com","password":"secret123"}'

    Response: {"message": "User registered successfully"}

**2. Login and get token**

    curl -X POST http://localhost:8080/api/auth/login \
      -H "Content-Type: application/json" \
      -d '{"username":"jane","password":"secret123"}'

    Response: {"token":"eyJ...","type":"Bearer","username":"jane"}

**3. Post a job (use token from step 2)**

    curl -X POST http://localhost:8080/api/jobs \
      -H "Content-Type: application/json" \
      -H "Authorization: Bearer YOUR_TOKEN" \
      -d '{"title":"Software Engineer","company":"TechCorp",
           "description":"Build products","location":"Austin TX",
           "salary":120000,"jobType":"FULL_TIME"}'

**4. Search jobs (no login needed)**

    curl "http://localhost:8080/api/jobs/search?title=engineer"

## Running Tests

    ./mvnw test
    # Result: Tests run: 15, Failures: 0, Errors: 0

## CI/CD Pipeline

Every push to main automatically:

1. Spins up a real PostgreSQL container on GitHub's servers
2. Runs all 15 tests against it
3. Builds the Docker image only if all tests pass

## Why This Architecture

- **Stateless JWT auth** — no server sessions, scales to multiple instances
- **Layered design** — Controller handles HTTP, Service handles logic, Repository handles data
- **Ownership enforcement** — users can only edit or delete their own job listings
- **Multi-stage Docker build** — compile stage is separate from runtime, keeping the image small
