#!/bin/bash

# AI学习平台 - 环境检查脚本
echo "========================================="
echo "AI学习平台 - 环境检查"
echo "========================================="
echo ""

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

check_command() {
    if command -v $1 &> /dev/null; then
        echo -e "${GREEN}✓${NC} $1: $(command -v $1)"
        if [ ! -z "$2" ]; then
            echo "  版本: $($1 $2 2>&1 | head -n 1)"
        fi
        return 0
    else
        echo -e "${RED}✗${NC} $1: 未安装"
        return 1
    fi
}

check_port() {
    if lsof -Pi :$1 -sTCP:LISTEN -t >/dev/null 2>&1; then
        echo -e "${YELLOW}⚠${NC} 端口 $1 已被占用"
        return 1
    else
        echo -e "${GREEN}✓${NC} 端口 $1 可用"
        return 0
    fi
}

echo "1. 检查必需软件"
echo "-----------------------------------"
check_command "java" "-version"
check_command "node" "--version"
check_command "npm" "--version"
check_command "mvn" "--version"
check_command "mysql" "--version"
check_command "redis-server" "--version"
check_command "docker" "--version"
check_command "docker-compose" "--version"
echo ""

echo "2. 检查端口占用"
echo "-----------------------------------"
check_port 3000  # 前端
check_port 8080  # 后端
check_port 3306  # MySQL
check_port 6379  # Redis
echo ""

echo "3. 检查项目文件"
echo "-----------------------------------"
if [ -f "README.md" ]; then
    echo -e "${GREEN}✓${NC} README.md 存在"
else
    echo -e "${RED}✗${NC} README.md 不存在"
fi

if [ -f "docker-compose.yml" ]; then
    echo -e "${GREEN}✓${NC} docker-compose.yml 存在"
else
    echo -e "${RED}✗${NC} docker-compose.yml 不存在"
fi

if [ -f "backend/pom.xml" ]; then
    echo -e "${GREEN}✓${NC} backend/pom.xml 存在"
else
    echo -e "${RED}✗${NC} backend/pom.xml 不存在"
fi

if [ -f "frontend/package.json" ]; then
    echo -e "${GREEN}✓${NC} frontend/package.json 存在"
else
    echo -e "${RED}✗${NC} frontend/package.json 不存在"
fi

if [ -f "sql/schema.sql" ]; then
    echo -e "${GREEN}✓${NC} sql/schema.sql 存在"
else
    echo -e "${RED}✗${NC} sql/schema.sql 不存在"
fi
echo ""

echo "4. 推荐启动方式"
echo "-----------------------------------"
echo "方式1: Docker Compose（推荐）"
echo "  $ docker-compose up -d"
echo ""
echo "方式2: 本地开发"
echo "  后端: cd backend && mvn spring-boot:run"
echo "  前端: cd frontend && npm run dev"
echo ""

echo "========================================="
echo "检查完成！"
echo "========================================="
echo ""
echo "详细文档："
echo "  - 快速开始: docs/QUICK_START.md"
echo "  - 完整说明: README.md"
echo "  - 部署指南: docs/DEPLOYMENT.md"
echo ""
