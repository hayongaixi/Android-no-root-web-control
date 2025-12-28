cd /root/backend_20251228155800

# 修改Socket.IO配置
sed -i "s/pingTimeout: 120000,/pingTimeout: 60000,/" server.js
sed -i "s/pingInterval: 50000,/pingInterval: 25000,/" server.js

# 在transports行后添加upgradeTimeout和allowUpgrades
sed -i "/transports: \['websocket', 'polling'\],/a\\    upgradeTimeout: 10000,\\n    allowUpgrades: true" server.js

# 重启服务
pm2 restart android-control

echo "Server updated and restarted"
