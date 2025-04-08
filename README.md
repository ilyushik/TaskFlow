# 🧩 TaskFlow

A distributed system built with **Spring Boot**, **MySQL**, **Apache Kafka**, and **AWS**, focused on task, project, user, and notification management.

---

## 📦 Microservices

### 1. 🔐 Auth Service
Responsible for user authentication and authorization.  
**Features:**
- User registration and login
- Role-based access control (`USER`, `ADMIN`)
- JWT-based authorization across all services

---

### 2. ✅ Task Service
Manages user tasks.  
**Features:**
- Create, update, delete, and view tasks
- Task statuses: `TODO`, `IN_PROGRESS`, `DONE`
- Task-to-project association

---

### 3. 📁 Project Service
Handles project creation and organization.  
**Features:**
- Create and manage projects
- Set deadlines and priorities
- Add project members

---

### 4. 🔔 Notification Service
Sends alerts and reminders to users.  
**Features:**
- Notify users about due tasks or project updates
- Supports email and push notifications
- Track read/unread status

---

### 5. 📊 Reporting Service
Generates reports on tasks and projects.  
**Features:**
- Task performance reports over specific periods
- Project and user statistics
- JSON-based output

---

## 🛠️ Tech Stack

<div align="center">
  <img height="64" width="64" src="https://cdn.simpleicons.org/Spring/94d31b" title="Spring Boot" />
  <img height="64" width="64" src="https://cdn.simpleicons.org/mysql/4479A1" title="MySQL" />
  <img height="64" width="64" src="https://cdn.simpleicons.org/amazonwebservices/232F3E" title="AWS" />
  <img height="64" width="64" src="https://cdn.simpleicons.org/apachekafka/231F20" title="Apache Kafka" />
  <img height="64" width="64" src="https://cdn.simpleicons.org/swagger/85EA2D" title="swagger" />
</div>

- **Spring Boot** — REST API development
- **MySQL** — Relational data storage
- **Apache Kafka** — Asynchronous communication between services
- **AWS** — For storage, email, and cloud infrastructure
- **Swagger/OpenAPI** — Auto-generated and interactive API documentation


