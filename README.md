# 🌸 Bloomcycle

A full-stack online flower marketplace connecting **Admins**, **Sellers**, and **Customers** — with dynamic freshness-based pricing, festival offers, waste reduction tracking, and more.

## Tech Stack

**Backend:** Java 21, Spring Boot 3, Spring Security (JWT), Spring Data JPA/Hibernate, MySQL, Maven
**Frontend:** React 18, React Router, Axios, Bootstrap 5
**Deployment:** Docker, Docker Compose

## Project Structure

```
flower-marketplace/
├── backend/     Spring Boot REST API
├── frontend/    React SPA
└── docker-compose.yml
```

## Features Implemented

- **Roles:** Admin, Seller, Customer — with JWT auth and Spring Security role-based access control (`ROLE_ADMIN`, `ROLE_SELLER`, `ROLE_CUSTOMER`)
- **Auth:** Register, Login, Forgot Password, Change Password (BCrypt hashing)
- **Shop module:** Create/update shop, logo, banner, description, address, business hours
- **Flower module:** Add/update/delete flowers, multiple images, quantity, unit (Piece/Bunch/Kg), freshness hours, harvest/expiry dates, out-of-stock toggle
- **Dynamic Pricing:** Price automatically decreases as a flower ages toward its freshness window, down to a clearance-sale floor (`util/DynamicPriceCalculator.java`)
- **Market Price:** Admin publishes a daily reference price per flower name; sellers can check it
- **Smart Price Suggestion:** Recommends a price adjustment based on current stock levels
- **Nearby Price Comparison:** Compare the same flower's price across shops
- **Festival Pricing:** Admin-managed festival offers (Valentine's, Pongal, Diwali, etc.)
- **Waste Reduction:** Route expired/unsold stock to Temple Donation, NGO, Compost, Incense Stick Factory, or Natural Color Makers, with reporting
- **Cart & Checkout:** Multi-seller cart splitting into per-seller orders, coupon codes, delivery address, payment method
- **Orders:** Full status lifecycle — Placed → Accepted → Preparing → Packed → Ready → Completed (or Rejected/Cancelled)
- **Wishlist & Reviews:** Save favorite flowers, rate sellers/flowers
- **Notifications:** In-app notifications for order placed/accepted/ready etc.
- **Trending Flowers / Best Seller Badge:** Backed by live order-count ranking
- **Admin Reports:** Daily / Monthly / Yearly sales, Waste Reduction report
- **Dashboards:** Admin (sellers, customers, orders, revenue, top/low-stock flowers) and Seller (revenue, today's/monthly orders, best seller, low stock alerts)
- **Coupons:** Admin-managed coupon codes with usage limits, min order amount, max discount cap

> Note: "AI Recommendation" (collaborative filtering) is listed as a future feature in the spec and is not implemented yet — the schema (order history, flower categories) is already in place to build it on top of.

## Getting Started

### Option A — Docker Compose (recommended)

```bash
docker compose up --build
```

This starts MySQL, the Spring Boot API (port 8080), and the React app (port 3000).

### Option B — Run locally

**Backend:**
```bash
cd backend
# create a MySQL database named flower_marketplace, or let ddl-auto create it
mvn spring-boot:run
```
Edit `src/main/resources/application.properties` to match your local MySQL credentials.

**Frontend:**
```bash
cd frontend
cp .env.example .env
npm install
npm start
```

The app will be available at `http://localhost:3000`, calling the API at `http://localhost:8080/api`.

## First-time setup

1. Register a **Seller** account, then create their shop under "My Shop".
2. Add flowers under "My Flowers" with a harvest date and freshness hours — the current price will update automatically as time passes.
3. Register a **Customer** account to browse, add to cart, and check out.
4. For an **Admin** account, register normally via the API as `SELLER` or `CUSTOMER` is blocked for `ADMIN` by design — insert an admin row directly into the `users` table (with a BCrypt-hashed password) or temporarily relax `AuthService.register()` to seed one, then re-lock it down.

## API Overview

All endpoints are prefixed with `/api`:
- `/auth/**` — public (register, login, forgot/change password)
- `/public/**` — public browsing (flowers, categories, search, compare, trending, festival offers)
- `/admin/**` — requires `ROLE_ADMIN`
- `/seller/**` — requires `ROLE_SELLER`
- `/customer/**` — requires `ROLE_CUSTOMER`

JWT is returned on login/register and must be sent as `Authorization: Bearer <token>` on subsequent requests.

## Extending This Project

- Wire up a real payment gateway (Razorpay/Stripe) in place of the `paymentMethod` string field
- Add image upload handling (multipart) instead of raw image URLs
- Build the "AI Recommendation" feature using `Flower.orderCount` / order history co-occurrence
- Add email/SMS delivery for the forgot-password flow and notifications
- Add pagination to flower/order listing endpoints for scale
