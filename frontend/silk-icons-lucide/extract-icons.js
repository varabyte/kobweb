#!/usr/bin/env node

/**
 * Script to extract Lucide icon data from npm package into lucide-icons.json
 *
 * Usage:
 *   1. npm install lucide@latest (or desired version)
 *   2. node extract-icons.js
 *
 * This will generate/update lucide-icons.json with the latest icon data.
 */

const fs = require('fs');
const path = require('path');

const iconsDir = 'node_modules/lucide/dist/esm/icons';

// Check if lucide package is installed
if (!fs.existsSync(iconsDir)) {
    console.error('❌ Lucide icons directory not found!');
    console.error('   Please run: npm install lucide@latest');
    console.error('   Then try again: node extract-icons.js');
    process.exit(1);
}

// Get all JavaScript icon files
const files = fs.readdirSync(iconsDir).filter(f => f.endsWith('.js') && f !== 'index.js');

if (files.length === 0) {
    console.error('❌ No icon files found in', iconsDir);
    process.exit(1);
}

console.log(`📦 Found ${files.length} icon files`);

const iconData = {};
let successCount = 0;
let errorCount = 0;

// Extract icon data from each file
files.forEach(file => {
    const iconName = file.replace('.js', '');
    const filePath = path.join(iconsDir, file);

    try {
        const content = fs.readFileSync(filePath, 'utf8');

        // Extract the array content: const IconName = [...];
        const match = content.match(/const\s+\w+\s*=\s*(\[[\s\S]*?\]);/);
        if (match) {
            // Safely evaluate the JavaScript array
            const iconArray = eval(match[1]);
            iconData[iconName] = iconArray;
            successCount++;
        } else {
            console.warn(`⚠️  Could not parse icon: ${iconName}`);
            errorCount++;
        }
    } catch (e) {
        console.error(`❌ Error parsing ${iconName}:`, e.message);
        errorCount++;
    }
});

// Write the JSON file
const outputFile = 'lucide-icons.json';
fs.writeFileSync(outputFile, JSON.stringify(iconData, null, 2));

// Report results
console.log(`\n✅ Successfully extracted ${successCount} icons`);
if (errorCount > 0) {
    console.log(`⚠️  ${errorCount} icons had errors`);
}
console.log(`📄 Generated: ${outputFile}`);
console.log(`📊 File size: ${(fs.statSync(outputFile).size / 1024).toFixed(1)} KB`);

// Check if we should clean up
if (process.argv.includes('--cleanup')) {
    console.log('🧹 Cleaning up node_modules...');
    fs.rmSync('node_modules', {recursive: true, force: true});
    fs.rmSync('package.json', {force: true});
    fs.rmSync('package-lock.json', {force: true});
    console.log('✅ Cleanup complete');
}

console.log('\n🎉 Icon extraction complete!');
console.log('💡 Run: ../../gradlew generateIcons to generate Kotlin composables');