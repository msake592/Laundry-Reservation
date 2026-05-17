# Laundry Reservation System

A simple role-based laundry reservation system built with Spring Boot.

## Current Tech Stack

- Java 17+
- Spring Boot 3
- Spring Security
- JWT Authentication
- Spring Data JPA / Hibernate
- Maven
- Basic HTML, CSS, and JavaScript frontend
- Postman for API testing

## Current Features

- User registration with BCrypt password hashing
- JWT-based login
- Role-based access control with ADMIN and USER roles
- Admin-authorized machine management
- Machine listing
- Reservation creation
- Reservation listing
- Start/end time validation
- Time conflict validation

## Roles

### ADMIN

- Create machines
- View machines
- Create reservations
- View reservations

### USER

- View machines
- Create reservations
- View reservations

## Authentication

JWT authentication is used.

After login, the backend returns a token. This token must be sent in the `Authorization` header for protected endpoints:

```http
Authorization: Bearer <token>
