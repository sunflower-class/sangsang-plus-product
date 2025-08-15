-- Product Service UUID Migration Script
-- WARNING: This will DROP all existing data!

-- 1. Drop existing tables (CASCADE to handle foreign key constraints)
-- product_images and product_details will be stored elsewhere
DROP TABLE IF EXISTS product_images CASCADE;
DROP TABLE IF EXISTS product_details CASCADE;
DROP TABLE IF EXISTS products CASCADE;

-- 2. Create products table with UUID for user_id and new fields
CREATE TABLE products (
    product_id BIGSERIAL PRIMARY KEY,
    user_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL CHECK (price >= 0),
    brand VARCHAR(100),
    source VARCHAR(50) DEFAULT 'DETAIL_SERVICE',
    status VARCHAR(20) DEFAULT 'ACTIVE',
    metadata TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. product_images and product_details tables are not created here
-- They will be stored in separate services

-- 4. Create indexes for better performance
CREATE INDEX idx_products_user_id ON products(user_id);
CREATE INDEX idx_products_category ON products(category);
CREATE INDEX idx_products_created_at ON products(created_at DESC);
CREATE INDEX idx_products_status ON products(status);
CREATE INDEX idx_products_source ON products(source);

-- 5. Add comments for documentation
COMMENT ON TABLE products IS 'Product information table with UUID user references';
COMMENT ON COLUMN products.user_id IS 'UUID of the user who created the product';

-- 6. Sample data for testing (optional)
-- Uncomment below to insert test data
/*
INSERT INTO products (user_id, name, description, category, price, brand, source, status, metadata) VALUES
    ('550e8400-e29b-41d4-a716-446655440001'::UUID, 'MacBook Pro 16"', 'Latest MacBook Pro with M3 chip', '전자제품', 3500000, 'Apple', 'DETAIL_SERVICE', 'ACTIVE', '{"parsed_from": "test_data"}'),
    ('550e8400-e29b-41d4-a716-446655440002'::UUID, 'iPhone 15 Pro', 'iPhone 15 Pro 256GB', '전자제품', 1500000, 'Apple', 'DETAIL_SERVICE', 'ACTIVE', '{"parsed_from": "test_data"}'),
    ('550e8400-e29b-41d4-a716-446655440001'::UUID, '무선 키보드', 'Apple Magic Keyboard', '전자제품', 150000, 'Apple', 'DETAIL_SERVICE', 'ACTIVE', '{"parsed_from": "test_data"}');
*/