#!/bin/bash
# test-fault-tolerance.sh
# Tests Resilience4j Circuit Breaker and Fallback logic in Docker

GATEWAY_URL="http://localhost:8080/api"
echo "Testing Fault Tolerance (Resilience4j) between Order & Catalog Services (Docker)"
echo "------------------------------------------------------------------------"

# 1. Register and get token
USER_EMAIL="fault_test$(date +%s)@example.com"
curl -s -X POST "$GATEWAY_URL/auth/register" -H "Content-Type: application/json" -d "{\"email\":\"$USER_EMAIL\",\"password\":\"password\",\"firstName\":\"John\",\"lastName\":\"Doe\"}" > /dev/null
TOKEN=$(curl -s -X POST "$GATEWAY_URL/auth/login" -H "Content-Type: application/json" -d "{\"email\":\"$USER_EMAIL\",\"password\":\"password\"}" | grep -o "\"token\":\"[^\"]*\"" | cut -d':' -f2 | tr -d '"')

if [ -z "$TOKEN" ]; then
    echo "Помилка авторизації. Переконайтеся, що всі сервіси запущені (docker compose up -d)"
    exit 1
fi

# 2. Stop Catalog Service Container
echo "[$(date +'%T')] Зупинка контейнера catalog-service..."
sudo docker compose stop catalog-service
sleep 5 # Чекаємо, поки зупиниться

# 3. Test Order Creation while Catalog is down
echo "[$(date +'%T')] Спроба створити замовлення (очікуємо Fallback-відповідь, оскільки Catalog вимкнений)..."
RESPONSE=$(curl -s -X POST "$GATEWAY_URL/orders" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{\"customerId\":1,\"items\":[{\"productId\":999,\"quantity\":1}]}")

echo -e "\n--- Відповідь від Order Service ---"
echo $RESPONSE | grep -o "\"message\":\"[^\"]*\"" || echo $RESPONSE
echo "-----------------------------------"

# 4. Restart Catalog Service Container
echo "[$(date +'%T')] Відновлення роботи контейнера catalog-service..."
sudo docker compose start catalog-service
echo "[$(date +'%T')] Чекаємо 15 секунд на підняття Spring Boot всередині контейнера..."
sleep 15
echo "[$(date +'%T')] Тестування Circuit Breaker завершено."
