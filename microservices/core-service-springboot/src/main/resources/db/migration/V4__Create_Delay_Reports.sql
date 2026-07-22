-- V4__Create_Delay_Reports.sql
-- Migration to create the delay_reports table

CREATE TABLE IF NOT EXISTS delay_reports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_incident_id VARCHAR(255) NOT NULL,
    assigned_time TIMESTAMP NOT NULL,
    expected_completion_time TIMESTAMP NOT NULL,
    actual_completion_time TIMESTAMP NOT NULL,
    total_delay_duration VARCHAR(100),
    reason_for_delay VARCHAR(255) NOT NULL,
    activities_performed_during_delay TEXT NOT NULL,
    meeting_details TEXT,
    technical_system_issues TEXT,
    dependency_approval_wait_time DECIMAL(10, 2),
    additional_comments TEXT,
    report_status ENUM('Draft', 'Submitted/Pending Review', 'Approved', 'Rejected/Clarification Needed') DEFAULT 'Draft',
    rejection_reason TEXT,
    employee_uid VARCHAR(128) NOT NULL,
    employee_name VARCHAR(255) NOT NULL,
    manager_uid VARCHAR(128) NOT NULL,
    manager_name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_employee (employee_uid),
    INDEX idx_manager (manager_uid),
    INDEX idx_status (report_status)
) ENGINE=InnoDB;
