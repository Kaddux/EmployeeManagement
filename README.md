# Employee Management System

A Spring Boot project for managing employees, departments, and family relationships with role-based access control, JWT authentication, email notifications, and Excel import/export.

## Tech Stack

- **Java 21** with **Spring Boot 4.1.0**
- **PostgreSQL** (primary) / MySQL (optional)
- **Spring Security** + **JWT** (jjwt 0.12.7) for authentication
- **Spring Data JPA** + **JDBC** for persistence
- **Spring Mail** for email notifications (SMTP)
- **Apache POI** for Excel import/export
- **SpringDoc OpenAPI** for API documentation
- **Docker** / **Docker Compose** for containerization
- **Maven** build tool
- **Project Lombok**

## Features

- **Employee CRUD** — Create, read, update, patch, delete employees with pagination
- **Partial Updates** — Strategy-pattern-based patch handlers for employees (name, email, password, department, address, date of birth, role), departments (name, code), and family (father/mother name, member count)
- **Department Management** — Manage departments with unique codes
- **Family Management** — Track family members per employee
- **JWT Authentication** — Login, token validation, email-based verification flow
- **Role-Based Access** — `ROLE_ADMIN` and `ROLE_EMPLOYEE` with method-level security
- **Role Management** — Admin-only endpoint to update employee roles
- **Excel Export/Import** — Stream export employees to `.xlsx`, batch import with error logging
- **Email Notifications** — Async events for registration, activation, deactivation, and verification
- **Email Rate Limiting** — Prevents excessive verification email requests
- **Scheduled Cleanup** — Auto-purge of expired verification tokens with warning notifications
- **API Versioning** — Configurable `/api/v1/` prefix via `WebMvcConfigurer`
- **Global Exception Handling** — Consistent `ApiErrorResponse` format for validation, conflicts, not-found, and server errors
- **OpenAPI/Swagger UI** — JWT bearer-auth documented at `/swagger-ui.html`
- **Docker Support** — Multi-stage Dockerfile and docker-compose setup

## Project Structure

```
EmployeeManagement/
├── employee-service/
│   ├── src/main/java/com/pm/employeeservice/
│   │   ├── controller/
│   │   │   ├── AuthController.java          # Login, verify email, set password
│   │   │   ├── EmployeeController.java      # CRUD + PATCH + export/import
│   │   │   ├── DepartmentController.java     # CRUD + PATCH
│   │   │   └── FamilyController.java        # CRUD + PATCH
│   │   ├── service/
│   │   │   ├── AuthService.java             # Authentication logic
│   │   │   ├── EmployeeService.java         # Employee business logic
│   │   │   ├── DepartmentService.java       # Department business logic
│   │   │   ├── FamilyService.java           # Family business logic
│   │   │   ├── CustomUserDetailsService.java
│   │   │   ├── EmployeeUserDetailsService.java
│   │   │   └── EmailFailureLogService.java
│   │   ├── repository/                      # JPA repositories
│   │   ├── model/                           # JPA entities
│   │   │   ├── Employee.java
│   │   │   ├── Department.java
│   │   │   ├── Family.java
│   │   │   ├── verificationTokens.java
│   │   │   └── EmailFailureLog.java
│   │   ├── dto/                             # Request/response DTOs
│   │   ├── mapper/                          # Entity-to-DTO mappers
│   │   ├── config/
│   │   │   ├── SecurityConfig.java          # Security filter chain
│   │   │   ├── JwtAuthenticationEntryPoint.java  # 401 JSON responses
│   │   │   ├── OpenAPIConfig.java           # Swagger + bearer auth
│   │   │   ├── ApiVersionConfig.java        # /api/v1/ prefix
│   │   │   └── DataInitializer.java         # Seed admin user
│   │   ├── filter/
│   │   │   └── JwtAuthenticationFilter.java # JWT validation filter
│   │   ├── mail/                            # Async event classes & listeners
│   │   ├── Excel/
│   │   │   ├── EmployeeExcelExporter.java   # Stream export to .xlsx
│   │   │   └── EmployeeExcelImporter.java   # Batch import from .xlsx
│   │   ├── EmployeePatchHandlers/           # Strategy: Name, Email, Password, Dept, Address, DOB
│   │   ├── DepartmentPatchHandlers/         # Strategy: Name, Code
│   │   ├── FamilyPatchHandlers/             # Strategy: FatherName, MotherName, Members
│   │   ├── Interface/
│   │   │   └── PatchHandler.java            # Strategy interface
│   │   ├── Exceptions/                      # Custom exceptions + global handler
│   │   ├── Enum/
│   │   │   └── Role.java                    # ROLE_ADMIN, ROLE_EMPLOYEE
│   │   └── schedules/
│   │       └── AccountCleanupScheduler.java # Periodic token purge
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   ├── data.sql                         # DDL + seed data
│   │   └── templates/set-password.html      # Thymeleaf template
│   └── Dockerfile
├── docker-compose.yml
└── .env
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

1. Start a PostgreSQL instance:
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

### Authentication

| Method | Endpoint                       | Auth | Description                             |
|--------|--------------------------------|------|-----------------------------------------|
| POST   | `/login`                       | No   | Login, returns JWT token                |
| GET    | `/validate`                    | Yes  | Validate JWT token                      |
| GET    | `/verify?token=`               | No   | Verify employee email via token         |
| GET    | `/set-password?token=`         | No   | Show set-password HTML form             |
| POST   | `/set-password`                | No   | Set password using verification token   |

### Employees

| Method | Endpoint                          | Auth    | Description                    |
|--------|-----------------------------------|---------|--------------------------------|
| GET    | `/employees`                      | EMPLOYEE, ADMIN | List employees (paginated)  |
| GET    | `/employees/{id}`                 | EMPLOYEE, ADMIN | Get employee by ID          |
| POST   | `/employees`                      | ADMIN   | Create employee                |
| PUT    | `/employees/{id}`                 | ADMIN   | Full update employee           |
| PATCH  | `/employees/{id}`                 | ADMIN   | Partial update employee        |
| PATCH  | `/employees/{id}/role`            | ADMIN   | Update employee role           |
| DELETE | `/employees/{id}`                 | ADMIN   | Delete employee                |
| POST   | `/employees/resend-activation`    | ADMIN   | Resend activation email        |
| GET    | `/employees/export`               | ADMIN   | Export employees to `.xlsx`    |
| POST   | `/employees/import`               | ADMIN   | Import employees from `.xlsx`  |

### Departments

| Method | Endpoint                        | Auth  | Description                 |
|--------|---------------------------------|-------|-----------------------------|
| POST   | `/department`                   | ADMIN | Create department           |
| PATCH  | `/department`                   | ADMIN | Partial update department   |
| DELETE | `/department/{department_id}`   | ADMIN | Delete department           |

### Family

| Method | Endpoint                    | Auth  | Description                   |
|--------|-----------------------------|-------|-------------------------------|
| GET    | `/family`                   | ADMIN | List all family records       |
| GET    | `/family/{id}`              | ADMIN | Get family record by ID       |
| POST   | `/family/{employeeId}`      | ADMIN | Create family for employee    |
| PATCH  | `/family/{id}`              | ADMIN | Partial update family record  |

## Partial Update (PATCH) Strategy

The PATCH endpoints use a strategy pattern for field-level updates. Each patchable field maps to a dedicated `PatchHandler` implementation:

**Employee:** Name, Email, Password, Department, Address, DateOfBirth, Role

**Department:** Name, Code

**Family:** FatherName, MotherName, NumberOfMembers

Handlers reflect over non-null fields in the request DTO and apply changes to the entity.

## Docker Images

- **employee-service**: Custom Spring Boot app (port `5002`)
- **employee-service-db**: PostgreSQL 16 Alpine (port `6001`)

## API Documentation

Once running, visit: [http://localhost:5002/swagger-ui.html](http://localhost:5002/swagger-ui.html)

## Environment Variables

| Variable                     | Description                  | Default     |
|------------------------------|------------------------------|-------------|
| `SPRING_DATASOURCE_URL`      | JDBC URL for PostgreSQL      | _required_  |
| `SPRING_DATASOURCE_USERNAME` | DB username                  | _required_  |
| `SPRING_DATASOURCE_PASSWORD` | DB password                  | _required_  |
| `JWT_SECRET`                 | Base64-encoded JWT secret    | _required_  |
| `SPRING_MAIL_PASSWORD`       | SMTP mail app password       | _optional_  |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Hibernate DDL mode       | `validate`  |
| `SPRING_SQL_INIT_MODE`       | SQL init mode                | `never`     |
