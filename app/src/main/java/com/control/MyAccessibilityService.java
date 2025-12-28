<<<<<<< HEAD
package com.control;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyAccessibilityService extends AccessibilityService {
    
    private static final String TAG = "AccessibilityService";
    private static MyAccessibilityService instance;
    private ExecutorService gestureExecutor;
    
    public static MyAccessibilityService getInstance() {
        return instance;
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        gestureExecutor = Executors.newSingleThreadExecutor();
        Log.d(TAG, "Accessibility Service Created");
    }
    
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // 处理无障碍事件
        Log.d(TAG, "Event: " + event.getEventType());
    }
    
    @Override
    public void onInterrupt() {
        Log.d(TAG, "Service Interrupted");
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
        if (gestureExecutor != null) {
            gestureExecutor.shutdown();
        }
    }
    
    /**
     * 点击指定坐标
     */
    public boolean click(float x, float y) {
        if (gestureExecutor == null) return false;
        
        final float finalX = x;
        final float finalY = y;
        
        gestureExecutor.execute(() -> {
            try {
                Path path = new Path();
                path.moveTo(finalX, finalY);
                GestureDescription gesture = new GestureDescription.Builder()
                    .addStroke(new GestureDescription.StrokeDescription(path, 0, 100))
                    .build();
                
                boolean result = dispatchGesture(gesture, null, null);
                Log.d(TAG, "Click at (" + finalX + ", " + finalY + "): " + result);
            } catch (Exception e) {
                Log.e(TAG, "Click error: " + e.getMessage());
            }
        });
        
        return true;
    }
    
    /**
     * 滑动操作
     */
    public boolean swipe(float x1, float y1, float x2, float y2, int duration) {
        if (gestureExecutor == null) return false;
        
        final float finalX1 = x1;
        final float finalY1 = y1;
        final float finalX2 = x2;
        final float finalY2 = y2;
        final int finalDuration = duration;
        
        gestureExecutor.execute(() -> {
            try {
                Path path = new Path();
                path.moveTo(finalX1, finalY1);
                path.lineTo(finalX2, finalY2);
                GestureDescription gesture = new GestureDescription.Builder()
                    .addStroke(new GestureDescription.StrokeDescription(path, 0, finalDuration))
                    .build();
                
                boolean result = dispatchGesture(gesture, null, null);
                Log.d(TAG, "Swipe from (" + finalX1 + ", " + finalY1 + ") to (" + finalX2 + ", " + finalY2 + "): " + result);
            } catch (Exception e) {
                Log.e(TAG, "Swipe error: " + e.getMessage());
            }
        });
        
        return true;
    }
    
    /**
     * 长按操作
     */
    public boolean longClick(float x, float y, int duration) {
        if (gestureExecutor == null) return false;
        
        final float finalX = x;
        final float finalY = y;
        final int finalDuration = duration;
        
        gestureExecutor.execute(() -> {
            try {
                Path path = new Path();
                path.moveTo(finalX, finalY);
                GestureDescription gesture = new GestureDescription.Builder()
                    .addStroke(new GestureDescription.StrokeDescription(path, 0, finalDuration))
                    .build();
                
                boolean result = dispatchGesture(gesture, null, null);
                Log.d(TAG, "Long click at (" + finalX + ", " + finalY + "): " + result);
            } catch (Exception e) {
                Log.e(TAG, "Long click error: " + e.getMessage());
            }
        });
        
        return true;
    }
    
    /**
     * 查找包含指定文本的节点
     */
    public AccessibilityNodeInfo findNodeByText(String text) {
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return null;
        
        return findNodeByTextRecursive(root, text);
    }
    
    private AccessibilityNodeInfo findNodeByTextRecursive(AccessibilityNodeInfo node, String text) {
        if (node == null) return null;
        
        CharSequence charSequence = node.getText();
        if (charSequence != null && charSequence.toString().contains(text)) {
            return node;
        }
        
        CharSequence contentDesc = node.getContentDescription();
        if (contentDesc != null && contentDesc.toString().contains(text)) {
            return node;
        }
        
        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo result = findNodeByTextRecursive(node.getChild(i), text);
            if (result != null) {
                return result;
            }
        }
        
        return null;
    }
    
    /**
     * 查找包含指定ID的节点
     */
    public AccessibilityNodeInfo findNodeById(String id) {
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return null;
        
        return findNodeByIdRecursive(root, id);
    }
    
    private AccessibilityNodeInfo findNodeByIdRecursive(AccessibilityNodeInfo node, String id) {
        if (node == null) return null;
        
        String viewId = node.getViewIdResourceName();
        if (viewId != null && viewId.contains(id)) {
            return node;
        }
        
        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo result = findNodeByIdRecursive(node.getChild(i), id);
            if (result != null) {
                return result;
            }
        }
        
        return null;
    }
    
    /**
     * 点击节点
     */
    public boolean clickNode(AccessibilityNodeInfo node) {
        if (node == null) return false;
        return node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
    }
    
    /**
     * 获取屏幕内容信息
     */
    public String getScreenContent() {
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return "无法获取屏幕内容";
        
        StringBuilder sb = new StringBuilder();
        traverseNode(root, sb, 0);
        return sb.toString();
    }
    
    private void traverseNode(AccessibilityNodeInfo node, StringBuilder sb, int depth) {
        if (node == null) return;
        
        for (int i = 0; i < depth; i++) {
            sb.append("  ");
        }
        
        CharSequence text = node.getText();
        CharSequence contentDesc = node.getContentDescription();
        Rect bounds = new Rect();
        node.getBoundsInScreen(bounds);
        
        sb.append("View: ")
          .append(text != null ? text : "")
          .append(contentDesc != null ? " (" + contentDesc + ")" : "")
          .append(" [").append(bounds.left).append(",").append(bounds.top)
          .append("-").append(bounds.right).append(",").append(bounds.bottom).append("]\n");
        
        for (int i = 0; i < node.getChildCount(); i++) {
            traverseNode(node.getChild(i), sb, depth + 1);
        }
    }
}
=======
package com.control;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyAccessibilityService extends AccessibilityService {
    
    private static final String TAG = "AccessibilityService";
    private static MyAccessibilityService instance;
    private ExecutorService gestureExecutor;
    
    public static MyAccessibilityService getInstance() {
        return instance;
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        gestureExecutor = Executors.newSingleThreadExecutor();
        Log.d(TAG, "Accessibility Service Created");
    }
    
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // 处理无障碍事件
        Log.d(TAG, "Event: " + event.getEventType());
    }
    
    @Override
    public void onInterrupt() {
        Log.d(TAG, "Service Interrupted");
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
        if (gestureExecutor != null) {
            gestureExecutor.shutdown();
        }
    }
    
    /**
     * 点击指定坐标
     */
    public boolean click(float x, float y) {
        if (gestureExecutor == null) return false;
        
        final float finalX = x;
        final float finalY = y;
        
        gestureExecutor.execute(() -> {
            try {
                Path path = new Path();
                path.moveTo(finalX, finalY);
                GestureDescription gesture = new GestureDescription.Builder()
                    .addStroke(new GestureDescription.StrokeDescription(path, 0, 100))
                    .build();
                
                boolean result = dispatchGesture(gesture, null, null);
                Log.d(TAG, "Click at (" + finalX + ", " + finalY + "): " + result);
            } catch (Exception e) {
                Log.e(TAG, "Click error: " + e.getMessage());
            }
        });
        
        return true;
    }
    
    /**
     * 滑动操作
     */
    public boolean swipe(float x1, float y1, float x2, float y2, int duration) {
        if (gestureExecutor == null) return false;
        
        final float finalX1 = x1;
        final float finalY1 = y1;
        final float finalX2 = x2;
        final float finalY2 = y2;
        final int finalDuration = duration;
        
        gestureExecutor.execute(() -> {
            try {
                Path path = new Path();
                path.moveTo(finalX1, finalY1);
                path.lineTo(finalX2, finalY2);
                GestureDescription gesture = new GestureDescription.Builder()
                    .addStroke(new GestureDescription.StrokeDescription(path, 0, finalDuration))
                    .build();
                
                boolean result = dispatchGesture(gesture, null, null);
                Log.d(TAG, "Swipe from (" + finalX1 + ", " + finalY1 + ") to (" + finalX2 + ", " + finalY2 + "): " + result);
            } catch (Exception e) {
                Log.e(TAG, "Swipe error: " + e.getMessage());
            }
        });
        
        return true;
    }
    
    /**
     * 长按操作
     */
    public boolean longClick(float x, float y, int duration) {
        if (gestureExecutor == null) return false;
        
        final float finalX = x;
        final float finalY = y;
        final int finalDuration = duration;
        
        gestureExecutor.execute(() -> {
            try {
                Path path = new Path();
                path.moveTo(finalX, finalY);
                GestureDescription gesture = new GestureDescription.Builder()
                    .addStroke(new GestureDescription.StrokeDescription(path, 0, finalDuration))
                    .build();
                
                boolean result = dispatchGesture(gesture, null, null);
                Log.d(TAG, "Long click at (" + finalX + ", " + finalY + "): " + result);
            } catch (Exception e) {
                Log.e(TAG, "Long click error: " + e.getMessage());
            }
        });
        
        return true;
    }
    
    /**
     * 查找包含指定文本的节点
     */
    public AccessibilityNodeInfo findNodeByText(String text) {
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return null;
        
        return findNodeByTextRecursive(root, text);
    }
    
    private AccessibilityNodeInfo findNodeByTextRecursive(AccessibilityNodeInfo node, String text) {
        if (node == null) return null;
        
        CharSequence charSequence = node.getText();
        if (charSequence != null && charSequence.toString().contains(text)) {
            return node;
        }
        
        CharSequence contentDesc = node.getContentDescription();
        if (contentDesc != null && contentDesc.toString().contains(text)) {
            return node;
        }
        
        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo result = findNodeByTextRecursive(node.getChild(i), text);
            if (result != null) {
                return result;
            }
        }
        
        return null;
    }
    
    /**
     * 查找包含指定ID的节点
     */
    public AccessibilityNodeInfo findNodeById(String id) {
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return null;
        
        return findNodeByIdRecursive(root, id);
    }
    
    private AccessibilityNodeInfo findNodeByIdRecursive(AccessibilityNodeInfo node, String id) {
        if (node == null) return null;
        
        String viewId = node.getViewIdResourceName();
        if (viewId != null && viewId.contains(id)) {
            return node;
        }
        
        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo result = findNodeByIdRecursive(node.getChild(i), id);
            if (result != null) {
                return result;
            }
        }
        
        return null;
    }
    
    /**
     * 点击节点
     */
    public boolean clickNode(AccessibilityNodeInfo node) {
        if (node == null) return false;
        return node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
    }
    
    /**
     * 获取屏幕内容信息
     */
    public String getScreenContent() {
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return "无法获取屏幕内容";
        
        StringBuilder sb = new StringBuilder();
        traverseNode(root, sb, 0);
        return sb.toString();
    }
    
    private void traverseNode(AccessibilityNodeInfo node, StringBuilder sb, int depth) {
        if (node == null) return;
        
        for (int i = 0; i < depth; i++) {
            sb.append("  ");
        }
        
        CharSequence text = node.getText();
        CharSequence contentDesc = node.getContentDescription();
        Rect bounds = new Rect();
        node.getBoundsInScreen(bounds);
        
        sb.append("View: ")
          .append(text != null ? text : "")
          .append(contentDesc != null ? " (" + contentDesc + ")" : "")
          .append(" [").append(bounds.left).append(",").append(bounds.top)
          .append("-").append(bounds.right).append(",").append(bounds.bottom).append("]\n");
        
        for (int i = 0; i < node.getChildCount(); i++) {
            traverseNode(node.getChild(i), sb, depth + 1);
        }
    }
}
>>>>>>> 352a9af7aaa9a9aa35dc5091be9f0a7961e36bb5
