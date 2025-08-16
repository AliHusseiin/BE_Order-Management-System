# Order Management System (OMS)

A Spring Boot application for managing customers, products, orders, and invoices with a complete workflow from order creation to invoice generation.

## What This Project Does

This is a backend REST API that handles:

- **Customer Management** - Register customers with multiple addresses
- **Product Catalog** - Manage products with inventory tracking
- **Order Processing** - Create orders, approve them, and track status
- **Invoice Generation** - Automatically generate invoices when orders are approved
- **Smart Filtering** - Search and filter all data with advanced queries

The system follows a realistic business workflow where orders go through approval before generating invoices.

## Key Features

- **JWT Authentication** - Secure login with role-based access (Admin/Customer)
- **Event-Driven Design** - Order approval automatically triggers invoice creation
- **Smart Error Handling** - User-friendly error messages for database constraints
- **Dynamic Filtering** - Advanced search across all entities with multiple operators
- **Clean Architecture** - Separated layers with proper dependency injection
- **Database Migrations** - Version-controlled schema with Flyway
- **API Documentation** - Auto-generated Swagger documentation

## Technology Stack

- **Java 17** with **Spring Boot 3.5.4**
- **PostgreSQL** database with **JPA/Hibernate**
- **Spring Security** with **JWT** authentication
- **Flyway** for database migrations
- **MapStruct** for object mapping
- **Swagger/OpenAPI** for documentation
- **Maven** for build management

## Quick Start

### Prerequisites

- Java 17+
- PostgreSQL 12+
- Maven 3.6+

### Setup

1. **Clone and navigate to project**

```bash
git clone <repository-url>
cd oms
```

2. **Create PostgreSQL database**

```sql
CREATE DATABASE oms;
CREATE USER postgres WITH PASSWORD '123456';
GRANT ALL PRIVILEGES ON DATABASE oms TO postgres;
```

3. **Configure database connection** (optional - uses defaults)

```bash
export DB_HOST=localhost
export DB_NAME=oms
export DB_USERNAME=postgres
export DB_PASSWORD=123456
```

4. **Build and run**

```bash
mvn clean compile
mvn spring-boot:run
```

The application starts at `http://localhost:8080/api`

**API Documentation**: http://localhost:8080/api/swagger-ui.html

## Default Login

```
Username: admin
Password: admin123
Role: ADMIN
```

## How to Test the API

### 1. Get Authentication Token

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'
```

### 2. Create a Customer

```bash
curl -X POST http://localhost:8080/api/v1/customers \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john.doe",
    "email": "john@example.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe",
    "mobile": "+1234567890",
    "addresses": [{
      "addressType": "HOME",
      "streetAddress": "123 Main St",
      "city": "New York",
      "state": "NY",
      "postalCode": "10001",
      "country": "USA",
      "isDefault": true
    }]
  }'
```

### 3. Create a Product

```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "productName": "Laptop",
    "description": "Gaming laptop",
    "price": 999.99,
    "stockQuantity": 10,
    "category": "Electronics"
  }'
```

### 4. Create and Approve an Order

```bash
# Create order (PENDING status)
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "orderItems": [{"productId": 1, "quantity": 1}]
  }'

# Approve order (generates invoice automatically)
curl -X POST http://localhost:8080/api/v1/orders/1/approve \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 5. Advanced Filtering Example

```bash
# Find electronics products over $500
curl -G "http://localhost:8080/api/v1/products" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d "filters[0].field=category" \
  -d "filters[0].operator=EQUALS" \
  -d "filters[0].value=Electronics" \
  -d "filters[1].field=price" \
  -d "filters[1].operator=GREATER_THAN" \
  -d "filters[1].value=500"
```

## API Endpoints

### Authentication

- `POST /api/v1/auth/login` - Login and get JWT token

### Customers

- `GET /api/v1/customers` - List customers (with filtering)
- `GET /api/v1/customers/{id}` - Get customer details
- `POST /api/v1/customers` - Create customer with addresses
- `DELETE /api/v1/customers/{id}` - Delete customer

### Products

- `GET /api/v1/products` - List products (with filtering)
- `GET /api/v1/products/{id}` - Get product details
- `POST /api/v1/products` - Create product
- `DELETE /api/v1/products/{id}` - Delete product

### Orders

- `GET /api/v1/orders` - List orders (with filtering)
- `GET /api/v1/orders/{id}` - Get order details
- `POST /api/v1/orders` - Create order
- `POST /api/v1/orders/{id}/approve` - Approve order

### Invoices

- `GET /api/v1/invoices` - List invoices (with filtering)
- `GET /api/v1/invoices/{id}` - Get invoice details

### Filtering

All list endpoints support filtering with operators: `EQUALS`, `NOT_EQUALS`, `LIKE`, `GREATER_THAN`, `LESS_THAN`, `IN`, etc.

## Architecture Highlights

### Order Workflow

```
PENDING → CONFIRMED → SHIPPED → DELIVERED
   ↓
CANCELLED (can happen from PENDING or CONFIRMED)
```

### Event-Driven Design

When an order is approved:

1. Order status changes to CONFIRMED
2. System publishes OrderApprovedEvent
3. Event handler automatically creates invoice
4. Invoice gets status GENERATED

### Error Handling

The system provides user-friendly error messages instead of technical database errors:

```json
{
  "status": 409,
  "error": "Conflict",
  "message": "Cannot delete customer - they have existing orders",
  "suggestion": "Please delete or reassign orders before deleting customer",
  "errorCode": "CUSTOMER_HAS_ORDERS"
}
```

## System Diagrams

The project includes detailed system diagrams:

- **Entity Relationship Diagram**: [OMS-ERD.pdf](./OMS-ERD.pdf)
- **Order Creation Flow**: [OMS-ORDER-CREATION-SEQUANCE.pdf](./OMS-ORDER-CREATION-SEQUANCE.pdf)
- **Order Approval Flow**: [OM-ORDER-APPROVEMENT-SEQUANCE.pdf](./OM-ORDER-APPROVEMENT-SEQUANCE.pdf)

## Project Structure

```
src/main/java/com/ejada/oms/
├── auth/          # Authentication & JWT handling
├── core/          # Shared utilities, exceptions, configurations
├── customer/      # Customer and Address management
├── product/       # Product catalog management
├── order/         # Order processing and events
└── invoice/       # Invoice generation and management
```

## Database

The application uses PostgreSQL with Flyway migrations:

- `V1__Create_initial_schema.sql` - Database schema
- `V2__Insert_seed_data.sql` - Sample data and admin user
- `V3__Add_missing_entity_columns.sql` - Additional columns

## What Makes This Project Special

1. **Real Business Logic** - Orders require approval before invoice generation
2. **Smart Constraints** - Prevents deleting customers/products that have orders
3. **Advanced Filtering** - Complex queries with multiple conditions
4. **Event Architecture** - Loose coupling between order and invoice systems
5. **Production Ready** - Proper error handling, logging, and configuration

---

**Built by Ali Hussein** - This project demonstrates modern Spring Boot development practices with clean architecture and real-world business requirements.
