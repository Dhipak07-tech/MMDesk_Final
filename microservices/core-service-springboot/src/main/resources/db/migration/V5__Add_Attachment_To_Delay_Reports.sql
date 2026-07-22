-- V5__Add_Attachment_To_Delay_Reports.sql
-- Add attachment fields to delay_reports table

ALTER TABLE delay_reports 
ADD COLUMN attachment_url VARCHAR(500) DEFAULT NULL,
ADD COLUMN attachment_name VARCHAR(255) DEFAULT NULL;
