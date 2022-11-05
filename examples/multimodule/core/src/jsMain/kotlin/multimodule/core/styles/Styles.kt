package multimodule.core.styles

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.style.*

val ErrorTextStyle = ComponentStyle.base("error-text") {
    Modifier.color(Colors.Red)
}

