package todo.components.widgets

import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.FontStyle
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.textAlign
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.graphics.toCssColor
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.hover
import com.varabyte.kobweb.silk.components.style.placeholderShown
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.alignItems
import org.jetbrains.compose.web.css.border
import org.jetbrains.compose.web.css.borderRadius
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.textDecoration
import todo.BORDER_COLOR

// TODO: Eliminate the need for "styleModifier" here by improving Modifier method coverage

private val INTERACT_COLOR = Color.rgb(0x00, 0x70, 0xf3)

/** Common styles for all todo widgets */
val TodoStyle = ComponentStyle("todo") {
    base {
        Modifier
            .width(85.percent)
            .height(5.cssRem)
            .styleModifier {
                property("border", "1px solid $BORDER_COLOR")
                borderRadius(10.px)
                property("transition", "color 0.15s ease, border-color 0.15s ease")
                textDecoration("none")
            }
    }
}

/** Styles for the bordered, outer container (the form component has an inner and outer layer) */
val TodoContainerStyle = ComponentStyle("todo-container") {
    base {
        Modifier
            .margin(0.5.cssRem)
            .styleModifier {
                display(DisplayStyle.Flex)
                textAlign(TextAlign.Left)
                alignItems(AlignItems.Center)
                property("border", "1px solid $BORDER_COLOR")
            }
    }
}

/** Styles for the text parts of todo widgets */
val TodoTextStyle = ComponentStyle("todo-text") {
    base {
        Modifier
            .padding(1.5.cssRem)
            .fontSize(1.25.cssRem)
            .styleModifier {
                // We use "A" tags for accessibility, but we want our colors to come from our container
                property("color", "inherit")
            }
    }
}

/** Styles for the input element which handles user input */
val TodoInputStyle = ComponentStyle("todo-input") {
    base {
        Modifier
            .fillMaxWidth()
            .background(Colors.Transparent)
            .styleModifier {
                border(0.px)
            }
    }

    placeholderShown {
        Modifier.fontStyle(FontStyle.Italic)
    }
}

/** Styles for mouse interaction with todo widgets */
val TodoClickableStyle = ComponentStyle("todo-clickable") {
    hover {
        Modifier
            .color(INTERACT_COLOR)
            .cursor(Cursor.Pointer)
            .styleModifier {
                property("border-color", INTERACT_COLOR.toCssColor())
                textDecoration("line-through")
            }
    }
}