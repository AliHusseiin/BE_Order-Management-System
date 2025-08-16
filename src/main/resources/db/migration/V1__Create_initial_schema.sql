-- Order Management System - DDL Script (Clean Database Design)
-- Database: PostgreSQL - Focused on data integrity, not business logic

-- Create User table for authentication
CREATE TABLE user_table (
    user_id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    modified_by VARCHAR(100)
);

-- Create Customer table
CREATE TABLE customer (
    customer_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    mobile VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_customer_user FOREIGN KEY (user_id) REFERENCES user_table(user_id) ON DELETE CASCADE
);

-- Create Address table
CREATE TABLE address (
    address_id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    address_type VARCHAR(50) NOT NULL,
    street_address VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    country VARCHAR(100) NOT NULL,
    is_default BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_address_customer FOREIGN KEY (customer_id) REFERENCES customer(customer_id) ON DELETE CASCADE
);

-- Create Product table
CREATE TABLE product (
    product_id BIGSERIAL PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    stock_quantity INTEGER NOT NULL,
    category VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_product_price CHECK (price >= 0),
    CONSTRAINT chk_stock_quantity CHECK (stock_quantity >= 0)
);

-- Create Order table with audit trail
CREATE TABLE order_table (
    order_id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    shipping_address_id BIGINT NOT NULL,
    created_by_user_id BIGINT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    order_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    order_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_order_customer FOREIGN KEY (customer_id) REFERENCES customer(customer_id) ON DELETE RESTRICT,
    CONSTRAINT fk_order_address FOREIGN KEY (shipping_address_id) REFERENCES address(address_id) ON DELETE RESTRICT,
    CONSTRAINT fk_order_created_by_user FOREIGN KEY (created_by_user_id) REFERENCES user_table(user_id) ON DELETE RESTRICT,
    CONSTRAINT chk_total_amount CHECK (total_amount >= 0)
);

-- Create Order Item table
CREATE TABLE order_item (
    order_item_id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    
    CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES order_table(order_id) ON DELETE CASCADE,
    CONSTRAINT fk_order_item_product FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE RESTRICT,
    CONSTRAINT chk_quantity CHECK (quantity > 0),
    CONSTRAINT chk_unit_price CHECK (unit_price >= 0),
    CONSTRAINT chk_subtotal CHECK (subtotal >= 0)
);

-- Create Invoice table
CREATE TABLE invoice (
    invoice_id BIGSERIAL PRIMARY KEY,
    order_id BIGINT UNIQUE NOT NULL,
    invoice_number VARCHAR(50) UNIQUE NOT NULL,
    invoice_amount DECIMAL(10,2) NOT NULL,
    tax_amount DECIMAL(10,2) DEFAULT 0,
    total_amount DECIMAL(10,2) NOT NULL,
    invoice_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    invoice_status VARCHAR(20) NOT NULL DEFAULT 'GENERATED',
    
    CONSTRAINT fk_invoice_order FOREIGN KEY (order_id) REFERENCES order_table(order_id) ON DELETE RESTRICT,
    CONSTRAINT chk_invoice_amount CHECK (invoice_amount >= 0),
    CONSTRAINT chk_tax_amount CHECK (tax_amount >= 0),
    CONSTRAINT chk_invoice_total CHECK (total_amount >= 0)
);

-- Create indexes for performance
CREATE INDEX idx_user_email ON user_table(email);
CREATE INDEX idx_user_username ON user_table(username);
CREATE INDEX idx_user_role ON user_table(role);
CREATE INDEX idx_customer_user_id ON customer(user_id);
CREATE INDEX idx_address_customer_id ON address(customer_id);
CREATE INDEX idx_address_default ON address(customer_id) WHERE is_default = true;
CREATE INDEX idx_product_category ON product(category);
CREATE INDEX idx_product_name ON product(product_name);
CREATE INDEX idx_order_customer_id ON order_table(customer_id);
CREATE INDEX idx_order_created_by_user ON order_table(created_by_user_id);
CREATE INDEX idx_order_status ON order_table(order_status);
CREATE INDEX idx_order_date ON order_table(order_date);
CREATE INDEX idx_order_item_order_id ON order_item(order_id);
CREATE INDEX idx_order_item_product_id ON order_item(product_id);
CREATE INDEX idx_invoice_order_id ON invoice(order_id);
CREATE INDEX idx_invoice_number ON invoice(invoice_number);
CREATE INDEX idx_invoice_status ON invoice(invoice_status);

-- Create triggers for updated_at timestamps only
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_user_updated_at BEFORE UPDATE ON user_table FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_customer_updated_at BEFORE UPDATE ON customer FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_address_updated_at BEFORE UPDATE ON address FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_product_updated_at BEFORE UPDATE ON product FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_order_updated_at BEFORE UPDATE ON order_table FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Simple function to generate invoice number
CREATE OR REPLACE FUNCTION generate_invoice_number()
RETURNS TRIGGER AS $$
BEGIN
    NEW.invoice_number = 'INV-' || TO_CHAR(CURRENT_DATE, 'YYYY') || '-' || LPAD(NEW.invoice_id::text, 6, '0');
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER generate_invoice_number_trigger BEFORE INSERT ON invoice FOR EACH ROW EXECUTE FUNCTION generate_invoice_number();