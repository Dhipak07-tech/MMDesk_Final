-- V1__Init_Normalized_Schema.sql
-- Flyway migration to normalize the database schema

-- 1. Create 'roles' table
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Create 'departments' table
CREATE TABLE IF NOT EXISTS departments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. Create 'categories' table
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. Create 'assignment_groups' table
CREATE TABLE IF NOT EXISTS assignment_groups (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 5. Add foreign key columns to 'tickets' table
-- Check if columns exist before adding them (MySQL 8+ does not have CREATE TABLE IF NOT EXISTS for columns, but Flyway is usually run once)
ALTER TABLE tickets ADD COLUMN category_id BIGINT;
ALTER TABLE tickets ADD COLUMN assignment_group_id BIGINT;
ALTER TABLE tickets ADD COLUMN assigned_to_user_id BIGINT;
ALTER TABLE tickets ADD COLUMN created_by_user_id BIGINT;

-- 6. Add constraints
ALTER TABLE tickets ADD CONSTRAINT fk_ticket_category FOREIGN KEY (category_id) REFERENCES categories(id);
ALTER TABLE tickets ADD CONSTRAINT fk_ticket_assignment_group FOREIGN KEY (assignment_group_id) REFERENCES assignment_groups(id);
ALTER TABLE tickets ADD CONSTRAINT fk_ticket_assigned_to FOREIGN KEY (assigned_to_user_id) REFERENCES users(id);
ALTER TABLE tickets ADD CONSTRAINT fk_ticket_created_by FOREIGN KEY (created_by_user_id) REFERENCES users(id);

-- 7. Add foreign key columns to 'users' table
ALTER TABLE users ADD COLUMN role_id BIGINT;
ALTER TABLE users ADD COLUMN department_id BIGINT;

ALTER TABLE users ADD CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES roles(id);
ALTER TABLE users ADD CONSTRAINT fk_user_department FOREIGN KEY (department_id) REFERENCES departments(id);
