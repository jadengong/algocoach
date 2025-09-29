# AlgoCoach MVP Test Script

## 🚀 Quick Start Guide

### 1. Start the Application
```bash
mvn spring-boot:run
```
The application will start on `http://localhost:8081`

### 2. View API Documentation
Visit: `http://localhost:8081/api-docs/mvp`

### 3. Test the MVP Workflow

#### Step 1: Register a User
```bash
curl -X POST http://localhost:8081/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com", 
    "password": "password123",
    "firstName": "Test",
    "lastName": "User"
  }'
```

#### Step 2: Login and Get JWT Token
```bash
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

**Save the JWT token from the response!**

#### Step 3: Get Personalized Recommendations
```bash
curl -X GET "http://localhost:8081/mvp/recommendations?limit=3" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### Step 4: Start Working on a Problem
```bash
curl -X POST http://localhost:8081/mvp/problems/1/start \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### Step 5: Record an Attempt
```bash
curl -X POST http://localhost:8081/mvp/problems/1/attempt \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### Step 6: Use a Hint
```bash
curl -X POST http://localhost:8081/mvp/problems/1/hint \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### Step 7: Mark Problem as Solved
```bash
# Solve with automatic confidence calculation
curl -X POST "http://localhost:8081/mvp/problems/1/solve?timeSpentMinutes=15" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Solve with explicit confidence score (0.0 to 1.0)
curl -X POST "http://localhost:8081/mvp/problems/1/solve?timeSpentMinutes=15&confidenceScore=0.8" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### Step 8: Bookmark a Problem
```bash
# Bookmark a problem
curl -X POST http://localhost:8081/mvp/problems/1/bookmark \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Remove bookmark (same endpoint toggles)
curl -X POST http://localhost:8081/mvp/problems/1/bookmark \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### Step 9: Check Your Progress
```bash
# Get dashboard
curl -X GET http://localhost:8081/mvp/dashboard \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Get stats
curl -X GET http://localhost:8081/mvp/stats \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Get solved problems
curl -X GET http://localhost:8081/mvp/progress/solved \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Get bookmarked problems
curl -X GET http://localhost:8081/mvp/bookmarks \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 4. Explore More Features

#### Get Problems by Topic
```bash
curl -X GET "http://localhost:8081/mvp/problems/topic/Array?limit=5" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### Get Random Problems
```bash
curl -X GET "http://localhost:8081/mvp/problems/random?difficulty=EASY&limit=3" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### Search Problems
```bash
curl -X GET "http://localhost:8081/problems/search?difficulty=EASY&topic=Array" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### Get System Statistics
```bash
# Get difficulty breakdown with percentages
curl -X GET http://localhost:8081/stats/difficulty-breakdown

# Get topic breakdown with percentages  
curl -X GET http://localhost:8081/stats/topic-breakdown

# Get overall stats
curl -X GET http://localhost:8081/stats/overview
```

#### Discover Problems with Advanced Filtering
```bash
# Get available filters
curl -X GET http://localhost:8081/problems/filters

# Discover problems with pagination and sorting
curl -X GET "http://localhost:8081/problems/discover?difficulty=EASY&sortBy=acceptance&page=0&size=5" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Discover problems by topic with pagination
curl -X GET "http://localhost:8081/mvp/problems/discover?topic=Array&sortBy=title&page=0&size=3" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Get all unsolved problems sorted by difficulty
curl -X GET "http://localhost:8081/mvp/problems/discover?sortBy=difficulty&page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 🎯 MVP Features Demonstrated

✅ **User Authentication** - Register, login, JWT token validation
✅ **Problem Management** - Browse, search, filter problems by difficulty/topic
✅ **Advanced Problem Discovery** - Pagination, sorting, and smart filtering
✅ **Personalized Recommendations** - AI-like recommendations based on user progress
✅ **Progress Tracking** - Track attempts, solve status, time spent, hints used
✅ **User Statistics** - Completion rates, progress by difficulty/topic
✅ **Problem Solving Workflow** - Start → Attempt → Hint → Solve/Give Up
✅ **Dashboard** - Personalized overview of recommendations and progress
✅ **Confidence Scoring** - Smart difficulty progression based on solving confidence
✅ **Problem Bookmarks** - Save problems for later practice
✅ **System Statistics** - Difficulty and topic breakdown with percentages

## 🗄️ Database Access

Visit `http://localhost:8081/h2-console` to view the database:
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: `password`

## 📊 Sample Data

The application comes pre-loaded with:
- 8 algorithm problems (Easy, Medium, Hard)
- Detailed descriptions, examples, and constraints
- Topics: Array, Stack, Dynamic Programming, Hash Table, String, Linked List

## 🔍 Advanced Problem Discovery (NEW!)

The enhanced AlgoCoach now includes **smart problem discovery** with advanced filtering and pagination:

### New Features
- **Pagination**: Browse problems page by page (default 10 per page)
- **Multiple Sorting Options**: Sort by difficulty, acceptance rate, or title
- **Smart Filtering**: Filter by difficulty and topic with real-time results
- **Personalized Discovery**: MVP endpoint excludes already solved problems
- **Filter Discovery**: Get available topics, difficulties, and sort options

### API Endpoints
- `GET /problems/filters` - Get available filter options
- `GET /problems/discover` - Public problem discovery with pagination
- `GET /mvp/problems/discover` - Personalized discovery (excludes solved problems)

### Example Response
```json
{
  "problems": [...],
  "totalCount": 8,
  "page": 0,
  "size": 5,
  "totalPages": 2,
  "hasNext": true,
  "hasPrevious": false,
  "filters": {
    "difficulty": "EASY",
    "topic": "all",
    "sortBy": "acceptance"
  }
}
```

## 🧠 Confidence Scoring System (NEW!)

The enhanced AlgoCoach now includes a **smart confidence scoring system** that makes problem recommendations more intelligent:

### How It Works
- **Automatic Calculation**: Confidence score (0.0-1.0) calculated based on:
  - Number of attempts (fewer = higher confidence)
  - Hints used (fewer = higher confidence) 
  - Time spent (faster = higher confidence)
- **Manual Input**: Users can also provide their own confidence rating
- **Smart Recommendations**: System uses confidence scores to suggest appropriate difficulty levels

### Benefits
- **Adaptive Learning**: Recommendations improve as users solve more problems
- **Confidence Tracking**: Users can see their confidence levels by difficulty/topic
- **Better Progression**: Prevents users from jumping to problems too hard/easy for their skill level

### API Response Example
```json
{
  "message": "Problem solved!",
  "progress": {
    "confidenceScore": 0.75,
    "attemptsCount": 2,
    "hintsUsed": 1,
    "timeSpentMinutes": 25
  },
  "confidenceScore": 0.75
}
```

## 🔖 Problem Bookmarks (NEW!)

The enhanced AlgoCoach now includes **problem bookmarking** functionality:

### How It Works
- **Toggle Bookmarks**: Click to bookmark/unbookmark any problem
- **Persistent Storage**: Bookmarks are saved per user
- **Easy Access**: View all bookmarked problems in one place
- **Smart Integration**: Works with existing progress tracking

### API Endpoints
- `POST /mvp/problems/{id}/bookmark` - Toggle bookmark status
- `GET /mvp/bookmarks` - Get all bookmarked problems

### Example Usage
```bash
# Bookmark a problem
curl -X POST http://localhost:8081/mvp/problems/1/bookmark \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Get bookmarked problems
curl -X GET http://localhost:8081/mvp/bookmarks \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Benefits
- **Personal Lists**: Create custom problem lists
- **Study Planning**: Mark problems for later review
- **Progress Tracking**: Keep track of interesting problems
- **User Experience**: Simple one-click bookmarking

## 📊 System Statistics (NEW!)

The enhanced AlgoCoach now includes **detailed system statistics** for better insights:

### New Statistics Endpoints
- **Difficulty Breakdown**: Shows problem distribution by difficulty with percentages
- **Topic Breakdown**: Shows problem distribution by topic with percentages  
- **Average Acceptance Rates**: Shows average acceptance rates by difficulty level

### API Endpoints
- `GET /stats/difficulty-breakdown` - Get difficulty statistics with percentages
- `GET /stats/topic-breakdown` - Get topic statistics with percentages
- `GET /stats/overview` - Get overall system statistics

### Example Response
```json
{
  "total": 8,
  "breakdown": {
    "easy": {
      "count": 5,
      "percentage": 63
    },
    "medium": {
      "count": 2, 
      "percentage": 25
    },
    "hard": {
      "count": 1,
      "percentage": 12
    }
  },
  "averageAcceptanceRates": {
    "easy": 47.08,
    "medium": 35.0,
    "hard": 35.2
  }
}
```

### Benefits
- **Data Insights**: Understand problem distribution across the platform
- **Planning**: Help users plan their learning path
- **Analytics**: Provide valuable metrics for platform improvement
- **Transparency**: Show users what types of problems are available

## 🔧 Next Steps for Full Product

1. **Frontend Interface** - React/Vue.js web app
2. **Code Execution** - Online code editor with test cases
3. **Advanced AI** - ML-based problem recommendations
4. **Mock Interviews** - Timed practice sessions
5. **Social Features** - Leaderboards, user profiles
6. **Analytics** - Detailed performance insights

## 🐛 Troubleshooting

- **401 Unauthorized**: Make sure you're using the correct JWT token
- **404 Not Found**: Check that the application is running on port 8081
- **500 Internal Server Error**: Check the application logs for details
