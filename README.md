# Laundry Management System

A role-based laundry machine and reservation management system built with Spring Boot and React.

## Tech Stack

### Backend

- Java 17+
- Spring Boot 3
- Spring Security
- JWT Authentication
- Spring Data JPA / Hibernate
- Maven

### Frontend

- React
- Vite
- Axios
- CSS

### Testing

- Postman for API testing

## Features

- User registration with BCrypt password hashing
- JWT-based login
- Role-based access control with ADMIN and USER roles
- Machine listing
- Admin-authorized machine management
- Reservation creation
- Reservation listing
- Reservation deletion with role and ownership checks
- Start/end time validation
- Time conflict validation
- Separate dashboard behavior for ADMIN and USER

## Roles and Permissions

### ADMIN

- View machines
- Create machines
- Update machine status
- Delete machines
- View all reservations
- Create reservations
- Delete any reservation

### USER

- View available machines
- Create reservations
- View all reservations
- Delete only their own reservations

## Authentication

JWT authentication is used.

After login, the backend returns a token. This token must be sent in the `Authorization` header for protected endpoints:

Authorization: Bearer <token>

The frontend stores the token after login and sends it automatically with protected API requests.

Reservation Rules

* Users can view all reservations.
* Users can delete only their own reservations.
* Admins can delete any reservation.
* Reservations cannot overlap for the same machine.
* Reservation end time must be after start time.
* Reservation start time must be in the future.


Project Structure
src/main/java/com/dormlaundry
├── config
├── controller
├── dto
├── exception
├── model
├── repository
├── security
└── service

laundry-frontend
├── src
│   ├── components
│   ├── pages
│   └── services
└── package.json

Development Status

The project currently supports JWT authentication, role-based access control, machine management, and reservation management.

Recent updates include:

* Users can now view all reservations.
* Users can delete only their own reservations.
* Admins can delete any reservation.
* Reservation delete permissions are enforced on the backend.
* User dashboard reservation behavior was updated on the frontend.

## Getting Started

### Prerequisites
Before running the application, make sure you have the following installed:
- Java 17 or higher
- Node.js (v18+ recommended)
- Your preferred SQL Database (e.g., PostgreSQL/MySQL)

### Installation & Running

#### 1. Backend Setup
Clone the repository and navigate to the project root:
```bash
# Navigate to backend (if applicable) or use root
# Update your application.properties / application.yml with your database credentials

# Run the Spring Boot application
./mvnw spring-boot:run
