# 部署指南

## 本地开发环境部署

### 1. 环境准备

确保已安装以下软件：
- JDK 17+
- Node.js 16+
- MySQL 8.0+
- Redis 7.x
- Maven 3.6+

### 2. 数据库初始化

```bash
# 登录MySQL
mysql -u root -p

# 创建数据库
CREATE DATABASE ai_learning_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 导入表结构
mysql -u root -p ai_learning_platform < sql/schema.sql
```

### 3. 后端启动

```bash
cd backend

# 修改配置文件
vim src/main/resources/application.yml
# 修改数据库连接、Redis配置、AI API密钥等

# 编译运行
mvn clean package
java -jar target/ai-learning-platform-1.0.0.jar

# 或直接运行
mvn spring-boot:run
```

访问：http://localhost:8080/api

### 4. 前端启动

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

访问：http://localhost:3000

## Docker部署（推荐）

### 方式一：Docker Compose一键部署

```bash
# 确保已安装Docker和Docker Compose

# 启动所有服务
docker-compose up -d

# 查看日志
docker-compose logs -f

# 停止服务
docker-compose down

# 停止并删除数据
docker-compose down -v
```

服务地址：
- 前端：http://localhost:3000
- 后端：http://localhost:8080/api
- MySQL：localhost:3306
- Redis：localhost:6379

### 方式二：单独构建镜像

**后端镜像**
```bash
cd backend
docker build -t ai-learning-backend:1.0 .
docker run -d \
  --name ai-learning-backend \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/ai_learning_platform \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=your_password \
  -e SPRING_REDIS_HOST=redis \
  ai-learning-backend:1.0
```

**前端镜像**
```bash
cd frontend
docker build -t ai-learning-frontend:1.0 .
docker run -d \
  --name ai-learning-frontend \
  -p 3000:80 \
  ai-learning-frontend:1.0
```

## 生产环境部署

### 服务器要求

**推荐配置**
- CPU: 4核+
- 内存: 8GB+
- 硬盘: 50GB+ SSD
- 操作系统: CentOS 7/8, Ubuntu 20.04+

**最低配置**
- CPU: 2核
- 内存: 4GB
- 硬盘: 20GB
- 操作系统: CentOS 7+, Ubuntu 18.04+

### 1. 服务器环境准备

```bash
# 安装Docker
curl -fsSL https://get.docker.com | sh
systemctl start docker
systemctl enable docker

# 安装Docker Compose
curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose

# 验证安装
docker --version
docker-compose --version
```

### 2. 配置文件准备

**后端生产配置**
```bash
cd backend/src/main/resources

# 创建生产环境配置
cat > application-prod.yml << EOF
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://mysql:3306/ai_learning_platform?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: \${DB_USERNAME:root}
    password: \${DB_PASSWORD:your_password}
  
  data:
    redis:
      host: \${REDIS_HOST:redis}
      port: 6379
      password: \${REDIS_PASSWORD:}

# AI配置
ai:
  api:
    base-url: \${AI_API_URL:https://api.openai.com/v1}
    api-key: \${AI_API_KEY:your_api_key}

# 阿里云VOD
aliyun:
  vod:
    access-key-id: \${ALIYUN_ACCESS_KEY:your_key}
    access-key-secret: \${ALIYUN_ACCESS_SECRET:your_secret}
EOF
```

### 3. Nginx反向代理配置

```bash
# 安装Nginx
sudo apt install nginx  # Ubuntu
sudo yum install nginx  # CentOS

# 配置文件
sudo vim /etc/nginx/sites-available/ai-learning

# 添加以下配置
server {
    listen 80;
    server_name your-domain.com;
    
    # 前端
    location / {
        proxy_pass http://localhost:3000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
    
    # 后端API
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        
        # 超时设置
        proxy_connect_timeout 600;
        proxy_send_timeout 600;
        proxy_read_timeout 600;
    }
}

# 启用配置
sudo ln -s /etc/nginx/sites-available/ai-learning /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

### 4. HTTPS配置（Let's Encrypt）

```bash
# 安装certbot
sudo apt install certbot python3-certbot-nginx

# 获取SSL证书
sudo certbot --nginx -d your-domain.com

# 自动续期
sudo certbot renew --dry-run
```

### 5. 启动服务

```bash
# 使用Docker Compose
docker-compose -f docker-compose.prod.yml up -d

# 查看日志
docker-compose logs -f backend
docker-compose logs -f frontend
```

### 6. 数据库备份

```bash
# 创建备份脚本
cat > /root/backup-db.sh << 'EOF'
#!/bin/bash
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="/backup/mysql"
mkdir -p $BACKUP_DIR

docker exec ai-learning-mysql mysqldump \
  -u root \
  -p${MYSQL_ROOT_PASSWORD} \
  ai_learning_platform > $BACKUP_DIR/backup_$DATE.sql

# 保留最近30天的备份
find $BACKUP_DIR -name "backup_*.sql" -mtime +30 -delete
EOF

chmod +x /root/backup-db.sh

# 添加定时任务
crontab -e
# 每天凌晨2点备份
0 2 * * * /root/backup-db.sh
```

### 7. 监控和日志

```bash
# 查看容器状态
docker ps
docker stats

# 查看日志
docker-compose logs -f --tail=100 backend
docker-compose logs -f --tail=100 frontend

# 日志持久化
# 后端日志在：backend-logs volume
# 可以通过docker volume inspect backend-logs查看路径
```

## 性能优化

### 1. MySQL优化

```sql
-- 查看慢查询
SHOW VARIABLES LIKE 'slow_query_log%';

-- 添加索引
ALTER TABLE learning_record ADD INDEX idx_user_course (user_id, course_id);
ALTER TABLE ai_chat_record ADD INDEX idx_user_session (user_id, session_id);
```

### 2. Redis缓存优化

```yaml
# application.yml
spring:
  data:
    redis:
      lettuce:
        pool:
          max-active: 16
          max-idle: 8
          min-idle: 2
```

### 3. JVM参数调优

```bash
# 在Dockerfile中添加
ENV JAVA_OPTS="-Xms2g -Xmx2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

### 4. Nginx优化

```nginx
# 开启Gzip
gzip on;
gzip_vary on;
gzip_min_length 1024;
gzip_types text/plain text/css application/json application/javascript;

# 静态资源缓存
location ~* \.(jpg|jpeg|png|gif|ico|css|js)$ {
    expires 7d;
    add_header Cache-Control "public, immutable";
}
```

## 故障排查

### 后端服务无法启动

```bash
# 查看日志
docker-compose logs backend

# 常见问题：
# 1. 数据库连接失败 - 检查MySQL是否启动，配置是否正确
# 2. Redis连接失败 - 检查Redis是否启动
# 3. 端口被占用 - 修改docker-compose.yml中的端口映射
```

### 前端无法访问后端

```bash
# 检查网络连接
docker network inspect ai-learning-network

# 检查后端是否正常
curl http://localhost:8080/api/health

# 检查Nginx配置
nginx -t
```

### 数据库性能问题

```sql
-- 查看慢查询
SELECT * FROM mysql.slow_log ORDER BY start_time DESC LIMIT 10;

-- 查看表大小
SELECT 
    table_name,
    ROUND((data_length + index_length) / 1024 / 1024, 2) AS 'Size (MB)'
FROM information_schema.tables
WHERE table_schema = 'ai_learning_platform'
ORDER BY (data_length + index_length) DESC;
```

## 安全建议

1. **修改默认密码**
   - 数据库root密码
   - Redis密码
   - 应用管理员密码

2. **防火墙配置**
```bash
# 只开放必要端口
sudo ufw allow 22    # SSH
sudo ufw allow 80    # HTTP
sudo ufw allow 443   # HTTPS
sudo ufw enable
```

3. **定期更新**
```bash
# 更新系统
sudo apt update && sudo apt upgrade

# 更新Docker镜像
docker-compose pull
docker-compose up -d
```

4. **备份策略**
   - 数据库每日备份
   - 重要文件异地备份
   - 定期测试恢复流程

## 扩容方案

### 垂直扩容
- 升级服务器配置（CPU、内存）
- 优化数据库配置
- 增加Redis内存

### 水平扩容
- 部署多个后端实例（负载均衡）
- MySQL主从复制/读写分离
- Redis集群

```yaml
# docker-compose.yml 多实例示例
services:
  backend-1:
    ...
  backend-2:
    ...
  nginx:
    # 配置负载均衡
```

## 联系支持

如遇到部署问题，请：
1. 查看项目README
2. 检查日志文件
3. 提交GitHub Issue
4. 联系技术支持

---

祝部署顺利！🚀
