@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

echo.
echo ╔══════════════════════════════════════════════════════════╗
echo ║     Android APK 自动构建工具                            ║
echo ║     No-Root Control System                              ║
echo ╚══════════════════════════════════════════════════════════╝
echo.

REM 检查Java
echo [步骤 1/5] 检查Java环境...
java -version >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo   ❌ 未检测到Java
    echo.
    echo   需要安装Java JDK才能构建Android项目
    echo.
    echo   请按以下步骤操作：
    echo.
    echo   1. 下载Java JDK（推荐JDK 11或17）
    echo   2. 安装后设置环境变量：
    echo      - 系统环境变量：JAVA_HOME（指向JDK安装目录）
    echo      - PATH：添加 %%JAVA_HOME%%\bin
    echo.
    echo   下载地址：
    echo   https://www.oracle.com/java/technologies/downloads/
    echo   或使用OpenJDK：
    echo   https://adoptium.net/
    echo.
    echo   安装完成后重新运行此脚本
    echo.
    pause
    exit /b 1
)

for /f "tokens=3" %%v in ('java -version 2^>^&1 ^| findstr /i "version"') do (
    set java_version=%%v
    set java_version=!java_version:"=!
)
echo   ✓ Java已安装: !java_version!
echo.

REM 检查Android SDK
echo [步骤 2/5] 检查Android SDK环境...
set ANDROID_CMD=adb
where %ANDROID_CMD% >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo   ❌ 未检测到Android SDK (adb命令不可用)
    echo.
    echo   最简单的方法是安装Android Studio：
    echo.
    echo   下载地址：
    echo   https://developer.android.com/studio
    echo.
    echo   安装后：
    echo   1. 打开Android Studio
    echo   2. Tools → SDK Manager
    echo   3. 安装Android SDK Platform-Tools
    echo   4. 安装Android SDK Build-Tools 34.0.0
    echo.
    pause
    exit /b 1
)

where adb >nul 2>&1
for /f "delims=" %%i in ('where adb') do set ADB_PATH=%%i
echo   ✓ Android SDK已安装: %ADB_PATH%
echo.

REM 进入项目目录
cd /d "%~dp0"

REM 清理旧构建
echo [步骤 3/5] 清理旧构建文件...
if exist gradlew.bat (
    call gradlew.bat clean >nul 2>&1
    if %ERRORLEVEL% equ 0 (
        echo   ✓ 清理完成
    ) else (
        echo   ⚠ 清理时出现警告，继续构建...
    )
) else (
    echo   ⚠ 未找到gradlew.bat，将直接构建
)
echo.

REM 构建APK
echo [步骤 4/5] 开始构建APK...
echo   这可能需要几分钟，首次运行会下载依赖...
echo.

if not exist gradlew.bat (
    echo   ❌ 错误：未找到gradlew.bat
    echo   请确保项目结构完整
    pause
    exit /b 1
)

call gradlew.bat assembleDebug

if %ERRORLEVEL% neq 0 (
    echo.
    echo   ❌ 构建失败！
    echo.
    echo   可能的原因：
    echo   1. Gradle下载失败（网络问题）
    echo   2. Android SDK版本不匹配
    echo   3. 项目配置错误
    echo.
    echo   请检查上方错误信息
    echo.
    pause
    exit /b 1
)

echo.
echo   ✓ 构建成功！
echo.

REM 检查输出文件
echo [步骤 5/5] 检查生成的APK文件...
set APK_PATH=app\build\outputs\apk\debug\app-debug.apk

if exist "%APK_PATH%" (
    for %%F in ("%APK_PATH%") do set APK_SIZE=%%~zF
    set /a APK_SIZE_MB=%APK_SIZE% / 1024 / 1024
    echo   ✓ APK文件已生成
    echo.
    echo   ════════════════════════════════════════════════════════
    echo   📱 APK信息：
    echo      位置: %CD%\%APK_PATH%
    echo      大小: !APK_SIZE_MB! MB
    echo.
    echo   ════════════════════════════════════════════════════════
    echo.
    echo   安装方法：
    echo.
    echo   方法1 - 使用ADB：
    echo   adb install "%APK_PATH%"
    echo.
    echo   方法2 - 直接传输：
    echo   将APK文件复制到手机，点击安装
    echo.
    echo   ════════════════════════════════════════════════════════
    echo.
    echo   首次安装后需要配置：
    echo   1. 打开手机设置 → 无障碍 → 启用 "Control Service"
    echo   2. 允许应用在其他应用上层显示
    echo   3. 访问 http://101.35.144.210:3000 开始控制
    echo.
) else (
    echo   ❌ 未找到APK文件
    echo   期望位置: %CD%\%APK_PATH%
    echo.
    pause
    exit /b 1
)

echo.
echo 按任意键打开APK所在文件夹...
pause >nul
explorer "%CD%\app\build\outputs\apk\debug\"

echo.
echo ✓ 构建完成！
endlocal
