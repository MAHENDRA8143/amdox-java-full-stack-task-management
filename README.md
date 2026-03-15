# 🚀 Amdox Task Management System

A **complete Java Full-Stack application** for managing tasks efficiently with secure authentication, role-based access, Kanban workflow, real-time collaboration, and deadline tracking.

---

## 📋 Table of Contents

- [Technology Stack](#technology-stack)
- [Features](#features)
- [Database Design](#database-design)
- [Project Structure](#project-structure)
- [Setup Instructions](#setup-instructions)
- [API Documentation](#api-documentation)
- [Deployment](#deployment)
- [Interview Q&A](#interview-qa)

---

## 🏗️ Technology Stack

### Backend
- **Java 17** with **Spring Boot 3.2.0**
- **Spring Security** with **JWT Token** Authentication
- **Spring Data JPA** with **Hibernate** ORM
- **MySQL 8.0** Database
- **Lombok** for code generation
- **Maven** Build Tool

### Frontend
- **React 18** with **React Router v6**
- **Bootstrap 5** for UI
- **Axios** for API calls
- **JavaScript ES6+**

---

## ✨ Features

### 1. **Secure Authentication**
- User registration & login
- JWT token-based authentication
- Password encryption (BCrypt)

### 2. **Role-Based Access Control**
- **ADMIN**: Full access to all tasks and users
- **EDITOR**: Create, edit own tasks
- **VIEWER**: Read-only access

### 3. **Task Management**
- Create, Read, Update, Delete (CRUD) tasks
- Task priorities (HIGH, MEDIUM, LOW)
- Task status (TODO, IN_PROGRESS, DONE)
- Due date tracking
- Task assignment to users

### 4. **Kanban Board**
- Drag-and-drop task movements
- Color-coded priorities
- Real-time status updates

### 5. **Collaboration**
- Comments on tasks
- User assignment
- Task history tracking

### 6. **Analytics**
- Overdue task tracking
- Task completion reports
- User productivity metrics

---

## 🗄️ Database Design

### Users Table
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    email VARCHAR(100) UNIQUE,
    password VARCHAR(255),
    role VARCHAR(20),
    active BOOLEAN
);
```

### Tasks Table
```sql
CREATE TABLE tasks (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100),
    description TEXT,
    priority VARCHAR(20),
    status VARCHAR(20),
    due_date DATE,
    assigned_to BIGINT,
    created_by BIGINT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (assigned_to) REFERENCES users(id),
    FOREIGN KEY (created_by) REFERENCES users(id)
);
```

### Comments Table
```sql
CREATE TABLE comments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    message TEXT,
    task_id BIGINT,
    user_id BIGINT,
    created_at TIMESTAMP,
    FOREIGN KEY (task_id) REFERENCES tasks(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

---

## 📁 Project Structure

```
amdox-task-management/
├── task-management-backend/
│   ├── src/main/java/com/amdox/taskmanager/
│   │   ├── controller/
│   │   │   ├── AuthController.java
│   │   │   ├── TaskController.java
│   │   │   └── CommentController.java
│   │   ├── service/
│   │   │   ├── AuthService.java
│   │   │   ├── TaskService.java
│   │   │   └── CommentService.java
│   │   ├── repository/
│   │   │   ├── UserRepository.java
│   │   │   ├── TaskRepository.java
│   │   │   └── CommentRepository.java
│   │   ├── model/
│   │   │   ├── User.java
│   │   │   ├── Task.java
│   │   │   └── Comment.java
│   │   ├── dto/
│   │   │   ├── LoginRequest.java
│   │   │   ├── RegisterRequest.java
│   │   │   ├── AuthResponse.java
│   │   │   ├── TaskRequest.java
│   │   │   └── CommentRequest.java
│   │   ├── security/
│   │   │   ├── JwtTokenProvider.java
│   │   │   ├── JwtInterceptor.java
│   │   │   └── WebConfig.java
│   │   └── TaskManagerApplication.java
│   ├── src/main/resources/
│   │   └── application.properties
│   └── pom.xml
│
├── task-management-frontend/
│   ├── src/
│   │   ├── pages/
│   │   │   ├── LoginPage.js
│   │   │   ├── RegisterPage.js
│   │   │   └── Dashboard.js
│   │   ├── components/
│   │   │   ├── Navbar.js
│   │   │   ├── TaskForm.js
│   │   │   ├── TaskList.js
│   │   │   ├── TaskItem.js
│   │   │   └── KanbanBoard.js
│   │   ├── services/
│   │   │   └── api.js
│   │   ├── App.js
│   │   ├── index.js
│   │   └── index.css
│   ├── public/
│   │   └── index.html
│   └── package.json
│
├── database-init.sql
└── README.md
```

---

## 🚀 Setup Instructions

### Prerequisites
- **Java 17** or higher
- **Node.js 16+** & **npm**
- **MySQL 8.0+**
- **Maven 3.8+**

### Step 1: Database Setup

```bash
# Connect to MySQL
mysql -u root -p

# Run the initialization script
mysql -u root -p < database-init.sql
```

### Step 2: Backend Setup

```bash
cd task-management-backend

# Install dependencies
mvn clean install

# Update MySQL credentials in src/main/resources/application.properties
# Default:
# spring.datasource.username=root
# spring.datasource.password=

# Run Spring Boot application
mvn spring-boot:run

# Backend runs on: http://localhost:8080/api
```

### Step 3: Frontend Setup

```bash
cd task-management-frontend

# Install dependencies
npm install

# Start React development server
npm start

# Frontend runs on: http://localhost:3000
```

---

## 📡 API Documentation

### Authentication APIs

#### Register
```
POST /api/auth/register
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "role": "VIEWER"
}

Response: 201 Created
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "message": "User registered successfully",
  "userId": 1,
  "userName": "John Doe",
  "role": "VIEWER"
}
```

#### Login
```
POST /api/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "password123"
}

Response: 200 OK
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "message": "Login successful",
  "userId": 1,
  "userName": "John Doe",
  "role": "VIEWER"
}
```

### Task APIs

#### Get All Tasks
```
GET /api/tasks
Authorization: Bearer <token>

Response: 200 OK
[
  {
    "id": 1,
    "title": "Complete project report",
    "description": "Finish quarterly report",
    "priority": "HIGH",
    "status": "IN_PROGRESS",
    "dueDate": "2024-12-31",
    "assignedTo": {...},
    "createdBy": {...},
    "createdAt": "2024-02-03T10:30:00"
  }
]
```

#### Create Task
```
POST /api/tasks
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Complete project report",
  "description": "Finish quarterly report",
  "priority": "HIGH",
  "status": "TODO",
  "dueDate": "2024-12-31",
  "assignedToId": 2
}

Response: 201 Created
```

#### Update Task
```
PUT /api/tasks/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Updated title",
  "status": "DONE",
  "priority": "MEDIUM"
}

Response: 200 OK
```

#### Delete Task
```
DELETE /api/tasks/{id}
Authorization: Bearer <token>

Response: 204 No Content
```

#### Get Overdue Tasks
```
GET /api/tasks/overdue/list
Authorization: Bearer <token>

Response: 200 OK
[...]
```

### Comment APIs

#### Add Comment
```
POST /api/tasks/{taskId}/comments
Authorization: Bearer <token>
Content-Type: application/json

{
  "message": "This task is progressing well"
}

Response: 201 Created
```

#### Get Task Comments
```
GET /api/tasks/{taskId}/comments
Authorization: Bearer <token>

Response: 200 OK
[...]
```

#### Delete Comment
```
DELETE /api/tasks/{taskId}/comments/{commentId}
Authorization: Bearer <token>

Response: 204 No Content
```

---

## 🌐 Deployment

### Deploy Backend to AWS/Railway

1. **Build JAR**
   ```bash
   mvn clean package
   ```

2. **Deploy to AWS EC2**
   ```bash
   # Upload JAR and run
   java -jar task-management-backend-1.0.0.jar
   ```

3. **Deploy to Railway**
   - Connect Git repository
   - Set environment variables
   - Auto-deploy on push

### Deploy Frontend to Netlify/Vercel

1. **Build React App**
   ```bash
   npm run build
   ```

2. **Deploy to Netlify**
   - Drag & drop `build` folder
   - Or connect GitHub repo for auto-deploy

3. **Deploy to Vercel**
   ```bash
   npm install -g vercel
   vercel --prod
   ```

### Production Environment Variables

**Backend (application.properties)**
```properties
spring.datasource.url=jdbc:mysql://prod-db-host:3306/amdox_taskdb
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
jwt.secret=${JWT_SECRET}
```

**Frontend (.env)**
```
REACT_APP_API_URL=https://api.amdox-tasks.com
```

---

## 💡 Interview Q&A

### Q1: Explain the project architecture
**A:** This is a **Java Full-Stack** application using:
- **Frontend**: React for interactive UI
- **Backend**: Spring Boot for REST APIs
- **Database**: MySQL for persistent storage
- **Security**: JWT tokens for stateless authentication

### Q2: How does JWT authentication work?
**A:**
1. User logs in with email & password
2. Backend validates and generates a JWT token
3. Frontend stores token in localStorage
4. For every request, token is sent in Authorization header
5. Backend validates token and extracts user info via JwtTokenProvider

### Q3: What's the role-based access control?
**A:**
- **ADMIN**: Can view/edit all tasks, manage users
- **EDITOR**: Can create & edit own tasks
- **VIEWER**: Can only view tasks

### Q4: How does the Kanban board work?
**A:**
- Tasks are displayed in 3 columns: TODO, IN_PROGRESS, DONE
- Drag-and-drop functionality moves tasks between columns
- Frontend updates task status via API
- Backend validates permission before updating

### Q5: What are the key features?
**A:**
- Secure authentication with JWT
- Role-based authorization
- CRUD operations on tasks
- Task assignment & tracking
- Comment collaboration
- Kanban workflow visualization
- Overdue task alerts
- Responsive design

### Q6: What's the database relationship?
**A:**
- Users table stores user credentials & roles
- Tasks table has foreign keys to Users (assigned_to, created_by)
- Comments table has foreign keys to Tasks and Users
- One-to-many relationship: User → Tasks, Task → Comments

### Q7: How do you handle errors?
**A:**
- Backend validates input before processing
- Proper HTTP status codes (200, 201, 400, 401, 403, 404, 500)
- Frontend catches errors and shows user-friendly messages
- JWT validation prevents unauthorized access

### Q8: What improvements can be added?
**A:**
- Email notifications for task assignments
- File attachments to tasks
- Real-time updates using WebSocket
- Advanced search & filtering
- Analytics dashboard
- Recurring tasks
- Team collaboration spaces

---

## 🧪 Testing the Application

### Test Account Credentials
```
Admin:
Email: admin@amdox.com
Password: password123

Editor:
Email: john@amdox.com
Password: password123

Viewer:
Email: jane@amdox.com
Password: password123
```

### Test Scenarios

1. **Authentication**
   - Register new user
   - Login with credentials
   - Logout functionality
   - Token expiration

2. **Task Management**
   - Create task with all fields
   - Update task status
   - Delete task
   - Filter by status/priority

3. **Kanban Board**
   - Drag task between columns
   - Update status in real-time
   - Color coding by priority

4. **Authorization**
   - EDITOR cannot delete other's tasks
   - VIEWER cannot create tasks
   - ADMIN can access everything

---

## 📚 Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [React Documentation](https://react.dev)
- [JWT Best Practices](https://tools.ietf.org/html/rfc7519)
- [MySQL Documentation](https://dev.mysql.com/doc/)

---

## 📞 Support

For issues or questions:
1. Check the database is running
2. Verify MySQL credentials
3. Check API endpoint URLs
4. Review browser console for errors
5. Check backend logs for exceptions

---

## 📝 License

This project is part of the Amdox Task Management System course/assignment.

---

**Created**: February 3, 2026  
**Version**: 1.0.0  
**Status**: Production Ready ✅
