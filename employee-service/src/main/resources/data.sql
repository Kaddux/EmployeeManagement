DROP TABLE IF EXISTS family;
DROP TABLE IF EXISTS employee CASCADE;
DROP TABLE IF EXISTS department CASCADE;

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

CREATE TABLE family
(
    family_id         UUID PRIMARY KEY,
    employee_id       UUID NOT NULL,
    father_name       VARCHAR(255),
    mother_name       VARCHAR(255),
    number_of_members VARCHAR(255),
    CONSTRAINT fk_family_employee FOREIGN KEY (employee_id) REFERENCES employee(id) ON DELETE CASCADE
);

CREATE TABLE department (
                            department_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                            department_name VARCHAR(100) NOT NULL,
                            department_code VARCHAR(10) NOT NULL,

    -- Ensures department codes are unique and index-optimized
                            CONSTRAINT uq_department_code UNIQUE (department_code)
);

-- Step A: Add the new column
ALTER TABLE employee
    ADD COLUMN department_id INT;

-- Step B: Add the Foreign Key constraint
ALTER TABLE employee
    ADD CONSTRAINT fk_employee_department
        FOREIGN KEY (department_id)
            REFERENCES department (department_id)
            ON DELETE SET NULL;

INSERT INTO department (department_name, department_code) VALUES
                                                              ('Engineering', 'ENG-01'),
                                                              ('Human Resources', 'HR-02'),
                                                              ('Finance', 'FIN-03'),
                                                              ('Marketing', 'MKT-04'),
                                                              ('Operations', 'OPS-05');


CREATE INDEX idx_family_employee_id ON family(employee_id);

INSERT INTO employee (id, name, email, role, password, address, date_of_birth, department_id) VALUES
    ('123e4567-e89b-12d3-a456-426614174000', 'John Doe',       'john.doe@example.com',       'ROLE_ADMIN',    '$2a$10$7QzQ1Q1Q1Q1Q1Q1Q1Q1QOuSomeDummyHashForJohnDoe11111111111', '123 Main St',   '1985-06-15', 1),
    ('123e4567-e89b-12d3-a456-426614174001', 'Jane Smith',     'jane.smith@example.com',     'ROLE_ADMIN',    '$2a$10$7QzQ1Q1Q1Q1Q1Q1Q1Q1QOuSomeDummyHashForJaneSmi1111111111', '456 Elm St',    '1990-09-23', 2),
    ('123e4567-e89b-12d3-a456-426614174003', 'Bob Brown',      'bob.brown@example.com',      'ROLE_EMPLOYEE', '$2a$10$7QzQ1Q1Q1Q1Q1Q1Q1Q1QOuSomeDummyHashForBobBrown1111111111', '321 Pine St',   '1982-11-30', 3),
    ('123e4567-e89b-12d3-a456-426614174004', 'Emily Davis',    'emily.davis@example.com',    'ROLE_EMPLOYEE', '$2a$10$7QzQ1Q1Q1Q1Q1Q1Q1Q1QOuSomeDummyHashForEmilyDav111111111', '654 Maple St',  '1995-02-05', 4),
    ('223e4567-e89b-12d3-a456-426614174005', 'Michael Green',  'michael.green@example.com',  'ROLE_EMPLOYEE', '$2a$10$7QzQ1Q1Q1Q1Q1Q1Q1Q1QOuSomeDummyHashForMichaelG111111111', '987 Cedar St',  '1988-07-25', 5);

ALTER TABLE employee ADD COLUMN enabled BOOLEAN DEFAULT true;

CREATE TABLE IF NOT EXISTS verification_tokens (
                                                   id BIGSERIAL PRIMARY KEY,
                                                   token VARCHAR(255) NOT NULL UNIQUE,
                                                   employee_id UUID NOT NULL,
                                                   expiry_date TIMESTAMP NOT NULL,


    -- Foreign key constraint: If a user is deleted, automatically delete their tokens
                                                   CONSTRAINT fk_verification_tokens_user
                                                       FOREIGN KEY (employee_id)
                                                           REFERENCES employee(id)
                                                           ON DELETE CASCADE
);

-- Index for high-performance lookups when users click the verification link
CREATE INDEX IF NOT EXISTS idx_verification_tokens_token ON verification_tokens(token);

ALTER TABLE  verification_tokens ADD COLUMN warningSent BOOLEAN default false;

ALTER TABLE verification_tokens DROP COLUMN warningSent;