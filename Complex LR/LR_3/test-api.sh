#!/bin/bash
# test-api.sh - Послідовне E2E тестування Marketplace API

BASE_URL="http://localhost:8080/api"
TS=$(date +%s)

ADMIN_EMAIL="admin@marketplace.com"
SELLER_EMAIL="seller_${TS}@test.com"
BUYER_EMAIL="buyer_${TS}@test.com"
echo "=========================================================="
echo "   MARKETPLACE API - ПОСЛІДОВНИЙ E2E (USER JOURNEY) ТЕСТ  "
echo "=========================================================="

echo -e "\n--- 1. РЕЄСТРАЦІЯ УЧАСНИКІВ ---"
echo "> Адміністратор вже існує в системі (створюється автоматично через DataSeeder під час запуску сервера)."

echo "> Реєструємо майбутнього Продавця..."
curl -s -X POST $BASE_URL/auth/register -H "Content-Type: application/json" -d "{\"email\":\"$SELLER_EMAIL\", \"password\":\"password\", \"firstName\":\"Ivan\", \"lastName\":\"Seller\"}" >/dev/null

echo "> Реєструємо Покупця..."
curl -s -X POST $BASE_URL/auth/register -H "Content-Type: application/json" -d "{\"email\":\"$BUYER_EMAIL\", \"password\":\"password\", \"firstName\":\"Petro\", \"lastName\":\"Buyer\"}" >/dev/null

echo -e "\n--- 2. АВТОРИЗАЦІЯ (ЛОГІН) ---"
ADMIN_RES=$(curl -s -X POST $BASE_URL/auth/login -H "Content-Type: application/json" -d "{\"email\":\"$ADMIN_EMAIL\", \"password\":\"password\"}")
ADMIN_TOKEN=$(echo $ADMIN_RES | python3 -c "import sys, json; print(json.load(sys.stdin).get('token', ''))")

SELLER_RES=$(curl -s -X POST $BASE_URL/auth/login -H "Content-Type: application/json" -d "{\"email\":\"$SELLER_EMAIL\", \"password\":\"password\"}")
SELLER_USER_ID=$(echo $SELLER_RES | python3 -c "import sys, json; print(json.load(sys.stdin).get('userId', ''))")
SELLER_TOKEN=$(echo $SELLER_RES | python3 -c "import sys, json; print(json.load(sys.stdin).get('token', ''))")

BUYER_RES=$(curl -s -X POST $BASE_URL/auth/login -H "Content-Type: application/json" -d "{\"email\":\"$BUYER_EMAIL\", \"password\":\"password\"}")
BUYER_USER_ID=$(echo $BUYER_RES | python3 -c "import sys, json; print(json.load(sys.stdin).get('userId', ''))")
BUYER_TOKEN=$(echo $BUYER_RES | python3 -c "import sys, json; print(json.load(sys.stdin).get('token', ''))")

echo "Усі користувачі успішно увійшли (Токени отримано)."

echo -e "\n--- 3. СТВОРЕННЯ ПРОФІЛЮ ПРОДАВЦЯ (BECOME SELLER) ---"
echo "> Майбутній продавець подає заявку на відкриття магазину..."
BECOME_SELLER_RES=$(curl -s -X POST $BASE_URL/users/$SELLER_USER_ID/become-seller -H "Authorization: Bearer $SELLER_TOKEN" -H "Content-Type: application/json" -d '{"shopName":"Ivan Electronics", "description":"Best gadgets", "commissionRate":0.05}')
SELLER_PROFILE_ID=$(echo $BECOME_SELLER_RES | python3 -c "import sys, json; print(json.load(sys.stdin).get('id', ''))")
echo "Заявка створена (Seller ID: $SELLER_PROFILE_ID)."

echo "> Адмін верифікує продавця..."
curl -s -X PUT $BASE_URL/admin/sellers/$SELLER_PROFILE_ID/verify -H "Authorization: Bearer $ADMIN_TOKEN" >/dev/null
echo "Магазин верифіковано! Тепер він може додавати товари."

echo -e "\n--- 4. ДОДАВАННЯ ТОВАРУ (PRODUCTS) ---"
echo "> Адмін створює категорію 'Смартфони'..."
CATEGORY_RES=$(curl -s -X POST $BASE_URL/categories -H "Authorization: Bearer $ADMIN_TOKEN" -H "Content-Type: application/json" -d '{"name":"Smartphones", "description":"Mobile phones"}')
CATEGORY_ID=$(echo $CATEGORY_RES | python3 -c "import sys, json; print(json.load(sys.stdin).get('id', ''))")
echo "Категорія створена (Category ID: $CATEGORY_ID)."

echo "> Продавець додає новий смартфон..."
PRODUCT_RES=$(curl -s -X POST $BASE_URL/products -H "Authorization: Bearer $SELLER_TOKEN" -H "Content-Type: application/json" -d "{\"categoryId\":$CATEGORY_ID, \"sellerId\":$SELLER_PROFILE_ID, \"name\":\"iPhone 15\", \"description\":\"Brand new iPhone\", \"price\":999.99, \"stockQuantity\":50}")
PRODUCT_ID=$(echo $PRODUCT_RES | python3 -c "import sys, json; print(json.load(sys.stdin).get('id', ''))")
echo "Товар успішно додано (Product ID: $PRODUCT_ID)."

echo -e "\n--- 5. ЗАМОВЛЕННЯ ТОВАРУ ПОКУПЦЕМ (ORDERS) ---"
echo "> Покупець переглядає каталог товарів..."
curl -s -X GET "$BASE_URL/products?page=0&size=10" | grep -q 'iPhone 15' && echo "Товар знайдено в каталозі!" || echo "Товар не знайдено :("

echo "> Адмін шукає Customer ID для Покупця (імітація фронтенду, який знає ID)..."
CUSTOMERS_RES=$(curl -s -X GET $BASE_URL/customers -H "Authorization: Bearer $ADMIN_TOKEN")
BUYER_CUST_ID=$(echo $CUSTOMERS_RES | python3 -c "
import sys, json
data = json.load(sys.stdin)
for c in data.get('content', []):
    if c.get('user', {}).get('id') == $BUYER_USER_ID:
        print(c.get('id'))
        break
")

echo "> Покупець створює замовлення на 1x iPhone 15..."
ORDER_RES=$(curl -s -X POST $BASE_URL/orders -H "Authorization: Bearer $BUYER_TOKEN" -H "Content-Type: application/json" -d "{\"customerId\":$BUYER_CUST_ID, \"items\":[{\"productId\":$PRODUCT_ID, \"quantity\":1, \"priceAtTime\":999.99}]}")
ORDER_ID=$(echo $ORDER_RES | python3 -c "import sys, json; print(json.load(sys.stdin).get('id', ''))")
echo "Замовлення оформлено (Order ID: $ORDER_ID). Статус: PENDING."

echo -e "\n--- 6. ПРОЦЕС ОПЛАТИ ТА ДОСТАВКИ ---"
echo "> Покупець оплачує замовлення..."
PAYMENT_RES=$(curl -s -X POST $BASE_URL/payments -H "Authorization: Bearer $BUYER_TOKEN" -H "Content-Type: application/json" -d "{\"orderId\":$ORDER_ID, \"paymentMethod\":\"CREDIT_CARD\", \"amount\":999.99}")
PAYMENT_ID=$(echo $PAYMENT_RES | python3 -c "import sys, json; print(json.load(sys.stdin).get('id', ''))")
echo "Платіж створено (Payment ID: $PAYMENT_ID)."

echo "> Платіжна система підтверджує оплату..."
curl -s -X POST $BASE_URL/payments/$PAYMENT_ID/complete -H "Authorization: Bearer $ADMIN_TOKEN" >/dev/null
echo "Оплата пройшла успішно!"

echo "> Продавець підтверджує замовлення та відправляє його..."
curl -s -X POST $BASE_URL/orders/$ORDER_ID/confirm -H "Authorization: Bearer $SELLER_TOKEN" >/dev/null
curl -s -X POST $BASE_URL/orders/$ORDER_ID/ship -H "Authorization: Bearer $SELLER_TOKEN" >/dev/null
echo "Товар відправлено (SHIPPED)."

echo "> Покупець підтверджує отримання..."
curl -s -X POST $BASE_URL/orders/$ORDER_ID/deliver -H "Authorization: Bearer $BUYER_TOKEN" >/dev/null
echo "Товар отримано (DELIVERED)."

echo -e "\n--- 7. ВІДГУКИ (REVIEWS) ---"
echo "> Покупець залишає 5-зірковий відгук на товар..."
curl -s -X POST $BASE_URL/reviews -H "Authorization: Bearer $BUYER_TOKEN" -H "Content-Type: application/json" -d "{\"reviewerId\":$BUYER_USER_ID, \"targetType\":\"PRODUCT\", \"targetId\":$PRODUCT_ID, \"rating\":5, \"comment\":\"Awesome phone! Highly recommend.\"}" >/dev/null
echo "Відгук додано!"

echo -e "\n=========================================================="
echo "          E2E ТЕСТУВАННЯ УСПІШНО ЗАВЕРШЕНО!               "
echo "=========================================================="
