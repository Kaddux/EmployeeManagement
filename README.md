# Employee Management System

A Spring Boot project for managing employees, departments, and family relationships with role-based access control, JWT authentication, email notifications, and Excel import/export.

## Tech Stack

- **Java 21** with **Spring Boot 4.1.0**
- **PostgreSQL** (primary) / MySQL (optional)
- **Spring Security** + **JWT** (jjwt 0.12.7) for authentication
- **Spring Data JPA** for persistence
- **Spring Mail** for email notifications (SMTP)
- **Apache POI** for Excel import/export
- **SpringDoc OpenAPI** for API documentation
- **Docker** / **Docker Compose** for containerization
- **Maven** build tool
- **Project Lombok**

## Features

- **Employee CRUD** — Create, read, update, patch, delete employees with pagination and sorting
- **Department Management** — Manage departments with unique codes
- **Family Management** — Track family members per employee
- **JWT Authentication** — Login, token validation, password setup via email verification
- **Role-Based Access** — `ADMIN` and `USER` roles with endpoint-level security
- **Excel Export/Import** — Bulk export employees to Excel, import employees from Excel files
- **Email Notifications** — Registration, activation, deactivation, and verification events
- **Scheduled Cleanup** — Automatic removal of expired verification tokens
- **API Versioning** — Configurable API version prefix
- **OpenAPI/Swagger UI** — Interactive API documentation at `/swagger-ui.html`
- **Docker Support** — Multi-stage Dockerfile and docker-compose setup

## Project Structure

```
EmployeeManagement/
├── employee-service/          # Main microservice module
│   ├── src/main/java/com/pm/employeeservice/
│   │   ├── controller/        # REST controllers (Auth, Employee, Department, Family)
│   │   ├── service/           # Business logic layer
│   │   ├── repository/        # JPA repositories
│   │   ├── model/             # JPA entities (Employee, Department, Family, VerificationToken)
│   │   ├── dto/               # Request/response DTOs
│   │   ├── mapper/            # Entity-to-DTO mappers
│   │   ├── config/            # Security, OpenAPI, API versioning, data initializer
│   │   ├── filter/            # JWT authentication filter
│   │   ├── mail/              # Email event classes and listeners
│   │   ├── Excel/             # Excel import/export engine
│   │   ├── PatchHandlers/     # Strategy pattern for partial updates
│   │   ├── Exceptions/        # Custom exceptions and global handler
│   │   ├── Enum/              # Enums (Role)
│   │   └── schedules/         # Scheduled tasks (account cleanup)
│   ├── src/main/resources/
│   │   └── application.properties
│   ├── data.sql               # Sample data and DDL
│   └── Dockerfile
├── docker-compose.yml         # PostgreSQL + service orchestration
└── .env                       # Environment variables
```

## Getting Started

### Prerequisites

- Java 21
- Maven 3.9+
- Docker & Docker Compose (optional)

### Run with Docker Compose

```bash
docker compose up --build
```

The service starts on `http://localhost:5002`.

### Run Locally

1. Start a PostgreSQL instance (e.g., via Docker):
   ```bash
   docker run -d --name emp-db -e POSTGRES_DB=db -e POSTGRES_USER=admin_user -e POSTGRES_PASSWORD=password -p 6001:5432 postgres:16-alpine
   ```

2. Set environment variables (or use `.env`):
   ```
   SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:PORT/DB_NAME
   SPRING_DATASOURCE_USERNAME=YOUR_DB_USER
   SPRING_DATASOURCE_PASSWORD=YOUR_DB_PASSWORD
   JWT_SECRET=your-base64-encoded-secret
   ```

3. Run the application:
   ```bash
   cd employee-service
   mvn spring-boot:run
   ```

## API Endpoints

| Method | Endpoint              | Auth Required | Description              |
|--------|-----------------------|---------------|--------------------------|
| POST   | `/login`              | No            | Login, returns JWT       |
| GET    | `/validate`           | Yes           | Validate JWT token       |
| POST   | `/set-password`       | No            | Set password via token   |
| GET    | `/employees`          | Yes           | List employees (paginated)|
| GET    | `/employees/{id}`     | Yes           | Get employee by ID       |
| POST   | `/employees`          | Admin         | Create employee          |
| PUT    | `/employees/{id}`     | Admin         | Update employee          |
| PATCH  | `/employees/{id}`     | Yes           | Partial update employee  |
| DELETE | `/employees/{id}`     | Admin         | Delete employee          |
| GET    | `/employees/export`   | Admin         | Export employees to Excel|
| POST   | `/employees/import`   | Admin         | Import employees from Excel|
| GET    | `/departments`        | Yes           | List departments         |
| POST   | `/departments`        | Admin         | Create department        |
| GET    | `/family`             | Yes           | List family members      |
| POST   | `/family`             | Yes           | Add family member        |

## Docker Images

- **employee-service**: Custom Spring Boot app (port `5002`)
- **employee-service-db**: PostgreSQL 16 Alpine (port `6001`)

## API Documentation

Once running, visit: [http://localhost:5002/swagger-ui.html](http://localhost:5002/swagger-ui.html)

## Environment Variables

| Variable                     | Description                  | Default                              |
|------------------------------|------------------------------|--------------------------------------|
| `SPRING_DATASOURCE_URL`      | JDBC URL for PostgreSQL      | _required_                           |
| `SPRING_DATASOURCE_USERNAME` | DB username                  | _required_                           |
| `SPRING_DATASOURCE_PASSWORD` | DB password                  | _required_                           |
| `JWT_SECRET`                 | Base64-encoded JWT secret    | _required_                           |
| `SPRING_MAIL_PASSWORD`       | SMTP mail password           | _optional_                           |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Hibernate DDL mode       | `validate`                           |
