# Silk Icons (Lucide)

Support for integration of [Lucide icons](https://lucide.dev/) in your Kobweb project.

Lucide is a beautiful & consistent icon toolkit made by the community. It provides a comprehensive collection of over
1,600+ open-source SVG icons with a consistent design language.

## Key Features

- **Tree-shakable**: Only imported icons are included in the final bundle
- **Type-safe**: Individual composables for each icon (e.g. `LiCamera`, `LiHeart`, `LiSearch`)
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
    LiCamera()
    
    // With customization
    LiHeart(
        size = 2.em,
        color = Colors.Red,
        strokeWidth = 3
    )
    
    // With modifier
    LiSearch(
        modifier = Modifier
            .margin(1.cssRem)
            .onClick { /* handle click */ }
    )
}
```

## Available Parameters

All Li icon composables accept the following parameters:

- `modifier: Modifier = Modifier` - Standard Compose modifier
- `size: CSSLengthValue = 1.em` - Icon size
- `strokeWidth: Number = 2` - Stroke width for the icon lines
- `color: CSSColorValue? = null` - Icon color (uses currentColor if null)

## Tree-Shaking Benefits

Unlike centralized icon systems, each Li icon is its own composable function. This means:

- **Only used icons are included** in your final JavaScript bundle
- **No lookup overhead** at runtime
- **Better dead code elimination** by bundlers
- **Smaller bundle sizes** for applications that use few icons

For example, if you only use `LiCamera` and `LiHeart`, only the SVG data for those two icons will be included in
your final bundle, not all 1,600+ icons.

## Icon Naming Convention

Lucide icon names are converted to PascalCase function names with the `Li` prefix:

- `camera` → `LiCamera()`
- `arrow-down` → `LiArrowDown()`
- `chevron-right` → `LiChevronRight()`
- `external-link` → `LiExternalLink()`

## Finding Icons

You can browse all available icons at [lucide.dev](https://lucide.dev/icons/). The function name will always be the icon
name in PascalCase with the `Li` prefix.

## Updating Icons

The icon data is stored in `lucide-icons.json` and was generated from Lucide v0.540.0. To update to a newer version:

1. Navigate to the `frontend/silk-icons-lucide` directory
2. Install/update the Lucide package:

```bash
cd frontend/silk-icons-lucide

# Install/update lucide package
npm install lucide@latest  # or your desired version
```

3. Extract icon data using the provided script:

```bash
# Extract icon data to lucide-icons.json
node extract-icons.js

# Optional: Clean up node_modules after extraction
node extract-icons.js --cleanup
```

4. Regenerate the Kotlin code:

```bash
../../gradlew generateIcons
```

The `extract-icons.js` script will:

- ✅ Check that Lucide is installed
- ✅ Extract all icon definitions into `lucide-icons.json`
- ✅ Provide detailed progress and error reporting
- ✅ Optionally clean up temporary `node_modules` (with `--cleanup` flag)

## Files Added/Modified
- `frontend/silk-icons-lucide/build.gradle.kts` - Build configuration with codegen
- `frontend/silk-icons-lucide/README.md` - Usage documentation
- `playground/site/src/jsMain/kotlin/playground/pages/Widgets.kt` - Usage examples
- Generated: `LucideIcons.kt` with all icon composables


## Examples

```kotlin
// Basic icons
LiHome()
LiUser()
LiSettings()

// Styled icons
LiHeart(color = Colors.Red, size = 1.5.em)
LiSearch(strokeWidth = 1.5)

// With interactions
LiMenu(
    modifier = Modifier
        .onClick { toggleMenu() }
        .cursor(Cursor.Pointer)
)

// Different sizes
Row {
    LiHeart(size = 0.8.em) // Small
    LiHeart(size = 1.2.em) // Medium  
    LiHeart(size = 2.em)   // Large
}
```

## Technical Details

- Icons are rendered as inline SVG elements
- Default viewBox is 24x24 units
- Uses stroke-based rendering with customizable stroke width
- Supports standard SVG elements: path, circle, rect, line, polyline, polygon
- No external dependencies at runtime