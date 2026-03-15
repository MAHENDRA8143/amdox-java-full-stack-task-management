# 🚀 Amdox Task Management - Complete Deployment Guide

This guide covers how to deploy the Amdox Task Management System to production environments.

---

## 📋 Pre-Deployment Checklist

- [ ] Database backup created
- [ ] API tested in local environment
- [ ] Frontend build verified
- [ ] Environment variables configured
- [ ] SSL certificates ready (for HTTPS)
- [ ] Domain name configured

---

## 🌐 Backend Deployment

### Option 1: AWS EC2

#### Step 1: Setup EC2 Instance
```bash
# SSH into EC2
ssh -i "key.pem" ec2-user@your-instance-ip

# Install Java 17
sudo yum update -y
sudo amazon-linux-extras install java-openjdk17 -y

# Install MySQL Client
sudo yum install mysql -y

# Install Maven (optional)
sudo yum install maven -y
```

#### Step 2: Build and Deploy
```bash
# Clone repository
git clone https://github.com/your-repo/amdox-task-management.git
cd amdox-task-management/task-management-backend

# Build JAR
mvn clean package -DskipTests

# Create application directory
sudo mkdir -p /opt/amdox
sudo cp target/task-management-backend-1.0.0.jar /opt/amdox/app.jar

# Create systemd service
sudo nano /etc/systemd/system/amdox-backend.service
```

**Service File:**
```ini
[Unit]
Description=Amdox Task Management Backend
After=network.target

[Service]
Type=simple
User=ec2-user
WorkingDirectory=/opt/amdox
ExecStart=/usr/bin/java -jar /opt/amdox/app.jar
Restart=on-failure
RestartSec=10
Environment="SPRING_DATASOURCE_URL=jdbc:mysql://prod-db:3306/amdox_taskdb"
Environment="SPRING_DATASOURCE_USERNAME=${DB_USERNAME}"
Environment="SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD}"
Environment="JWT_SECRET=${JWT_SECRET}"

[Install]
WantedBy=multi-user.target
```

#### Step 3: Start Service
```bash
sudo systemctl daemon-reload
sudo systemctl start amdox-backend
sudo systemctl enable amdox-backend
sudo systemctl status amdox-backend
```

### Option 2: Railway.app

1. Create account at [railway.app](https://railway.app)
2. Connect GitHub repository
3. Add MySQL database from Railway marketplace
4. Set environment variables in settings
5. Deploy automatically on push

**Environment Variables:**
```
SPRING_DATASOURCE_URL=jdbc:mysql://host:port/amdox_taskdb
SPRING_DATASOURCE_USERNAME=${DATABASE_USER}
SPRING_DATASOURCE_PASSWORD=${DATABASE_PASSWORD}
JWT_SECRET=${JWT_SECRET_KEY}
```

### Option 3: Docker Deployment

**Create Dockerfile:**
```dockerfile
FROM openjdk:17-slim
COPY target/task-management-backend-1.0.0.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8080
```

**Build and Run:**
```bash
docker build -t amdox-backend:latest .
docker run -d \
  --name amdox-backend \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/amdox_taskdb \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=password \
  amdox-backend:latest
```

---

## 🎨 Frontend Deployment

### Option 1: Netlify

1. **Build Project**
   ```bash
   cd task-management-frontend
   npm run build
   ```

2. **Deploy via Netlify UI**
   - Go to netlify.com
   - Click "New site from Git"
   - Select your repository
   - Set build command: `npm run build`
   - Set publish directory: `build`
   - Deploy

3. **Configure Environment**
   ```
   REACT_APP_API_URL=https://api.amdox-tasks.com
   ```

### Option 2: Vercel

1. **Install Vercel CLI**
   ```bash
   npm install -g vercel
   ```

2. **Deploy**
   ```bash
   cd task-management-frontend
   vercel --prod
   ```

3. **Configure .env.production**
   ```
   REACT_APP_API_URL=https://api.amdox-tasks.com
   ```

### Option 3: AWS S3 + CloudFront

1. **Build React App**
   ```bash
   npm run build
   ```

2. **Create S3 Bucket**
   ```bash
   aws s3 mb s3://amdox-tasks-frontend
   ```

3. **Upload Build Files**
   ```bash
   aws s3 sync build/ s3://amdox-tasks-frontend --delete
   ```

4. **Create CloudFront Distribution**
   - Set origin to S3 bucket
   - Enable caching
   - Configure SSL certificate
   - Set index document to index.html

### Option 4: Docker & Nginx

**Dockerfile:**
```dockerfile
FROM node:18 as build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/build /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

**Build and Run:**
```bash
docker build -t amdox-frontend:latest .
docker run -d -p 80:80 amdox-frontend:latest
```

---

## 🗄️ Database Deployment

### AWS RDS MySQL

1. **Create RDS Instance**
   ```bash
   aws rds create-db-instance \
     --db-instance-identifier amdox-db \
     --db-instance-class db.t3.micro \
     --engine mysql \
     --allocated-storage 20 \
     --master-username admin \
     --master-user-password ${DB_PASSWORD}
   ```

2. **Initialize Database**
   ```bash
   mysql -h your-rds-endpoint -u admin -p < database-init.sql
   ```

3. **Update Backend Config**
   ```properties
   spring.datasource.url=jdbc:mysql://your-rds-endpoint:3306/amdox_taskdb
   spring.datasource.username=admin
   spring.datasource.password=${DB_PASSWORD}
   ```

---

## 🔒 SSL/HTTPS Setup

### Using Let's Encrypt (Free)

```bash
# Install Certbot
sudo apt-get install certbot -y

# Generate Certificate
sudo certbot certonly --standalone -d yourdomain.com -d www.yourdomain.com

# Configure in backend
server.ssl.key-store=/etc/letsencrypt/live/yourdomain.com/keystore.p12
server.ssl.key-store-password=${KEYSTORE_PASSWORD}
server.ssl.key-store-type=PKCS12
server.port=443
```

### Configure Nginx as Reverse Proxy

```nginx
server {
    listen 80;
    server_name yourdomain.com;
    redirect http://$server_name$request_uri https://$server_name$request_uri;
}

server {
    listen 443 ssl;
    server_name yourdomain.com;

    ssl_certificate /etc/letsencrypt/live/yourdomain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/yourdomain.com/privkey.pem;

    location /api {
        proxy_pass http://localhost:8080/api;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    location / {
        proxy_pass http://localhost:3000;
        proxy_set_header Host $host;
    }
}
```

---

## 📊 Monitoring & Logging

### Application Logs
```bash
# View backend logs
sudo journalctl -u amdox-backend -f

# View Docker logs
docker logs -f amdox-backend
```

### Setup CloudWatch (AWS)
```properties
logging.config=classpath:logback-spring.xml
logging.level.com.amdox=INFO
```

### Setup Monitoring
- **AWS CloudWatch**: Monitor EC2, RDS metrics
- **New Relic**: APM monitoring
- **ELK Stack**: Log aggregation

---

## 🔄 CI/CD Pipeline

### GitHub Actions Example

```yaml
name: Deploy to Production

on:
  push:
    branches: [main]

jobs:
  build-and-deploy:
    runs-on: ubuntu-latest

    steps:
    - uses: actions/checkout@v2

    - name: Build Backend
      run: |
        cd task-management-backend
        mvn clean package -DskipTests

    - name: Build Frontend
      run: |
        cd task-management-frontend
        npm install
        npm run build

    - name: Deploy to AWS
      env:
        AWS_ACCESS_KEY_ID: ${{ secrets.AWS_ACCESS_KEY_ID }}
        AWS_SECRET_ACCESS_KEY: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
      run: |
        # Deploy scripts here
        ./deploy.sh
```

---

## 🆘 Troubleshooting

### Backend Issues

**Cannot connect to database:**
```bash
# Check connection
mysql -h db-host -u username -p

# Verify credentials in application.properties
# Check security groups in AWS
```

**Port already in use:**
```bash
# Find process using port 8080
lsof -i :8080

# Kill process
kill -9 <PID>
```

### Frontend Issues

**CORS errors:**
- Update CORS configuration in WebConfig.java
- Add frontend URL to allowedOrigins

**Blank page:**
- Check API URL configuration
- Check browser console for errors
- Verify backend is running

---

## 📈 Performance Optimization

1. **Database**
   - Add indexes on frequently queried columns
   - Enable query caching
   - Regular backups

2. **Backend**
   - Enable gzip compression
   - Use connection pooling (HikariCP)
   - Implement caching (Redis)

3. **Frontend**
   - Code splitting
   - Lazy loading components
   - Image optimization

---

## ✅ Post-Deployment

1. **Verify Deployment**
   - Test all APIs
   - Test authentication flow
   - Check database connectivity

2. **Backup Strategy**
   ```bash
   # Daily MySQL backups
   0 2 * * * mysqldump -u root -p${DB_PASSWORD} amdox_taskdb > /backups/amdox_$(date +\%Y\%m\%d).sql
   ```

3. **Monitoring Setup**
   - Set up alerts for CPU/Memory
   - Monitor error rates
   - Track API response times

---

**Deployment Complete!** 🎉
