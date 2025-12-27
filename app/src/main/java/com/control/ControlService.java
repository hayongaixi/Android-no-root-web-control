package com.control;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ControlService extends Service {
    
    private static final String TAG = "ControlService";
    private static final String CHANNEL_ID = "ControlServiceChannel";
    private static final int SERVER_PORT = 8888;
    
    private ServerSocket serverSocket;
    private boolean isRunning = false;
    
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(1, createNotification());
        startServer();
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
        isRunning = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "Error closing server socket: " + e.getMessage());
        }
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
            .setContentText("Running...")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .build();
    }
    
    private void startServer() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(SERVER_PORT);
                isRunning = true;
                Log.d(TAG, "Server started on port " + SERVER_PORT);
                
                while (isRunning) {
                    Socket clientSocket = serverSocket.accept();
                    Log.d(TAG, "Client connected: " + clientSocket.getInetAddress());
                    new Thread(new ClientHandler(clientSocket)).start();
                }
            } catch (IOException e) {
                Log.e(TAG, "Server error: " + e.getMessage());
            }
        }).start();
    }
    
    private class ClientHandler implements Runnable {
        private Socket socket;
        
        public ClientHandler(Socket socket) {
            this.socket = socket;
        }
        
        @Override
        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                OutputStream output = socket.getOutputStream();
                String line;
                
                while ((line = reader.readLine()) != null) {
                    Log.d(TAG, "Received command: " + line);
                    String response = handleCommand(line);
                    output.write((response + "\n").getBytes());
                    output.flush();
                }
            } catch (IOException e) {
                Log.e(TAG, "Client handler error: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing socket: " + e.getMessage());
                }
            }
        }
        
        private String handleCommand(String command) {
            MyAccessibilityService accessibilityService = MyAccessibilityService.getInstance();
            if (accessibilityService == null) {
                return "ERROR: Accessibility service not available";
            }
            
            try {
                String[] parts = command.split("\\s+");
                String action = parts[0];
                
                switch (action) {
                    case "CLICK":
                        if (parts.length >= 3) {
                            float x = Float.parseFloat(parts[1]);
                            float y = Float.parseFloat(parts[2]);
                            accessibilityService.click(x, y);
                            return "OK";
                        }
                        return "ERROR: Invalid CLICK command";
                    
                    case "SWIPE":
                        if (parts.length >= 5) {
                            float x1 = Float.parseFloat(parts[1]);
                            float y1 = Float.parseFloat(parts[2]);
                            float x2 = Float.parseFloat(parts[3]);
                            float y2 = Float.parseFloat(parts[4]);
                            int duration = parts.length >= 6 ? Integer.parseInt(parts[5]) : 300;
                            accessibilityService.swipe(x1, y1, x2, y2, duration);
                            return "OK";
                        }
                        return "ERROR: Invalid SWIPE command";
                    
                    case "LONG_CLICK":
                        if (parts.length >= 3) {
                            float x = Float.parseFloat(parts[1]);
                            float y = Float.parseFloat(parts[2]);
                            int duration = parts.length >= 4 ? Integer.parseInt(parts[3]) : 1000;
                            accessibilityService.longClick(x, y, duration);
                            return "OK";
                        }
                        return "ERROR: Invalid LONG_CLICK command";
                    
                    case "GET_SCREEN":
                        String screenContent = accessibilityService.getScreenContent();
                        return screenContent;
                    
                    case "FIND_TEXT":
                        if (parts.length >= 2) {
                            String text = command.substring(command.indexOf(' ') + 1);
                            var node = accessibilityService.findNodeByText(text);
                            if (node != null) {
                                return "FOUND";
                            }
                            return "NOT_FOUND";
                        }
                        return "ERROR: Invalid FIND_TEXT command";
                    
                    case "CLICK_TEXT":
                        if (parts.length >= 2) {
                            String text = command.substring(command.indexOf(' ') + 1);
                            var node = accessibilityService.findNodeByText(text);
                            if (node != null) {
                                boolean result = accessibilityService.clickNode(node);
                                return result ? "OK" : "FAILED";
                            }
                            return "NOT_FOUND";
                        }
                        return "ERROR: Invalid CLICK_TEXT command";
                    
                    default:
                        return "ERROR: Unknown command";
                }
            } catch (Exception e) {
                Log.e(TAG, "Command execution error: " + e.getMessage());
                return "ERROR: " + e.getMessage();
            }
        }
    }
}
