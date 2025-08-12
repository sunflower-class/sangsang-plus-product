-- Product Service UUID Migration Script
-- WARNING: This will DROP all existing data!

-- 1. Drop existing tables (CASCADE to handle foreign key constraints)
DROP TABLE IF EXISTS product_images CASCADE;
DROP TABLE IF EXISTS products CASCADE;

-- 2. Create products table with UUID for user_id
CREATE TABLE products (
    product_id BIGSERIAL PRIMARY KEY,
    user_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. Create product_images table
CREATE TABLE product_images (
    image_id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    url VARCHAR(500) NOT NULL,
    alt_text VARCHAR(255),
    display_order INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_product_images_product
        FOREIGN KEY (product_id) 
        REFERENCES products(product_id) 
        ON DELETE CASCADE
);

CREATE TABLE product_details (
    product_id BIGINT PRIMARY KEY,
    content TEXT NOT NULL,  -- HTML 내용
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) 
        REFERENCES products(product_id) 
        ON DELETE CASCADE
);

-- 4. Create indexes for better performance
CREATE INDEX idx_products_user_id ON products(user_id);
CREATE INDEX idx_products_category ON products(category);
CREATE INDEX idx_products_created_at ON products(created_at DESC);
CREATE INDEX idx_product_images_product_id ON product_images(product_id);
CREATE INDEX idx_product_images_display_order ON product_images(product_id, display_order);

-- 5. Add comments for documentation
COMMENT ON TABLE products IS 'Product information table with UUID user references';
COMMENT ON COLUMN products.user_id IS 'UUID of the user who created the product';

-- 6. Sample data for testing (optional)
-- Uncomment below to insert test data
/*
INSERT INTO products (user_id, title, description, category, price) VALUES
    ('550e8400-e29b-41d4-a716-446655440001'::UUID, 'MacBook Pro 16"', 'Latest MacBook Pro with M3 chip', '전자제품', 3500000),
    ('550e8400-e29b-41d4-a716-446655440002'::UUID, 'iPhone 15 Pro', 'iPhone 15 Pro 256GB', '전자제품', 1500000),
    ('550e8400-e29b-41d4-a716-446655440001'::UUID, '무선 키보드', 'Apple Magic Keyboard', '전자제품', 150000);

INSERT INTO product_images (product_id, url, alt_text, display_order) VALUES
    (1, 'https://example.com/macbook1.jpg', 'MacBook Pro front view', 0),
    (1, 'https://example.com/macbook2.jpg', 'MacBook Pro side view', 1),
    (2, 'https://example.com/iphone1.jpg', 'iPhone 15 Pro', 0);
*/