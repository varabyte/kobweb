# Silk Icons (Lucide)

Support for integration of [Lucide icons](https://lucide.dev/) in your Kobweb project.

Lucide is a beautiful & consistent icon toolkit made by the community. It provides a comprehensive collection of over
1,600+ open-source SVG icons with a consistent design language.

## Key Features

- **Tree-shakable**: Only imported icons are included in the final bundle
- **Type-safe**: Individual composables for each icon (e.g. `LuCamera`, `LuHeart`, `LuSearch`)
- **Customizable**: Support for size, stroke width, color, and standard modifiers
- **Performance**: Direct SVG rendering for optimal performance
- **No runtime dependencies**: All icon data is embedded at build time

## Usage

Add the dependency to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.varabyte.kobwebx:silk-icons-lucide:$kobwebVersion")
}
```

Use icons in your Compose code:

```kotlin
import com.varabyte.kobweb.silk.components.icons.lucide.*

@Composable
fun MyComponent() {
    // Basic usage
    LuCamera()
    
    // With customization
    LuHeart(
        size = 2.em,
        color = Colors.Red,
        strokeWidth = 3
    )
    
    // With modifier
    LuSearch(
        modifier = Modifier
            .margin(1.cssRem)
            .onClick { /* handle click */ }
    )
}
```

## Available Parameters

All Lu icon composables accept the following parameters:

- `modifier: Modifier = Modifier` - Standard Compose modifier
- `size: CSSLengthValue = 1.em` - Icon size
- `strokeWidth: Number = 2` - Stroke width for the icon lines
- `color: CSSColorValue? = null` - Icon color (uses currentColor if null)

## Tree-Shaking Benefits

Unlike centralized icon systems, each Lu icon is its own composable function. This means:

- ✅ **Only used icons are included** in your final JavaScript bundle
- ✅ **No lookup overhead** at runtime
- ✅ **Better dead code elimination** by bundlers
- ✅ **Smaller bundle sizes** for applications that use few icons

For example, if you only use `LuCamera` and `LuHeart`, only the SVG data for those two icons will be included in
your final bundle, not all 1,600+ icons.

## Icon Naming Convention

Lu icon names are converted to PascalCase function names with the `Lu` prefix:

- `camera` → `LuCamera()`
- `arrow-down` → `LuArrowDown()`
- `chevron-right` → `LuChevronRight()`
- `external-link` → `LuExternalLink()`

## Finding Icons

You can browse all available icons at [lucide.dev](https://lucide.dev/icons/). The function name will always be the icon
name in PascalCase with the `Lu` prefix.

## Updating Icons

The icon data is stored in `lucide-icons.json` and was generated from Lucide v0.540.0. To update to a newer version:

1. Update the Lucide version in your package manager or download manually
2. Navigate to the `frontend/silk-icons-lucide` directory
3. Run the extraction script to regenerate the JSON file:

```bash
cd frontend/silk-icons-lucide

# Install/update lucide package
npm install lucide@latest  # or your desired version

# Extract icon data to JSON
node -e "
const fs = require('fs');
const path = require('path');

const iconsDir = 'node_modules/lucide/dist/esm/icons';
const files = fs.readdirSync(iconsDir).filter(f => f.endsWith('.js') && f !== 'index.js');

const iconData = {};

files.forEach(file => {
  const iconName = file.replace('.js', '');
  const filePath = path.join(iconsDir, file);
  const content = fs.readFileSync(filePath, 'utf8');
  
  const match = content.match(/const \w+ = (\[[\s\S]*?\]);/);
  if (match) {
    try {
      const iconArray = eval(match[1]);
      iconData[iconName] = iconArray;
    } catch (e) {
      console.error('Error parsing', iconName, e.message);
    }
  }
});

fs.writeFileSync('lucide-icons.json', JSON.stringify(iconData, null, 2));
console.log('Generated lucide-icons.json with', Object.keys(iconData).length, 'icons');
"
```

4. Regenerate the Kotlin code:

```bash
../../gradlew generateIcons
```

5. Clean up temporary node_modules if desired:

```bash
rm -rf node_modules package*.json
```

## Examples

```kotlin
// Basic icons
LuHome()
LuUser()
LuSettings()

// Styled icons
LuHeart(color = Colors.Red, size = 1.5.em)
LuSearch(strokeWidth = 1.5)

// With interactions
LuMenu(
    modifier = Modifier
        .onClick { toggleMenu() }
        .cursor(Cursor.Pointer)
)

// Different sizes
Row {
    LuIcon(size = 0.8.em) // Small
    LuIcon(size = 1.2.em) // Medium  
    LuIcon(size = 2.em)   // Large
}
```

## Technical Details

- Icons are rendered as inline SVG elements
- Default viewBox is 24x24 units
- Uses stroke-based rendering with customizable stroke width
- Supports standard SVG elements: path, circle, rect, line, polyline, polygon
- No external dependencies at runtime