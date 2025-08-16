-- Add missing columns to match entity structure
-- BaseEntity requires: version column
-- AuditableEntity requires: version, created_at, updated_at, created_by, modified_by

-- Add version column to all tables (required by BaseEntity)
ALTER TABLE customer ADD COLUMN version BIGINT DEFAULT 0;
ALTER TABLE address ADD COLUMN version BIGINT DEFAULT 0;
ALTER TABLE product ADD COLUMN version BIGINT DEFAULT 0;
ALTER TABLE order_table ADD COLUMN version BIGINT DEFAULT 0;
ALTER TABLE order_item ADD COLUMN version BIGINT DEFAULT 0;
ALTER TABLE invoice ADD COLUMN version BIGINT DEFAULT 0;

-- Add audit columns to tables that extend AuditableEntity
-- Product extends BaseEntity (only needs version - already added above)
-- Customer extends AuditableEntity (needs audit columns)
ALTER TABLE customer ADD COLUMN created_by VARCHAR(100);
ALTER TABLE customer ADD COLUMN modified_by VARCHAR(100);

-- Address extends AuditableEntity (needs audit columns)
ALTER TABLE address ADD COLUMN created_by VARCHAR(100);
ALTER TABLE address ADD COLUMN modified_by VARCHAR(100);

-- Order extends AuditableEntity (needs audit columns and created_at)
ALTER TABLE order_table ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE order_table ADD COLUMN created_by VARCHAR(100);
ALTER TABLE order_table ADD COLUMN modified_by VARCHAR(100);

-- Invoice extends BaseEntity (only needs version - already added above)
-- OrderItem extends BaseEntity (only needs version - already added above)