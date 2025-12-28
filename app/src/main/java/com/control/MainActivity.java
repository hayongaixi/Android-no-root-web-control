<<<<<<< HEAD
package com.control;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        finish();
        
        // 检查无障碍服务权限
        if (!isAccessibilityServiceEnabled(this)) {
            openAccessibilitySettings();
        }
        
        // 检查悬浮窗权限
        if (!Settings.canDrawOverlays(this)) {
            openOverlaySettings();
        }
        
        // 启动控制服务
        startService(new Intent(this, ControlService.class));
        
        Toast.makeText(this, "服务已启动", Toast.LENGTH_SHORT).show();
    }
    
    private boolean isAccessibilityServiceEnabled(Context context) {
        String service = getPackageName() + "/com.control.MyAccessibilityService";
        String enabledServices = Settings.Secure.getString(
            context.getContentResolver(),
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        );
        return enabledServices != null && enabledServices.contains(service);
    }
    
    private void openAccessibilitySettings() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    
    private void openOverlaySettings() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
=======
package com.control;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        finish();
        
        // 检查无障碍服务权限
        if (!isAccessibilityServiceEnabled(this)) {
            openAccessibilitySettings();
        }
        
        // 检查悬浮窗权限
        if (!Settings.canDrawOverlays(this)) {
            openOverlaySettings();
        }
        
        // 启动控制服务
        startService(new Intent(this, ControlService.class));
        
        Toast.makeText(this, "服务已启动", Toast.LENGTH_SHORT).show();
    }
    
    private boolean isAccessibilityServiceEnabled(Context context) {
        String service = getPackageName() + "/com.control.MyAccessibilityService";
        String enabledServices = Settings.Secure.getString(
            context.getContentResolver(),
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        );
        return enabledServices != null && enabledServices.contains(service);
    }
    
    private void openAccessibilitySettings() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    
    private void openOverlaySettings() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
>>>>>>> 352a9af7aaa9a9aa35dc5091be9f0a7961e36bb5
