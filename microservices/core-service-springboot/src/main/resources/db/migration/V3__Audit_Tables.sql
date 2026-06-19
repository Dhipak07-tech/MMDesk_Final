-- V3__Audit_Tables.sql
-- Flyway migration to create Hibernate Envers audit tables

CREATE TABLE IF NOT EXISTS revinfo (
    rev INT AUTO_INCREMENT PRIMARY KEY,
    revtstmp BIGINT
);

CREATE TABLE IF NOT EXISTS tickets_aud (
    id BIGINT NOT NULL,
    rev INT NOT NULL,
    revtype TINYINT,
    
    -- Tracked fields for auditing
    category_id BIGINT,
    category VARCHAR(100),
    assignment_group_id BIGINT,
    assignment_group VARCHAR(100),
    assigned_to_user_id BIGINT,
    assigned_to VARCHAR(128),
    assigned_to_name VARCHAR(255),
    created_by_user_id BIGINT,
    created_by VARCHAR(128),
    created_by_name VARCHAR(255),
    status VARCHAR(30),
    priority VARCHAR(20),
    title VARCHAR(500),
    description TEXT,
    
    PRIMARY KEY (id, rev),
    CONSTRAINT fk_tickets_aud_revinfo FOREIGN KEY (rev) REFERENCES revinfo(rev)
);
