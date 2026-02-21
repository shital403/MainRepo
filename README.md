# 👗 FashionStore - Full-Stack E-Commerce Clothing Website

A complete full-stack e-commerce platform for clothing built with **Java Spring Boot 3.2**, **Spring Security + JWT**, **MySQL**, and **Razorpay** payment integration.

## 🚀 Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.2.3 |
| Security | Spring Security 6 + JWT (JJWT 0.12.x) |
| Database | MySQL 8+ |
| ORM | Spring Data JPA / Hibernate |
| Build | Maven |
| Frontend | Thymeleaf |
| Payments | Razorpay |
| File Storage | Local filesystem |
| Utilities | Lombok |

## 📁 Project Structure

```
src/main/java/com/fashionstore/
├── FashionStoreApplication.java
├── config/
│   ├── CorsConfig.java
│   ├── DataLoader.java          # Seeds initial data on startup
│   ├── RazorpayConfig.java
│   └── SecurityConfig.java
├── controller/
│   ├── AuthController.java
│   ├── CartController.java
│   ├── CategoryController.java
│   ├── HomeController.java
│   ├── OrderController.java
│   ├── PaymentController.java
│   └── ProductController.java
├── dto/
│   ├── request/                 # LoginRequest, RegisterRequest, etc.
│   └── response/                # JwtResponse, ProductResponse, etc.
├── entity/
│   ├── Cart.java
│   ├── CartItem.java
│   ├── Category.java
│   ├── ERole.java
│   ├── Order.java
│   ├── OrderItem.java
│   ├── OrderStatus.java
│   ├── Product.java
│   ├── Role.java
│   └── User.java
├── exception/
│   ├── BadRequestException.java
│   ├── GlobalExceptionHandler.java
│   └── ResourceNotFoundException.java
├── repository/                  # Spring Data JPA repositories
├── security/
│   ├── CustomUserDetails.java
│   ├── JwtAuthFilter.java
│   ├── JwtUtils.java
│   └── UserDetailsServiceImpl.java
└── service/
    ├── AuthService.java (+ impl)
    ├── CartService.java (+ impl)
    ├── CategoryService.java (+ impl)
    ├── FileStorageService.java (+ impl)
    ├── OrderService.java (+ impl)
    └── ProductService.java (+ impl)
```

## ⚙️ Prerequisites

- **Java 17+**
- **Maven 3.6+**
- **MySQL 8+**

## 🛠️ Setup Instructions

### 1. Clone the Repository

```bash
git clone <repository-url>
cd fashion-store
```

### 2. Configure MySQL

Create a MySQL database (or let Spring auto-create it):

```sql
CREATE DATABASE fashionstore_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Update `application.yml`

Edit `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/fashionstore_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
    username: your_mysql_username
    password: your_mysql_password

razorpay:
  key:
    id: your_razorpay_key_id       # From Razorpay Dashboard
    secret: your_razorpay_key_secret
```

### 4. Build and Run

```bash
mvn clean install
mvn spring-boot:run
```

The application starts at **http://localhost:8080**

### 5. Initial Data (Auto-loaded)

On first startup, `DataLoader` automatically seeds:

| Resource | Details |
|----------|---------|
| Roles | `ROLE_ADMIN`, `ROLE_CUSTOMER` |
| Admin User | `admin@fashionstore.com` / `Admin@123` |
| Categories | Men, Women, Kids, Accessories |
| Products | 4 sample products |

## 🔑 API Authentication

The API uses **JWT Bearer tokens**. After login, include the token in all protected requests:

```
Authorization: Bearer <your_jwt_token>
```

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@fashionstore.com","password":"Admin@123"}'
```

### Register

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john",
    "email": "john@example.com",
    "password": "Pass@123",
    "firstName": "John",
    "lastName": "Doe",
    "phone": "9876543210"
  }'
```

## 📡 API Endpoints

### Public Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/login` | Login and get JWT |
| POST | `/api/auth/register` | Register new user |
| GET | `/api/products` | List products (paginated, filterable) |
| GET | `/api/products/{id}` | Get product by ID |
| GET | `/api/products/search?keyword=` | Search products |
| GET | `/api/categories` | List all categories |
| GET | `/api/categories/{id}` | Get category by ID |

### Product Filters (Query Params)

```
GET /api/products?pageNo=0&pageSize=12&sortBy=price&sortDir=asc
                 &categoryId=1&minPrice=100&maxPrice=1000&keyword=shirt
```

### Authenticated Endpoints (Customers + Admins)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/cart` | View cart |
| POST | `/api/cart/add` | Add item to cart |
| PUT | `/api/cart/items/{id}?quantity=2` | Update cart item |
| DELETE | `/api/cart/items/{id}` | Remove cart item |
| DELETE | `/api/cart/clear` | Clear entire cart |
| POST | `/api/orders/place` | Place order |
| GET | `/api/orders/my-orders` | Get my orders |
| GET | `/api/orders/{id}` | Get order by ID |
| DELETE | `/api/orders/{id}/cancel` | Cancel order |
| POST | `/api/payment/create-order` | Create Razorpay order |
| POST | `/api/payment/verify` | Verify payment signature |

### Admin-Only Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/admin/products` | Create product |
| PUT | `/api/admin/products/{id}` | Update product |
| DELETE | `/api/admin/products/{id}` | Delete product |
| POST | `/api/admin/products/{id}/image` | Upload product image |
| POST | `/api/admin/categories` | Create category |
| PUT | `/api/admin/categories/{id}` | Update category |
| DELETE | `/api/admin/categories/{id}` | Delete category |
| GET | `/api/admin/orders` | List all orders |
| PUT | `/api/admin/orders/{id}/status` | Update order status |

## 💳 Payment Flow (Razorpay)

1. **Create Order**: `POST /api/payment/create-order` with `{"amount": 1299, "currency": "INR"}`
2. **Frontend**: Open Razorpay checkout with the `orderId` returned
3. **Verify**: `POST /api/payment/verify` with `{razorpay_order_id, razorpay_payment_id, razorpay_signature}`
4. **Place Order**: Use `paymentId` in `POST /api/orders/place`

## 📷 File Uploads

Product images are stored in the `uploads/` directory relative to the working directory.

```bash
curl -X POST http://localhost:8080/api/admin/products/1/image \
  -H "Authorization: Bearer <token>" \
  -F "file=@product.jpg"
```

Uploaded images are stored as UUID-named files (e.g., `a1b2c3d4-....jpg`). The `imageUrl` field in ProductResponse contains the filename.

## 🏥 Health Check

```
GET http://localhost:8080/actuator/health
```

## 🔒 Security Configuration

- **Public**: Auth endpoints, GET products/categories, static resources
- **Authenticated**: Cart, Orders, Payment
- **Admin only**: All `/api/admin/**` endpoints
- CORS configured to allow all origins (suitable for development)
- Passwords are BCrypt-encoded
- JWT tokens expire after 24 hours

## 🗃️ Entity Relationships

```
User (1) ──── (1) Cart (1) ──── (N) CartItem (N) ──── (1) Product
User (1) ──── (N) Order (1) ──── (N) OrderItem (N) ──── (1) Product
Category (1) ──── (N) Product
User (N) ──── (N) Role
```

## 📝 Sample Requests

### Add to Cart

```json
POST /api/cart/add
{
  "productId": 1,
  "quantity": 2,
  "size": "M",
  "color": "White"
}
```

### Place Order

```json
POST /api/orders/place
{
  "shippingAddress": "123 Main St, Mumbai, Maharashtra 400001",
  "paymentId": "pay_abc123xyz",
  "cartItemIds": [1, 2]
}
```

### Update Order Status (Admin)

```json
PUT /api/admin/orders/1/status
{
  "status": "SHIPPED"
}
```

Order statuses: `PLACED` → `CONFIRMED` → `SHIPPED` → `DELIVERED` (or `CANCELLED`)
