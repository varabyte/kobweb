package multimodule.core.components.widgets

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.dom.ElementRefScope
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributesBuilder
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.style.*
import com.varabyte.kobweb.silk.components.text.SpanText
import multimodule.core.G
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement

val TextInputLabelStyle = ComponentStyle.base("text-input-label") {
    Modifier
        .fontSize(G.Ui.Text.Small)
        .color(Colors.Grey)
}

val TextInputStyle = ComponentStyle.base("text-input") {
    Modifier
        .width(G.Ui.Width.Medium)
        .margin(bottom = 10.px)
        .fontSize(G.Ui.Text.Medium)
        .borderStyle(LineStyle.Solid)
}

/** A text input box with a descriptive label above it. */
@Composable
fun LabeledTextInput(label: String, mask: Boolean = false, ref: ((HTMLInputElement) -> Unit)? = null, onCommit: () -> Unit = {}, onValueChanged: (String) -> Unit) {
    Column {
        SpanText(label, TextInputLabelStyle.toModifier())
        TextInput(mask, ref, onCommit, onValueChanged)
    }
}

/** An uncontrolled text input box. */
@Composable
fun TextInput(mask: Boolean = false, ref: ((HTMLInputElement) -> Unit)? = null, onCommit: () -> Unit = {}, onValueChanged: (String) -> Unit) {
    Input(
        if (mask) InputType.Password else InputType.Text,
        attrs = TextInputStyle.toModifier().toAttrs {
            if (ref != null) {
                this.ref { element ->
                    ref(element)
                    onDispose { }
                }
            }
            onInput { onValueChanged(it.value) }
            onKeyUp { evt ->
                if (evt.code == "Enter") {
                    evt.preventDefault()
                    onCommit()
                }
            }
        }
    )
}

/** A controlled text input box. */
@Composable
fun TextInput(text: String, modifier: Modifier = Modifier, mask: Boolean = false, ref: ((HTMLInputElement) -> Unit)? = null, onCommit: () -> Unit = {}, onValueChanged: (String) -> Unit) {
    Input(
        if (mask) InputType.Password else InputType.Text,
        attrs = TextInputStyle.toModifier().then(modifier).toAttrs {
            if (ref != null) {
                this.ref { element ->
                    ref(element)
                    onDispose { }
                }
            }
            value(text)
            onInput { onValueChanged(it.value) }
            onKeyUp { evt ->
                if (evt.code == "Enter") {
                    evt.preventDefault()
                    onCommit()
                }
            }
        }
    )
}