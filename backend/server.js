const express = require('express');
const http = require('http');
const socketio = require('socket.io');
const cors = require('cors');
const bodyParser = require('body-parser');
const path = require('path');

const app = express();
const server = http.createServer(app);
const io = require('socket.io')(server, {
    origins: '*:*',
    pingTimeout: 120000,
    pingInterval: 50000,
    transports: ['websocket', 'polling']
});

const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(bodyParser.json());
app.use(express.static(path.join(__dirname, 'public')));

// 设备连接管理
const devices = new Map();

io.on('connection', (socket) => {
    console.log('Client connected:', socket.id);
    
    // 自动注册设备
    devices.set(socket.id, {
        id: socket.id,
        name: 'Device ' + socket.id.substring(0, 8),
        lastSeen: new Date(),
        socket: socket
    });
    console.log('Device auto-registered:', socket.id);
    broadcastDevices();
    
    socket.on('register_device', (data) => {
        const device = devices.get(socket.id);
        if (device) {
            device.name = data.name || device.name;
        }
        broadcastDevices();
    });
    
    socket.on('command_result', (data) => {
        console.log('Command result:', data);
        // 广播给所有Web客户端
        socket.broadcast.emit('command_result', data);
    });
    
    socket.on('disconnect', (reason) => {
        devices.delete(socket.id);
        console.log('Client disconnected:', socket.id, 'Reason:', reason);
        broadcastDevices();
    });
});

// 广播设备列表（不包含socket对象避免循环引用）
function broadcastDevices() {
    const deviceList = Array.from(devices.values()).map(d => ({
        id: d.id,
        name: d.name,
        lastSeen: d.lastSeen
    }));
    io.emit('device_list', deviceList);
}

// API路由
app.get('/api/devices', (req, res) => {
    const deviceList = Array.from(devices.values()).map(d => ({
        id: d.id,
        name: d.name,
        lastSeen: d.lastSeen
    }));
    res.json(deviceList);
});

app.post('/api/command', (req, res) => {
    const { deviceId, command } = req.body;
    
    if (!deviceId || !command) {
        return res.status(400).json({ error: 'Missing deviceId or command' });
    }
    
    const device = devices.get(deviceId);
    if (!device) {
        return res.status(404).json({ error: 'Device not found' });
    }
    
    device.socket.emit('execute_command', command);
    res.json({ success: true });
});

app.post('/api/command/:deviceId', (req, res) => {
    const { deviceId } = req.params;
    const command = req.body;
    
    const device = devices.get(deviceId);
    if (!device) {
        return res.status(404).json({ error: 'Device not found' });
    }
    
    device.socket.emit('execute_command', command);
    res.json({ success: true });
});

app.get('/', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'index.html'));
});

server.listen(PORT, '0.0.0.0', () => {
    console.log(`Server running on port ${PORT}`);
});
