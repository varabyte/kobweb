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

fun Modifier.size(size: CSSPercentageValue): Modifier = castOrCreate().apply {
    add {
        this.width(size)
        this.height(size)
    }
}


fun Modifier.width(size: Dp): Modifier = castOrCreate().apply {
    add {
        this.width(size.value.px)
    }
}

fun Modifier.width(size: CSSPercentageValue): Modifier = castOrCreate().apply {
    add {
        this.width(size)
    }
}

fun Modifier.height(size: Dp): Modifier = castOrCreate().apply {
    add {
        this.height(size.value.px)
    }
}

fun Modifier.height(size: CSSPercentageValue): Modifier = castOrCreate().apply {
    add {
        this.height(size)
    }
}

fun Modifier.minWidth(size: Dp): Modifier = castOrCreate().apply {
    add {
        this.minWidth(size.value.px)
    }
}

fun Modifier.minWidth(size: CSSPercentageValue): Modifier = castOrCreate().apply {
    add {
        this.minWidth(size)
    }
}

fun Modifier.minHeight(size: Dp): Modifier = castOrCreate().apply {
    add {
        this.minHeight(size.value.px)
    }
}

fun Modifier.minHeight(size: CSSPercentageValue): Modifier = castOrCreate().apply {
    add {
        this.minHeight(size)
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

fun Modifier.padding(topBottom: Dp, leftRight: Dp): Modifier = castOrCreate().apply {
    add {
        // Compose padding is the same thing as CSS margin, confusingly... (it puts space around the current composable,
        // as opposed to doing anything with its children)
        this.margin(topBottom.value.px, leftRight.value.px)
    }
}

fun Modifier.padding(top: Dp, right: Dp, bottom: Dp, left: Dp): Modifier = castOrCreate().apply {
    add {
        // Compose padding is the same thing as CSS margin, confusingly... (it puts space around the current composable,
        // as opposed to doing anything with its children)
        this.margin(top.value.px, right.value.px, bottom.value.px, left.value.px)
    }
}

fun Modifier.userSelect(userSelect: UserSelect): Modifier = castOrCreate().apply {
    add {
        this.userSelect(userSelect)
    }
}

