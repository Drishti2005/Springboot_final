# 🚀 Team Git Contribution Guide
## B2B Wholesale Management System

> **4 Members:** Drishti · Ashish · Aryaman · S  
> Each member works on their own branch and pushes only their assigned folder/files.

---

## 📋 Module Assignment

| Member | Role | Branch Name | Assigned Files/Folders |
|--------|------|-------------|------------------------|
| **Drishti** | Core Pricing Engine & Catalog | `feature/drishti-pricing-engine` | Entities, Repositories, Pricing Logic |
| **Ashish** | Credit System & Checkout + Invoice | `feature/ashish-checkout-invoice` | Checkout, Invoice, Credit Management |
| **Aryaman** | Bulk Upload & Security | `feature/aryaman-bulk-security` | Bulk Upload, JWT Security, Auth |
| **S** | React Frontend & State | `feature/s-react-frontend` | Entire `frontend/` folder |

---

## 🔧 One-Time Setup (All Members)

Everyone does this **once** before starting:

```bash
# 1. Clone the repository
git clone https://github.com/<your-org>/BulkOrderManagement.git
cd BulkOrderManagement

# 2. Set your identity
git config user.name "Your Name"
git config user.email "your@email.com"

# 3. Make sure you are on main and it is up to date
git checkout main
git pull origin main
```

---

---

## 👩‍💻 Member 1 — Drishti
### Role: Core Pricing Engine & Product Catalog

**Assigned Files:**
```
src/main/java/trivedi/gmail/com/BulkOrderManagement/entity/
    ├── Product.java
    ├── TieredPrice.java
    ├── OrderItem.java
    ├── Order.java
    ├── OrderStatus.java
    ├── Role.java
    └── User.java

src/main/java/trivedi/gmail/com/BulkOrderManagement/repository/
    ├── ProductRepository.java
    └── TieredPriceRepository.java

src/main/java/trivedi/gmail/com/BulkOrderManagement/service/
    └── PricingEngine.java

src/main/java/trivedi/gmail/com/BulkOrderManagement/controller/
    └── ProductController.java

src/main/java/trivedi/gmail/com/BulkOrderManagement/dto/
    ├── PriceQuoteRequest.java
    └── PriceQuoteResponse.java

src/main/java/trivedi/gmail/com/BulkOrderManagement/exception/
    └── MinimumOrderException.java
```

**Git Commands:**

```bash
# Step 1: Create and switch to your branch
git checkout main
git pull origin main
git checkout -b feature/drishti-pricing-engine

# Step 2: Stage only your assigned files
git add src/main/java/trivedi/gmail/com/BulkOrderManagement/entity/Product.java
git add src/main/java/trivedi/gmail/com/BulkOrderManagement/entity/TieredPrice.java
git add src/main/java/trivedi/gmail/com/BulkOrderManagement/entity/OrderItem.java
git add src/main/java/trivedi/gmail/com/BulkOrderManagement/entity/Order.java
git add src/main/java/trivedi/gmail/com/BulkOrderManagement/entity/OrderStatus.java
git add src/main/java/trivedi/gmail/com/BulkOrderManagement/entity/Role.java
git add src/main/java/trivedi/gmail/com/BulkOrderManagement/entity/User.java
git add src/main/java/trivedi/gmail/com/BulkOrderManagement/repository/ProductRepository.java
git add src/main/java/trivedi/gmail/com/BulkOrderManagement/repository/TieredPriceRepository.java
git add src/main/java/trivedi/gmail/com/BulkOrderManagement/service/PricingEngine.java
git add src/main/java/trivedi/gmail/com/BulkOrderManagement/controller/ProductController.java
git add src/main/java/trivedi/gmail/com/BulkOrderManagement/dto/PriceQuoteRequest.java
git add src/main/java/trivedi/gmail/com/BulkOrderManagement/dto/PriceQuoteResponse.java
git add src/main/java/trivedi/gmail/com/BulkOrderManagement/exception/MinimumOrderException.java

# Step 3: Commit with a clear message
git commit -m "feat(pricing): add tiered pricing engine, product catalog entities and repository

- Product entity with MOQ enforcement
- TieredPrice entity with One-to-Many relationship to Product
- PricingEngine service: resolves unit price based on quantity tiers
- MinimumOrderException thrown when quantity < MOQ
- ProductController: CRUD endpoints for product catalog
- PriceQuoteRequest/Response DTOs"

# Step 4: Push your branch to GitHub
git push -u origin feature/drishti-pricing-engine

# Step 5: Open a Pull Request on GitHub
# Go to: https://github.com/<your-org>/BulkOrderManagement
# Click "Compare & pull request" for branch feature/drishti-pricing-engine
# Title: "feat: Tiered Pricing Engine & Product Catalog"
# Assign reviewers if needed, then click "Create pull request"
```

---

---

## 👨‍💻 Member 2 — Ashish
### Role: Credit System, Checkout & Invoice Generation

**Assigned Files:**
```
src/main/java/trivedi/gmail/com/BulkOrderManagement/entity/
    ├── RetailerProfile.java
    └── Invoice.java

src/main/java/trivedi/gmail/com/BulkOrderManagement/repository/
    ├── RetailerProfileRepository.java
    ├── OrderRepository.java
    └── InvoiceRepository.java

src/main/java/trivedi/gmail/com/BulkOrderManagement/service/
    ├── CheckoutService.java
    └── InvoiceService.java

src/main/java/trivedi/gmail/com/BulkOrderManagement/controller/
    └── OrderController.java

src/main/java/trivedi/gmail/com/BulkOrderManagement/dto/
    ├── CheckoutRequest.java
    └── OrderResponse.java

src/main/java/trivedi/gmail/com/BulkOrderManagement/exception/
    ├── CreditLimitExceededException.java
    └── InsufficientStockException.java
```

**Git Commands:**

```bash
# Step 1: Create and switch to your branch
git checkout main
git pull origin main
git checkout -b feature/ashish-checkout-invoice

# Step 2: Stage only your assigned files
git add src/main/java/trivedi/gmail/com/BulkOrderManagement/entity/RetailerProfile.java
git add src/main/java/trivedi/gmail/com/BulkOrderManagement/entity/Invoice.java
git add src/main/java/trivedi/gmail/com/BulkOrderManagement/repository/RetailerProfileRepository.java
git add src/main/java/trivedi/gmail/com/BulkOrderManagement/repository/OrderRepository.java
git add src/main/java/trivedi/gmail/com/BulkOrderManagement/repository/InvoiceRepository.java
git add src/main/java/trivedi/gmail/com/BulkOrderManagement/service/CheckoutService.java
git add src/main/java/trivedi/gmail/com/BulkOrderManagement/service/InvoiceService.java
git add src/main/java/trivedi/gmail/com/BulkOrderManagement/controller/OrderController.java
git add src/main/java/trivedi/gmail/com/BulkOrderManagement/dto/CheckoutRequest.java
git add src/main/java/trivedi/gmail/com/BulkOrderManagement/dto/OrderResponse.java
git add src/main/java/trivedi/gmail/com/BulkOrderManagement/exception/CreditLimitExceededException.java
git add src/main/java/trivedi/gmail/com/BulkOrderManagement/exception/InsufficientStockException.java

# Step 3: Commit with a clear message
git commit -m "feat(checkout): add credit system, checkout flow and PDF invoice generation

- RetailerProfile entity with creditLimit and outstandingBalance
- CheckoutService: validates stock, enforces credit limit, places order
- InvoiceService: generates PDF invoice using iText7 with GST breakdown
- OrderController: checkout, order history, quick re-order, invoice download
- CreditLimitExceededException and InsufficientStockException
- OrderResponse DTO to avoid JSON serialization issues"

# Step 4: Push your branch to GitHub
git push -u origin feature/ashish-checkout-invoice

# Step 5: Open a Pull Request on GitHub
# Go to: https://github.com/<your-org>/BulkOrderManagement
# Click "Compare & pull request" for branch feature/ashish-checkout-invoice
# Title: "feat: Credit System, Checkout & PDF Invoice Generation"
# Click "Create pull request"
```

---

---

## 👨‍💻 Member 3 — Aryaman
### Role: Bulk Upload, JWT Security & Authentication

**Assigned Files:**
```
src/main/java/trivedi/gmail/com/BulkOrderManagement/security/
    ├── SecurityConfig.java
    ├── JwtTokenProvider.java
    ├── JwtAuthenticationFilter.java
    └── CustomUserDetailsService.java

src/main/java/trivedi/gmail/com/BulkOrderManagement/service/
    ├── AuthService.java
    └── BulkUploadService.java

src/main/java/trivedi/gmail/com/BulkOrderManagement/controller/
    ├── AuthController.java
    ├── AdminController.java
    ├── BulkUploadController.java
    └── WelcomeController.java

src/main/java/trivedi/gmail/com/BulkOrderManagement/config/
    └── OpenApiConfig.java

src/main/java/trivedi/gmail/com/BulkOrderManagement/dto/
    ├── RegisterRequest.java
    ├── LoginRequest.java
    ├── AuthResponse.java
    └── BulkUploadResult.java

src/main/java/trivedi/gmail/com/BulkOrderManagement/exception/
    ├── ResourceNotFoundException.java
    └── GlobalExceptionHandler.java

src/main/java/trivedi/gmail/com/BulkOrderManagement/
    └── BulkOrderManagementApplication.java

src/main/resources/
    └── application.properties

pom.xml
docs/bulk-upload-template.csv
```

**Git Commands:**

```bash
# Step 1: Create and switch to your branch
git checkout main
git pull origin main
git checkout -b feature/aryaman-bulk-security

# Step 2: Stage only your assigned files
git add src/main/java/trivedi/gmail/com/BulkOrderManagement/security/
git add src/main/java/trivedi/gmail/com/BulkOrderManagement/service/AuthService.java
git add src/main/java/trivedi/gmail/com/BulkOrderManagement/service/BulkUploadService.java
git add src/main/java/trivedi/gmail/com/BulkOrderManagement/controller/AuthController.java
git add src/main/java/trivedi/gmail/com/BulkOrderManagement/controller/AdminController.java
git add src/main/java/trivedi/gmail/com/BulkOrderManagement/controller/BulkUploadController.java
git add src/main/java/trivedi/gmail/com/BulkOrderManagement/controller/WelcomeController.java
git add src/main/java/trivedi/gmail/com/BulkOrderManagement/config/OpenApiConfig.java
git add src/main/java/trivedi/gmail/com/BulkOrderManagement/dto/RegisterRequest.java
git add src/main/java/trivedi/gmail/com/BulkOrderManagement/dto/LoginRequest.java
git add src/main/java/trivedi/gmail/com/BulkOrderManagement/dto/AuthResponse.java
git add src/main/java/trivedi/gmail/com/BulkOrderManagement/dto/BulkUploadResult.java
git add src/main/java/trivedi/gmail/com/BulkOrderManagement/exception/ResourceNotFoundException.java
git add src/main/java/trivedi/gmail/com/BulkOrderManagement/exception/GlobalExceptionHandler.java
git add src/main/java/trivedi/gmail/com/BulkOrderManagement/BulkOrderManagementApplication.java
git add src/main/resources/application.properties
git add pom.xml
git add docs/bulk-upload-template.csv

# Step 3: Commit with a clear message
git commit -m "feat(security): add JWT auth, bulk CSV upload, admin approval workflow

- Spring Security config with stateless JWT sessions
- JwtTokenProvider: HS256 token generation and validation
- JwtAuthenticationFilter: blocks unapproved retailers from pricing APIs
- AuthController: register and login endpoints
- AdminController: pending retailer list and approval endpoint
- BulkUploadService: CSV parsing with SKU/stock/MOQ validation
- BulkUploadController: multipart CSV file upload endpoint
- GlobalExceptionHandler: structured JSON error responses
- OpenAPI/Swagger configuration
- pom.xml with all dependencies (JWT, iText7, OpenCSV, POI, Springdoc)"

# Step 4: Push your branch to GitHub
git push -u origin feature/aryaman-bulk-security

# Step 5: Open a Pull Request on GitHub
# Go to: https://github.com/<your-org>/BulkOrderManagement
# Click "Compare & pull request" for branch feature/aryaman-bulk-security
# Title: "feat: JWT Security, Bulk CSV Upload & Admin Approval"
# Click "Create pull request"
```

---

---

## 👨‍💻 Member 4 — S
### Role: React Frontend & Redux State Management

**Assigned Files:**
```
frontend/
    ├── package.json
    ├── public/
    │   ├── index.html
    │   └── bulk-template.csv
    └── src/
        ├── index.js
        ├── index.css
        ├── App.js
        ├── api/
        │   └── axiosConfig.js
        ├── store/
        │   ├── index.js
        │   └── slices/
        │       ├── authSlice.js
        │       └── cartSlice.js
        ├── components/
        │   ├── Navbar.jsx
        │   └── Navbar.module.css
        └── pages/
            ├── LoginPage.jsx
            ├── RegisterPage.jsx
            ├── Auth.module.css
            ├── PendingApproval.jsx
            ├── PendingApproval.module.css
            ├── AdminDashboard.jsx
            ├── AdminDashboard.module.css
            ├── RetailerDashboard.jsx
            ├── RetailerDashboard.module.css
            ├── WholesalerDashboard.jsx
            └── WholesalerDashboard.module.css
```

**Git Commands:**

```bash
# Step 1: Create and switch to your branch
git checkout main
git pull origin main
git checkout -b feature/s-react-frontend

# Step 2: Stage the entire frontend folder
git add frontend/

# Step 3: Commit with a clear message
git commit -m "feat(frontend): add complete React frontend with Redux state management

- LoginPage and RegisterPage with form validation
- PendingApproval page for unapproved retailer accounts
- AdminDashboard: pending approvals, product catalog view, add product form
- RetailerDashboard: product catalog, real-time tiered pricing cart,
  checkout with GST breakdown, order history, invoice download,
  Quick Re-order button, CSV bulk upload
- WholesalerDashboard: product management and inventory view
- Redux store: authSlice (JWT persistence) and cartSlice (live pricing)
- Axios interceptors for JWT injection and 401 handling
- Role-based routing (ADMIN / WHOLESALER / RETAILER)
- CSS Modules for scoped styling throughout"

# Step 4: Push your branch to GitHub
git push -u origin feature/s-react-frontend

# Step 5: Open a Pull Request on GitHub
# Go to: https://github.com/<your-org>/BulkOrderManagement
# Click "Compare & pull request" for branch feature/s-react-frontend
# Title: "feat: Complete React Frontend with Redux & Role-based Routing"
# Click "Create pull request"
```

---

---

## 🔄 Keeping Your Branch Up to Date

If `main` gets new commits while you are working, sync your branch:

```bash
git checkout main
git pull origin main
git checkout feature/your-branch-name
git merge main
# Fix any merge conflicts, then:
git push origin feature/your-branch-name
```

---

## ✅ Pull Request Checklist

Before opening a PR, make sure:

- [ ] Your branch is up to date with `main`
- [ ] You only staged **your assigned files** (run `git status` to check)
- [ ] The commit message clearly describes what you built
- [ ] The project builds without errors (`./mvnw clean package -DskipTests`)
- [ ] You have not committed `node_modules/`, `.env`, or `target/` folders

---

## 🌿 Branch Overview

```
main
├── feature/drishti-pricing-engine     ← Drishti  (Entities + Pricing Engine)
├── feature/ashish-checkout-invoice    ← Ashish   (Checkout + Invoice PDF)
├── feature/aryaman-bulk-security      ← Aryaman  (JWT Security + Bulk Upload)
└── feature/s-react-frontend           ← S        (React UI + Redux)
```

---

## 📌 Quick Reference — Most Used Git Commands

```bash
git status                        # See what files are changed
git branch                        # List all local branches
git branch -a                     # List all branches including remote
git log --oneline -10             # See last 10 commits
git diff filename.java            # See changes in a file before staging
git restore filename.java         # Undo changes to a file (before commit)
git stash                         # Temporarily save uncommitted changes
git stash pop                     # Restore stashed changes
```

---

*Generated for B2B Wholesale Management System — Hackathon Project*
