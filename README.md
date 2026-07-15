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
- **Email Notifications** — Registration, activation, deactivation, verification, and Excel import events
- **Email Verification Flow** — Token-based email verification with set-password page
- **Email Failure Logging** — Persistent logging of failed email deliveries
- **MapStruct Mappings** — Entity-to-DTO mapping with MapStruct
- **Strategy Pattern for PATCH** — Field-specific patch handlers for Employee, Department, and Family
- **Scheduled Cleanup** — Automatic removal of expired verification tokens
- **API Versioning** — Configurable API version prefix
- **OpenAPI/Swagger UI** — Interactive API documentation at `/swagger-ui.html`
- **Thymeleaf Templates** — Email verification HTML template
- **Docker Support** — Multi-stage Dockerfile and docker-compose setup
- **GitHub Actions CI** — Build and test pipeline on `main`/`develop` branches

## Project Structure

```
EmployeeManagement/
├── pom.xml                    # Parent POM (Spring Boot 4.1.0, Java 26)
├── src/main/resources/
│   └── application.properties # Minimal top-level config
├── employee-service/          # Single microservice module
│   ├── pom.xml                # Service POM (Java 21, Spring Boot 4.1.0)
│   ├── Dockerfile             # Multi-stage Docker build
│   ├── .github/workflows/
│   │   └── ci.yaml            # GitHub Actions CI pipeline
│   ├── src/main/java/com/pm/employeeservice/
│   │   ├── controller/        # REST controllers (Auth, Employee, Department, Family)
│   │   ├── service/           # Business logic layer
│   │   ├── repository/        # JPA repositories (inc. EmailFailureLogRepository)
│   │   ├── model/             # JPA entities (Employee, Department, Family, verificationTokens, EmailFailureLog)
│   │   ├── dto/               # Request/response DTOs
│   │   ├── mapper/            # Entity-to-DTO mappers (MapStruct)
│   │   ├── config/            # Security, OpenAPI, API versioning
│   │   ├── filter/            # JWT authentication filter
│   │   ├── mail/              # Email event classes and listeners
│   │   ├── Excel/             # Excel import/export engine (Apache POI)
│   │   ├── EmployeePatchHandlers/  # Strategy: partial update on Employee fields
│   │   ├── DepartmentPatchHandlers/# Strategy: partial update on Department fields
│   │   ├── FamilyPatchHandlers/    # Strategy: partial update on Family fields
│   │   ├── Interface/         # PatchHandler interface
│   │   ├── Exceptions/        # Custom exceptions and global handler
│   │   ├── Enum/              # Enums (Role)
│   │   ├── schedules/         # Scheduled tasks (expired token cleanup)
│   │   └── util/              # Utilities (JwtUtil)
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   ├── data.sql           # Schema + seed data
│   │   └── templates/
│   │       └── email-verification.html  # Thymeleaf email template
│   └── src/test/
│       ├── java/com/pm/employeeservice/
│       │   ├── ControllerTests/
│       │   ├── ServiceTests/
│       │   ├── RepositoryTests/
│       │   ├── ModelTests/
│       │   ├── MapperTests/
│       │   ├── FilterTests/
│       │   ├── ConfigTests/
│       │   ├── ExceptionsTests/
│       │   └── UtilTests/
│       └── resources/
│           └── application.properties  # H2 test config
├── docker-compose.yml         # PostgreSQL + service orchestration
├── .env                       # Environment variables
├── FAMILY_RELATIONSHIP_ANALYSIS.md
├── JWT_VERIFICATION_GUIDE.md
├── QUICK_REFERENCE.md
├── STATUS_REPORT.md
├── BEFORE_AFTER_CODE.md
├── STITCH_MCP_PROMPT.md
├── test-jwt-auth.sh           # Shell script for JWT auth testing
├── mvnw / mvnw.cmd            # Maven wrapper
└── HELP.md
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

| Method | Endpoint              | Auth Required | Description                  |
|--------|-----------------------|---------------|------------------------------|
| POST   | `/login`              | No            | Login, returns JWT           |
| GET    | `/validate`           | Yes           | Validate JWT token           |
| GET    | `/verify?token=`     | No            | Verify employee email        |
| GET    | `/set-password?token=`| No            | Show set-password form (HTML)|
| POST   | `/set-password`       | No            | Set password via token       |
| GET    | `/employees`          | Yes           | List employees (paginated)   |
| GET    | `/employees/{id}`     | Yes           | Get employee by ID           |
| POST   | `/employees`          | Admin         | Create employee              |
| PUT    | `/employees/{id}`     | Admin         | Update employee              |
| PATCH  | `/employees/{id}`     | Yes           | Partial update employee      |
| DELETE | `/employees/{id}`     | Admin         | Delete employee              |
| GET    | `/employees/export`   | Admin         | Export employees to Excel    |
| POST   | `/employees/import`   | Admin         | Import employees from Excel  |
| GET    | `/departments`        | Yes           | List departments             |
| POST   | `/departments`        | Admin         | Create department            |
| GET    | `/departments/{id}`   | Yes           | Get department by ID         |
| PATCH  | `/departments/{id}`   | Admin         | Partial update department    |
| DELETE | `/departments/{id}`   | Admin         | Delete department            |
| GET    | `/family`             | Yes           | List family members          |
| POST   | `/family`             | Yes           | Add family member            |
| GET    | `/family/{id}`        | Yes           | Get family member by ID      |
| PATCH  | `/family/{id}`        | Yes           | Partial update family member |
| DELETE | `/family/{id}`        | Admin         | Delete family member         |

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
