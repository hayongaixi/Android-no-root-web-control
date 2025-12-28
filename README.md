<<<<<<< HEAD
# Android远程控制系统

这是一个基于无障碍服务的Android远程控制系统，包含Android客户端和Web后端。

## 功能特性

### Android客户端
- ✅ 隐藏桌面图标（应用启动器不可见）
- ✅ 开机自启动
- ✅ 无障碍服务（读取屏幕内容）
- ✅ 屏幕点击、滑动、长按操作
- ✅ 文本查找和点击
- ✅ TCP服务端接收控制命令

### Web后端
- ✅ Socket.IO实时通信
- ✅ 设备管理和连接
- ✅ 美观的Web控制面板
- ✅ RESTful API接口
- ✅ Docker容器化部署

## Android应用使用说明

### 1. 编译安装
使用Android Studio打开项目，构建APK并安装到设备。

### 2. 权限设置
安装后，应用会自动引导您设置以下权限：

1. **无障碍服务权限**
   - 打开设置 → 无障碍 → Control Service
   - 启用服务

2. **悬浮窗权限**
   - 如果提示，允许应用在其他应用上层显示

### 3. 启动服务
应用安装后首次启动会：
- 启动控制服务（端口8888）
- 显示服务运行通知
- 开机自启动

## Web后端部署

### 本地运行
```bash
cd backend
npm install
npm start
```
访问 http://localhost:3000

### Docker部署
```bash
cd backend
docker build -t android-control .
docker run -d -p 3000:3000 --name android-control android-control
```

### 自动部署到Lighthouse
```bash
# 需要先安装sshpass
# Ubuntu/Debian: apt-get install sshpass
# macOS: brew install sshpass

chmod +x backend/deploy.sh
./backend/deploy.sh
```

## 控制命令

### TCP协议（端口8888）
```
CLICK <x> <y>                  # 点击坐标
SWIPE <x1> <y1> <x2> <y2> [duration]  # 滑动
LONG_CLICK <x> <y> [duration]  # 长按
GET_SCREEN                     # 获取屏幕内容
FIND_TEXT <text>               # 查找文本
CLICK_TEXT <text>             # 点击包含文本的元素
```

### WebSocket API
```javascript
// 连接设备
socket.emit('register_device', { name: 'My Device' });

// 执行命令
socket.emit('execute_command', {
  deviceId: 'device-id',
  command: { action: 'click', x: 500, y: 1000 }
});
```

## 技术栈

- **Android**: Java, AccessibilityService
- **Backend**: Node.js, Express, Socket.IO
- **Frontend**: HTML5, CSS3, JavaScript
- **Deployment**: Docker, Lighthouse

## 注意事项

1. 应用需要无障碍服务权限才能正常工作
2. 确保设备和服务器在同一网络或设备可以访问服务器
3. 首次使用需要手动在Android设备上开启相关权限
4. 服务会在后台持续运行，显示在通知栏

## 安全提示

- 本工具仅供学习和技术研究使用
- 使用前请确保您有合法权限控制目标设备
- 不要用于未经授权的设备控制
- 请遵守相关法律法规
=======
# Android远程控制系统

这是一个基于无障碍服务的Android远程控制系统，包含Android客户端和Web后端。

## 功能特性

### Android客户端
- ✅ 隐藏桌面图标（应用启动器不可见）
- ✅ 开机自启动
- ✅ 无障碍服务（读取屏幕内容）
- ✅ 屏幕点击、滑动、长按操作
- ✅ 文本查找和点击
- ✅ TCP服务端接收控制命令

### Web后端
- ✅ Socket.IO实时通信
- ✅ 设备管理和连接
- ✅ 美观的Web控制面板
- ✅ RESTful API接口
- ✅ Docker容器化部署

## Android应用使用说明

### 1. 编译安装
使用Android Studio打开项目，构建APK并安装到设备。

### 2. 权限设置
安装后，应用会自动引导您设置以下权限：

1. **无障碍服务权限**
   - 打开设置 → 无障碍 → Control Service
   - 启用服务

2. **悬浮窗权限**
   - 如果提示，允许应用在其他应用上层显示

### 3. 启动服务
应用安装后首次启动会：
- 启动控制服务（端口8888）
- 显示服务运行通知
- 开机自启动

## Web后端部署

### 本地运行
```bash
cd backend
npm install
npm start
```
访问 http://localhost:3000

### Docker部署
```bash
cd backend
docker build -t android-control .
docker run -d -p 3000:3000 --name android-control android-control
```

### 自动部署到Lighthouse
```bash
# 需要先安装sshpass
# Ubuntu/Debian: apt-get install sshpass
# macOS: brew install sshpass

chmod +x backend/deploy.sh
./backend/deploy.sh
```

## 控制命令

### TCP协议（端口8888）
```
CLICK <x> <y>                  # 点击坐标
SWIPE <x1> <y1> <x2> <y2> [duration]  # 滑动
LONG_CLICK <x> <y> [duration]  # 长按
GET_SCREEN                     # 获取屏幕内容
FIND_TEXT <text>               # 查找文本
CLICK_TEXT <text>             # 点击包含文本的元素
```

### WebSocket API
```javascript
// 连接设备
socket.emit('register_device', { name: 'My Device' });

// 执行命令
socket.emit('execute_command', {
  deviceId: 'device-id',
  command: { action: 'click', x: 500, y: 1000 }
});
```

## 技术栈

- **Android**: Java, AccessibilityService
- **Backend**: Node.js, Express, Socket.IO
- **Frontend**: HTML5, CSS3, JavaScript
- **Deployment**: Docker, Lighthouse

## 注意事项

1. 应用需要无障碍服务权限才能正常工作
2. 确保设备和服务器在同一网络或设备可以访问服务器
3. 首次使用需要手动在Android设备上开启相关权限
4. 服务会在后台持续运行，显示在通知栏

## 安全提示

- 本工具仅供学习和技术研究使用
- 使用前请确保您有合法权限控制目标设备
- 不要用于未经授权的设备控制
- 请遵守相关法律法规
>>>>>>> 352a9af7aaa9a9aa35dc5091be9f0a7961e36bb5
