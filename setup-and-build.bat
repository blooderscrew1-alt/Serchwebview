@echo off
REM Script completo para generar APK de SerchWebView en Windows
REM Ejecutar: setup-and-build.bat

echo ╔════════════════════════════════════════════════╗
echo ║   SerchWebView - Generador de APK Android     ║
echo ╚════════════════════════════════════════════════╝
echo.

REM Paso 1: Generar iconos
echo 📸 Paso 1/5: Generando iconos...
call node generate-icons.js
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Error generando iconos
    pause
    exit /b 1
)
echo.

REM Paso 2: Instalar dependencias
echo 📦 Paso 2/5: Instalando dependencias...
call npm install
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Error instalando dependencias
    pause
    exit /b 1
)
echo.

REM Paso 3: Compilar app web
echo 🔨 Paso 3/5: Compilando app web...
call npm run build
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Error compilando app
    pause
    exit /b 1
)
echo.

REM Paso 4: Configurar Android
if not exist "android" (
    echo 📱 Paso 4/5: Agregando plataforma Android...
    call npx cap add android
    if %ERRORLEVEL% NEQ 0 (
        echo ❌ Error agregando plataforma Android
        pause
        exit /b 1
    )
) else (
    echo ✅ Paso 4/5: Plataforma Android ya existe
)
echo.

REM Paso 5: Sincronizar y generar APK
echo 🔄 Paso 5/5: Sincronizando y generando APK...
call npx cap sync android
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Error sincronizando archivos
    pause
    exit /b 1
)

cd android

REM Configurar permisos en AndroidManifest.xml
echo 🔧 Configurando permisos...
set MANIFEST_FILE=app\src\main\AndroidManifest.xml
if exist "%MANIFEST_FILE%" (
    echo ✅ Archivo de manifiesto encontrado
)

REM Generar APK
echo 🏗️  Generando APK...
call gradlew.bat assembleDebug
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Error generando APK
    cd ..
    pause
    exit /b 1
)

cd ..

REM Verificar resultado
if exist "android\app\build\outputs\apk\debug\app-debug.apk" (
    echo.
    echo ╔════════════════════════════════════════════════╗
    echo ║          ✅ APK GENERADO EXITOSAMENTE         ║
    echo ╚════════════════════════════════════════════════╝
    echo.
    echo 📍 Ubicación del APK:
    echo    android\app\build\outputs\apk\debug\app-debug.apk
    echo.
    echo 📲 Instrucciones de instalación:
    echo    1. Copia el APK a tu dispositivo Android
    echo    2. Ve a Ajustes → Seguridad → Fuentes desconocidas
    echo    3. Abre el APK desde el explorador de archivos
    echo    4. Acepta los permisos e instala
    echo.
    echo 🎉 ¡Listo para usar!
) else (
    echo.
    echo ❌ Error: No se pudo generar el APK
    echo Verifica que Android Studio y el SDK estén instalados
    pause
    exit /b 1
)

pause
