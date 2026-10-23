const sharp = require('sharp');
const fs = require('fs');
const path = require('path');

async function generateIcons() {
  const svgPath = path.join(__dirname, 'public', 'icon.svg');
  const svgBuffer = fs.readFileSync(svgPath);

  console.log('🎨 Generando iconos para Android...');

  try {
    // Generar icono 512x512
    await sharp(svgBuffer)
      .resize(512, 512)
      .png()
      .toFile(path.join(__dirname, 'public', 'icon-512.png'));
    console.log('✅ icon-512.png generado');

    // Generar icono 192x192
    await sharp(svgBuffer)
      .resize(192, 192)
      .png()
      .toFile(path.join(__dirname, 'public', 'icon-192.png'));
    console.log('✅ icon-192.png generado');

    // Generar iconos adicionales para Android
    const sizes = [36, 48, 72, 96, 144, 192, 512];
    
    for (const size of sizes) {
      await sharp(svgBuffer)
        .resize(size, size)
        .png()
        .toFile(path.join(__dirname, 'public', `icon-${size}.png`));
      console.log(`✅ icon-${size}.png generado`);
    }

    console.log('\n🎉 ¡Todos los iconos generados exitosamente!');
  } catch (error) {
    console.error('❌ Error generando iconos:', error);
    process.exit(1);
  }
}

generateIcons();
