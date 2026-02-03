-- WomenShop Database Schema
-- Create the database
CREATE DATABASE IF NOT EXISTS womenshop;
USE womenshop;

-- Table for application settings and capital tracking
CREATE TABLE app_settings (
    id INT PRIMARY KEY AUTO_INCREMENT,
    initial_capital DECIMAL(10, 2) NOT NULL,
    current_capital DECIMAL(10, 2) NOT NULL,
    total_income DECIMAL(10, 2) DEFAULT 0.00,
    total_cost DECIMAL(10, 2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Base table for common product attributes
CREATE TABLE products (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    category ENUM('CLOTHES', 'SHOES', 'ACCESSORIES') NOT NULL,
    purchase_price DECIMAL(10, 2) NOT NULL,
    selling_price DECIMAL(10, 2) NOT NULL,
    stock_quantity INT DEFAULT 0,
    discount_active BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category (category)
);

-- Specific table for Clothes with additional attributes
CREATE TABLE clothes (
    product_id INT PRIMARY KEY,
    size VARCHAR(10) NOT NULL,
    color VARCHAR(50) NOT NULL,
    material VARCHAR(50),
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- Specific table for Shoes with additional attributes
CREATE TABLE shoes (
    product_id INT PRIMARY KEY,
    shoe_size INT NOT NULL,
    color VARCHAR(50) NOT NULL,
    brand VARCHAR(50),
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- Specific table for Accessories with additional attributes
CREATE TABLE accessories (
    product_id INT PRIMARY KEY,
    type VARCHAR(50) NOT NULL, -- e.g., bag, jewelry, scarf
    color VARCHAR(50),
    material VARCHAR(50),
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- Table for transaction history
CREATE TABLE transactions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT NOT NULL,
    transaction_type ENUM('PURCHASE', 'SALE') NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    INDEX idx_date (transaction_date),
    INDEX idx_type (transaction_type)
);

-- Initialize app settings with default capital
INSERT INTO app_settings (initial_capital, current_capital, total_income, total_cost)
VALUES (10000.00, 10000.00, 0.00, 0.00);

-- Sample data for testing
INSERT INTO products (name, category, purchase_price, selling_price, stock_quantity) VALUES
('Summer Dress', 'CLOTHES', 25.00, 50.00, 0),
('Leather Jacket', 'CLOTHES', 80.00, 150.00, 0),
('Running Sneakers', 'SHOES', 40.00, 80.00, 0),
('High Heels', 'SHOES', 50.00, 100.00, 0),
('Designer Handbag', 'ACCESSORIES', 30.00, 80.00, 0),
('Silver Necklace', 'ACCESSORIES', 15.00, 40.00, 0);

-- Insert corresponding category-specific data
INSERT INTO clothes (product_id, size, color, material) VALUES
(1, 'M', 'Yellow', 'Cotton'),
(2, 'L', 'Black', 'Leather');

INSERT INTO shoes (product_id, shoe_size, color, brand) VALUES
(3, 38, 'White', 'Nike'),
(4, 37, 'Red', 'Zara');

INSERT INTO accessories (product_id, type, color, material) VALUES
(5, 'Handbag', 'Brown', 'Leather'),
(6, 'Necklace', 'Silver', 'Sterling Silver');