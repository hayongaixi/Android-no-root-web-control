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
            socket = IO.socket(SERVER_URL);
            
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    isConnected = true;
                    Log.d(TAG, "Connected to server");
                    updateNotification();
                    
                    // 注册设备
                    JSONObject deviceData = new JSONObject();
                    try {
                        deviceData.put("name", android.os.Build.MODEL);
                        socket.emit("register_device", deviceData);
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
            
            socket.on(Socket.EVENT_ERROR, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.e(TAG, "Socket error: " + args[0]);
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
            
            socket.connect();
            
        } catch (URISyntaxException e) {
            Log.e(TAG, "Invalid server URL: " + e.getMessage());
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
            Log.d(TAG, "Received command: " + action);
            
            MyAccessibilityService accessibilityService = MyAccessibilityService.getInstance();
            if (accessibilityService == null) {
                Log.e(TAG, "Accessibility service not available");
                return;
            }
            
            switch (action) {
                case "click":
                    float x = (float) commandData.getDouble("x");
                    float y = (float) commandData.getDouble("y");
                    accessibilityService.click(x, y);
                    break;
                    
                case "swipe":
                    float x1 = (float) commandData.getDouble("x1");
                    float y1 = (float) commandData.getDouble("y1");
                    float x2 = (float) commandData.getDouble("x2");
                    float y2 = (float) commandData.getDouble("y2");
                    int duration = commandData.optInt("duration", 300);
                    accessibilityService.swipe(x1, y1, x2, y2, duration);
                    break;
                    
                case "longClick":
                    float lx = (float) commandData.getDouble("x");
                    float ly = (float) commandData.getDouble("y");
                    int lduration = commandData.optInt("duration", 1000);
                    accessibilityService.longClick(lx, ly, lduration);
                    break;
                    
                case "getScreen":
                    String screenContent = accessibilityService.getScreenContent();
                    JSONObject response = new JSONObject();
                    response.put("result", screenContent);
                    socket.emit("command_result", response);
                    break;
                    
                default:
                    Log.w(TAG, "Unknown command: " + action);
            }
            
        } catch (JSONException e) {
            Log.e(TAG, "Error handling command: " + e.getMessage());
        }
    }
}
