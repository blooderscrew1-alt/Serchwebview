#!/bin/bash

# Script para generar APK de SerchWebView
# Ejecutar: chmod +x build-apk.sh && ./build-apk.sh

echo "🚀 Generando APK de SerchWebView..."
echo ""

# Verificar dependencias
echo "📋 Verificando dependencias..."
if ! command -v node &> /dev/null; then
    echo "❌ Error: Node.js no está instalado"
    exit 1
fi

if ! command -v java &> /dev/null; then
    echo "❌ Error: Java no está instalado"
    exit 1
fi

# Instalar dependencias npm
echo "📦 Instalando dependencias npm..."
npm install

# Compilar la app web
echo "🔨 Compilando app web..."
npm run build

# Verificar si ya existe la carpeta android
if [ ! -d "android" ]; then
    echo "📱 Agregando plataforma Android..."
    npx cap add android
else
    echo "✅ Plataforma Android ya existe"
fi

# Sincronizar archivos
echo "🔄 Sincronizando archivos..."
npx cap sync android

# Generar APK debug
echo "🏗️  Generando APK debug..."
cd android
./gradlew assembleDebug

# Verificar si se generó el APK
if [ -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
    echo ""
    echo "✅ ¡APK generado exitosamente!"
    echo "📍 Ubicación: android/app/build/outputs/apk/debug/app-debug.apk"
    echo ""
    echo "📲 Para instalar en tu dispositivo Android:"
    echo "   1. Copia el APK a tu dispositivo"
    echo "   2. Habilita 'Instalar desde fuentes desconocidas'"
    echo "   3. Abre el APK desde el explorador de archivos"
else
    echo "❌ Error: No se pudo generar el APK"
    exit 1
fi

cd ..

echo ""
echo "🎉 ¡Proceso completado!"
