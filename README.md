# AlgoCoach

Platform for technical interview preparation with personalized recommendations

## Features

### Core Features
- User Authentication - JWT-based secure authentication
- Problem Management - Browse, search, and filter coding problems
- Personalized Recommendations - Smart recommendations based on user progress and confidence scores
- Progress Tracking - Track attempts, solve status, time spent, and hints used
- Smart Difficulty Progression - Adaptive recommendations based on confidence scores
- Problem Bookmarks - Save problems for later practice
- Comprehensive Statistics - Progress insights by difficulty and topic

### Technical Features
- Rate Limiting - AOP-based rate limiting per endpoint
- Input Validation - Comprehensive validation with detailed error messages
- Error Handling - Centralized exception handling with consistent responses
- Security - Spring Security with JWT tokens
- Testing - Unit and integration tests for core components

## Architecture

### Technology Stack
- Backend: Spring Boot 3.2.0, Java 17
- Database: H2 (development), JPA/Hibernate
- Security: Spring Security + JWT
- Validation: Bean Validation (Jakarta)
- Testing: JUnit 5, MockMvc
- Build Tool: Maven

### Project Structure
```
src/main/java/com/algocoach/
├── controller/          # REST API endpoints
├── service/            # Business logic
├── repository/         # Data access layer
├── domain/             # Entity models
├── dto/               # Data transfer objects
├── exception/         # Custom exceptions
├── config/            # Configuration classes
├── security/          # Security configuration
├── aspect/            # AOP aspects (rate limiting)
├── annotation/        # Custom annotations
└── util/              # Utility classes
```

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+

### 1. Clone and Run
```bash
git clone <repository-url>
cd algocoach
mvn spring-boot:run
```

The application will start on `http://localhost:8081`

### 2. Database Access
Visit `http://localhost:8081/h2-console` to view the database:
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: `password`

### 3. API Documentation
Visit `http://localhost:8081/api-docs/mvp` for detailed API documentation (dev profile only)

## API Usage Examples

### Authentication Flow

#### 1. Register a User
```bash
curl -X POST http://localhost:8081/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com", 
    "password": "Password123",
    "firstName": "Test",
    "lastName": "User"
  }'
```

#### 2. Login and Get JWT Token
```bash
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "Password123"
  }'
```

### Problem Management

#### Get Personalized Recommendations
```bash
curl -X GET "http://localhost:8081/mvp/recommendations?limit=5" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### Start Working on a Problem
```bash
curl -X POST http://localhost:8081/mvp/problems/1/start \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### Mark Problem as Solved
```bash
curl -X POST "http://localhost:8081/mvp/problems/1/solve?timeSpentMinutes=15&confidenceScore=0.8" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### Get User Progress
```bash
curl -X GET http://localhost:8081/mvp/dashboard \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Testing

### Run Tests
```bash
mvn test
```

### Test Coverage
- Unit Tests: Core business logic and configuration
- Integration Tests: API endpoints and error handling
- Rate Limiting Tests: Verify rate limiting behavior

### Test Scripts
- `mvp-test-script.md` - Comprehensive API testing guide
- `error-handling-test-script.md` - Error handling validation tests

## Configuration

### Environment Profiles
- Development (`dev`): H2 database, detailed logging, dev-only endpoints
- Production (`prod`): Production database, minimal logging

### Rate Limiting
Current rate limits:
- Registration: 5 requests/minute
- Login: 10 requests/minute
- Recommendations: 100 requests/minute
- Problem actions: 20-30 requests/minute

### Security
- JWT token expiration: 24 hours
- Password requirements: 8-128 characters with uppercase, lowercase, and number
- CORS enabled for cross-origin requests

## Sample Data

The application comes pre-loaded with:
- 8 algorithm problems (Easy, Medium, Hard)
- Detailed descriptions, examples, and constraints
- Topics: Array, Stack, Dynamic Programming, Hash Table, String, Linked List

## Error Handling

All errors follow a consistent format:
```json
{
  "timestamp": "2024-01-01T12:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Detailed error message",
  "path": "/api/endpoint",
  "errorCode": "ERROR_CODE",
  "validationErrors": [...]
}
```

### Error Codes
- `VALIDATION_ERROR`: Input validation failed
- `RESOURCE_NOT_FOUND`: Requested resource doesn't exist
- `BUSINESS_LOGIC_ERROR`: Business rule violation
- `AUTHENTICATION_ERROR`: Authentication failed
- `RATE_LIMIT_EXCEEDED`: Too many requests

## Development

### Adding New Features
1. Create domain entities in `domain/` package
2. Add repository interfaces in `repository/` package
3. Implement business logic in `service/` package
4. Create REST endpoints in `controller/` package
5. Add appropriate tests

### Code Style
- Follow Spring Boot conventions
- Use meaningful variable and method names
- Add JavaDoc for public APIs
- Include validation annotations on DTOs

## Future Enhancements

### Phase 1: Production Readiness
- [ ] Database migration to PostgreSQL/MySQL
- [ ] Environment-specific configurations
- [ ] OpenAPI/Swagger documentation
- [ ] Docker containerization

### Phase 2: Performance & Scalability
- [ ] Redis caching
- [ ] Database optimization
- [ ] Distributed rate limiting
- [ ] Pagination improvements

### Phase 3: Advanced Features
- [ ] Machine learning-based recommendations
- [ ] Real-time coding sessions
- [ ] File upload support
- [ ] Email notifications

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For questions or support, please open an issue in the repository.

---

Built with Spring Boot
