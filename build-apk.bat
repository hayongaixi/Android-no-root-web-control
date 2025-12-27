@echo off
echo ================================================
echo   Android APK Build Script
echo ================================================
echo.

cd /d "%~dp0"

echo [1/3] Checking Java installation...
java -version >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java JDK and set JAVA_HOME
    pause
    exit /b 1
)
echo OK: Java is installed
echo.

echo [2/3] Cleaning previous builds...
call gradlew.bat clean
if %ERRORLEVEL% neq 0 (
    echo ERROR: Clean failed
    pause
    exit /b 1
)
echo OK: Clean completed
echo.

echo [3/3] Building APK...
call gradlew.bat assembleDebug
if %ERRORLEVEL% neq 0 (
    echo ERROR: Build failed
    pause
    exit /b 1
)
echo.

echo ================================================
echo   APK Build Successful!
echo ================================================
echo.
echo APK Location:
echo   app\build\outputs\apk\debug\app-debug.apk
echo.
echo You can now install this APK on your Android device.
echo.
echo To install via ADB:
echo   adb install app\build\outputs\apk\debug\app-debug.apk
echo.
echo Or transfer the APK file to your device and install.
echo.
pause
