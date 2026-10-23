# 📱 SerchWebView - APK Android

## ✅ Estado del Proyecto

**App web compilada y lista para empaquetar como APK**

---

## 🎯 Qué se ha creado

### Archivos principales:
- ✅ App React completa con navegador de video
- ✅ Bloqueador de anuncios integrado
- ✅ Modo pantalla completa
- ✅ Configuración PWA (instalable como app)
- ✅ Configuración Capacitor para Android
- ✅ Scripts automáticos para generar APK
- ✅ Iconos SVG y generador de iconos PNG
- ✅ Documentación completa

### Archivos de configuración Android:
- ✅ `capacitor.config.ts` - Configuración Capacitor
- ✅ `public/manifest.json` - PWA manifest
- ✅ `public/sw.js` - Service Worker
- ✅ `public/icon.svg` - Icono vectorial
- ✅ `generate-icons.js` - Generador de iconos

### Scripts de compilación:
- ✅ `setup-and-build.sh` - Script automático (Linux/Mac)
- ✅ `setup-and-build.bat` - Script automático (Windows)
- ✅ `build-apk.sh` - Script básico (Linux/Mac)
- ✅ `build-apk.bat` - Script básico (Windows)

### Documentación:
- ✅ `README.md` - Guía completa
- ✅ `GUIA-RAPIDA.md` - Guía rápida paso a paso

---

## 🚀 Cómo Generar la APK

### Opción 1: Automático (Recomendado)

**Linux/Mac:**
```bash
chmod +x setup-and-build.sh
./setup-and-build.sh
```

**Windows:**
```bash
setup-and-build.bat
```

### Opción 2: Manual

```bash
# 1. Instalar dependencias
npm install

# 2. Generar iconos
node generate-icons.js

# 3. Compilar
npm run build

# 4. Agregar Android
npx cap add android

# 5. Sincronizar
npx cap sync android

# 6. Generar APK
cd android
./gradlew assembleDebug
```

---

## 📋 Requisitos para Generar APK

1. **Node.js** 16+ → https://nodejs.org/
2. **Java JDK** 11+ → https://adoptium.net/
3. **Android Studio** → https://developer.android.com/studio
4. **Android SDK** (se instala con Android Studio)

---

## 📦 Resultado

Después de ejecutar el script, obtendrás:

```
android/app/build/outputs/apk/debug/app-debug.apk
```

**Tamaño estimado:** 5-8 MB

---

## 📲 Instalar en Android

1. Copiar APK al dispositivo
2. Habilitar "Fuentes desconocidas" en Ajustes
3. Abrir APK y tocar "Instalar"
4. ¡Listo!

---

## 🎨 Características de la App

- ✅ Navegador ultra ligero
- ✅ Bloqueador de anuncios y pop-ups
- ✅ Compatible con extensiones Chrome
- ✅ Modo pantalla completa
- ✅ Salida con botón atrás
- ✅ Sin historial de pestañas
- ✅ Soporte iframe/video embebido
- ✅ Diseño mobile-first
- ✅ Tema oscuro

---

## 🔧 Próximos Pasos

1. **Instalar requisitos** (Node.js, Java, Android Studio)
2. **Ejecutar script** (`./setup-and-build.sh` o `setup-and-build.bat`)
3. **Probar APK** en dispositivo Android
4. **Generar APK firmado** para distribución (ver README.md)

---

## 📞 Soporte

- **Guía rápida:** Ver `GUIA-RAPIDA.md`
- **Guía completa:** Ver `README.md`
- **Documentación Capacitor:** https://capacitorjs.com/docs

---

## 🎉 ¡Todo listo!

La app está completamente funcional y lista para ser empaquetada como APK nativa de Android.

**Solo necesitas:**
1. Instalar los requisitos (Node.js, Java, Android Studio)
2. Ejecutar el script de compilación
3. Instalar el APK en tu dispositivo

**¡Disfruta de SerchWebView!** 🚀
