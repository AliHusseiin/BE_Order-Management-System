-- Order Management System - Seed Data
-- Insert initial data for testing and development

-- Insert Admin User (password: admin123)
-- BCrypt hash for 'admin123': $2a$10$WO0X0o8igZNlzHmaj35RWuDcaQUDyVd6w0sj20jxBKKsgMmyjc0Hi
INSERT INTO user_table (username, email, password_hash, role, is_active) VALUES
('admin', 'admin@oms.com', '$2a$10$WO0X0o8igZNlzHmaj35RWuDcaQUDyVd6w0sj20jxBKKsgMmyjc0Hi', 'ADMIN', true);

-- Insert Test Customer Users (password: customer123)
-- BCrypt hash for 'customer123': $2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.
INSERT INTO user_table (username, email, password_hash, role, is_active) VALUES
('john.doe', 'john.doe@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'CUSTOMER', true),
('jane.smith', 'jane.smith@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'CUSTOMER', true),
('bob.wilson', 'bob.wilson@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'CUSTOMER', true);

-- Insert Customer profiles
INSERT INTO customer (user_id, first_name, last_name, mobile) VALUES
((SELECT user_id FROM user_table WHERE username = 'john.doe'), 'John', 'Doe', '+1-555-1234'),
((SELECT user_id FROM user_table WHERE username = 'jane.smith'), 'Jane', 'Smith', '+1-555-5678'),
((SELECT user_id FROM user_table WHERE username = 'bob.wilson'), 'Bob', 'Wilson', '+1-555-9012');

-- Insert Customer addresses
INSERT INTO address (customer_id, address_type, street_address, city, state, postal_code, country, is_default) VALUES
((SELECT customer_id FROM customer WHERE first_name = 'John' AND last_name = 'Doe'), 'HOME', '123 Main Street', 'New York', 'NY', '10001', 'USA', true),
((SELECT customer_id FROM customer WHERE first_name = 'John' AND last_name = 'Doe'), 'WORK', '456 Business Ave', 'New York', 'NY', '10002', 'USA', false),
((SELECT customer_id FROM customer WHERE first_name = 'Jane' AND last_name = 'Smith'), 'HOME', '789 Oak Street', 'Los Angeles', 'CA', '90001', 'USA', true),
((SELECT customer_id FROM customer WHERE first_name = 'Bob' AND last_name = 'Wilson'), 'HOME', '321 Pine Street', 'Chicago', 'IL', '60601', 'USA', true);

-- Insert Sample Products
INSERT INTO product (product_name, description, price, stock_quantity, category) VALUES
('Laptop Computer', 'High-performance laptop for business and gaming', 1299.99, 50, 'Electronics'),
('Wireless Mouse', 'Ergonomic wireless mouse with long battery life', 29.99, 200, 'Electronics'),
('Office Chair', 'Comfortable ergonomic office chair with lumbar support', 399.99, 25, 'Furniture'),
('Desk Lamp', 'LED desk lamp with adjustable brightness and color temperature', 79.99, 75, 'Furniture'),
('Coffee Maker', 'Automatic drip coffee maker with programmable timer', 149.99, 30, 'Appliances'),
('Bluetooth Headphones', 'Noise-cancelling wireless headphones', 199.99, 60, 'Electronics'),
('Standing Desk', 'Height-adjustable standing desk', 599.99, 15, 'Furniture'),
('Mechanical Keyboard', 'RGB mechanical gaming keyboard', 129.99, 40, 'Electronics'),
('Water Bottle', 'Insulated stainless steel water bottle', 24.99, 100, 'Accessories'),
('Notebook Set', 'Pack of 3 premium notebooks', 19.99, 150, 'Stationery');

-- Insert Sample Orders
INSERT INTO order_table (customer_id, shipping_address_id, created_by_user_id, total_amount, order_status, order_date) VALUES
-- John Doe's orders
((SELECT customer_id FROM customer WHERE first_name = 'John' AND last_name = 'Doe'), 
 (SELECT address_id FROM address WHERE customer_id = (SELECT customer_id FROM customer WHERE first_name = 'John' AND last_name = 'Doe') AND is_default = true),
 (SELECT user_id FROM user_table WHERE username = 'admin'),
 1429.98, 'PENDING', CURRENT_TIMESTAMP - INTERVAL '2 days'),

-- Jane Smith's orders  
((SELECT customer_id FROM customer WHERE first_name = 'Jane' AND last_name = 'Smith'),
 (SELECT address_id FROM address WHERE customer_id = (SELECT customer_id FROM customer WHERE first_name = 'Jane' AND last_name = 'Smith') AND is_default = true),
 (SELECT user_id FROM user_table WHERE username = 'admin'),
 279.97, 'CONFIRMED', CURRENT_TIMESTAMP - INTERVAL '1 day'),

-- Bob Wilson's orders
((SELECT customer_id FROM customer WHERE first_name = 'Bob' AND last_name = 'Wilson'),
 (SELECT address_id FROM address WHERE customer_id = (SELECT customer_id FROM customer WHERE first_name = 'Bob' AND last_name = 'Wilson') AND is_default = true),
 (SELECT user_id FROM user_table WHERE username = 'admin'),
 729.98, 'SHIPPED', CURRENT_TIMESTAMP - INTERVAL '3 days');

-- Insert Order Items
-- John Doe's order items (Laptop + Mouse)
INSERT INTO order_item (order_id, product_id, quantity, unit_price, subtotal) VALUES
((SELECT order_id FROM order_table WHERE customer_id = (SELECT customer_id FROM customer WHERE first_name = 'John' AND last_name = 'Doe') LIMIT 1),
 (SELECT product_id FROM product WHERE product_name = 'Laptop Computer'), 1, 1299.99, 1299.99),
((SELECT order_id FROM order_table WHERE customer_id = (SELECT customer_id FROM customer WHERE first_name = 'John' AND last_name = 'Doe') LIMIT 1),
 (SELECT product_id FROM product WHERE product_name = 'Wireless Mouse'), 1, 29.99, 29.99),
((SELECT order_id FROM order_table WHERE customer_id = (SELECT customer_id FROM customer WHERE first_name = 'John' AND last_name = 'Doe') LIMIT 1),
 (SELECT product_id FROM product WHERE product_name = 'Mechanical Keyboard'), 1, 129.99, 129.99);

-- Jane Smith's order items (Headphones + Coffee Maker)  
INSERT INTO order_item (order_id, product_id, quantity, unit_price, subtotal) VALUES
((SELECT order_id FROM order_table WHERE customer_id = (SELECT customer_id FROM customer WHERE first_name = 'Jane' AND last_name = 'Smith') LIMIT 1),
 (SELECT product_id FROM product WHERE product_name = 'Bluetooth Headphones'), 1, 199.99, 199.99),
((SELECT order_id FROM order_table WHERE customer_id = (SELECT customer_id FROM customer WHERE first_name = 'Jane' AND last_name = 'Smith') LIMIT 1),
 (SELECT product_id FROM product WHERE product_name = 'Desk Lamp'), 1, 79.99, 79.99);

-- Bob Wilson's order items (Office Chair + Standing Desk)
INSERT INTO order_item (order_id, product_id, quantity, unit_price, subtotal) VALUES
((SELECT order_id FROM order_table WHERE customer_id = (SELECT customer_id FROM customer WHERE first_name = 'Bob' AND last_name = 'Wilson') LIMIT 1),
 (SELECT product_id FROM product WHERE product_name = 'Office Chair'), 1, 399.99, 399.99),
((SELECT order_id FROM order_table WHERE customer_id = (SELECT customer_id FROM customer WHERE first_name = 'Bob' AND last_name = 'Wilson') LIMIT 1),
 (SELECT product_id FROM product WHERE product_name = 'Standing Desk'), 1, 599.99, 599.99);

-- Insert Sample Invoices for CONFIRMED and SHIPPED orders
INSERT INTO invoice (order_id, invoice_number, invoice_amount, tax_amount, total_amount, invoice_status) VALUES
-- Jane Smith's invoice (CONFIRMED order)
((SELECT order_id FROM order_table WHERE customer_id = (SELECT customer_id FROM customer WHERE first_name = 'Jane' AND last_name = 'Smith') LIMIT 1),
 'INV-2025-000001', 279.97, 22.40, 302.37, 'GENERATED'),

-- Bob Wilson's invoice (SHIPPED order) 
((SELECT order_id FROM order_table WHERE customer_id = (SELECT customer_id FROM customer WHERE first_name = 'Bob' AND last_name = 'Wilson') LIMIT 1),
 'INV-2025-000002', 729.98, 58.40, 788.38, 'SENT');