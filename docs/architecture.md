# AstraCart Phase 1 Architecture Documentation

## Overview

AstraCart is a distributed event-driven commerce platform built with microservices architecture. Phase 1 focuses on establishing a solid foundation with clean architecture, proper database design, JWT security, inter-service communication, and containerization.

## Architecture Diagram

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

## Microservices

### 1. User Service (Port 8081)
**Responsibilities:**
- User authentication and authorization
- JWT token generation and validation
- User profile management
- Role-based access control

**Key Features:**
- BCrypt password hashing
- JWT access/refresh tokens
- Role-based security (USER, ADMIN)
- Input validation and error handling
- Comprehensive audit trails

**Database:** PostgreSQL (astracart_users)
**Tables:** users, roles, user_roles

### 2. Product Service (Port 8082)
**Responsibilities:**
- Product catalog management
- Inventory tracking
- Category management
- Product search and filtering

**Key Features:**
- Soft delete for products
- Stock level monitoring
- Price range filtering
- Full-text search
- Image management
- Performance optimized queries

**Database:** PostgreSQL (astracart_products)
**Tables:** products, categories, product_images

### 3. Order Service (Port 8083)
**Responsibilities:**
- Order creation and management
- Order status tracking
- Inter-service communication
- Order history maintenance

**Key Features:**
- Order state management
- Status history tracking
- JSONB for address storage
- Optimistic locking
- REST calls to other services

**Database:** PostgreSQL (astracart_orders)
**Tables:** orders, order_items, order_status_history

### 4. API Gateway (Port 8080)
**Responsibilities:**
- Request routing
- Cross-Origin Resource Sharing (CORS)
- Request/response logging
- Centralized security

**Key Features:**
- Spring Cloud Gateway
- Dynamic routing
- Global CORS configuration
- Request header enrichment

## Database Design

### Database-per-Service Pattern
Each microservice has its own PostgreSQL database to ensure:
- **Service Isolation:** Each service owns its data
- **Independent Scaling:** Databases can scale independently
- **Technology Flexibility:** Services can use different databases if needed
- **Failure Isolation:** Database issues don't cascade

### Key Design Principles
1. **Indexing Strategy:** Optimized for query patterns
2. **Audit Fields:** created_at, updated_at, version for all entities
3. **Soft Deletes:** Products use soft delete pattern
4. **JSONB Storage:** Flexible storage for addresses and complex data
5. **Optimistic Locking:** Version fields prevent concurrent modification conflicts

## Security Architecture

### JWT-Based Authentication
1. **User Authentication:** Login generates JWT tokens
2. **Token Structure:** Access token (1 hour) + Refresh token (7 days)
3. **Token Validation:** Each service validates JWT tokens
4. **Role-Based Access:** Method-level security with @PreAuthorize

### Security Flow
```
Client → API Gateway → Service (JWT Validation) → Resource
```

## Inter-Service Communication

### REST Communication
- **Synchronous Communication:** Order Service calls Product Service
- **WebClient:** Reactive HTTP client for service calls
- **Error Handling:** Circuit breaker pattern (Phase 2)
- **Service Discovery:** Static configuration (Phase 2: Eureka)

## Technology Stack

### Backend
- **Java 17:** Modern Java features
- **Spring Boot 3.2:** Latest Spring framework
- **Spring Security:** Authentication and authorization
- **Spring Data JPA:** Database abstraction
- **PostgreSQL:** Primary database
- **Flyway:** Database migrations

### Containerization
- **Docker:** Container runtime
- **Docker Compose:** Multi-container orchestration
- **Multi-stage builds:** Optimized image sizes

### Testing
- **JUnit 5:** Unit testing
- **TestContainers:** Integration testing with real databases
- **MockMvc:** Web layer testing
- **Spring Boot Test:** Spring context testing

## Performance Considerations

### Database Optimization
1. **Connection Pooling:** HikariCP with tuned settings
2. **Query Optimization:** Proper indexing strategies
3. **Batch Operations:** JDBC batch for bulk operations
4. **Lazy Loading:** JPA relationships loaded on demand

### Application Performance
1. **Caching:** Redis integration planned for Phase 3
2. **Pagination:** All list endpoints support pagination
3. **Async Processing:** WebFlux for non-blocking operations
4. **Resource Management:** Proper connection and thread pool sizing

## Scalability Strategy

### Horizontal Scaling
- **Stateless Services:** All services are stateless
- **Load Balancing:** Multiple instances behind load balancer
- **Database Scaling:** Read replicas and connection pooling
- **Container Orchestration:** Ready for Kubernetes deployment

### Monitoring & Observability
- **Actuator Endpoints:** Health checks and metrics
- **Logging:** Structured logging with correlation IDs
- **Performance Monitoring:** Metrics collection ready
- **Distributed Tracing:** Planned for Phase 2

## Development Workflow

### Local Development
```bash
# Start all services
docker-compose up -d

# Build individual service
cd user-service && mvn clean install

# Run tests
mvn test

# View logs
docker-compose logs -f user-service
```

### Database Migrations
```bash
# Run migrations
mvn flyway:migrate

# Validate migrations
mvn flyway:validate
```

## API Documentation

### Authentication Endpoints (Port 8080 → 8081)
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login

### Product Endpoints (Port 8080 → 8082)
- `GET /api/products` - List products with pagination
- `GET /api/products/{id}` - Get product by ID
- `POST /api/products` - Create product (Admin only)
- `PUT /api/products/{id}` - Update product (Admin only)
- `DELETE /api/products/{id}` - Delete product (Admin only)

### Order Endpoints (Port 8080 → 8083)
- `POST /api/orders` - Create order
- `GET /api/orders/{id}` - Get order by ID
- `GET /api/orders/user/{userId}` - Get user orders
- `PUT /api/orders/{id}/status` - Update order status

## Failure Scenarios & Handling

### Database Failures
- **Connection Timeouts:** Configured connection timeouts
- **Deadlocks:** Optimistic locking prevents deadlocks
- **Data Corruption:** Transaction rollback and validation

### Service Failures
- **Service Unavailable:** Circuit breaker pattern (Phase 2)
- **Network Issues:** Retry mechanisms with exponential backoff
- **Partial Failures:** Transaction management and compensation

### Security Failures
- **Invalid Tokens:** JWT validation with proper error responses
- **Unauthorized Access:** Role-based access control
- **Injection Attacks:** Input validation and parameterized queries

## Phase 2 Preparation

### Planned Enhancements
1. **Apache Kafka:** Event-driven architecture
2. **Service Registry:** Eureka for service discovery
3. **Circuit Breaker:** Resilience4j for fault tolerance
4. **Distributed Tracing:** Zipkin for request tracing
5. **Redis:** Caching and session management

### Migration Strategy
- **Backward Compatibility:** Maintain API compatibility
- **Gradual Rollout:** Feature flags for new functionality
- **Data Migration:** Careful database schema evolution
- **Performance Testing:** Load testing before deployment

## Production Readiness Checklist

### Security
- [x] JWT authentication
- [x] Role-based authorization
- [x] Input validation
- [x] SQL injection prevention
- [x] CORS configuration

### Performance
- [x] Database indexing
- [x] Connection pooling
- [x] Pagination support
- [x] Optimistic locking
- [x] Resource cleanup

### Reliability
- [x] Error handling
- [x] Transaction management
- [x] Health checks
- [x] Logging
- [x] Containerization

### Maintainability
- [x] Clean architecture
- [x] Comprehensive tests
- [x] Documentation
- [x] Database migrations
- [x] API versioning ready

## Conclusion

Phase 1 establishes a solid foundation for AstraCart with production-ready microservices. The architecture follows industry best practices for scalability, security, and maintainability. Each service is independently deployable, testable, and scalable, setting the stage for the event-driven enhancements planned for Phase 2.
