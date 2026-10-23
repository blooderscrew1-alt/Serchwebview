#!/bin/bash

# Script completo para generar APK de SerchWebView
# Uso: chmod +x setup-and-build.sh && ./setup-and-build.sh

set -e

echo "╔════════════════════════════════════════════════╗"
echo "║   SerchWebView - Generador de APK Android     ║"
echo "╚════════════════════════════════════════════════╝"
echo ""

# Paso 1: Generar iconos
echo "📸 Paso 1/5: Generando iconos..."
node generate-icons.js
echo ""

# Paso 2: Instalar dependencias
echo "📦 Paso 2/5: Instalando dependencias..."
npm install
echo ""

# Paso 3: Compilar app web
echo "🔨 Paso 3/5: Compilando app web..."
npm run build
echo ""

# Paso 4: Configurar Android
if [ ! -d "android" ]; then
    echo "📱 Paso 4/5: Agregando plataforma Android..."
    npx cap add android
else
    echo "✅ Paso 4/5: Plataforma Android ya existe"
fi
echo ""

# Paso 5: Sincronizar y generar APK
echo "🔄 Paso 5/5: Sincronizando y generando APK..."
npx cap sync android

cd android

# Configurar permisos en AndroidManifest.xml
echo "🔧 Configurando permisos..."
MANIFEST_FILE="app/src/main/AndroidManifest.xml"
if [ -f "$MANIFEST_FILE" ]; then
    # Agregar permisos si no existen
    if ! grep -q "android.permission.INTERNET" "$MANIFEST_FILE"; then
        sed -i '/<manifest/a\    <uses-permission android:name="android.permission.INTERNET"/>\n    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>' "$MANIFEST_FILE"
        echo "✅ Permisos agregados"
    fi
fi

# Configurar WebView
echo "🔧 Configurando WebView..."
STRINGS_FILE="app/src/main/res/values/strings.xml"
if [ -f "$STRINGS_FILE" ]; then
    echo "✅ Archivos de recursos encontrados"
fi

# Generar APK
echo "🏗️  Generando APK..."
./gradlew assembleDebug

cd ..

# Verificar resultado
if [ -f "android/app/build/outputs/apk/debug/app-debug.apk" ]; then
    echo ""
    echo "╔════════════════════════════════════════════════╗"
    echo "║          ✅ APK GENERADO EXITOSAMENTE         ║"
    echo "╚════════════════════════════════════════════════╝"
    echo ""
    echo "📍 Ubicación del APK:"
    echo "   android/app/build/outputs/apk/debug/app-debug.apk"
    echo ""
    echo "📲 Instrucciones de instalación:"
    echo "   1. Copia el APK a tu dispositivo Android"
    echo "   2. Ve a Ajustes → Seguridad → Fuentes desconocidas"
    echo "   3. Abre el APK desde el explorador de archivos"
    echo "   4. Acepta los permisos e instala"
    echo ""
    echo "🎉 ¡Listo para usar!"
else
    echo ""
    echo "❌ Error: No se pudo generar el APK"
    echo "Verifica que Android Studio y el SDK estén instalados"
    exit 1
fi
