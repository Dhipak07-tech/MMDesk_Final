-- V2__Migrate_Data.sql
-- Flyway migration to populate the normalized tables from the existing denormalized data

-- 1. Migrate Categories
INSERT INTO categories (name)
SELECT DISTINCT category FROM tickets WHERE category IS NOT NULL AND category != ''
ON DUPLICATE KEY UPDATE id=id;

UPDATE tickets t
JOIN categories c ON t.category = c.name
SET t.category_id = c.id;

-- 2. Migrate Assignment Groups
INSERT INTO assignment_groups (name)
SELECT DISTINCT assignment_group FROM tickets WHERE assignment_group IS NOT NULL AND assignment_group != ''
ON DUPLICATE KEY UPDATE id=id;

UPDATE tickets t
JOIN assignment_groups ag ON t.assignment_group = ag.name
SET t.assignment_group_id = ag.id;

-- 3. Migrate Users (Assigned To & Created By)
-- Link tickets to users based on user IDs (if 'assigned_to' maps to 'users.uid' or 'users.email')
-- Assuming assigned_to in tickets stores the UID or Email of the user:
UPDATE tickets t
JOIN users u ON t.assigned_to = u.uid OR t.assigned_to = u.email
SET t.assigned_to_user_id = u.id;

UPDATE tickets t
JOIN users u ON t.created_by = u.uid OR t.created_by = u.email
SET t.created_by_user_id = u.id;

-- 4. Migrate Roles
INSERT INTO roles (name)
SELECT DISTINCT role FROM users WHERE role IS NOT NULL AND role != ''
ON DUPLICATE KEY UPDATE id=id;

UPDATE users u
JOIN roles r ON u.role = r.name
SET u.role_id = r.id;

-- 5. Migrate Departments
INSERT INTO departments (name)
SELECT DISTINCT department FROM users WHERE department IS NOT NULL AND department != ''
ON DUPLICATE KEY UPDATE id=id;

UPDATE users u
JOIN departments d ON u.department = d.name
SET u.department_id = d.id;
