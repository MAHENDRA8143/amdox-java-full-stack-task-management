# ⚡ Quick Start Guide - Amdox Task Management

Get the application running in **10 minutes**!

---

## 🚀 Prerequisites

Before you begin, ensure you have installed:
- **Java 17+**: Download from [Oracle JDK](https://www.oracle.com/java/technologies/downloads/)
- **Node.js 16+**: Download from [Node.js](https://nodejs.org/)
- **MySQL 8.0+**: Download from [MySQL](https://www.mysql.com/downloads/)
- **Maven 3.8+**: Usually comes with Java IDEs

---

## 📝 Step 1: Database Setup (2 minutes)

### 1A. Start MySQL
```bash
# Windows
"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqld.exe"

# Or use MySQL Workbench
```

### 1B. Initialize Database
```bash
# Open MySQL CLI
mysql -u root -p

# Then run (or paste the entire database-init.sql file)
source /path/to/database-init.sql

# Or import the SQL file
mysql -u root -p amdox_taskdb < database-init.sql
```

**Default Test Credentials:**
```
Email: admin@amdox.com
Password: password123
```

---

## 🔧 Step 2: Backend Setup (3 minutes)

### 2A. Navigate to Backend Directory
```bash
cd task-management-backend
```

### 2B. Build the Project
```bash
mvn clean install
```

### 2C. Run Spring Boot Server
```bash
mvn spring-boot:run
```

**Expected Output:**
```
Tomcat started on port(s): 8080
Started TaskManagerApplication
```

✅ **Backend is running on**: `http://localhost:8080/api`

---

## 🎨 Step 3: Frontend Setup (3 minutes)

### 3A. In a NEW Terminal, Navigate to Frontend
```bash
cd task-management-frontend
```

### 3B. Install Dependencies
```bash
npm install
```

### 3C. Start React Server
```bash
npm start
```

**Browser should open automatically**  
✅ **Frontend is running on**: `http://localhost:3000`

---

## ✅ Step 4: Test the Application (2 minutes)

### 4A. Login
1. Go to `http://localhost:3000` in your browser
2. You'll see the login page
3. Use test credentials:
   - Email: `admin@amdox.com`
   - Password: `password123`

### 4B. Create a Task
1. Click "Create Task" button
2. Fill in the form:
   - Title: "Learn Spring Boot"
   - Priority: "HIGH"
   - Due Date: Tomorrow's date
3. Click "Create Task"

### 4C. View Tasks
1. See all tasks in List View
2. Switch to Kanban Board view
3. Drag tasks between columns

### 4D. Logout
1. Click logout in navbar
2. You'll be redirected to login

---

## 🐛 Troubleshooting

### Error: "Cannot connect to database"
```bash
# Check if MySQL is running
mysql -u root -p -e "SELECT VERSION();"

# Verify database was created
mysql -u root -p -e "SHOW DATABASES;"

# Check application.properties has correct credentials
# Default: username=root, password=(empty)
```

### Error: "Port 8080 already in use"
```bash
# Find process using port 8080
lsof -i :8080      # Mac/Linux
netstat -ano | findstr :8080  # Windows

# Kill the process
kill -9 <PID>      # Mac/Linux
taskkill /PID <PID> /F  # Windows

# Run backend on different port (optional)
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

### Error: "Port 3000 already in use"
```bash
# Kill process
lsof -i :3000      # Mac/Linux
netstat -ano | findstr :3000  # Windows

kill -9 <PID>      # Mac/Linux
taskkill /PID <PID> /F  # Windows
```

### Frontend shows blank page
```bash
# Check browser console (F12) for errors
# Common issues:
# 1. Backend not running
# 2. Wrong API URL in services/api.js
# 3. Check network tab in DevTools
```

---

## 📱 Using the Application

### Create Task
```
1. Click "Create Task" button
2. Enter task details
3. Click "Create Task"
```

### Edit Task (List View)
```
1. Click "Edit" button on task
2. Modify fields
3. Click "Save"
```

### Delete Task
```
1. Click "Delete" button
2. Confirm deletion
```

### Kanban Board
```
1. Switch to "Kanban Board" view
2. Drag task card to another column
3. Task status updates automatically
```

### Add Comment
```
Coming in next version!
```

---

## 🧪 Test Accounts

### Account 1: Admin
```
Email: admin@amdox.com
Password: password123
Role: ADMIN (Full access)
```

### Account 2: Editor
```
Email: john@amdox.com
Password: password123
Role: EDITOR (Can create & edit own tasks)
```

### Account 3: Viewer
```
Email: jane@amdox.com
Password: password123
Role: VIEWER (Read-only)
```

---

## 📚 Key API Endpoints

### Authentication
```
POST /api/auth/register    - Register new user
POST /api/auth/login       - Login user
```

### Tasks
```
GET    /api/tasks          - Get all tasks
POST   /api/tasks          - Create task
GET    /api/tasks/{id}     - Get specific task
PUT    /api/tasks/{id}     - Update task
DELETE /api/tasks/{id}     - Delete task
```

### Testing Endpoints (Postman)
1. Register new user
2. Login and copy token
3. Add Authorization header: `Bearer <token>`
4. Test other endpoints

---

## 🎓 Project File Guide

### Backend Files
```
task-management-backend/
├── src/main/java/com/amdox/taskmanager/
│   ├── controller/       - REST API endpoints
│   ├── service/          - Business logic
│   ├── repository/       - Database queries
│   ├── model/            - JPA entities
│   ├── dto/              - Request/Response objects
│   └── security/         - JWT & authentication
└── pom.xml               - Maven dependencies
```

### Frontend Files
```
task-management-frontend/
├── src/pages/            - Page components
├── src/components/       - Reusable components
├── src/services/         - API client
├── App.js                - Main app
└── package.json          - NPM dependencies
```

---

## 📚 Important Files to Review

1. **Backend**: `src/main/resources/application.properties` - Database config
2. **Frontend**: `src/services/api.js` - API configuration
3. **Database**: `database-init.sql` - Database schema
4. **Documentation**: `README.md` - Full documentation

---

## ✨ What's Next?

### Learn More
- [ ] Review the API documentation
- [ ] Understand JWT authentication
- [ ] Study the database schema
- [ ] Explore React components
- [ ] Check deployment guide

### Try This
- [ ] Register a new user
- [ ] Create multiple tasks
- [ ] Test Kanban board
- [ ] Try different roles
- [ ] Review API in DevTools Network tab

### Deployment
- [ ] Follow DEPLOYMENT_GUIDE.md for production setup
- [ ] Set up CI/CD pipeline
- [ ] Configure SSL certificates
- [ ] Set up monitoring

---

## 💡 Tips & Tricks

### Debug Mode
```bash
# Backend - Enable debug logging
# In application.properties:
logging.level.com.amdox=DEBUG
```

### Frontend Console Logs
```javascript
// Check if token is stored
console.log(localStorage.getItem('token'));

// Check API calls in Network tab (F12)
```

### Database Inspection
```bash
# Connect to database
mysql -u root -p amdox_taskdb

# View tables
SHOW TABLES;

# View users
SELECT * FROM users;

# View tasks
SELECT * FROM tasks;
```

---

## 🆘 Still Having Issues?

1. **Check Error Messages**: Read the error in browser console or terminal
2. **Review Logs**: Check Spring Boot logs in terminal
3. **Verify Credentials**: Ensure MySQL username/password match
4. **Restart Services**: Stop and restart backend/frontend
5. **Check Ports**: Make sure ports 8080 and 3000 are available

---

## 🎉 You're Ready!

Congratulations! You have successfully set up the **Amdox Task Management System**.

Happy coding! 🚀

---

**Need Help?**
- Check README.md for detailed documentation
- Review PROJECT_REPORT.md for system architecture
- Read DEPLOYMENT_GUIDE.md for production setup
