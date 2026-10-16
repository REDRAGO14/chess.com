# ♟️ Multiplayer Chess Platform

A real-time multiplayer chess platform built with **Spring Boot**, **MySQL**, **WebSocket/STOMP**, **JWT authentication**, wallet/escrow, and **Chapa payments**.

> **Status:** Week 1 — Backend Foundation

## 🧭 Navigation

- [Overview](#-overview)
- [Features](#-features)
- [Architecture](#-architecture)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Roadmap](#-roadmap)
- [Getting Started](#-getting-started)
- [Git Workflow](#-git-workflow)
- [Contributing](#-contributing)
- [Team](#-team)

## 📌 Overview

The platform is being developed as a modular Spring Boot application for online multiplayer chess.

The current backend starts with a simple layered structure and will grow as new features are implemented. 

## ✨ Features

### Current Foundation
- User management
- DTO-based requests and responses
- Repository layer with JPA
- Service layer
- REST API foundation

### Planned
- JWT authentication and authorization
- Chess rules and move validation
- Real-time multiplayer with WebSocket/STOMP
- Matchmaking
- Wallet and transactions
- Escrow
- Chapa payments
- Manual receipt upload and admin verification
- Docker deployment

## 🏗️ Architecture

The project starts with a simple layered architecture:

```text
Client
  ↓
Controller
  ↓
DTO
  ↓
Service
  ↓
Repository
  ↓
MySQL
```

### Current Structure

```text
com.game.chess/
├── Controller/
│   └── UserController
├── DTO/
│   ├── UserRequest
│   └── UserResponse
├── Model/
│   └── User
├── Repository/
│   └── UserRepository
├── Service/
└── ChessApplication
```

### Planned Modules

As development progresses, additional modules will be introduced:

```text
com.game.chess/
├── Controller/
├── DTO/
├── Model/
├── Repository/
├── Service/
├── Security/       # JWT & authorization
├── Chess/          # Chess rules & game logic
├── WebSocket/      # Real-time gameplay
├── Matchmaking/    # Finding players
├── Wallet/         # Wallet & transactions
├── Payment/        # Chapa & payment handling
├── Admin/          # Administrative verification
└── ChessApplication
```

These modules will be added when their corresponding features are implemented.

## 🛠️ Tech Stack

| Technology | Purpose |
|---|---|
| Java 17+ | Backend |
| Spring Boot | Backend framework |
| Spring Security + JWT | Authentication |
| Spring Data JPA | Database access |
| MySQL | Database |
| WebSocket + STOMP | Real-time gameplay |
| Chapa | Payments |
| Docker | Containerization |
| Maven | Build tool |

## 🗃️ Core Domain

The main chess domain will grow around:

```text
User
  │
  ▼
Game
  │
  ▼
Move History
```

Later, wallet, transaction, escrow, payment, and admin records will be added.

## 🗺️ Roadmap

### Week 1 — Foundation
- MySQL configuration
- User, Game, and Move entities
- Entity relationships
- DTO validation
- Global exception handling
- Basic REST APIs

### Week 2 — Authentication
- Spring Security
- Password hashing
- JWT authentication
- Roles and authorization

### Week 3 — Chess Logic
- Board representation
- Move validation
- Check and checkmate
- Game state
- Game history

### Week 4 — Real-Time Multiplayer
- WebSocket/STOMP
- Matchmaking
- Game rooms
- Live moves
- Connection handling

### Week 5 — Wallet & Payments
- Wallet and transactions
- Escrow
- Chapa integration
- Receipt upload
- Admin verification
- Docker/deployment preparation

## 🚀 Getting Started

### Requirements

- Java 17+
- Maven
- MySQL
- Git
- Docker (optional)

### Clone

```bash
git clone <repository-url>
cd chess-platform
```

### Database

Create the database:

```sql
CREATE DATABASE chess_platform;
```

Configure local application properties:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/chess_platform
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
spring.jpa.hibernate.ddl-auto=update
```

> Never commit passwords, JWT secrets, API keys, or payment credentials.

### Run

Linux/macOS:

```bash
./mvnw spring-boot:run
```

Windows:

```bash
mvnw.cmd spring-boot:run
```

## 🔀 Git Workflow

Use a branch for each developer or feature:

```text
master
├── redragon
├── ananya
└── biruk
```

Example:

```bash
git checkout -b feature-name
git add .
git commit -m "feat: add game entity"
git push origin feature-name
```

Open a Pull Request, review the changes, and merge into `master` after approval.

### Commit Style

```text
feat: add user registration
fix: correct game validation
refactor: simplify game service
docs: update README
test: add game service tests
```

## 🤝 Contributing

1. Pick or create a task.
2. Work on a dedicated branch.
3. Keep changes focused.
4. Test your changes.
5. Open a Pull Request.
6. Review the code with the team.
7. Merge after approval.

Team members should rotate responsibilities and understand the major parts of the backend.

## 👥 Team

| Member | Role |
|---|---|
| Redragon | Backend Developer |
| Ananya | Backend Developer |
| Biruk | Backend Developer |

## 📚 Documentation

Project roadmap:

https://chessprojectroadmap.vercel.app/

Recommended documentation structure:

```text
docs/
├── architecture/
├── api/
└── development/
```

## 📄 License

