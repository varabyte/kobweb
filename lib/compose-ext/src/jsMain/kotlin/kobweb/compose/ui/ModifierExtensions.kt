package kobweb.compose.ui

import androidx.compose.web.events.SyntheticMouseEvent
import kobweb.compose.css.UserSelect
import kobweb.compose.css.cursor
import kobweb.compose.css.userSelect
import kobweb.compose.ui.graphics.toCssColor
import org.jetbrains.compose.common.foundation.layout.fillMaxHeight
import org.jetbrains.compose.common.foundation.layout.fillMaxWidth
import org.jetbrains.compose.common.internal.castOrCreate
import org.jetbrains.compose.common.ui.Modifier
import org.jetbrains.compose.common.ui.unit.Dp
import org.jetbrains.compose.web.css.*
import kobweb.compose.css.Cursor as KobwebCursor
import kobweb.compose.ui.graphics.Color as KobwebColor

fun Modifier.borderRadius(size: Dp): Modifier = castOrCreate().apply {
    add {
        this.borderRadius(size.value.px)
    }
}

fun Modifier.background(color: KobwebColor): Modifier = castOrCreate().apply {
    add {
        this.backgroundColor(color.toCssColor())
    }
}

fun Modifier.color(color: KobwebColor) = castOrCreate().apply {
    add {
        this.color(color.toCssColor())
    }
}

fun Modifier.cursor(cursor: KobwebCursor) = castOrCreate().apply {
    add {
        this.cursor(cursor)
    }
}

fun Modifier.fillMaxSize(): Modifier = castOrCreate().apply {
    fillMaxWidth()
    fillMaxHeight(1.0f)
}

fun Modifier.height(size: Dp): Modifier = castOrCreate().apply {
    add {
        this.height(size.value.px)
    }
}

fun Modifier.minWidth(size: Dp): Modifier = castOrCreate().apply {
    add {
        this.minWidth(size.value.px)
    }
}

fun Modifier.minHeight(size: Dp): Modifier = castOrCreate().apply {
    add {
        this.minHeight(size.value.px)
    }
}

fun Modifier.onMouseDown(onMouseDown: (SyntheticMouseEvent) -> Unit) = castOrCreate().apply {
    addAttributeBuilder {
        this.onMouseDown { evt -> onMouseDown(evt) }
    }
}

fun Modifier.onMouseEnter(onMouseEnter: (SyntheticMouseEvent) -> Unit) = castOrCreate().apply {
    addAttributeBuilder {
        this.onMouseEnter { evt -> onMouseEnter(evt) }
    }
}

fun Modifier.onMouseLeave(onMouseLeave: (SyntheticMouseEvent) -> Unit) = castOrCreate().apply {
    addAttributeBuilder {
        this.onMouseLeave { evt -> onMouseLeave(evt) }
    }
}

fun Modifier.onMouseMove(onMouseMove: (SyntheticMouseEvent) -> Unit) = castOrCreate().apply {
    addAttributeBuilder {
        this.onMouseMove { evt -> onMouseMove(evt) }
    }
}

fun Modifier.onMouseUp(onMouseUp: (SyntheticMouseEvent) -> Unit) = castOrCreate().apply {
    addAttributeBuilder {
        this.onMouseUp { evt -> onMouseUp(evt) }
    }
}

fun Modifier.padding(width: Dp, height: Dp): Modifier = castOrCreate().apply {
    // yes, it's not a typo, what Modifier.padding does is actually adding margin
    add {
        this.margin(width.value.px, height.value.px)
    }
}

fun Modifier.userSelect(userSelect: UserSelect): Modifier = castOrCreate().apply {
    // yes, it's not a typo, what Modifier.padding does is actually adding margin
    add {
        this.userSelect(userSelect)
    }
}

