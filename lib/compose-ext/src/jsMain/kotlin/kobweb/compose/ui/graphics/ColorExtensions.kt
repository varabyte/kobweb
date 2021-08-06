package kobweb.compose.ui.graphics

import org.jetbrains.compose.common.core.graphics.Color
import org.jetbrains.compose.web.css.CSSColorValue
import org.jetbrains.compose.web.css.rgb

fun Color.toCssColor(): CSSColorValue {
    return rgb(this.red, this.green, this.blue)
}
