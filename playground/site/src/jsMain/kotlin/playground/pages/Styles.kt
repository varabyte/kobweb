@file:Suppress("UnusedReceiverParameter") // extending containers for organization purposes

package playground.pages

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.dom.ref
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.silk.components.layout.SimpleGrid
import com.varabyte.kobweb.silk.components.layout.numColumns
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.ComponentKind
import com.varabyte.kobweb.silk.style.CssName
import com.varabyte.kobweb.silk.style.CssPrefix
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.addVariant
import com.varabyte.kobweb.silk.style.extendedBy
import com.varabyte.kobweb.silk.style.toModifier
import org.jetbrains.compose.web.css.*
import playground.components.layouts.PageLayout

// This page tests all the different variations of declaring, prefixing, and naming styles

@Composable
private fun ClassNamesBox(modifier: Modifier) {
    val classNames = remember { mutableStateListOf<String>() }
    Box(Modifier.padding(1.cssRem).border(width = 1.px, LineStyle.Solid, Colors.Black).borderRadius(5.px)
        .then(modifier), contentAlignment = Alignment.TopStart, ref = ref { element ->
        classNames.clear()
        classNames.addAll(element.className.split(" ").filter { !it.startsWith("kobweb") })
    }) {
        Column(horizontalAlignment = Alignment.Start) {
            classNames.forEach { SpanText(it) }
        }
    }
}

val BasicStyle = CssStyle { }
val ExtendingStyle = BasicStyle.extendedBy { }
val ExtraExtendingStyle = ExtendingStyle.extendedBy { }

interface WidgetKind : ComponentKind

val WidgetStyle = CssStyle<WidgetKind> { }
val ExampleWidgetVariant = WidgetStyle.addVariant { }

class RestrictedStyle : CssStyle.Restricted.Base(Modifier) {
    companion object {
        val SM = RestrictedStyle()
        val LG = RestrictedStyle()
    }
}

object ObjectContainer {
    val BasicStyle = CssStyle { }
    val ExtendingStyle = BasicStyle.extendedBy { }

    interface ObjectContainedWidgetKind : ComponentKind

    val WidgetStyle = CssStyle<ObjectContainedWidgetKind> { }
    val ExampleWidgetVariant = WidgetStyle.addVariant { }
}

private val _ObjectExtensionStyle = CssStyle { }
val ObjectContainer.ExtensionStyle get() = _ObjectExtensionStyle

class ClassContainer {
    companion object {
        val BasicStyle = CssStyle { }
        val ExtendingStyle = BasicStyle.extendedBy { }

        interface ClassContainedWidgetKind : ComponentKind

        val WidgetStyle = CssStyle<ClassContainedWidgetKind> { }
        val ExampleWidgetVariant = WidgetStyle.addVariant { }
    }
}

private val _ClassExtensionStyle = CssStyle { }
val ClassContainer.Companion.ExtensionStyle get() = _ClassExtensionStyle


@CssPrefix("test")
@CssName("basic")
val NamedBasicStyle = CssStyle { }

@CssPrefix("test")
@CssName("extending")
val NamedExtendingStyle = BasicStyle.extendedBy { }

interface NamedWidgetKind : ComponentKind

@CssPrefix("test")
@CssName("widget")
val NamedWidgetStyle = CssStyle<NamedWidgetKind> { }

@CssPrefix("unused")
@CssName("-example")
val NamedExampleWidgetVariant = NamedWidgetStyle.addVariant { }

@CssPrefix("test")
@CssName("obj")
object NamedObjectContainer {
    val BasicStyle = CssStyle { }

    @CssPrefix("prefix")
    @CssName("named")
    val NameOverriddenStyle = CssStyle { }
}

@CssPrefix("test")
@CssName("cls")
class NamedClassContainer {
    companion object {
        val BasicStyle = CssStyle { }

        @CssPrefix("prefix")
        @CssName("named")
        val NameOverriddenStyle = CssStyle { }
    }
}

@Page
@Composable
fun StylesPage() {
    PageLayout("Styles") {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            val styleModifiers = listOf(
                BasicStyle.toModifier(),
                ExtendingStyle.toModifier(),
                ExtraExtendingStyle.toModifier(),

                WidgetStyle.toModifier(),
                WidgetStyle.toModifier(ExampleWidgetVariant),

                RestrictedStyle.SM.toModifier(),
                RestrictedStyle.LG.toModifier(),

                ObjectContainer.BasicStyle.toModifier(),
                ObjectContainer.ExtendingStyle.toModifier(),
                ObjectContainer.WidgetStyle.toModifier(ObjectContainer.ExampleWidgetVariant),
                ObjectContainer.ExtensionStyle.toModifier(),

                ClassContainer.BasicStyle.toModifier(),
                ClassContainer.ExtendingStyle.toModifier(),
                ClassContainer.WidgetStyle.toModifier(ClassContainer.ExampleWidgetVariant),
                ClassContainer.ExtensionStyle.toModifier(),

                NamedBasicStyle.toModifier(),
                NamedExtendingStyle.toModifier(),
                NamedWidgetStyle.toModifier(NamedExampleWidgetVariant),

                NamedObjectContainer.BasicStyle.toModifier(),
                NamedObjectContainer.NameOverriddenStyle.toModifier(),

                NamedClassContainer.BasicStyle.toModifier(),
                NamedClassContainer.NameOverriddenStyle.toModifier(),
            )

            SimpleGrid(numColumns(3), Modifier.gap(1.cssRem)) {
                styleModifiers.forEach { modifier ->
                    ClassNamesBox(modifier)
                }
            }
        }
    }
}
