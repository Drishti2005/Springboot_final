# B2B Wholesale Management System

A full-stack B2B platform built with **Spring Boot 3.2**, **PostgreSQL**, and **React 18**.

---

## Architecture

```
backend/   → Spring Boot (Java 17)
frontend/  → React + Redux + Material UI
docs/      → CSV templates, API docs
```

---

## Module Ownership

| Member | Module | Key Files |
|--------|--------|-----------|
| 1 | Pricing Engine & Catalog | `PricingEngine.java`, `Product.java`, `TieredPrice.java` |
| 2 | Credit System & Checkout | `CheckoutService.java`, `InvoiceService.java`, `RetailerProfile.java` |
| 3 | Bulk Upload & Inventory | `BulkUploadService.java`, `BulkUploadController.java` |
| 4 | React Frontend & State | `WholesaleCart.jsx`, `cartSlice.js`, `ProductCatalog.jsx` |

---

## Quick Start

### 1. Database
```sql
CREATE DATABASE bulk_order_db;
```

### 2. Backend
```bash
# Update src/main/resources/application.properties with your DB credentials
./mvnw spring-boot:run
```

### 3. Frontend
```bash
cd frontend
npm install
npm start
```

### 4. Swagger UI
Open: http://localhost:8080/swagger-ui.html

---

## Key Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/auth/register` | Register Retailer/Wholesaler |
| POST | `/api/auth/login` | Login → JWT |
| PATCH | `/api/admin/approve/{id}` | Admin approves Retailer |
| GET | `/api/pricing/quote?productId=1&quantity=100` | Tiered price quote |
| POST | `/api/orders/checkout` | Place order (credit check + invoice) |
| GET | `/api/orders/last` | Last order (Quick Re-order) |
| GET | `/api/orders/{id}/invoice` | Download PDF invoice |
| POST | `/api/bulk/upload` | CSV bulk order upload |

---

## Tiered Pricing Example

| Tier | Min Qty | Unit Price |
|------|---------|------------|
| Base | 1 | ₹12.00 |
| Tier 1 | 50 | ₹10.50 |
| Tier 2 | 100 | ₹9.00 |

---

## Security Flow

1. Retailer registers → `is_approved = false`
2. Admin calls `PATCH /api/admin/approve/{id}`
3. Until approved, `JwtAuthenticationFilter` blocks `/api/pricing/**` with HTTP 403
4. Frontend shows "Pending Approval" banner and hides all prices

---

## CSV Bulk Upload Format

See `docs/bulk-upload-template.csv`:
```
SKU,Quantity
PROD-001,100
PROD-002,50
```
