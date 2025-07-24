-- Product Service Database Initialization Script
-- Run this script with admin privileges

-- Create database for product service
CREATE DATABASE product_db WITH 
    OWNER = sangsangplus_admin
    ENCODING = 'UTF8'
    CONNECTION LIMIT = -1;

-- Connect to product_db
\c product_db;

-- Create schema if needed (optional, but recommended for organization)
CREATE SCHEMA IF NOT EXISTS product_schema;

-- Grant all privileges on schema to the user
GRANT ALL PRIVILEGES ON SCHEMA product_schema TO sangsangplus_admin;
GRANT ALL PRIVILEGES ON DATABASE product_db TO sangsangplus_admin;

-- Set default search path
ALTER DATABASE product_db SET search_path TO product_schema, public;

-- Create products table
CREATE TABLE IF NOT EXISTS products (
    product_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    user_email VARCHAR(255),
    user_name VARCHAR(255),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL CHECK (price > 0),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create product_images table
CREATE TABLE IF NOT EXISTS product_images (
    image_id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    url VARCHAR(500) NOT NULL,
    alt_text VARCHAR(255),
    display_order INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_product
        FOREIGN KEY(product_id) 
        REFERENCES products(product_id)
        ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_products_user_id ON products(user_id);
CREATE INDEX idx_products_category ON products(category);
CREATE INDEX idx_products_created_at ON products(created_at DESC);
CREATE INDEX idx_product_images_product_id ON product_images(product_id);
CREATE INDEX idx_product_images_display_order ON product_images(product_id, display_order);

-- Create update trigger for updated_at column
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_products_updated_at BEFORE UPDATE
    ON products FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();