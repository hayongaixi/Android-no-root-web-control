package com.control;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import java.net.URISyntaxException;

public class ControlService extends Service {
    
    private static final String TAG = "ControlService";
    private static final String CHANNEL_ID = "ControlServiceChannel";
    private static final String SERVER_URL = "http://101.35.144.210:3000";
    
    private Socket socket;
    private boolean isConnected = false;
    
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(1, createNotification());
        connectToServer();
        Log.d(TAG, "ControlService created");
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "ControlService started");
        return START_STICKY;
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        disconnectFromServer();
        Log.d(TAG, "ControlService destroyed");
    }
    
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Control Service",
                NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Remote control service");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
    
    private Notification createNotification() {
        return new Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("Control Service")
            .setContentText(isConnected ? "Connected" : "Connecting...")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .build();
    }
    
    private void connectToServer() {
        try {
            Log.d(TAG, "Attempting to connect to: " + SERVER_URL);
            IO.Options options = new IO.Options();
            options.transports = new String[]{"websocket"};
            options.reconnection = true;
            options.reconnectionDelay = 5000;
            options.reconnectionDelayMax = 30000;
            options.randomizationFactor = 0.5;
            options.reconnectionAttempts = 20;
            options.timeout = 20000;
            socket = IO.socket(SERVER_URL, options);
            Log.d(TAG, "Socket object created with WebSocket transport");
            
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    isConnected = true;
                    Log.d(TAG, "========== CONNECTED TO SERVER ==========");
                    updateNotification();
                    
                    // 注册设备
                    JSONObject deviceData = new JSONObject();
                    try {
                        deviceData.put("name", android.os.Build.MODEL);
                        Log.d(TAG, "========== EMITTING register_device: " + deviceData.toString());
                        socket.emit("register_device", deviceData);
                        Log.d(TAG, "========== register_device EMITTED ==========");
                    } catch (JSONException e) {
                        Log.e(TAG, "Error creating device data: " + e.getMessage());
                    }
                }
            });
            
            socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    isConnected = false;
                    Log.d(TAG, "Disconnected from server");
                    updateNotification();
                }
            });
            
            socket.on("error", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.e(TAG, "========== SOCKET ERROR ==========" + (args.length > 0 ? args[0] : "unknown"));
                }
            });

            socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.e(TAG, "========== CONNECT ERROR ==========" + (args.length > 0 ? args[0] : "unknown"));
                }
            });
            
            socket.on("execute_command", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    if (args.length > 0) {
                        try {
                            JSONObject commandData = new JSONObject(args[0].toString());
                            handleCommand(commandData);
                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing command: " + e.getMessage());
                        }
                    }
                }
            });
            
            Log.d(TAG, "========== CALLING socket.connect() ==========");
            socket.connect();
            Log.d(TAG, "========== socket.connect() CALLED ==========");
            
        } catch (URISyntaxException e) {
            Log.e(TAG, "Invalid server URL: " + e.getMessage(), e);
        } catch (Exception e) {
            Log.e(TAG, "Connection error: " + e.getMessage(), e);
        }
    }
    
    private void disconnectFromServer() {
        if (socket != null) {
            socket.disconnect();
            socket = null;
        }
    }
    
    private void updateNotification() {
        Notification notification = createNotification();
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.notify(1, notification);
    }
    
    private void handleCommand(JSONObject commandData) {
        try {
            String action = commandData.getString("action");
            Log.d(TAG, "========== Received command: " + action + " ==========");
            
            MyAccessibilityService accessibilityService = MyAccessibilityService.getInstance();
            if (accessibilityService == null) {
                Log.e(TAG, "========== Accessibility service not available ==========");
                JSONObject errorResponse = new JSONObject();
                errorResponse.put("error", "Accessibility service not available");
                socket.emit("command_result", errorResponse);
                return;
            }
            
            JSONObject response = new JSONObject();
            
            switch (action) {
                case "click":
                    float x = (float) commandData.getDouble("x");
                    float y = (float) commandData.getDouble("y");
                    boolean clickResult = accessibilityService.click(x, y);
                    response.put("result", "Click executed: " + clickResult);
                    Log.d(TAG, "========== Click executed: " + clickResult + " ==========");
                    break;
                    
                case "swipe":
                    float x1 = (float) commandData.getDouble("x1");
                    float y1 = (float) commandData.getDouble("y1");
                    float x2 = (float) commandData.getDouble("x2");
                    float y2 = (float) commandData.getDouble("y2");
                    int duration = commandData.optInt("duration", 300);
                    boolean swipeResult = accessibilityService.swipe(x1, y1, x2, y2, duration);
                    response.put("result", "Swipe executed: " + swipeResult);
                    Log.d(TAG, "========== Swipe executed: " + swipeResult + " ==========");
                    break;
                    
                case "longClick":
                    float lx = (float) commandData.getDouble("x");
                    float ly = (float) commandData.getDouble("y");
                    int lduration = commandData.optInt("duration", 1000);
                    boolean longClickResult = accessibilityService.longClick(lx, ly, lduration);
                    response.put("result", "Long click executed: " + longClickResult);
                    Log.d(TAG, "========== Long click executed: " + longClickResult + " ==========");
                    break;
                    
                case "getScreen":
                    Log.d(TAG, "========== Calling getScreenContent ==========");
                    String screenContent = accessibilityService.getScreenContent();
                    Log.d(TAG, "========== getScreenContent returned: " + (screenContent != null ? screenContent.length() + " chars" : "null"));
                    response.put("result", screenContent);
                    break;
                    
                default:
                    Log.w(TAG, "Unknown command: " + action);
                    response.put("error", "Unknown command: " + action);
            }
            
            Log.d(TAG, "========== EMITTING command_result: " + response.toString() + " ==========");
            socket.emit("command_result", response);
            Log.d(TAG, "========== command_result EMITTED ==========");
            
        } catch (JSONException e) {
            Log.e(TAG, "========== Error handling command: " + e.getMessage() + " ==========");
            try {
                JSONObject errorResponse = new JSONObject();
                errorResponse.put("error", "Error handling command: " + e.getMessage());
                socket.emit("command_result", errorResponse);
            } catch (JSONException ex) {
                Log.e(TAG, "Error sending error response: " + ex.getMessage());
            }
        }
    }
}
