# Test Authentication Endpoints

Write-Host "Testing AlgoCoach Authentication..."

# Test Registration
Write-Host "`n1. Testing User Registration..."
$registerBody = @{
    username = "testuser"
    email = "test@example.com"
    password = "password123"
    firstName = "Test"
    lastName = "User"
} | ConvertTo-Json

try {
    $registerResponse = Invoke-RestMethod -Uri "http://localhost:8081/auth/register" -Method POST -ContentType "application/json" -Body $registerBody
    Write-Host "Registration successful!"
    Write-Host "Response: $($registerResponse | ConvertTo-Json -Depth 3)"
    $token = $registerResponse.token
} catch {
    Write-Host "Registration failed: $($_.Exception.Message)"
    Write-Host "Response: $($_.Exception.Response)"
}

# Test Login
Write-Host "`n2. Testing User Login..."
$loginBody = @{
    username = "testuser"
    password = "password123"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8081/auth/login" -Method POST -ContentType "application/json" -Body $loginBody
    Write-Host "Login successful!"
    Write-Host "Response: $($loginResponse | ConvertTo-Json -Depth 3)"
    $token = $loginResponse.token
} catch {
    Write-Host "Login failed: $($_.Exception.Message)"
}

# Test Protected Endpoint
if ($token) {
    Write-Host "`n3. Testing Protected Endpoint (Problems)..."
    $headers = @{
        "Authorization" = "Bearer $token"
    }
    
    try {
        $problemsResponse = Invoke-RestMethod -Uri "http://localhost:8081/problems" -Method GET -Headers $headers
        Write-Host "Protected endpoint access successful!"
        Write-Host "Found $($problemsResponse.Count) problems"
    } catch {
        Write-Host "Protected endpoint access failed: $($_.Exception.Message)"
    }
}

Write-Host "`nTest completed!"
