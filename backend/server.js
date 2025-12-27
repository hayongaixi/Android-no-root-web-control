const express = require('express');
const http = require('http');
const { Server } = require('socket.io');
const cors = require('cors');
const bodyParser = require('body-parser');
const path = require('path');

const app = express();
const server = http.createServer(app);
const io = new Server(server, {
    cors: {
        origin: "*",
        methods: ["GET", "POST"]
    }
});

const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(bodyParser.json());
app.use(express.static(path.join(__dirname, 'public')));

// 设备连接管理
const devices = new Map();

io.on('connection', (socket) => {
    console.log('Client connected:', socket.id);
    
    socket.on('register_device', (data) => {
        devices.set(socket.id, {
            id: socket.id,
            name: data.name || 'Unknown Device',
            lastSeen: new Date(),
            socket: socket
        });
        console.log('Device registered:', data.name);
        io.emit('device_list', Array.from(devices.values()));
    });
    
    socket.on('command_result', (data) => {
        console.log('Command result:', data);
    });
    
    socket.on('disconnect', () => {
        devices.delete(socket.id);
        console.log('Client disconnected:', socket.id);
        io.emit('device_list', Array.from(devices.values()));
    });
});

// API路由
app.get('/api/devices', (req, res) => {
    res.json(Array.from(devices.values()));
});

app.post('/api/command', (req, res) => {
    const { deviceId, command } = req.body;
    
    if (!deviceId || !command) {
        return res.status(400).json({ error: 'Missing deviceId or command' });
    }
    
    const device = Array.from(devices.values()).find(d => d.id === deviceId);
    if (!device) {
        return res.status(404).json({ error: 'Device not found' });
    }
    
    device.socket.emit('execute_command', command);
    res.json({ success: true });
});

app.post('/api/command/:deviceId', (req, res) => {
    const { deviceId } = req.params;
    const command = req.body;
    
    const device = Array.from(devices.values()).find(d => d.id === deviceId);
    if (!device) {
        return res.status(404).json({ error: 'Device not found' });
    }
    
    device.socket.emit('execute_command', command);
    res.json({ success: true });
});

app.get('/', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'index.html'));
});

server.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
});
