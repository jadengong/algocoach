# AlgoCoach Error Handling & Validation Test Script

## 🚀 Quick Start

### 1. Start the Application
```bash
mvn spring-boot:run
```
The application will start on `http://localhost:8081`

### 2. Test Error Handling Scenarios

## 📋 **Validation Error Tests**

### Test 1: Invalid Registration Data
```bash
curl -X POST http://localhost:8081/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "ab",
    "email": "invalid-email",
    "password": "123",
    "firstName": "",
    "lastName": ""
  }'
```

**Expected Response:**
```json
{
  "timestamp": "2024-01-01T12:00:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Request validation failed",
  "path": "/auth/register",
  "errorCode": "VALIDATION_ERROR",
  "validationErrors": [
    {
      "field": "username",
      "rejectedValue": "ab",
      "message": "Username must be between 3 and 20 characters"
    },
    {
      "field": "email",
      "rejectedValue": "invalid-email",
      "message": "Email should be valid"
    },
    {
      "field": "password",
      "rejectedValue": "123",
      "message": "Password must be at least 6 characters"
    }
  ]
}
```

### Test 2: Invalid Login Data
```bash
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "",
    "password": ""
  }'
```

### Test 3: Invalid Parameter Types
```bash
curl -X GET "http://localhost:8081/error-test/param-validation?value=abc&name=test"
```

## 🔒 **Authentication Error Tests**

### Test 4: Invalid Token
```bash
curl -X POST http://localhost:8081/auth/validate \
  -H "Authorization: Bearer invalid-token"
```

### Test 5: Missing Token
```bash
curl -X GET http://localhost:8081/mvp/recommendations
```

## 📊 **Business Logic Error Tests**

### Test 6: Duplicate Registration
```bash
# First registration
curl -X POST http://localhost:8081/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "firstName": "Test",
    "lastName": "User"
  }'

# Try to register again with same username
curl -X POST http://localhost:8081/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test2@example.com",
    "password": "password123",
    "firstName": "Test",
    "lastName": "User"
  }'
```

### Test 7: Invalid Problem ID
```bash
curl -X GET http://localhost:8081/error-test/not-found/999
```

## ⚡ **Rate Limiting Tests**

### Test 8: Rate Limit Exceeded
```bash
# Call the rate-limited endpoint multiple times quickly
for i in {1..5}; do
  curl -X GET http://localhost:8081/error-test/rate-limit
  echo "Request $i"
done
```

**Expected Response (after 2 requests):**
```json
{
  "timestamp": "2024-01-01T12:00:00",
  "status": 429,
  "error": "Too Many Requests",
  "message": "Rate limit exceeded. Try again later.",
  "path": "/error-test/rate-limit",
  "errorCode": "RATE_LIMIT_EXCEEDED"
}
```

## 🔍 **Resource Not Found Tests**

### Test 9: Non-existent Problem
```bash
curl -X GET http://localhost:8081/error-test/not-found/999
```

### Test 10: Invalid Endpoint
```bash
curl -X GET http://localhost:8081/non-existent-endpoint
```

## 🛠️ **Server Error Tests**

### Test 11: Internal Server Error
```bash
curl -X GET http://localhost:8081/error-test/server-error
```

## 📝 **Advanced Validation Tests**

### Test 12: Complex Validation
```bash
curl -X POST http://localhost:8081/error-test/validation \
  -H "Content-Type: application/json" \
  -d '{
    "name": "",
    "age": 150
  }'
```

### Test 13: Parameter Validation with Limits
```bash
curl -X GET "http://localhost:8081/mvp/recommendations?limit=50"
```

## 🎯 **MVP Endpoint Validation Tests**

### Test 14: Invalid Problem ID in MVP
```bash
# First, get a valid token
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'

# Then try to start a non-existent problem
curl -X POST http://localhost:8081/mvp/problems/999/start \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Test 15: Invalid Confidence Score
```bash
curl -X POST "http://localhost:8081/mvp/problems/1/solve?confidenceScore=2.0" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 📊 **Error Response Format**

All error responses follow this consistent format:

```json
{
  "timestamp": "2024-01-01T12:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Detailed error message",
  "path": "/api/endpoint",
  "errorCode": "ERROR_CODE",
  "validationErrors": [
    {
      "field": "fieldName",
      "rejectedValue": "invalidValue",
      "message": "Validation message"
    }
  ]
}
```

## 🔧 **Rate Limiting Configuration**

Current rate limits:
- **Registration**: 5 requests/minute
- **Login**: 10 requests/minute
- **Token Validation**: 100 requests/minute
- **Health Checks**: 200 requests/minute
- **Recommendations**: 100 requests/minute
- **Topic Searches**: 50 requests/minute
- **Problem Actions**: 20-30 requests/minute

## ✅ **Validation Rules**

### User Registration:
- Username: 3-20 characters, required
- Email: Valid email format, required
- Password: Minimum 6 characters, required
- First/Last Name: Required, not blank

### Problem Operations:
- Problem ID: Must be positive integer
- Time Spent: 0-1440 minutes (24 hours)
- Confidence Score: 0.0-1.0
- Limit Parameters: 1-50 for most endpoints

## 🚨 **Error Codes**

- `VALIDATION_ERROR`: Input validation failed
- `RESOURCE_NOT_FOUND`: Requested resource doesn't exist
- `BUSINESS_LOGIC_ERROR`: Business rule violation
- `AUTHENTICATION_ERROR`: Authentication failed
- `RATE_LIMIT_EXCEEDED`: Too many requests
- `TYPE_MISMATCH`: Invalid parameter type
- `MISSING_PARAMETER`: Required parameter missing
- `INTERNAL_ERROR`: Unexpected server error

## 🎉 **Success Indicators**

✅ **Proper Error Handling Implemented When:**
- All endpoints return consistent error format
- Validation errors show specific field issues
- Rate limiting prevents abuse
- Authentication errors are properly handled
- Resource not found errors are clear
- Business logic violations are caught
- Server errors don't expose sensitive information

## 🔄 **Next Steps**

1. **Test all scenarios** using the above commands
2. **Verify error responses** match expected format
3. **Check rate limiting** works correctly
4. **Validate input constraints** are enforced
5. **Ensure security** - no sensitive data in error messages
