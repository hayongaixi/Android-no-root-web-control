# APK构建快速指南

## 🚀 最简单的方法 - 使用在线构建（推荐）

如果您没有安装Android开发环境，可以使用在线构建服务：

### 方法1：GitHub Actions（免费）

1. **注册GitHub账号**（如果没有）：https://github.com/

2. **创建GitHub仓库**
   - 访问 https://github.com/new
   - 仓库名称：`no-root-control`
   - 选择Public或Private
   - 点击"Create repository"

3. **上传项目到GitHub**
   - 进入新创建的仓库
   - 点击"uploading an existing file"
   - 将整个项目文件夹拖入上传
   - 点击"Commit changes"

4. **触发构建**
   - 进入仓库页面
   - 点击"Actions"标签
   - 选择"Build Android APK"工作流
   - 点击"Run workflow"
   - 选择"debug"
   - 点击"Run workflow"按钮

5. **下载APK**
   - 等待构建完成（约2-5分钟）
   - 构建成功后，在Actions页面找到该次运行
   - 滚动到底部，点击"Artifacts"
   - 下载`app-debug.zip`
   - 解压得到APK文件

### 方法2：使用在线构建网站

如果不想用GitHub，可以使用这些在线构建服务：

- **AppVeyor**: https://www.appveyor.com/
- **Codemagic**: https://codemagic.io/
- **Bitrise**: https://www.bitrise.io/

这些服务通常提供免费额度，可以直接上传代码或连接GitHub仓库自动构建。

---

## 💻 本地构建（需要安装开发环境）

### 准备工作

#### 1. 安装Java JDK
```
推荐使用OpenJDK（免费）：
下载地址：https://adoptium.net/

或使用Oracle JDK：
下载地址：https://www.oracle.com/java/technologies/downloads/

建议版本：JDK 11 或 JDK 17（LTS版本）

安装步骤：
1. 下载安装包
2. 运行安装程序
3. 设置环境变量 JAVA_HOME
4. 重启命令行窗口
```

#### 2. 安装Android开发工具

**方案A：安装Android Studio（推荐，最简单）**

```
下载地址：https://developer.android.com/studio

安装步骤：
1. 下载并安装Android Studio
2. 首次启动会自动安装Android SDK
3. 等待SDK下载完成（需要15-30分钟）
```

**方案B：仅安装Android SDK Command-line Tools**

```
下载地址：https://developer.android.com/studio#command-tools

安装步骤：
1. 下载Command-line Tools
2. 解压到 C:\android-sdk\
3. 使用sdkmanager安装所需组件：
   - Platform Tools (adb等)
   - Build Tools 34.0.0
   - Android Platform 34

4. 设置环境变量：
   ANDROID_HOME = C:\android-sdk
   PATH添加：
   - %ANDROID_HOME%\cmdline-tools\latest\bin
   - %ANDROID_HOME%\platform-tools
```

### 验证安装

打开新的命令提示符窗口，运行：

```bash
# 检查Java
java -version

# 检查Android SDK
adb version
```

如果都显示版本号，说明安装成功！

### 开始构建

#### 方法A：使用一键构建脚本

```bash
# 进入项目目录
cd d:/Windows-Users/Documents/Android/no-root-control

# 运行构建脚本
build-apk-advanced.bat
```

#### 方法B：手动构建

```bash
# 进入项目目录
cd d:/Windows-Users/Documents/Android/no-root-control

# 清理旧构建
gradlew.bat clean

# 构建Debug版APK
gradlew.bat assembleDebug

# 构建Release版APK（需要签名配置）
gradlew.bat assembleRelease
```

### 获取APK文件

构建成功后，APK位于：

```
app\build\outputs\apk\debug\app-debug.apk
```

---

## 📱 安装APK到手机

### 方法1：使用ADB安装（需要USB调试）

```bash
# 1. 在手机上启用开发者选项和USB调试
#    设置 → 关于手机 → 连续点击版本号7次
#    返回设置 → 开发者选项 → 启用USB调试

# 2. 连接手机到电脑（选择文件传输模式）

# 3. 测试连接
adb devices

# 4. 安装APK
adb install app\build\outputs\apk\debug\app-debug.apk
```

### 方法2：直接传输安装

```bash
# 1. 将APK文件复制到手机存储
#    - 使用USB数据线
#    - 或使用云盘/微信/QQ传输

# 2. 在手机上找到APK文件
#    使用文件管理器应用

# 3. 点击APK文件
#    允许"未知来源"应用安装

# 4. 完成安装
```

---

## ⚙️ 首次使用配置

安装APK后，需要配置权限：

1. **启用无障碍服务**
   - 打开手机设置
   - 进入"无障碍"或"辅助功能"
   - 找到"Control Service"
   - 启用服务

2. **允许悬浮窗权限**（如果提示）
   - 允许应用在其他应用上层显示

3. **验证服务运行**
   - 查看通知栏，应该有"Control Service Running"的通知

4. **访问控制面板**
   - 在浏览器打开：http://101.35.144.210:3000
   - 设备会自动连接

---

## ❓ 常见问题

### Q: 构建时提示"SDK location not found"
**A:** 需要设置ANDROID_HOME环境变量，指向Android SDK安装目录

### Q: 构建时提示"Could not resolve com.android.tools.build:gradle"
**A:** 网络问题，尝试配置国内镜像源

### Q: ADB设备未识别
**A:**
- 检查USB调试是否开启
- 更换USB接口或数据线
- 在手机上允许USB调试授权

### Q: 安装时提示"解析包错误"
**A:** APK文件损坏或版本不兼容，重新构建

### Q: 服务启动后无法连接到Web服务器
**A:** 检查网络连接，确保手机可以访问互联网

---

## 📞 获取帮助

如果遇到问题：

1. 查看构建日志中的错误信息
2. 确保所有环境变量已正确设置
3. 尝试使用在线构建服务
4. 访问GitHub Issues提交问题

---

## 🎯 推荐流程

**最快的方式（无需安装任何工具）：**

1. 使用GitHub Actions在线构建 ⭐⭐⭐⭐⭐
2. 下载生成的APK
3. 传输到手机安装
4. 配置权限
5. 开始使用！

**本机开发（适合持续开发）：**

1. 安装Android Studio
2. 克隆或打开项目
3. 运行build-apk-advanced.bat
4. 使用ADB直接安装测试
