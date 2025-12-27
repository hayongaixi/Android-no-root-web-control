#!/bin/bash

# 服务器配置
SERVER_HOST="101.35.144.210"
SERVER_USER="root"
SERVER_PASSWORD="qyyqyy822"
SERVER_PORT=22

# 部署路径
DEPLOY_PATH="/opt/android-control"

# SSH连接并执行部署
sshpass -p "$SERVER_PASSWORD" ssh -o StrictHostKeyChecking=no ${SERVER_USER}@${SERVER_HOST} -p ${SERVER_PORT} << 'ENDSSH'
    # 创建部署目录
    mkdir -p /opt/android-control
    
    # 停止旧容器
    cd /opt/android-control
    docker stop android-control 2>/dev/null || true
    docker rm android-control 2>/dev/null || true
    
    # 清理旧镜像
    docker rmi android-control:latest 2>/dev/null || true
ENDSSH

# 使用SCP上传文件
sshpass -p "$SERVER_PASSWORD" scp -o StrictHostKeyChecking=no -r ${SSH_OPTS} \
    backend/* ${SERVER_USER}@${SERVER_HOST}:${DEPLOY_PATH}/

# SSH连接并启动容器
sshpass -p "$SERVER_PASSWORD" ssh -o StrictHostKeyChecking=no ${SERVER_USER}@${SERVER_HOST} -p ${SERVER_PORT} << 'ENDSSH'
    cd /opt/android-control
    
    # 构建镜像
    docker build -t android-control:latest .
    
    # 运行容器
    docker run -d \
        --name android-control \
        --restart unless-stopped \
        -p 3000:3000 \
        android-control:latest
    
    # 查看日志
    docker logs android-control
    
    echo "========================================"
    echo "部署完成!"
    echo "访问地址: http://101.35.144.210:3000"
    echo "========================================"
ENDSSH
