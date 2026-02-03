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

-- Enhanced sample data for testing
INSERT INTO products (name, category, purchase_price, selling_price, stock_quantity) VALUES
-- Clothes
('Summer Floral Dress', 'CLOTHES', 25.00, 60.00, 0),
('Leather Jacket', 'CLOTHES', 80.00, 180.00, 0),
('Casual Denim Jeans', 'CLOTHES', 30.00, 70.00, 0),
('Silk Evening Gown', 'CLOTHES', 120.00, 280.00, 0),
('Cotton T-Shirt', 'CLOTHES', 10.00, 25.00, 0),
('Wool Cardigan', 'CLOTHES', 45.00, 95.00, 0),
('Elegant Blazer', 'CLOTHES', 65.00, 145.00, 0),
('Maxi Skirt', 'CLOTHES', 28.00, 65.00, 0),
-- Shoes
('Running Sneakers', 'SHOES', 40.00, 85.00, 0),
('High Heels', 'SHOES', 50.00, 110.00, 0),
('Ballet Flats', 'SHOES', 35.00, 75.00, 0),
('Ankle Boots', 'SHOES', 70.00, 150.00, 0),
('Sandals', 'SHOES', 25.00, 55.00, 0),
('Wedge Heels', 'SHOES', 55.00, 120.00, 0),
-- Accessories
('Designer Handbag', 'ACCESSORIES', 30.00, 90.00, 0),
('Silver Necklace', 'ACCESSORIES', 15.00, 45.00, 0),
('Silk Scarf', 'ACCESSORIES', 12.00, 35.00, 0),
('Leather Belt', 'ACCESSORIES', 18.00, 40.00, 0),
('Sunglasses', 'ACCESSORIES', 25.00, 65.00, 0),
('Fashion Watch', 'ACCESSORIES', 40.00, 95.00, 0),
('Pearl Earrings', 'ACCESSORIES', 35.00, 85.00, 0),
('Crossbody Bag', 'ACCESSORIES', 28.00, 70.00, 0);

-- Insert corresponding category-specific data for Clothes
INSERT INTO clothes (product_id, size, color, material) VALUES
                                                            (1, 'M', 'Floral Multi', 'Cotton'),
                                                            (2, 'L', 'Black', 'Genuine Leather'),
                                                            (3, 'M', 'Blue', 'Denim'),
                                                            (4, 'M', 'Red', 'Silk'),
                                                            (5, 'S', 'White', 'Cotton'),
                                                            (6, 'L', 'Gray', 'Wool'),
                                                            (7, 'M', 'Navy', 'Polyester Blend'),
                                                            (8, 'S', 'Black', 'Chiffon');

-- Insert corresponding category-specific data for Shoes
INSERT INTO shoes (product_id, shoe_size, color, brand) VALUES
                                                            (9, 38, 'White', 'Nike'),
                                                            (10, 37, 'Red', 'Zara'),
                                                            (11, 39, 'Nude', 'Clarks'),
                                                            (12, 38, 'Brown', 'Timberland'),
                                                            (13, 37, 'Gold', 'Steve Madden'),
                                                            (14, 38, 'Beige', 'Aldo');

-- Insert corresponding category-specific data for Accessories
INSERT INTO accessories (product_id, type, color, material) VALUES
                                                                (15, 'Handbag', 'Brown', 'Leather'),
                                                                (16, 'Necklace', 'Silver', 'Sterling Silver'),
                                                                (17, 'Scarf', 'Pink', 'Silk'),
                                                                (18, 'Belt', 'Black', 'Leather'),
                                                                (19, 'Sunglasses', 'Black', 'Plastic/Metal'),
                                                                (20, 'Watch', 'Gold', 'Metal'),
                                                                (21, 'Earrings', 'White', 'Pearl'),
                                                                (22, 'Bag', 'Burgundy', 'Faux Leather');