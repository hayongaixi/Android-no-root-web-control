# Android APK 打包指南

## 前置要求

### 1. 安装Java JDK
- 下载并安装 Java JDK 8 或更高版本
- 设置环境变量 `JAVA_HOME`
- 将 `%JAVA_HOME%\bin` 添加到系统 PATH

### 2. 安装Android SDK
- 下载并安装 Android Studio（包含SDK）
- 或单独安装 Android SDK Tools
- 设置环境变量 `ANDROID_HOME`
- 将 `%ANDROID_HOME%\tools` 和 `%ANDROID_HOME%\platform-tools` 添加到 PATH

### 3. 接受Android SDK许可
打开命令提示符（CMD）或PowerShell，运行：
```bash
sdkmanager --licenses
```
输入 `y` 接受所有许可

## 打包方法

### 方法一：使用打包脚本（推荐）

双击运行 `build-apk.bat`，脚本会自动完成：
1. 检查Java安装
2. 清理旧的构建文件
3. 编译并生成APK

### 方法二：手动命令行打包

1. 打开命令提示符或PowerShell

2. 进入项目目录：
```bash
cd d:/Windows-Users/Documents/Android/no-root-control
```

3. 清理旧构建：
```bash
gradlew.bat clean
```

4. 编译Debug版本APK：
```bash
gradlew.bat assembleDebug
```

5. 编译Release版本APK（需要签名）：
```bash
gradlew.bat assembleRelease
```

### 方法三：使用Android Studio

1. 打开Android Studio
2. File → Open → 选择项目文件夹
3. Build → Build Bundle(s) / APK(s) → Build APK(s)
4. 等待构建完成，点击通知中的 "locate" 查看APK

## APK位置

构建成功后，APK文件位于：
```
app\build\outputs\apk\debug\app-debug.apk
```

## 安装APK

### 方法一：使用ADB安装
```bash
adb devices  # 检查设备连接
adb install app\build\outputs\apk\debug\app-debug.apk
```

### 方法二：直接传输
1. 将APK文件复制到手机存储
2. 在手机上打开文件管理器
3. 点击APK文件安装
4. 允许"未知来源"应用安装

## 常见问题

### 1. gradlew.bat 找不到
- 确保在正确的目录下执行命令
- 检查项目根目录是否有gradlew.bat文件

### 2. Java版本不兼容
- 确保Java JDK版本为8或更高
- 检查JAVA_HOME环境变量设置

### 3. Android SDK未找到
- 确保ANDROID_HOME环境变量正确设置
- 检查SDK中的platform-tools和build-tools版本

### 4. 构建失败：license not accepted
```bash
sdkmanager --licenses
```
重新接受许可协议

### 5. 缺少图标资源
如果遇到图标相关错误，可以：
- 使用Android Studio生成默认图标
- 或使用在线工具生成ic_launcher
- 或暂时移除AndroidManifest.xml中的图标引用

## 调试版本 vs 发布版本

- **Debug版本**: 用于开发和测试，使用默认debug密钥签名
- **Release版本**: 用于正式发布，需要配置自己的签名密钥

配置Release签名：
```gradle
// app/build.gradle
android {
    signingConfigs {
        release {
            storeFile file("keystore.jks")
            storePassword "your_password"
            keyAlias "your_key_alias"
            keyPassword "your_key_password"
        }
    }
    buildTypes {
        release {
            signingConfig signingConfigs.release
        }
    }
}
```

## 注意事项

1. 首次构建会下载Gradle依赖，可能需要较长时间
2. 确保网络连接正常
3. 如果遇到网络问题，可以配置国内镜像源
4. Debug APK只能用于测试，不能发布到应用商店
