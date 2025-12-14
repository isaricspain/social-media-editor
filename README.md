# Social Media Editor

A web application for editing social media content with secure authentication.

## Project Structure

```
social-media-editor/
├── backend/          # Spring Boot backend
├── frontend/         # React frontend
├── CLAUDE.md         # Development instructions for Claude Code
└── README.md         # This file
```

## Quick Start

### Backend (Spring Boot)

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Run the Spring Boot application:
   ```bash
   mvn spring-boot:run
   ```

   The backend will start on http://localhost:8080

### Frontend (React)

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the React development server:
   ```bash
   npm start
   ```

   The frontend will start on http://localhost:3000

## Login Credentials

For testing purposes, a default user is created:
- Username: `testuser`
- Password: `password123`

## Features

- **Secure Login**: Username/password authentication with JWT tokens
- **Material-UI Design**: Modern, responsive login interface
- **BCrypt Password Hashing**: Secure password storage
- **CORS Enabled**: Frontend and backend communication
- **H2 Database**: In-memory database for development
- **Registration Support**: New user registration functionality

## API Endpoints

- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration

## Technologies Used

### Backend
- Spring Boot 3.2.0
- Spring Security
- Spring Data JPA
- H2 Database
- JWT (JSON Web Tokens)
- BCrypt

### Frontend
- React 18
- Material-UI (MUI)
- Axios for API calls