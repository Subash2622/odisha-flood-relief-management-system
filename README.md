# Odisha Flood Relief & NGO Management System

A digital platform for transparent flood relief management, donations, volunteer coordination, and NGO membership.

## Tech Stack

| Layer | Technologies |
|-------|-------------|
| Backend | Java 8, Spring Boot 2.5.x, Spring Security, JWT, JPA, MySQL, Maven |
| Frontend | React, Material UI, React Router, Axios, React Hook Form, Chart.js |
| Database | MySQL 8 |

## Features

- **Role-Based Access**: CEO, Admin, Volunteer, Member, User
- **Donations**: Preset/custom amounts, QR codes, PDF receipts
- **Membership**: Application, approval, digital card, QR code, PDF download
- **Volunteers**: Application, assignment, work tracking, photo uploads
- **Flood Reports**: Citizen reporting with GPS, photos, urgency levels
- **Campaigns**: Create, track progress, close campaigns
- **Relief Distribution**: Inventory tracking for food, medicine, blankets, etc.
- **CEO Dashboard**: Financial analytics, audit logs, PDF/Excel reports
- **Notifications**: In-app notifications (SMS/Email ready structure)

## Project Structure

```
├── backend/          # Spring Boot REST API
├── frontend/         # React SPA
├── database/         # SQL schema
├── postman/          # API collection
├── .gitignore        # Root ignore rules
└── docker-compose.yml
```

## Prerequisites

- Java 8+
- Maven 3.6+
- Node.js 16+
- MySQL 8

## Backend Setup

1. Create MySQL database:
   ```sql
   CREATE DATABASE flood_relief_ngo;
   ```

2. Update `backend/src/main/resources/application.properties` with your DB credentials.

3. Build and run (**from the `backend` folder** so uploads resolve correctly):
   ```bash
   cd backend
   mvn clean install
   mvn spring-boot:run
   ```

4. API root: `http://localhost:8080/api/` (returns API status JSON)
5. Swagger UI: `http://localhost:8080/api/swagger-ui/`

### Default CEO Account

| Field | Value |
|-------|-------|
| Username | `ceo` |
| Password | `ceo123` |

## Frontend Setup

```bash
cd frontend
cp .env.example .env
npm install
npm start
```

Frontend runs at: `http://localhost:3000`

`.env` should contain:
```
REACT_APP_API_URL=http://localhost:8080/api
```

## Docker Setup

```bash
docker-compose up --build
```

- Frontend: http://localhost:3000
- Backend: http://localhost:8080/api
- MySQL: localhost:3306

## GitHub Push (enterprise hygiene)

Do **not** commit secrets, `uploads/`, `node_modules/`, or `target/`. Root, backend, and frontend `.gitignore` files are already configured.

```bash
git init
git add .
git status   # review: no .env, uploads, target, node_modules
git commit -m "Initial commit: Odisha Flood Relief NGO Management System"
git branch -M main
git remote add origin https://github.com/<your-org>/<your-repo>.git
git push -u origin main
```

Before pushing, change default passwords and JWT secret in production.

## Key Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | API status |
| POST | `/auth/register` | Register user |
| POST | `/auth/login` | Login |
| GET | `/campaigns/active` | Active campaigns |
| POST | `/donations/guest` | Guest donation |
| GET | `/donations/{id}/receipt` | Download donation PDF receipt |
| GET | `/donations/{id}/qr` | Donation QR image |
| POST | `/membership/apply` | Apply membership |
| GET | `/membership/my/card` | Download membership card PDF |
| POST | `/volunteers/apply` | Apply volunteer |
| POST | `/flood-reports/public` | Report flood |
| GET | `/dashboard/ceo` | CEO dashboard |
| GET | `/ceo/reports/donations/excel` | Export donations Excel |
| GET | `/ceo/reports/donations/pdf` | Export donations PDF |

## Security

- JWT Bearer token authentication
- BCrypt password encryption
- Role-based endpoint authorization
- CEO-only access to financial data and bank details
- Public access for uploads, receipts, and guest donation flows

## License

MIT
