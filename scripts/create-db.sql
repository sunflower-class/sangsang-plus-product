-- Azure PostgreSQL Database Creation Script
-- Run this with admin user (sangsangplus_admin)

-- Create database for product service
CREATE DATABASE product_db;

-- Grant all privileges
GRANT ALL PRIVILEGES ON DATABASE product_db TO sangsangplus_admin;