# Simple test script
$body = '{"username":"testuser","email":"test@example.com","password":"password123","firstName":"Test","lastName":"User"}'

Write-Host "Testing registration..."
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8081/auth/register" -Method POST -ContentType "application/json" -Body $body
    Write-Host "Registration successful!"
    Write-Host "Response: $($response | ConvertTo-Json)"
} catch {
    Write-Host "Registration failed: $($_.Exception.Message)"
    Write-Host "Status Code: $($_.Exception.Response.StatusCode)"
}
