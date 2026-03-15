# AstraCart - Event-Driven Scalable Commerce Platform

## 🚀 Project Overview

AstraCart is a production-grade, distributed commerce platform built with microservices architecture. This project demonstrates modern software engineering practices including clean architecture, containerization, security, and scalability patterns.

### 🏗️ Phase 1 Features

✅ **Microservices Architecture**
- User Service (Authentication & Authorization)
- Product Service (Catalog & Inventory Management)
- Order Service (Order Processing & Management)
- API Gateway (Routing & Security)

✅ **Production-Grade Features**
- JWT-based authentication with refresh tokens
- Database-per-service pattern with PostgreSQL
- RESTful API design with proper validation
- Docker containerization
- Comprehensive error handling
- Audit trails and logging
- Optimistic locking for concurrency
- Soft delete patterns
- Pagination and sorting

✅ **Engineering Excellence**
- Clean architecture with separation of concerns
- Comprehensive test coverage (70%+)
- Database migrations with Flyway
- Performance optimization
- Security best practices
- Production-ready configuration

## 🛠️ Technology Stack

### Backend
- **Java 17** - Modern Java features
- **Spring Boot 3.2** - Application framework
- **Spring Security** - Authentication & authorization
- **Spring Data JPA** - Database abstraction
- **PostgreSQL** - Primary database
- **Flyway** - Database migrations
- **JUnit 5** - Testing framework
- **TestContainers** - Integration testing

### Infrastructure
- **Docker** - Containerization
- **Docker Compose** - Multi-container orchestration
- **Spring Cloud Gateway** - API Gateway

## 📋 Prerequisites

- Java 17+
- Maven 3.8+
- Docker & Docker Compose
- PostgreSQL (for local development)

## 🚀 Quick Start

### 1. Clone and Build
```bash
git clone <repository-url>
cd astracart
mvn clean install
```

### 2. Start Infrastructure
```bash
# Start all services with databases
docker-compose up -d

# View logs
docker-compose logs -f
```

### 3. Access Services

| Service | Port | Description |
|---------|------|-------------|
| API Gateway | 8080 | Main entry point |
| User Service | 8081 | Authentication |
| Product Service | 8082 | Product catalog |
| Order Service | 8083 | Order management |
| Users DB | 5432 | PostgreSQL |
| Products DB | 5433 | PostgreSQL |
| Orders DB | 5434 | PostgreSQL |

## 📚 API Documentation

### Authentication (via Gateway:8080)
```bash
# Register user
POST /api/auth/register
{
  "email": "user@example.com",
  "password": "SecurePass123!",
  "firstName": "John",
  "lastName": "Doe"
}

# Login
POST /api/auth/login
{
  "email": "user@example.com",
  "password": "SecurePass123!"
}
```

### Products (via Gateway:8080)
```bash
# Get all products (paginated)
GET /api/products?page=0&size=10&sort=name,asc

# Get product by ID
GET /api/products/1

# Search products
GET /api/products/search?search=laptop&page=0&size=10

# Create product (Admin only)
POST /api/products
Authorization: Bearer <token>
{
  "name": "Laptop Pro",
  "description": "High-performance laptop",
  "sku": "LAPTOP-001",
  "price": 1299.99,
  "categoryId": 1,
  "stockQuantity": 50
}
```

### Orders (via Gateway:8080)
```bash
# Create order
POST /api/orders
Authorization: Bearer <token>
{
  "userId": 1,
  "items": [
    {
      "productId": 1,
      "quantity": 2
    }
  ],
  "shippingAddress": {
    "street": "123 Main St",
    "city": "New York",
    "zipCode": "10001"
  }
}

# Get user orders
GET /api/orders/user/1
Authorization: Bearer <token>
```

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   API Gateway   │    │  User Service   │    │ Product Service │
│   (Port 8080)   │    │   (Port 8081)   │    │   (Port 8082)   │
│                 │    │                 │    │                 │
│ - Routing       │◄──►│ - Auth & Users  │◄──►│ - Product Mgmt  │
│ - Security      │    │ - JWT Tokens    │    │ - Inventory     │
│ - CORS          │    │ - User Profiles │    │ - Categories    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │
                    ┌─────────────────┐
                    │ Order Service   │
                    │   (Port 8083)   │
                    │                 │
                    │ - Order Mgmt    │
                    │ - Order Status  │
                    │ - REST Calls    │
                    └─────────────────┘
```

## 🧪 Testing

### Run Tests
```bash
# Run all tests
mvn test

# Run tests for specific service
cd user-service && mvn test

# Run integration tests
mvn test -P integration-test
```

### Test Coverage
- Unit Tests: 70%+ coverage
- Integration Tests: Database and API testing
- TestContainers: Real database testing

## 📊 Database Schema

Each service has its own PostgreSQL database:

### User Service (astracart_users)
- `users` - User accounts and profiles
- `roles` - User roles (USER, ADMIN)
- `user_roles` - Many-to-many relationship

### Product Service (astracart_products)
- `products` - Product catalog with soft delete
- `categories` - Hierarchical categories
- `product_images` - Product images

### Order Service (astracart_orders)
- `orders` - Order information with status tracking
- `order_items` - Order line items
- `order_status_history` - Order status changes

## 🔧 Configuration

### Environment Variables
```bash
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/dbname
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=password

# JWT Configuration
JWT_SECRET=your-secret-key
JWT_ACCESS_TOKEN_EXPIRATION=3600
JWT_REFRESH_TOKEN_EXPIRATION=604800
```

### Docker Configuration
- **HikariCP:** Connection pooling (max 20 connections)
- **Resource Limits:** Memory and CPU limits
- **Health Checks:** Container health monitoring
- **Volume Persistence:** Database data persistence

## 🚀 Deployment

### Docker Compose
```bash
# Production deployment
docker-compose -f docker-compose.yml up -d

# Scale services
docker-compose up -d --scale user-service=2 --scale product-service=2
```

### Environment-Specific Configurations
- **Development:** Local databases, debug logging
- **Staging:** Containerized services, production-like config
- **Production:** External databases, optimized settings

## 📈 Performance

### Optimization Features
- **Database Indexing:** Optimized for query patterns
- **Connection Pooling:** HikariCP with tuned settings
- **Pagination:** Prevents large result sets
- **Lazy Loading:** JPA relationships on demand
- **Caching Ready:** Prepared for Redis integration

### Monitoring
- **Actuator Endpoints:** Health checks and metrics
- **Structured Logging:** JSON format for log aggregation
- **Performance Metrics:** JVM and application metrics

## 🔒 Security

### Authentication & Authorization
- **JWT Tokens:** Access (1h) and Refresh (7d) tokens
- **Role-Based Access:** @PreAuthorize annotations
- **Password Security:** BCrypt hashing
- **Input Validation:** Comprehensive validation rules

### Security Headers
- CORS configuration
- Content Security Policy ready
- HTTPS enforcement ready

## 🛣️ Roadmap

### Phase 2 (Next)
- [ ] Apache Kafka for event-driven architecture
- [ ] Service Registry (Eureka)
- [ ] Circuit Breaker (Resilience4j)
- [ ] Distributed Tracing (Zipkin)
- [ ] Redis for caching

### Phase 3
- [ ] Rate Limiting
- [ ] Advanced Monitoring
- [ ] CI/CD Pipeline
- [ ] Kubernetes Deployment

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📞 Support

For questions and support:
- Create an issue in the repository
- Check the documentation in `/docs`
- Review the API examples above

---

**Built with ❤️ using modern Java and Spring ecosystem**
