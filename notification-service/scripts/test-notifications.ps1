Write-Host "🔔 Testing Notification Service" -ForegroundColor Green
Write-Host "===============================" -ForegroundColor Yellow

# Send test requests
Write-Host "🚀 Sending test tracking requests..." -ForegroundColor Cyan

# Test 1: New shipment
Invoke-RestMethod -Uri "http://localhost:8080/api/v1/trackings" -Method Post -Body '{"trackingId": "NOTIFY_TEST_1"}' -ContentType "application/json"

Start-Sleep -Seconds 3

# Test 2: Another new shipment
Invoke-RestMethod -Uri "http://localhost:8080/api/v1/trackings" -Method Post -Body '{"trackingId": "NOTIFY_TEST_2"}' -ContentType "application/json"

Start-Sleep -Seconds 3

# Test 3: Update existing shipment
Invoke-RestMethod -Uri "http://localhost:8080/api/v1/trackings" -Method Post -Body '{"trackingId": "NOTIFY_TEST_1"}' -ContentType "application/json"

Write-Host "⏳ Waiting for notifications to process..." -ForegroundColor Yellow
Start-Sleep -Seconds 5

Write-Host "✅ Notification test completed!" -ForegroundColor Green
Write-Host "📝 Check logs: docker-compose logs notification-service" -ForegroundColor Cyan
Write-Host "📊 Check metrics: http://localhost:8081/actuator/prometheus" -ForegroundColor Cyan