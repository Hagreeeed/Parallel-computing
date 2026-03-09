# Лабораторна робота №2
## RESTful сервіси. Розробка базового REST API у Spring Boot
### Варіант 22 — Платформа маркетплейсу · Відмінний рівень

---

## 1. Мета роботи

Розробка базового REST API з використанням Spring Boot для платформи маркетплейсу
з in-memory зберіганням даних, складною доменною моделлю (8 сутностей + Enum-и),
реалізацією бізнес-логіки та обробкою виняткових ситуацій відповідно до принципів
REST-архітектури.

---

## 2. Доменна модель

### 2.1 Enum-и

```java
enum UserRole        { CUSTOMER, SELLER, ADMIN }
enum UserStatus      { ACTIVE, BANNED, DELETED }

enum ProductStatus   { ACTIVE, INACTIVE, OUT_OF_STOCK }

enum OrderStatus     { PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED }

enum PaymentStatus   { PENDING, PROCESSING, COMPLETED, FAILED, REFUNDED }
enum PaymentMethod   { CARD, BANK_TRANSFER, CASH_ON_DELIVERY }

enum ReviewTarget    { PRODUCT, SELLER }
```

---

### 2.2 Сутності

#### `User` — обліковий запис
| Поле | Тип | Опис |
|------|-----|------|
| `id` | Long | PK |
| `email` | String | Унікальний, @NotBlank |
| `passwordHash` | String | Хеш пароля |
| `role` | UserRole | `CUSTOMER / SELLER / ADMIN` |
| `status` | UserStatus | `ACTIVE / BANNED / DELETED` |
| `createdAt` | LocalDateTime | Дата реєстрації |

> `User` містить лише автентифікаційні дані. Доменна інформація винесена в `Customer` або `Seller`.

---

#### `Customer` — профіль покупця (One-to-One → User)
| Поле | Тип | Опис |
|------|-----|------|
| `id` | Long | PK |
| `userId` | Long | FK → User (унікальний) |
| `firstName` | String | Ім'я |
| `lastName` | String | Прізвище |
| `phone` | String | Телефон |
| `shippingAddress` | String | Адреса доставки |

---

#### `Seller` — профіль продавця (One-to-One → User)
| Поле | Тип | Опис |
|------|-----|------|
| `id` | Long | PK |
| `userId` | Long | FK → User (унікальний) |
| `shopName` | String | Назва магазину |
| `description` | String | Опис |
| `rating` | Double | Середній рейтинг (0.0–5.0), авторозрахунок |
| `totalSales` | Integer | Кількість виконаних замовлень |
| `commissionRate` | Double | Відсоток комісії платформи (напр. 0.05) |
| `verified` | Boolean | Верифікований платформою |

---

#### `Category` — категорія товарів (з підкатегоріями)
| Поле | Тип | Опис |
|------|-----|------|
| `id` | Long | PK |
| `name` | String | Назва |
| `description` | String | Опис |
| `parentId` | Long | FK → Category (nullable, self-reference) |

> Зв'язок: `Category` → `Category` (One-to-Many, self-reference)

---

#### `Product` — товар
| Поле | Тип | Опис |
|------|-----|------|
| `id` | Long | PK |
| `sellerId` | Long | FK → Seller |
| `categoryIds` | List\<Long\> | FK → Category (Many-to-Many) |
| `name` | String | Назва |
| `description` | String | Опис |
| `price` | Double | Ціна |
| `stock` | Integer | Залишок |
| `status` | ProductStatus | `ACTIVE / INACTIVE / OUT_OF_STOCK` |
| `createdAt` | LocalDateTime | Дата додавання |

> Бізнес-правило: при `stock = 0` → автоматично `status = OUT_OF_STOCK`

---

#### `Order` — замовлення
| Поле | Тип | Опис |
|------|-----|------|
| `id` | Long | PK |
| `customerId` | Long | FK → Customer |
| `items` | List\<OrderItem\> | Позиції (вбудовані) |
| `status` | OrderStatus | `PENDING / CONFIRMED / SHIPPED / DELIVERED / CANCELLED` |
| `totalAmount` | Double | Сума без знижки |
| `discountAmount` | Double | Знижка |
| `finalAmount` | Double | Сума до оплати |
| `createdAt` | LocalDateTime | Дата |

##### `OrderItem` (вбудований у Order)
| Поле | Тип | Опис |
|------|-----|------|
| `productId` | Long | FK → Product |
| `sellerId` | Long | Знімок продавця на момент замовлення |
| `productName` | String | Знімок назви |
| `unitPrice` | Double | Знімок ціни |
| `quantity` | Integer | Кількість |
| `subtotal` | Double | `unitPrice x quantity` |

> Знімок (`productName`, `unitPrice`, `sellerId`) зберігається, щоб зміни товару не впливали на існуючі замовлення.

---

#### `Payment` — платіж
| Поле | Тип | Опис |
|------|-----|------|
| `id` | Long | PK |
| `orderId` | Long | FK → Order (One-to-One) |
| `method` | PaymentMethod | `CARD / BANK_TRANSFER / CASH_ON_DELIVERY` |
| `status` | PaymentStatus | `PENDING / PROCESSING / COMPLETED / FAILED / REFUNDED` |
| `amount` | Double | Сума платежу (= `order.finalAmount`) |
| `transactionId` | String | Зовнішній ID транзакції (nullable) |
| `paidAt` | LocalDateTime | Час успішної оплати (nullable) |

---

#### `Review` — відгук
| Поле | Тип | Опис |
|------|-----|------|
| `id` | Long | PK |
| `authorId` | Long | FK → Customer |
| `target` | ReviewTarget | `PRODUCT` або `SELLER` |
| `targetId` | Long | ID товару або продавця |
| `rating` | Integer | 1–5 |
| `comment` | String | Текст |
| `createdAt` | LocalDateTime | Дата |

---

#### `Commission` — комісія платформи
| Поле | Тип | Опис |
|------|-----|------|
| `id` | Long | PK |
| `orderId` | Long | FK → Order |
| `sellerId` | Long | FK → Seller |
| `orderAmount` | Double | Сума замовлення |
| `rate` | Double | Знімок `seller.commissionRate` |
| `amount` | Double | `orderAmount x rate` |
| `paid` | Boolean | Сплачено платформі |
| `createdAt` | LocalDateTime | Дата нарахування |

---

### 2.3 Діаграма зв'язків

```
User ──(1:1)──► Customer
User ──(1:1)──► Seller

Customer ──(1:N)──► Order
Seller   ──(1:N)──► Product
Seller   ──(1:N)──► Commission

Category ──(1:N, self)──► Category
Product  ──(M:N)────────► Category

Order ──(1:1)──► Payment
Order ──(1:1)──► Commission
Order ──(1:N)──► OrderItem  [embedded]

Customer ──(1:N)──► Review
Product  ──(1:N)──► Review  [via target=PRODUCT]
Seller   ──(1:N)──► Review  [via target=SELLER]
```

| Зв'язок | Тип |
|---------|-----|
| User — Customer | One-to-One |
| User — Seller | One-to-One |
| Seller → Product | One-to-Many |
| Category → Category | One-to-Many (self-reference) |
| Product — Category | Many-to-Many |
| Customer → Order | One-to-Many |
| Order → OrderItem | One-to-Many (embedded) |
| Order — Payment | One-to-One |
| Order — Commission | One-to-One |
| Customer → Review | One-to-Many |

---

## 3. Машини станів

### Order
```
PENDING ──► CONFIRMED ──► SHIPPED ──► DELIVERED
   |              |
   └──────────────┴──► CANCELLED
```

### Payment
```
PENDING ──► PROCESSING ──► COMPLETED
                      └──► FAILED ──► REFUNDED
```

---

## 4. REST Endpoints

### `/api/users`
| Метод | URL | Призначення | Код |
|-------|-----|-------------|-----|
| GET | `/api/users` | Всі користувачі | 200 |
| GET | `/api/users/{id}` | Користувач за ID | 200 / 404 |
| POST | `/api/users` | Створити акаунт | 201 / 400 |

### `/api/customers`
| Метод | URL | Призначення | Код |
|-------|-----|-------------|-----|
| GET | `/api/customers` | Всі покупці | 200 |
| GET | `/api/customers/{id}` | Покупець за ID | 200 / 404 |
| POST | `/api/customers` | Створити профіль | 201 / 400 |

### `/api/sellers`
| Метод | URL | Призначення | Код |
|-------|-----|-------------|-----|
| GET | `/api/sellers` | Всі продавці | 200 |
| GET | `/api/sellers/{id}` | Продавець за ID | 200 / 404 |
| GET | `/api/sellers/{id}/products` | Товари продавця | 200 |
| POST | `/api/sellers` | Зареєструвати продавця | 201 / 400 |

### `/api/categories`
| Метод | URL | Призначення | Код |
|-------|-----|-------------|-----|
| GET | `/api/categories` | Всі категорії | 200 |
| GET | `/api/categories/{id}` | Категорія за ID | 200 / 404 |
| GET | `/api/categories/{id}/subcategories` | Підкатегорії | 200 |
| POST | `/api/categories` | Створити категорію | 201 |

### `/api/products`
| Метод | URL | Призначення | Код |
|-------|-----|-------------|-----|
| GET | `/api/products` | Всі активні товари | 200 |
| GET | `/api/products/{id}` | Товар за ID | 200 / 404 |
| GET | `/api/products?categoryId={id}` | Фільтр за категорією | 200 |
| GET | `/api/products?sellerId={id}` | Фільтр за продавцем | 200 |
| POST | `/api/products` | Додати товар | 201 / 400 |
| POST | `/api/products/{id}/deactivate` | Деактивувати | 200 / 400 |

### `/api/orders`
| Метод | URL | Призначення | Код |
|-------|-----|-------------|-----|
| GET | `/api/orders` | Всі замовлення | 200 |
| GET | `/api/orders/{id}` | Замовлення за ID | 200 / 404 |
| GET | `/api/orders/customer/{customerId}` | Замовлення покупця | 200 |
| POST | `/api/orders` | Створити замовлення | 201 / 400 |
| POST | `/api/orders/{id}/confirm` | Підтвердити | 200 / 400 |
| POST | `/api/orders/{id}/ship` | Відправити | 200 / 400 |
| POST | `/api/orders/{id}/deliver` | Доставити | 200 / 400 |
| POST | `/api/orders/{id}/cancel` | Скасувати | 200 / 400 |

### `/api/payments`
| Метод | URL | Призначення | Код |
|-------|-----|-------------|-----|
| GET | `/api/payments/{id}` | Платіж за ID | 200 / 404 |
| GET | `/api/payments/order/{orderId}` | Платіж по замовленню | 200 / 404 |
| POST | `/api/payments` | Ініціювати платіж | 201 / 400 |
| POST | `/api/payments/{id}/complete` | Підтвердити оплату | 200 / 400 |
| POST | `/api/payments/{id}/fail` | Зафіксувати помилку | 200 / 400 |
| POST | `/api/payments/{id}/refund` | Повернення коштів | 200 / 400 |

### `/api/reviews`
| Метод | URL | Призначення | Код |
|-------|-----|-------------|-----|
| GET | `/api/reviews/product/{productId}` | Відгуки на товар | 200 |
| GET | `/api/reviews/seller/{sellerId}` | Відгуки на продавця | 200 |
| POST | `/api/reviews` | Залишити відгук | 201 / 400 |

### `/api/commissions`
| Метод | URL | Призначення | Код |
|-------|-----|-------------|-----|
| GET | `/api/commissions/seller/{sellerId}` | Комісії продавця | 200 |
| POST | `/api/commissions/{id}/pay` | Позначити сплаченою | 200 / 400 |

---

## 5. Бізнес-логіка

### Реєстрація профілів
- `Customer` можна створити лише для `User` з роллю `CUSTOMER`
- `Seller` можна створити лише для `User` з роллю `SELLER`
- Один `User` — максимум один профіль `Customer` або `Seller`

### Замовлення
- Перевірка `product.status == ACTIVE` для кожної позиції
- Перевірка `stock >= quantity` для кожної позиції
- Знімок даних: зберегти `unitPrice`, `productName`, `sellerId` у `OrderItem`
- `totalAmount = Σ(unitPrice × quantity)`
- Знижка: якщо `totalAmount > 5000` → `discountAmount = totalAmount × 0.05`
- `finalAmount = totalAmount − discountAmount`
- Переходи статусів суворо за машиною станів, інакше `400`
- При `CANCELLED` → повернути `stock` для кожної позиції

### Платіж
- Ініціювати можна лише для замовлення зі статусом `CONFIRMED`
- `amount` завжди дорівнює `order.finalAmount`
- Один платіж на одне замовлення (перевірка дублів)
- При `COMPLETED` → автоматично створюється `Commission`
- `REFUNDED` допустимий лише з `FAILED`

### Комісія
- Створюється автоматично при `payment.status = COMPLETED`
- `amount = payment.amount × seller.commissionRate`
- Зберігається знімок `rate` на момент нарахування

### Відгук
- Дозволений лише якщо існує замовлення покупця зі статусом `DELIVERED` з цим товаром / продавцем
- Один покупець — один відгук на `target + targetId`
- Після збереження → перерахунок `seller.rating` як середнього всіх оцінок

### Product (автоматика)
- При `stock == 0` → `status = OUT_OF_STOCK` автоматично
- Stock знижується при підтвердженні замовлення (`CONFIRMED`)

---

## 6. Структура пакетів

```
src/main/java/com/marketplace/
├── controller/
│   ├── UserController.java
│   ├── CustomerController.java
│   ├── SellerController.java
│   ├── CategoryController.java
│   ├── ProductController.java
│   ├── OrderController.java
│   ├── PaymentController.java
│   ├── ReviewController.java
│   └── CommissionController.java
├── service/          [аналогічно]
├── repository/       [аналогічно, in-memory HashMap]
├── model/
│   ├── User.java
│   ├── Customer.java
│   ├── Seller.java
│   ├── Category.java
│   ├── Product.java
│   ├── Order.java
│   ├── OrderItem.java
│   ├── Payment.java
│   ├── Review.java
│   ├── Commission.java
│   └── enums/
│       ├── UserRole.java
│       ├── UserStatus.java
│       ├── ProductStatus.java
│       ├── OrderStatus.java
│       ├── PaymentStatus.java
│       ├── PaymentMethod.java
│       └── ReviewTarget.java
├── dto/
│   ├── request/
│   │   ├── CreateUserRequest.java
│   │   ├── CreateCustomerRequest.java
│   │   ├── CreateSellerRequest.java
│   │   ├── CreateProductRequest.java
│   │   ├── CreateOrderRequest.java
│   │   ├── CreatePaymentRequest.java
│   │   └── CreateReviewRequest.java
│   └── response/
│       ├── OrderResponse.java
│       ├── PaymentResponse.java
│       └── ErrorResponse.java
├── exception/
│   ├── ResourceNotFoundException.java
│   ├── BusinessRuleException.java
│   ├── InvalidStatusTransitionException.java
│   └── GlobalExceptionHandler.java
└── MarketplaceApplication.java
```

---

## 7. Обробка помилок

| Виняток | HTTP | Сценарій |
|---------|------|----------|
| `ResourceNotFoundException` | 404 | Сутність не знайдена за ID |
| `BusinessRuleException` | 400 | Порушення бізнес-правила |
| `InvalidStatusTransitionException` | 400 | Недозволений перехід статусу |
| `@Valid` MethodArgumentNotValidException | 400 | Порожні / некоректні поля |

```json
{
  "timestamp": "2026-03-09T14:00:00",
  "status": 400,
  "error": "Invalid Status Transition",
  "message": "Cannot transition Order from PENDING to SHIPPED"
}
```

---

## 8. Тестові сценарії

### Happy path
```
1.  POST /api/users              → CUSTOMER акаунт         → 201
2.  POST /api/users              → SELLER акаунт           → 201
3.  POST /api/customers          → профіль покупця          → 201
4.  POST /api/sellers            → профіль продавця         → 201
5.  POST /api/categories         → "Electronics"            → 201
6.  POST /api/categories         → "Headphones" (parent=1)  → 201
7.  POST /api/products           → товар, stock=10          → 201
8.  POST /api/orders             → 2 одиниці товару         → 201, stock=8
9.  POST /api/orders/1/confirm                              → 200
10. POST /api/payments           → CARD                     → 201, PENDING
11. POST /api/payments/1/complete                           → 200, Commission auto
12. POST /api/orders/1/ship                                 → 200
13. POST /api/orders/1/deliver                              → 200
14. POST /api/reviews            → PRODUCT, rating=5        → 201, seller.rating updated
```

### Error path
```
POST /api/sellers (userId з роллю CUSTOMER) → 400 "User role must be SELLER"
POST /api/orders (stock=0)                  → 400 "Insufficient stock for product 3"
POST /api/orders (product INACTIVE)         → 400 "Product 5 is not available"
POST /api/orders/1/ship (status=PENDING)    → 400 "Cannot transition from PENDING to SHIPPED"
POST /api/payments (order=PENDING)          → 400 "Payment requires CONFIRMED order"
POST /api/payments (повторний)              → 400 "Payment already exists for order 1"
POST /api/reviews (без DELIVERED)           → 400 "No delivered orders with this product"
POST /api/reviews (повторний)               → 400 "Review already submitted"
GET  /api/products/999                      → 404 "Product with id 999 not found"
```

---

## 9. Підготовка до ЛР №3

| Зв'язок | JPA-анотація |
|---------|-------------|
| `User — Customer` | `@OneToOne @JoinColumn` |
| `User — Seller` | `@OneToOne @JoinColumn` |
| `Seller → Product` | `@OneToMany / @ManyToOne` |
| `Product — Category` | `@ManyToMany @JoinTable` |
| `Category → Category` | `@ManyToOne(self)` |
| `Order → OrderItem` | `@ElementCollection` або `@OneToMany` |
| `Order — Payment` | `@OneToOne` |
| `Order — Commission` | `@OneToOne` |
| Enum-и | `@Enumerated(EnumType.STRING)` |

---

## 10. Висновки

У лабораторній роботі №2 розроблено REST API платформи маркетплейсу відмінного рівня:

- **8 сутностей** з чітким розділенням: `User` — автентифікація, `Customer` / `Seller` — доменні профілі
- **7 Enum-типів** для всіх дискретних станів та варіантів
- **Усі типи зв'язків**: One-to-One, One-to-Many, Many-to-Many, self-reference
- **Дві машини станів**: `Order` (5 станів) та `Payment` (5 станів) з валідацією переходів
- **Нетривіальна бізнес-логіка**: знижки, знімки цін, автоматична комісія, перерахунок рейтингу
- Архітектура повністю готова до підключення JPA у ЛР №3
