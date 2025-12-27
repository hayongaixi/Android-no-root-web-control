# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep Accessibility Service classes
-keep public class com.control.MyAccessibilityService { *; }
-keep public class com.control.ControlService { *; }
-keep public class com.control.BootReceiver { *; }

# Keep all classes in com.control package
-keep class com.control.** { *; }

# Keep AccessibilityService methods
-keepclassmembers class * extends android.accessibilityservice.AccessibilityService {
    public *;
}
