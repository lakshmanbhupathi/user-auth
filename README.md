# User Authentication with 2FA

A comprehensive Spring Boot authentication system with Email OTP and Google Authenticator 2FA, session management, and request auditing.

## Features

- **User Registration & Login** with email verification
- **Two-Factor Authentication (2FA)**:
    - Email OTP (mandatory)
    - Google Authenticator (optional)
- **Session Management**:
    - JWT-based authentication
    - Automatic session cleanup after 30 minutes of inactivity
    - Session history tracking
- **Request Auditing**: Track all API requests with detailed information
- **User Profile Management**
- **Docker Support**: Fully containerized with Docker Compose

## Technologies

- Java 21
- Spring Boot 3.5.7
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT (JSON Web Tokens)
- Google Authenticator (TOTP)
- Docker & Docker Compose
- Maven

## Prerequisites

- Docker & Docker Compose
- (Optional) Java 21 and Maven for local development


## Quick Start

1. **Clone the repository**
   
   cd user-auth
   

2. **Start the application**
   
   docker-compose up --build
   

3. **Access the application**
    - API: http://localhost:8080
    - PostgreSQL: localhost:5432

4. **Import Postman Collection**
    - Import `User_Auth_with_2FA.postman_collection.json` into Postman
    - The collection includes all API endpoints and test credentials

5. **Stopping the Application**

docker-compose down

## Test Credentials

Two test users are automatically created on startup:

#### User 1
- **Username**: testuser1
- **Email**: test1@example.com
- **Password**: Test123!

#### User 2
- **Username**: testuser2
- **Email**: test2@example.com
- **Password**: Test123!

## Authentication Flow

### Signup Flow
1. **POST** `/api/auth/signup` - Register with username, email, password
2. Check console logs for Email OTP
3. **POST** `/api/auth/verify-signup-otp` - Verify OTP to activate account

### Login Flow (without Google Authenticator)
1. **POST** `/api/auth/login` - Login with username and password
2. Check console logs for Email OTP
3. **POST** `/api/auth/verify-login-otp` - Verify OTP to get session token
4. Use the token in `Authorization: Bearer <token>` header for subsequent requests

### Login Flow (with Google Authenticator enabled)
1. **POST** `/api/auth/login` - Login with username and password
2. Check console logs for Email OTP
3. **POST** `/api/auth/verify-login-otp` - Verify OTP
4. **POST** `/api/auth/verify-gauth` - Verify 6-digit code from Google Authenticator app
5. Use the token in `Authorization: Bearer <token>` header for subsequent requests

### Setup Google Authenticator
1. Login and get session token
2. **POST** `/api/auth/setup-gauth` - Get QR code URL and secret key
3. Scan QR code with Google Authenticator app
4. **POST** `/api/auth/enable-gauth?code=<6-digit-code>` - Verify and enable GAuth

### TODO
- Migrate Flyway instead of CommandLineRunner
- Async RequestAudit and SessionHistory 
- Move Schedular properties to configurable
- [low] Break-up AuthenticationController and AuthenticationService
- Mock OTP into Email Integration
- Tests

