# Marketplace REST API

Spring Boot REST API для платформи маркетплейсу (ЛР №2, варіант 22).

## Запуск

Відкрити проєкт в IntelliJ IDEA → Run `MarketplaceApplication.java`
Сервер запуститься на `http://localhost:8080`

---

## Тестові запити (Happy Path)

### 1. Створити CUSTOMER акаунт
```
POST http://localhost:8080/api/users
Content-Type: application/json

{"email":"customer@test.com","password":"pass123","role":"CUSTOMER"}
```
→ **201 Created**

### 2. Створити SELLER акаунт
```
POST http://localhost:8080/api/users
Content-Type: application/json

{"email":"seller@test.com","password":"pass456","role":"SELLER"}
```
→ **201 Created**

### 3. Створити профіль покупця
```
POST http://localhost:8080/api/customers
Content-Type: application/json

{"userId":1,"firstName":"Іван","lastName":"Петренко","phone":"+380501234567","shippingAddress":"Київ, Україна"}
```
→ **201 Created**

### 4. Зареєструвати продавця
```
POST http://localhost:8080/api/sellers
Content-Type: application/json

{"userId":2,"shopName":"TechShop","description":"Найкраща електроніка","commissionRate":0.05}
```
→ **201 Created**

### 5. Створити категорію "Electronics"
```
POST http://localhost:8080/api/categories
Content-Type: application/json

{"name":"Electronics","description":"Електронні пристрої"}
```
→ **201 Created**

### 6. Створити підкатегорію "Headphones" (parent=1)
```
POST http://localhost:8080/api/categories
Content-Type: application/json

{"name":"Headphones","description":"Навушники","parentId":1}
```
→ **201 Created**

### 7. Додати товар (stock=10)
```
POST http://localhost:8080/api/products
Content-Type: application/json

{"sellerId":1,"categoryIds":[2],"name":"Бездротові навушники","description":"Преміум якість","price":1500.0,"stock":10}
```
→ **201 Created**

### 8. Створити замовлення (2 одиниці)
```
POST http://localhost:8080/api/orders
Content-Type: application/json

{"customerId":1,"items":[{"productId":1,"quantity":2}]}
```
→ **201 Created** (totalAmount=3000, discountAmount=0, finalAmount=3000)

### 9. Підтвердити замовлення
```
POST http://localhost:8080/api/orders/1/confirm
```
→ **200 OK** (stock: 10→8)

### 10. Створити платіж
```
POST http://localhost:8080/api/payments
Content-Type: application/json

{"orderId":1,"method":"CARD"}
```
→ **201 Created** (status=PENDING, amount=3000)

### 11. Підтвердити оплату
```
POST http://localhost:8080/api/payments/1/complete
```
→ **200 OK** (status=COMPLETED, Commission автоматично створено: 3000×0.05=150)

### 12. Відправити замовлення
```
POST http://localhost:8080/api/orders/1/ship
```
→ **200 OK** (status=SHIPPED)

### 13. Доставити замовлення
```
POST http://localhost:8080/api/orders/1/deliver
```
→ **200 OK** (status=DELIVERED)

### 14. Залишити відгук
```
POST http://localhost:8080/api/reviews
Content-Type: application/json

{"authorId":1,"target":"PRODUCT","targetId":1,"rating":5,"comment":"Чудові навушники!"}
```
→ **201 Created** (seller.rating оновлюється)

---

## GET-запити

### Користувачі
```
GET http://localhost:8080/api/users
GET http://localhost:8080/api/users/1
```

### Покупці
```
GET http://localhost:8080/api/customers
GET http://localhost:8080/api/customers/1
```

### Продавці
```
GET http://localhost:8080/api/sellers
GET http://localhost:8080/api/sellers/1
GET http://localhost:8080/api/sellers/1/products
```

### Категорії
```
GET http://localhost:8080/api/categories
GET http://localhost:8080/api/categories/1
GET http://localhost:8080/api/categories/1/subcategories
```

### Товари
```
GET http://localhost:8080/api/products
GET http://localhost:8080/api/products/1
GET http://localhost:8080/api/products?categoryId=2
GET http://localhost:8080/api/products?sellerId=1
```

### Замовлення
```
GET http://localhost:8080/api/orders
GET http://localhost:8080/api/orders/1
GET http://localhost:8080/api/orders/customer/1
```

### Платежі
```
GET http://localhost:8080/api/payments/1
GET http://localhost:8080/api/payments/order/1
```

### Відгуки
```
GET http://localhost:8080/api/reviews/product/1
GET http://localhost:8080/api/reviews/seller/1
```

### Комісії
```
GET http://localhost:8080/api/commissions/seller/1
```

---

## Тестові запити з помилками

### ResourceNotFoundException (404)

**Користувач не знайдений:**
```
GET http://localhost:8080/api/users/999
```
→ **404** `{"timestamp":"...","status":404,"error":"Not Found","message":"User with id 999 not found"}`

**Товар не знайдений:**
```
GET http://localhost:8080/api/products/999
```
→ **404** `{"timestamp":"...","status":404,"error":"Not Found","message":"Product with id 999 not found"}`

**Замовлення не знайдене:**
```
GET http://localhost:8080/api/orders/999
```
→ **404** `{"timestamp":"...","status":404,"error":"Not Found","message":"Order with id 999 not found"}`

**Платіж не знайдений:**
```
GET http://localhost:8080/api/payments/999
```
→ **404** `{"timestamp":"...","status":404,"error":"Not Found","message":"Payment with id 999 not found"}`

**Категорія не знайдена:**
```
GET http://localhost:8080/api/categories/999
```
→ **404** `{"timestamp":"...","status":404,"error":"Not Found","message":"Category with id 999 not found"}`

**Покупець не знайдений:**
```
GET http://localhost:8080/api/customers/999
```
→ **404** `{"timestamp":"...","status":404,"error":"Not Found","message":"Customer with id 999 not found"}`

**Продавець не знайдений:**
```
GET http://localhost:8080/api/sellers/999
```
→ **404** `{"timestamp":"...","status":404,"error":"Not Found","message":"Seller with id 999 not found"}`

---

### BusinessRuleException (400)

**Створення продавця для користувача з роллю CUSTOMER:**
```
POST http://localhost:8080/api/sellers
Content-Type: application/json

{"userId":1,"shopName":"BadShop"}
```
→ **400** `{"message":"User role must be SELLER"}`

**Створення покупця для користувача з роллю SELLER:**
```
POST http://localhost:8080/api/customers
Content-Type: application/json

{"userId":2,"firstName":"Test","lastName":"Test"}
```
→ **400** `{"message":"User role must be CUSTOMER"}`

**Дублікат email при реєстрації:**
```
POST http://localhost:8080/api/users
Content-Type: application/json

{"email":"customer@test.com","password":"pass","role":"CUSTOMER"}
```
→ **400** `{"message":"User with email customer@test.com already exists"}`

**Повторний профіль покупця:**
```
POST http://localhost:8080/api/customers
Content-Type: application/json

{"userId":1,"firstName":"Test","lastName":"Test"}
```
→ **400** `{"message":"Customer profile already exists for user 1"}`

**Повторний профіль продавця:**
```
POST http://localhost:8080/api/sellers
Content-Type: application/json

{"userId":2,"shopName":"Shop2"}
```
→ **400** `{"message":"Seller profile already exists for user 2"}`

**Замовлення з неактивним товаром:**
(спочатку деактивувати: `POST /api/products/1/deactivate`)
```
POST http://localhost:8080/api/orders
Content-Type: application/json

{"customerId":1,"items":[{"productId":1,"quantity":1}]}
```
→ **400** `{"message":"Product 1 is not available"}`

**Замовлення з недостатнім stock:**
```
POST http://localhost:8080/api/orders
Content-Type: application/json

{"customerId":1,"items":[{"productId":1,"quantity":999}]}
```
→ **400** `{"message":"Insufficient stock for product 1"}`

**Платіж для не-CONFIRMED замовлення:**
```
POST http://localhost:8080/api/payments
Content-Type: application/json

{"orderId":1,"method":"CARD"}
```
→ **400** `{"message":"Payment requires CONFIRMED order"}` (якщо замовлення ще PENDING)

**Повторний платіж для замовлення:**
```
POST http://localhost:8080/api/payments
Content-Type: application/json

{"orderId":1,"method":"CARD"}
```
→ **400** `{"message":"Payment already exists for order 1"}`

**Відгук без DELIVERED замовлення:**
```
POST http://localhost:8080/api/reviews
Content-Type: application/json

{"authorId":1,"target":"PRODUCT","targetId":1,"rating":5,"comment":"Test"}
```
→ **400** `{"message":"No delivered orders with this product"}`

**Повторний відгук:**
```
POST http://localhost:8080/api/reviews
Content-Type: application/json

{"authorId":1,"target":"PRODUCT","targetId":1,"rating":4,"comment":"Another"}
```
→ **400** `{"message":"Review already submitted"}`

**Повторна оплата комісії:**
```
POST http://localhost:8080/api/commissions/1/pay
```
→ **400** `{"message":"Commission 1 is already paid"}` (після першого POST)

**Деактивація вже неактивного товару:**
```
POST http://localhost:8080/api/products/1/deactivate
```
→ **400** `{"message":"Product 1 is already inactive"}` (після першої деактивації)

---

### InvalidStatusTransitionException (400)

**Відправити PENDING замовлення (минаючи CONFIRMED):**
```
POST http://localhost:8080/api/orders/1/ship
```
→ **400** `{"message":"Cannot transition Order from PENDING to SHIPPED"}`

**Підтвердити DELIVERED замовлення:**
```
POST http://localhost:8080/api/orders/1/confirm
```
→ **400** `{"message":"Cannot transition Order from DELIVERED to CONFIRMED"}`

**Доставити PENDING замовлення:**
```
POST http://localhost:8080/api/orders/1/deliver
```
→ **400** `{"message":"Cannot transition Order from PENDING to DELIVERED"}`

**Скасувати DELIVERED замовлення:**
```
POST http://localhost:8080/api/orders/1/cancel
```
→ **400** `{"message":"Cannot transition Order from DELIVERED to CANCELLED"}`

**Скасувати SHIPPED замовлення:**
```
POST http://localhost:8080/api/orders/1/cancel
```
→ **400** `{"message":"Cannot transition Order from SHIPPED to CANCELLED"}`

**Refund з PENDING платежу:**
```
POST http://localhost:8080/api/payments/1/refund
```
→ **400** `{"message":"Cannot transition Payment from PENDING to REFUNDED"}`

**Fail з PENDING платежу:**
```
POST http://localhost:8080/api/payments/1/fail
```
→ **400** `{"message":"Cannot transition Payment from PENDING to FAILED"}`

---

### Validation Error (400)

**Порожній email:**
```
POST http://localhost:8080/api/users
Content-Type: application/json

{"email":"","password":"pass","role":"CUSTOMER"}
```
→ **400** `{"message":"email: Email is required"}`

**Невалідний email:**
```
POST http://localhost:8080/api/users
Content-Type: application/json

{"email":"not-an-email","password":"pass","role":"CUSTOMER"}
```
→ **400** `{"message":"email: Invalid email format"}`

**Без пароля:**
```
POST http://localhost:8080/api/users
Content-Type: application/json

{"email":"test@test.com","role":"CUSTOMER"}
```
→ **400** `{"message":"password: Password is required"}`

**Від'ємна ціна товару:**
```
POST http://localhost:8080/api/products
Content-Type: application/json

{"sellerId":1,"name":"Bad Product","price":-100,"stock":5}
```
→ **400** `{"message":"price: Price must be positive"}`

**Порожній список позицій замовлення:**
```
POST http://localhost:8080/api/orders
Content-Type: application/json

{"customerId":1,"items":[]}
```
→ **400** `{"message":"items: Order must have at least one item"}`

**Рейтинг поза діапазоном 1–5:**
```
POST http://localhost:8080/api/reviews
Content-Type: application/json

{"authorId":1,"target":"PRODUCT","targetId":1,"rating":10,"comment":"Too much"}
```
→ **400** `{"message":"rating: Rating must be at most 5"}`

**Рейтинг 0:**
```
POST http://localhost:8080/api/reviews
Content-Type: application/json

{"authorId":1,"target":"PRODUCT","targetId":1,"rating":0,"comment":"Zero"}
```
→ **400** `{"message":"rating: Rating must be at least 1"}`

**Порожнє ім'я категорії:**
```
POST http://localhost:8080/api/categories
Content-Type: application/json

{"name":"","description":"Empty name"}
```
→ **400** `{"message":"name: Category name is required"}`

**Без назви товару:**
```
POST http://localhost:8080/api/products
Content-Type: application/json

{"sellerId":1,"price":100,"stock":5}
```
→ **400** `{"message":"name: Product name is required"}`
