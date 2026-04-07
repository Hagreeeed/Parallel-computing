#!/bin/bash
# Marketplace REST API — Test Script
# Happy path + Error path scenarios

BASE="http://localhost:8080/api"
PASS=0
FAIL=0

check() {
  local desc="$1" expected="$2" actual="$3"
  if [ "$expected" = "$actual" ]; then
    echo "✅ $desc (HTTP $actual)"
    PASS=$((PASS+1))
  else
    echo "❌ $desc — expected $expected, got $actual"
    FAIL=$((FAIL+1))
  fi
}

show_error() {
  local desc="$1"
  echo "   ↳ $(cat /tmp/r.json | python3 -c 'import sys,json; d=json.load(sys.stdin); print(d.get("message",""))' 2>/dev/null)"
}

echo "========================================="
echo "       MARKETPLACE API TEST SUITE"
echo "========================================="
echo ""
echo "=== HAPPY PATH ==="
echo ""

# 1. Create CUSTOMER user
CODE=$(curl -s -o /tmp/r.json -w "%{http_code}" -X POST "$BASE/users" \
  -H "Content-Type: application/json" \
  -d '{"email":"customer@test.com","password":"pass123","role":"CUSTOMER"}')
check "1. POST /api/users (CUSTOMER)" "201" "$CODE"

# 2. Create SELLER user
CODE=$(curl -s -o /tmp/r.json -w "%{http_code}" -X POST "$BASE/users" \
  -H "Content-Type: application/json" \
  -d '{"email":"seller@test.com","password":"pass456","role":"SELLER"}')
check "2. POST /api/users (SELLER)" "201" "$CODE"

# 3. Create customer profile
CODE=$(curl -s -o /tmp/r.json -w "%{http_code}" -X POST "$BASE/customers" \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"firstName":"Іван","lastName":"Петренко","phone":"+380501234567","shippingAddress":"Київ, Україна"}')
check "3. POST /api/customers" "201" "$CODE"

# 4. Create seller profile
CODE=$(curl -s -o /tmp/r.json -w "%{http_code}" -X POST "$BASE/sellers" \
  -H "Content-Type: application/json" \
  -d '{"userId":2,"shopName":"TechShop","description":"Найкраща електроніка","commissionRate":0.05}')
check "4. POST /api/sellers" "201" "$CODE"

# 5. Create category "Electronics"
CODE=$(curl -s -o /tmp/r.json -w "%{http_code}" -X POST "$BASE/categories" \
  -H "Content-Type: application/json" \
  -d '{"name":"Electronics","description":"Електронні пристрої"}')
check "5. POST /api/categories (Electronics)" "201" "$CODE"

# 6. Create subcategory "Headphones" (parent=1)
CODE=$(curl -s -o /tmp/r.json -w "%{http_code}" -X POST "$BASE/categories" \
  -H "Content-Type: application/json" \
  -d '{"name":"Headphones","description":"Навушники","parentId":1}')
check "6. POST /api/categories (Headphones, parent=1)" "201" "$CODE"

# 7. Create product, stock=10
CODE=$(curl -s -o /tmp/r.json -w "%{http_code}" -X POST "$BASE/products" \
  -H "Content-Type: application/json" \
  -d '{"sellerId":1,"categoryIds":[2],"name":"Бездротові навушники","description":"Преміум якість","price":1500.0,"stock":10}')
check "7. POST /api/products (stock=10)" "201" "$CODE"

# 8. Create order — 2 units
CODE=$(curl -s -o /tmp/r.json -w "%{http_code}" -X POST "$BASE/orders" \
  -H "Content-Type: application/json" \
  -d '{"customerId":1,"items":[{"productId":1,"quantity":2}]}')
check "8. POST /api/orders (qty=2)" "201" "$CODE"

# 9. Confirm order
CODE=$(curl -s -o /tmp/r.json -w "%{http_code}" -X POST "$BASE/orders/1/confirm")
check "9. POST /api/orders/1/confirm" "200" "$CODE"
STOCK=$(curl -s "$BASE/products/1" | python3 -c 'import sys,json; print(json.load(sys.stdin)["stock"])' 2>/dev/null)
echo "   📦 Stock після confirm: $STOCK (очікувалося 8)"

# 10. Create payment (CARD)
CODE=$(curl -s -o /tmp/r.json -w "%{http_code}" -X POST "$BASE/payments" \
  -H "Content-Type: application/json" \
  -d '{"orderId":1,"method":"CARD"}')
check "10. POST /api/payments (CARD)" "201" "$CODE"

# 11. Complete payment → auto commission
CODE=$(curl -s -o /tmp/r.json -w "%{http_code}" -X POST "$BASE/payments/1/complete")
check "11. POST /api/payments/1/complete" "200" "$CODE"
COMM_AMT=$(curl -s "$BASE/commissions/seller/1" | python3 -c 'import sys,json; d=json.load(sys.stdin); print(d[0]["amount"] if d else "none")' 2>/dev/null)
echo "   💰 Комісія автоматично створена: $COMM_AMT (очікувалося 150.0)"

# 12. Ship order
CODE=$(curl -s -o /tmp/r.json -w "%{http_code}" -X POST "$BASE/orders/1/ship")
check "12. POST /api/orders/1/ship" "200" "$CODE"

# 13. Deliver order
CODE=$(curl -s -o /tmp/r.json -w "%{http_code}" -X POST "$BASE/orders/1/deliver")
check "13. POST /api/orders/1/deliver" "200" "$CODE"

# 14. Create review (PRODUCT, rating=5)
CODE=$(curl -s -o /tmp/r.json -w "%{http_code}" -X POST "$BASE/reviews" \
  -H "Content-Type: application/json" \
  -d '{"authorId":1,"target":"PRODUCT","targetId":1,"rating":5,"comment":"Чудові навушники!"}')
check "14. POST /api/reviews (PRODUCT, rating=5)" "201" "$CODE"

# GET-запити
echo ""
echo "=== GET REQUESTS ==="
echo ""
CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/users")
check "GET /api/users" "200" "$CODE"
CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/users/1")
check "GET /api/users/1" "200" "$CODE"
CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/customers")
check "GET /api/customers" "200" "$CODE"
CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/sellers")
check "GET /api/sellers" "200" "$CODE"
CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/sellers/1/products")
check "GET /api/sellers/1/products" "200" "$CODE"
CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/categories")
check "GET /api/categories" "200" "$CODE"
CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/categories/1/subcategories")
check "GET /api/categories/1/subcategories" "200" "$CODE"
CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/products")
check "GET /api/products" "200" "$CODE"
CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/products?categoryId=2")
check "GET /api/products?categoryId=2" "200" "$CODE"
CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/products?sellerId=1")
check "GET /api/products?sellerId=1" "200" "$CODE"
CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/orders")
check "GET /api/orders" "200" "$CODE"
CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/orders/customer/1")
check "GET /api/orders/customer/1" "200" "$CODE"
CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/payments/1")
check "GET /api/payments/1" "200" "$CODE"
CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/payments/order/1")
check "GET /api/payments/order/1" "200" "$CODE"
CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/reviews/product/1")
check "GET /api/reviews/product/1" "200" "$CODE"
CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/commissions/seller/1")
check "GET /api/commissions/seller/1" "200" "$CODE"

echo ""
echo "=== ERROR PATH — 404 Not Found ==="
echo ""

CODE=$(curl -s -o /tmp/r.json -w "%{http_code}" "$BASE/users/999")
check "GET /api/users/999" "404" "$CODE"; show_error

CODE=$(curl -s -o /tmp/r.json -w "%{http_code}" "$BASE/products/999")
check "GET /api/products/999" "404" "$CODE"; show_error

CODE=$(curl -s -o /tmp/r.json -w "%{http_code}" "$BASE/orders/999")
check "GET /api/orders/999" "404" "$CODE"; show_error

CODE=$(curl -s -o /tmp/r.json -w "%{http_code}" "$BASE/payments/999")
check "GET /api/payments/999" "404" "$CODE"; show_error

CODE=$(curl -s -o /tmp/r.json -w "%{http_code}" "$BASE/categories/999")
check "GET /api/categories/999" "404" "$CODE"; show_error

CODE=$(curl -s -o /tmp/r.json -w "%{http_code}" "$BASE/customers/999")
check "GET /api/customers/999" "404" "$CODE"; show_error

CODE=$(curl -s -o /tmp/r.json -w "%{http_code}" "$BASE/sellers/999")
check "GET /api/sellers/999" "404" "$CODE"; show_error

echo ""
echo "=== ERROR PATH — 400 Business Rules ==="
echo ""

# Seller з роллю CUSTOMER
CODE=$(curl -s -o /tmp/r.json -w "%{http_code}" -X POST "$BASE/sellers" \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"shopName":"BadShop"}')
check "POST /api/sellers (userId з роллю CUSTOMER)" "400" "$CODE"; show_error

# Customer з роллю SELLER
CODE=$(curl -s -o /tmp/r.json -w "%{http_code}" -X POST "$BASE/customers" \
  -H "Content-Type: application/json" \
  -d '{"userId":2,"firstName":"Test","lastName":"Test"}')
check "POST /api/customers (userId з роллю SELLER)" "400" "$CODE"; show_error

# Дублікат email
CODE=$(curl -s -o /tmp/r.json -w "%{http_code}" -X POST "$BASE/users" \
  -H "Content-Type: application/json" \
  -d '{"email":"customer@test.com","password":"pass","role":"CUSTOMER"}')
check "POST /api/users (дублікат email)" "400" "$CODE"; show_error

# Повторний профіль покупця
CODE=$(curl -s -o /tmp/r.json -w "%{http_code}" -X POST "$BASE/customers" \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"firstName":"Test","lastName":"Test"}')
check "POST /api/customers (повторний профіль)" "400" "$CODE"; show_error

# Повторний профіль продавця
CODE=$(curl -s -o /tmp/r.json -w "%{http_code}" -X POST "$BASE/sellers" \
  -H "Content-Type: application/json" \
  -d '{"userId":2,"shopName":"Shop2"}')
check "POST /api/sellers (повторний профіль)" "400" "$CODE"; show_error

# Замовлення з недостатнім stock
CODE=$(curl -s -o /tmp/r.json -w "%{http_code}" -X POST "$BASE/orders" \
  -H "Content-Type: application/json" \
  -d '{"customerId":1,"items":[{"productId":1,"quantity":999}]}')
check "POST /api/orders (stock=999, занадто багато)" "400" "$CODE"; show_error

# Деактивуємо товар для наступного тесту
curl -s -X POST "$BASE/products/1/deactivate" > /dev/null

# Замовлення з неактивним товаром
CODE=$(curl -s -o /tmp/r.json -w "%{http_code}" -X POST "$BASE/orders" \
  -H "Content-Type: application/json" \
  -d '{"customerId":1,"items":[{"productId":1,"quantity":1}]}')
check "POST /api/orders (product INACTIVE)" "400" "$CODE"; show_error

# Повторна деактивація товару
CODE=$(curl -s -o /tmp/r.json -w "%{http_code}" -X POST "$BASE/products/1/deactivate")
check "POST /api/products/1/deactivate (вже INACTIVE)" "400" "$CODE"; show_error

# Повторний платіж
CODE=$(curl -s -o /tmp/r.json -w "%{http_code}" -X POST "$BASE/payments" \
  -H "Content-Type: application/json" \
  -d '{"orderId":1,"method":"CARD"}')
check "POST /api/payments (дублікат для order 1)" "400" "$CODE"; show_error

# Платіж для не-CONFIRMED замовлення — створюємо нове PENDING
curl -s -X POST "$BASE/products" \
  -H "Content-Type: application/json" \
  -d '{"sellerId":1,"categoryIds":[2],"name":"Earbuds","description":"Compact","price":500.0,"stock":20}' > /dev/null
curl -s -X POST "$BASE/orders" \
  -H "Content-Type: application/json" \
  -d '{"customerId":1,"items":[{"productId":2,"quantity":1}]}' > /dev/null
CODE=$(curl -s -o /tmp/r.json -w "%{http_code}" -X POST "$BASE/payments" \
  -H "Content-Type: application/json" \
  -d '{"orderId":2,"method":"CARD"}')
check "POST /api/payments (order PENDING)" "400" "$CODE"; show_error

# Повторний відгук
CODE=$(curl -s -o /tmp/r.json -w "%{http_code}" -X POST "$BASE/reviews" \
  -H "Content-Type: application/json" \
  -d '{"authorId":1,"target":"PRODUCT","targetId":1,"rating":4,"comment":"Ще раз"}')
check "POST /api/reviews (дублікат)" "400" "$CODE"; show_error

# Відгук без DELIVERED замовлення
CODE=$(curl -s -o /tmp/r.json -w "%{http_code}" -X POST "$BASE/reviews" \
  -H "Content-Type: application/json" \
  -d '{"authorId":1,"target":"PRODUCT","targetId":2,"rating":4,"comment":"Test"}')
check "POST /api/reviews (немає DELIVERED для product 2)" "400" "$CODE"; show_error

# Повторна оплата комісії
curl -s -X POST "$BASE/commissions/1/pay" > /dev/null
CODE=$(curl -s -o /tmp/r.json -w "%{http_code}" -X POST "$BASE/commissions/1/pay")
check "POST /api/commissions/1/pay (вже сплачена)" "400" "$CODE"; show_error

echo ""
echo "=== ERROR PATH — 400 Invalid Status Transitions ==="
echo ""

# Ship PENDING order (order 2 is PENDING)
CODE=$(curl -s -o /tmp/r.json -w "%{http_code}" -X POST "$BASE/orders/2/ship")
check "POST /api/orders/2/ship (PENDING→SHIPPED)" "400" "$CODE"; show_error

# Deliver PENDING order
CODE=$(curl -s -o /tmp/r.json -w "%{http_code}" -X POST "$BASE/orders/2/deliver")
check "POST /api/orders/2/deliver (PENDING→DELIVERED)" "400" "$CODE"; show_error

# Confirm DELIVERED order (order 1 is DELIVERED)
CODE=$(curl -s -o /tmp/r.json -w "%{http_code}" -X POST "$BASE/orders/1/confirm")
check "POST /api/orders/1/confirm (DELIVERED→CONFIRMED)" "400" "$CODE"; show_error

# Ship DELIVERED order
CODE=$(curl -s -o /tmp/r.json -w "%{http_code}" -X POST "$BASE/orders/1/ship")
check "POST /api/orders/1/ship (DELIVERED→SHIPPED)" "400" "$CODE"; show_error

# Cancel DELIVERED order
CODE=$(curl -s -o /tmp/r.json -w "%{http_code}" -X POST "$BASE/orders/1/cancel")
check "POST /api/orders/1/cancel (DELIVERED→CANCELLED)" "400" "$CODE"; show_error

# Refund PENDING payment
curl -s -X POST "$BASE/orders/2/confirm" > /dev/null
curl -s -X POST "$BASE/payments" \
  -H "Content-Type: application/json" \
  -d '{"orderId":2,"method":"BANK_TRANSFER"}' > /dev/null
CODE=$(curl -s -o /tmp/r.json -w "%{http_code}" -X POST "$BASE/payments/2/refund")
check "POST /api/payments/2/refund (PENDING→REFUNDED)" "400" "$CODE"; show_error

# Fail PENDING payment
CODE=$(curl -s -o /tmp/r.json -w "%{http_code}" -X POST "$BASE/payments/2/fail")
check "POST /api/payments/2/fail (PENDING→FAILED)" "400" "$CODE"; show_error

echo ""
echo "=== ERROR PATH — 400 Validation ==="
echo ""

# Порожній email
CODE=$(curl -s -o /tmp/r.json -w "%{http_code}" -X POST "$BASE/users" \
  -H "Content-Type: application/json" \
  -d '{"email":"","password":"pass","role":"CUSTOMER"}')
check "POST /api/users (порожній email)" "400" "$CODE"; show_error

# Невалідний email
CODE=$(curl -s -o /tmp/r.json -w "%{http_code}" -X POST "$BASE/users" \
  -H "Content-Type: application/json" \
  -d '{"email":"not-email","password":"pass","role":"CUSTOMER"}')
check "POST /api/users (невалідний email)" "400" "$CODE"; show_error

# Без пароля
CODE=$(curl -s -o /tmp/r.json -w "%{http_code}" -X POST "$BASE/users" \
  -H "Content-Type: application/json" \
  -d '{"email":"new@test.com","role":"CUSTOMER"}')
check "POST /api/users (без пароля)" "400" "$CODE"; show_error

# Від'ємна ціна
CODE=$(curl -s -o /tmp/r.json -w "%{http_code}" -X POST "$BASE/products" \
  -H "Content-Type: application/json" \
  -d '{"sellerId":1,"name":"Bad","price":-100,"stock":5}')
check "POST /api/products (від'ємна ціна)" "400" "$CODE"; show_error

# Порожній список items
CODE=$(curl -s -o /tmp/r.json -w "%{http_code}" -X POST "$BASE/orders" \
  -H "Content-Type: application/json" \
  -d '{"customerId":1,"items":[]}')
check "POST /api/orders (порожній items)" "400" "$CODE"; show_error

# Рейтинг > 5
CODE=$(curl -s -o /tmp/r.json -w "%{http_code}" -X POST "$BASE/reviews" \
  -H "Content-Type: application/json" \
  -d '{"authorId":1,"target":"PRODUCT","targetId":1,"rating":10,"comment":"Too much"}')
check "POST /api/reviews (rating=10, >5)" "400" "$CODE"; show_error

# Рейтинг 0
CODE=$(curl -s -o /tmp/r.json -w "%{http_code}" -X POST "$BASE/reviews" \
  -H "Content-Type: application/json" \
  -d '{"authorId":1,"target":"PRODUCT","targetId":1,"rating":0,"comment":"Zero"}')
check "POST /api/reviews (rating=0, <1)" "400" "$CODE"; show_error

# Порожнє ім'я категорії
CODE=$(curl -s -o /tmp/r.json -w "%{http_code}" -X POST "$BASE/categories" \
  -H "Content-Type: application/json" \
  -d '{"name":"","description":"Empty"}')
check "POST /api/categories (порожнє ім'я)" "400" "$CODE"; show_error

# Без назви товару
CODE=$(curl -s -o /tmp/r.json -w "%{http_code}" -X POST "$BASE/products" \
  -H "Content-Type: application/json" \
  -d '{"sellerId":1,"price":100,"stock":5}')
check "POST /api/products (без назви)" "400" "$CODE"; show_error

echo ""
echo "========================================="
echo "  RESULTS: ✅ $PASS passed, ❌ $FAIL failed"
echo "========================================="
