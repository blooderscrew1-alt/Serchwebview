# SerchWebView - Navegador de Video Ultra Ligero para Android

App Android nativa para reproducir videos web con bloqueador de anuncios integrado.

## 🎯 Características

- ✅ **Ultra ligero y rápido** - Diseño minimalista sin opciones innecesarias
- ✅ **Bloqueador de anuncios** - Bloquea anuncios y pop-ups automáticamente
- ✅ **Compatible con extensiones Chrome** - Soporte para extensiones
- ✅ **Salida sencilla** - Cierra con botón atrás o tecla ESC
- ✅ **Pantalla completa** - Modo inmersivo para videos
- ✅ **Sin historial** - No guarda pestañas, solo reproduce
- ✅ **Soporte iframe** - Videos web embebidos

## 🚀 Generar APK (3 Métodos)

### Método 1: Script Automático (Recomendado)

**Linux/Mac:**
```bash
chmod +x setup-and-build.sh
./setup-and-build.sh
```

**Windows:**
```bash
setup-and-build.bat
```

Este script hará todo automáticamente:
1. Genera los iconos
2. Instala dependencias
3. Compila la app web
4. Configura Android
5. Genera el APK

### Método 2: Manual con Capacitor

```bash
# 1. Instalar dependencias
npm install

# 2. Generar iconos
node generate-icons.js

# 3. Compilar app web
npm run build

# 4. Agregar plataforma Android
npx cap add android

# 5. Sincronizar archivos
npx cap sync android

# 6. Abrir en Android Studio
npx cap open android

# 7. En Android Studio: Build → Build APK
```

### Método 3: Solo APK Debug (Rápido)

```bash
npm install
npm run build
npx cap add android
npx cap sync android
cd android
./gradlew assembleDebug
```

El APK estará en: `android/app/build/outputs/apk/debug/app-debug.apk`

## 📋 Requisitos Previos

### Para generar APK:
- **Node.js** 16 o superior
- **Java JDK** 11 o superior
- **Android Studio** (con SDK instalado)
- **Android SDK** (API 21 o superior)

### Instalación de requisitos:

**Ubuntu/Debian:**
```bash
# Node.js
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt-get install -y nodejs

# Java
sudo apt-get install openjdk-17-jdk

# Android Studio
# Descargar desde: https://developer.android.com/studio
```

**Windows:**
1. Instalar Node.js: https://nodejs.org/
2. Instalar Java JDK: https://adoptium.net/
3. Instalar Android Studio: https://developer.android.com/studio

**Mac:**
```bash
# Con Homebrew
brew install node
brew install openjdk@17
# Android Studio desde: https://developer.android.com/studio
```

## 📱 Instalar APK en Android

1. **Copiar APK al dispositivo:**
   - USB: Copia `app-debug.apk` a tu dispositivo
   - Email: Envíatelo por email
   - Cloud: Sube a Google Drive/Dropbox

2. **Habilitar instalación:**
   - Ajustes → Seguridad → Fuentes desconocidas (activar)
   - O: Ajustes → Apps → Chrome → Permitir desde esta fuente

3. **Instalar:**
   - Abre el archivo APK desde el explorador
   - Toca "Instalar"
   - Acepta los permisos

## 🎨 Generar Iconos

```bash
node generate-icons.js
```

Esto generará todos los tamaños necesarios:
- icon-36.png (mdpi)
- icon-48.png (mdpi)
- icon-72.png (hdpi)
- icon-96.png (xhdpi)
- icon-144.png (xxhdpi)
- icon-192.png (xxxhdpi)
- icon-512.png (Google Play)

## 🛠️ Comandos Útiles

```bash
# Desarrollo local
npm run dev

# Build producción
npm run build

# Preview del build
npm run preview

# Agregar Android (primera vez)
npx cap add android

# Sincronizar cambios
npx cap sync

# Abrir en Android Studio
npx cap open android

# Generar APK debug
cd android && ./gradlew assembleDebug

# Generar APK release
cd android && ./gradlew assembleRelease
```

## 📦 Estructura del Proyecto

```
serchwebview/
├── src/                          # Código fuente React
│   ├── App.tsx                   # Componente principal
│   ├── components/               # Componentes UI
│   │   ├── HomeScreen.tsx        # Pantalla de inicio
│   │   ├── BrowserView.tsx       # Vista del navegador
│   │   └── AdBlockerOverlay.tsx  # Notificaciones de bloqueo
│   ├── main.tsx                  # Punto de entrada
│   └── index.css                 # Estilos globales
├── public/                       # Archivos estáticos
│   ├── icon.svg                  # Icono vectorial
│   ├── icon-*.png                # Iconos generados
│   ├── manifest.json             # PWA manifest
│   └── sw.js                     # Service Worker
├── android/                      # Proyecto Android (generado)
├── capacitor.config.ts           # Configuración Capacitor
├── generate-icons.js             # Script de iconos
├── setup-and-build.sh            # Script automático (Linux/Mac)
├── setup-and-build.bat           # Script automático (Windows)
└── package.json                  # Dependencias
```

## 🔧 Configuración

### Capacitor (capacitor.config.ts)
```typescript
{
  appId: 'com.serchwebview.app',
  appName: 'SerchWebView',
  webDir: 'dist',
  server: {
    androidScheme: 'https'
  }
}
```

### Permisos Android
La app requiere estos permisos (se agregan automáticamente):
- `INTERNET` - Acceso a internet
- `ACCESS_NETWORK_STATE` - Estado de red
- `ACCESS_WIFI_STATE` - Estado WiFi

## 📲 PWA (Alternativa sin APK)

Si prefieres usar la app como PWA sin generar APK:

1. **Compilar la app:**
   ```bash
   npm run build
   npm run preview
   ```

2. **Abrir en Chrome/Edge** en tu dispositivo Android

3. **Instalar como app:**
   - Menú (⋮) → "Agregar a pantalla de inicio"
   - O: Menú → "Instalar aplicación"

4. La app se instalará como una app nativa con icono propio

## 🐛 Solución de Problemas

### Error: "SDK location not found"
```bash
# Crear archivo local.properties en android/
echo "sdk.dir=/path/to/Android/Sdk" > android/local.properties
```

### Error: "JAVA_HOME not set"
```bash
# Linux/Mac
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk

# Windows
set JAVA_HOME=C:\Program Files\Java\jdk-17
```

### Error: "gradlew permission denied"
```bash
chmod +x android/gradlew
```

### APK no se instala
- Verifica que "Fuentes desconocidas" esté habilitado
- Verifica que la versión de Android sea 5.0+ (API 21+)
- Desinstala versiones anteriores de la app

## 📄 Licencia

MIT

## 🤝 Contribuir

1. Fork el proyecto
2. Crea una rama (`git checkout -b feature/nueva-funcionalidad`)
3. Commit cambios (`git commit -m 'Agregar funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Abre un Pull Request

## 📞 Soporte

Si tienes problemas o preguntas:
1. Revisa la sección de Solución de Problemas
2. Verifica los requisitos del sistema
3. Consulta la documentación de Capacitor: https://capacitorjs.com/

---

**Hecho con ❤️ para reproducción de videos sin distracciones**
