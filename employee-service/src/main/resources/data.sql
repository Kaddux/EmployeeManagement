DROP TABLE IF EXISTS employee;

CREATE TABLE employee
(
    id            UUID PRIMARY KEY,
    name          VARCHAR(255)        NOT NULL,
    email         VARCHAR(255) UNIQUE NOT NULL,
    role          VARCHAR(255)        NOT NULL,
    password      VARCHAR(255)        NOT NULL,
    address       VARCHAR(255)        NOT NULL,
    date_of_birth DATE                NOT NULL
);

-- Admin Users
INSERT INTO employee (id, name, email, role, password, address, date_of_birth)
VALUES ('123e4567-e89b-12d3-a456-426614174000', 'John Doe', 'john.doe@example.com', 'ROLE_ADMIN', '$2a$12$R9h/cIPz0gi.URNNX3kh2OPST9/zBkqquzaG.yZ.2S4S0Z5kZ0fS6', '123 Main St, Springfield', '1985-06-15');

INSERT INTO employee (id, name, email, role, password, address, date_of_birth)
VALUES ('123e4567-e89b-12d3-a456-426614174001', 'Jane Smith', 'jane.smith@example.com', 'ROLE_ADMIN', '$2a$12$KkQ1y05.j9y.1E.f.J/uQ.jN9z7fJ91y29.f.J/uQ.jN9z7fJ91y2', '456 Elm St, Shelbyville', '1990-09-23');

-- Employee Users
INSERT INTO employee (id, name, email, role, password, address, date_of_birth)
VALUES ('123e4567-e89b-12d3-a456-426614174003', 'Bob Brown', 'bob.brown@example.com', 'ROLE_EMPLOYEE', '$2a$12$XyG1h90.m8n.2D.g.K/vR.kN0a8gK02z30.g.K/vR.kN0a8gK02z3', '321 Pine St, Springfield', '1982-11-30');
-- ... (and so on for other employees)

-- Employee Users
INSERT INTO employee (id, name, email, role, password, address, date_of_birth)
SELECT '123e4567-e89b-12d3-a456-426614174003', 'Bob Brown', 'bob.brown@example.com', 'ROLE_EMPLOYEE', 'hashed_pass_03', '321 Pine St, Springfield', '1982-11-30'
WHERE NOT EXISTS (SELECT 1 FROM employee WHERE id = '123e4567-e89b-12d3-a456-426614174003');

INSERT INTO employee (id, name, email, role, password, address, date_of_birth)
SELECT '123e4567-e89b-12d3-a456-426614174004', 'Emily Davis', 'emily.davis@example.com', 'ROLE_EMPLOYEE', 'hashed_pass_04', '654 Maple St, Shelbyville', '1995-02-05'
WHERE NOT EXISTS (SELECT 1 FROM employee WHERE id = '123e4567-e89b-12d3-a456-426614174004');

INSERT INTO employee (id, name, email, role, password, address, date_of_birth)
SELECT '223e4567-e89b-12d3-a456-426614174005', 'Michael Green', 'michael.green@example.com', 'ROLE_EMPLOYEE', 'hashed_pass_05', '987 Cedar St, Springfield', '1988-07-25'
WHERE NOT EXISTS (SELECT 1 FROM employee WHERE id = '223e4567-e89b-12d3-a456-426614174005');

INSERT INTO employee (id, name, email, role, password, address, date_of_birth)
SELECT '223e4567-e89b-12d3-a456-426614174006', 'Sarah Taylor', 'sarah.taylor@example.com', 'ROLE_EMPLOYEE', 'hashed_pass_06', '123 Birch St, Shelbyville', '1992-04-18'
WHERE NOT EXISTS (SELECT 1 FROM employee WHERE id = '223e4567-e89b-12d3-a456-426614174006');

INSERT INTO employee (id, name, email, role, password, address, date_of_birth)
SELECT '223e4567-e89b-12d3-a456-426614174007', 'David Wilson', 'david.wilson@example.com', 'ROLE_EMPLOYEE', 'hashed_pass_07', '456 Ash St, Capital City', '1975-01-11'
WHERE NOT EXISTS (SELECT 1 FROM employee WHERE id = '223e4567-e89b-12d3-a456-426614174007');

INSERT INTO employee (id, name, email, role, password, address, date_of_birth)
SELECT '223e4567-e89b-12d3-a456-426614174008', 'Laura White', 'laura.white@example.com', 'ROLE_EMPLOYEE', 'hashed_pass_08', '789 Palm St, Springfield', '1989-09-02'
WHERE NOT EXISTS (SELECT 1 FROM employee WHERE id = '223e4567-e89b-12d3-a456-426614174008');

INSERT INTO employee (id, name, email, role, password, address, date_of_birth)
SELECT '223e4567-e89b-12d3-a456-426614174009', 'James Harris', 'james.harris@example.com', 'ROLE_EMPLOYEE', 'hashed_pass_09', '321 Cherry St, Shelbyville', '1993-11-15'
WHERE NOT EXISTS (SELECT 1 FROM employee WHERE id = '223e4567-e89b-12d3-a456-426614174009');

INSERT INTO employee (id, name, email, role, password, address, date_of_birth)
SELECT '223e4567-e89b-12d3-a456-426614174010', 'Emma Moore', 'emma.moore@example.com', 'ROLE_EMPLOYEE', 'hashed_pass_10', '654 Spruce St, Capital City', '1980-08-09'
WHERE NOT EXISTS (SELECT 1 FROM employee WHERE id = '223e4567-e89b-12d3-a456-426614174010');

INSERT INTO employee (id, name, email, role, password, address, date_of_birth)
SELECT '223e4567-e89b-12d3-a456-426614174011', 'Ethan Martinez', 'ethan.martinez@example.com', 'ROLE_EMPLOYEE', 'hashed_pass_11', '987 Redwood St, Springfield', '1984-05-03'
WHERE NOT EXISTS (SELECT 1 FROM employee WHERE id = '223e4567-e89b-12d3-a456-426614174011');

INSERT INTO employee (id, name, email, role, password, address, date_of_birth)
SELECT '223e4567-e89b-12d3-a456-426614174012', 'Sophia Clark', 'sophia.clark@example.com', 'ROLE_EMPLOYEE', 'hashed_pass_12', '123 Hickory St, Shelbyville', '1991-12-25'
WHERE NOT EXISTS (SELECT 1 FROM employee WHERE id = '223e4567-e89b-12d3-a456-426614174012');

INSERT INTO employee (id, name, email, role, password, address, date_of_birth)
SELECT '223e4567-e89b-12d3-a456-426614174013', 'Daniel Lewis', 'daniel.lewis@example.com', 'ROLE_EMPLOYEE', 'hashed_pass_13', '456 Cypress St, Capital City', '1976-06-08'
WHERE NOT EXISTS (SELECT 1 FROM employee WHERE id = '223e4567-e89b-12d3-a456-426614174013');

INSERT INTO employee (id, name, email, role, password, address, date_of_birth)
SELECT '223e4567-e89b-12d3-a456-426614174014', 'Isabella Walker', 'isabella.walker@example.com', 'ROLE_EMPLOYEE', 'hashed_pass_14', '789 Willow St, Springfield', '1987-10-17'
WHERE NOT EXISTS (SELECT 1 FROM employee WHERE id = '223e4567-e89b-12d3-a456-426614174014');