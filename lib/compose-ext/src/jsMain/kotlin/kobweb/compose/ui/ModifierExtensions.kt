package kobweb.compose.ui

import kobweb.compose.ui.graphics.toCssColor
import org.jetbrains.compose.common.core.graphics.Color
import org.jetbrains.compose.common.foundation.layout.fillMaxHeight
import org.jetbrains.compose.common.foundation.layout.fillMaxWidth
import org.jetbrains.compose.common.internal.castOrCreate
import org.jetbrains.compose.common.ui.Modifier
import org.jetbrains.compose.common.ui.unit.Dp
import org.jetbrains.compose.web.css.*

fun Modifier.borderRadius(size: Dp): Modifier = castOrCreate().apply {
    add {
        borderRadius(size.value.px)
    }
}

fun Modifier.color(color: Color) = castOrCreate().apply {
    add {
        color(color.toCssColor())
    }
}

fun Modifier.fillMaxSize(): Modifier = castOrCreate().apply {
    fillMaxWidth()
    fillMaxHeight(1.0f)
}

fun Modifier.height(size: Dp): Modifier = castOrCreate().apply {
    add {
        height(size.value.px)
    }
}

fun Modifier.padding(width: Dp, height: Dp): Modifier = castOrCreate().apply {
    // yes, it's not a typo, what Modifier.padding does is actually adding margin
    add {
        margin(width.value.px, height.value.px)
    }
}

