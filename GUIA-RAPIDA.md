# 🚀 Guía Rápida: Generar APK de SerchWebView

## ⚡ Método Más Rápido (1 comando)

### Linux/Mac:
```bash
chmod +x setup-and-build.sh && ./setup-and-build.sh
```

### Windows:
```bash
setup-and-build.bat
```

---

## 📝 Método Manual (Paso a Paso)

### 1️⃣ Instalar Requisitos

**Verificar Node.js:**
```bash
node --version  # Debe ser v16+
```

**Verificar Java:**
```bash
java -version  # Debe ser JDK 11+
```

**Instalar Android Studio:**
- Descargar: https://developer.android.com/studio
- Durante instalación, marcar "Android SDK" y "Android Virtual Device"

### 2️⃣ Preparar el Proyecto

```bash
# Instalar dependencias
npm install

# Generar iconos
node generate-icons.js

# Compilar la app
npm run build
```

### 3️⃣ Configurar Android

```bash
# Agregar plataforma Android (solo primera vez)
npx cap add android

# Sincronizar archivos
npx cap sync android
```

### 4️⃣ Generar APK

**Opción A: Desde terminal**
```bash
cd android
./gradlew assembleDebug
```

**Opción B: Desde Android Studio**
```bash
npx cap open android
# En Android Studio: Build → Build Bundle(s) / APK(s) → Build APK(s)
```

### 5️⃣ Ubicar el APK

```
android/app/build/outputs/apk/debug/app-debug.apk
```

---

## 📲 Instalar en Android

1. **Transferir APK al dispositivo:**
   - Cable USB
   - Email
   - Google Drive / Dropbox
   - Telegram / WhatsApp

2. **Permitir instalación:**
   - Ajustes → Seguridad → Fuentes desconocidas ✓
   - O: Ajustes → Apps → Tu navegador → Permitir apps desconocidas ✓

3. **Instalar:**
   - Abrir archivo APK
   - Tocar "Instalar"
   - Esperar a que termine

---

## 🔐 APK para Producción (Firmado)

### 1. Crear keystore:
```bash
keytool -genkey -v -keystore serchwebview.keystore -alias serchwebview -keyalg RSA -keysize 2048 -validity 10000
```

### 2. Configurar en `android/app/build.gradle`:
```gradle
android {
    signingConfigs {
        release {
            storeFile file('../serchwebview.keystore')
            storePassword 'TU_PASSWORD'
            keyAlias 'serchwebview'
            keyPassword 'TU_PASSWORD'
        }
    }
    buildTypes {
        release {
            signingConfig signingConfigs.release
        }
    }
}
```

### 3. Generar APK firmado:
```bash
cd android
./gradlew assembleRelease
```

---

## 🐛 Problemas Comunes

### ❌ "SDK location not found"
```bash
echo "sdk.dir=/home/usuario/Android/Sdk" > android/local.properties
```

### ❌ "JAVA_HOME not set"
```bash
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
```

### ❌ "Permission denied: gradlew"
```bash
chmod +x android/gradlew
```

### ❌ "Build failed" en Windows
```bash
# Usar PowerShell como administrador
cd android
.\gradlew.bat assembleDebug
```

---

## 📦 Tamaño del APK

- **Debug APK:** ~5-8 MB
- **Release APK:** ~3-5 MB (optimizado)

---

## 🎯 Próximos Pasos

1. ✅ Generar APK debug y probar en dispositivo
2. ✅ Probar todas las funcionalidades
3. ✅ Generar APK firmado para distribución
4. ✅ Subir a Google Play Store (opcional)

---

## 📞 Ayuda Adicional

- Documentación Capacitor: https://capacitorjs.com/docs
- Documentación Android: https://developer.android.com/docs
- Issues del proyecto: Revisa README.md

---

**¡Listo! Tu APK de SerchWebView está listo para usar 🎉**
