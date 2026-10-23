@echo off
REM Script para generar APK de SerchWebView en Windows
REM Ejecutar: build-apk.bat

echo 🚀 Generando APK de SerchWebView...
echo.

REM Verificar dependencias
echo 📋 Verificando dependencias...
where node >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Error: Node.js no está instalado
    pause
    exit /b 1
)

where java >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Error: Java no está instalado
    pause
    exit /b 1
)

REM Instalar dependencias npm
echo 📦 Instalando dependencias npm...
call npm install

REM Compilar la app web
echo 🔨 Compilando app web...
call npm run build

REM Verificar si ya existe la carpeta android
if not exist "android" (
    echo 📱 Agregando plataforma Android...
    call npx cap add android
) else (
    echo ✅ Plataforma Android ya existe
)

REM Sincronizar archivos
echo 🔄 Sincronizando archivos...
call npx cap sync android

REM Generar APK debug
echo 🏗️  Generando APK debug...
cd android
call gradlew.bat assembleDebug

REM Verificar si se generó el APK
if exist "app\build\outputs\apk\debug\app-debug.apk" (
    echo.
    echo ✅ ¡APK generado exitosamente!
    echo 📍 Ubicación: android\app\build\outputs\apk\debug\app-debug.apk
    echo.
    echo 📲 Para instalar en tu dispositivo Android:
    echo    1. Copia el APK a tu dispositivo
    echo    2. Habilita 'Instalar desde fuentes desconocidas'
    echo    3. Abre el APK desde el explorador de archivos
) else (
    echo ❌ Error: No se pudo generar el APK
    pause
    exit /b 1
)

cd ..

echo.
echo 🎉 ¡Proceso completado!
pause
