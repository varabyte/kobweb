package com.varabyte.kobweb.silk

import com.varabyte.kobweb.compose.attributes.ComparableAttrsScope
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.init.initSilk
import com.varabyte.kobweb.silk.style.ComponentKind
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.addVariantBase
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.extendedBy
import com.varabyte.kobweb.silk.style.extendedByBase
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.theme.ImmutableSilkTheme
import com.varabyte.kobweb.silk.theme.MutableSilkTheme
import com.varabyte.kobweb.silk.theme.SilkTheme
import com.varabyte.kobweb.silk.theme._SilkTheme
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.truthish.assertThat
import org.jetbrains.compose.web.css.*
import org.w3c.dom.Element
import kotlin.test.Test

@Suppress("LocalVariableName")
class CssStyleTest {
    @Test
    fun componentVariantStylesSheetOrder() {
        // Styles must be non-empty to be registered
        val stylesheet = StyleSheet()
        val BaseStyle = CssStyle.base<ComponentKind> { Modifier.color(Colors.Red) }
        val BaseStyleName = "base-style"
        val Variant = BaseStyle.addVariantBase { Modifier.color(Colors.Red) }
        val VariantName = "secondary-style"
        _SilkTheme = MutableSilkTheme().apply {
            // Intentionally register out of order
            registerVariant(VariantName, Variant)
            registerStyle(BaseStyleName, BaseStyle)
        }.let(::ImmutableSilkTheme)
        SilkTheme.registerStylesInto(stylesheet)

        assertThat(stylesheet.cssRules.toList().map { it.header })
            .containsExactly(".${BaseStyleName}", ".${VariantName}")
            .inOrder()
    }

    @Test
    fun cssStyleExtendedStylesheetOrder() {
        // Styles must be non-empty to be registered
        val stylesheet = StyleSheet()
        val BaseStyle = CssStyle.base { Modifier.color(Colors.Red) }
        val BaseStyleName = "base-style"
        val SecondaryStyle = BaseStyle.extendedByBase { Modifier.color(Colors.Red) }
        val SecondaryStyleName = "secondary-style"
        val TertiaryStyle = SecondaryStyle.extendedByBase { Modifier.color(Colors.Red) }
        val TertiaryStyleName = "tertiary-style"
        _SilkTheme = MutableSilkTheme().apply {
            // Intentionally register out of order
            registerStyle(TertiaryStyleName, TertiaryStyle)
            registerStyle(BaseStyleName, BaseStyle)
            registerStyle(SecondaryStyleName, SecondaryStyle)
        }.let(::ImmutableSilkTheme)
        SilkTheme.registerStylesInto(stylesheet)

        assertThat(stylesheet.cssRules.toList().map { it.header })
            .containsExactly(".${BaseStyleName}", ".${SecondaryStyleName}", ".${TertiaryStyleName}")
            .inOrder()
    }

    @Test
    fun cssStyleExtendedClasses() {
        val BaseStyle = CssStyle { }
        val BaseStyleName = "base-style"
        val SecondaryStyle = BaseStyle.extendedBy { }
        val SecondaryStyleName = "secondary-style"
        val TertiaryStyle = SecondaryStyle.extendedBy { }
        val TertiaryStyleName = "tertiary-style"
        initSilk {
            with(it.theme) {
                registerStyle(BaseStyleName, BaseStyle)
                registerStyle(TertiaryStyleName, TertiaryStyle)
                registerStyle(SecondaryStyleName, SecondaryStyle)
            }
        }

        callComposable {
            ComparableAttrsScope<Element>().apply(BaseStyle.toModifier().toAttrs()).run {
                assertThat(classes).containsExactly(BaseStyleName)
            }
            ComparableAttrsScope<Element>().apply(SecondaryStyle.toModifier().toAttrs()).run {
                assertThat(classes).containsExactly(SecondaryStyleName, BaseStyleName)
            }
            ComparableAttrsScope<Element>().apply(TertiaryStyle.toModifier().toAttrs()).run {
                assertThat(classes).containsExactly(TertiaryStyleName, SecondaryStyleName, BaseStyleName)
            }
        }
    }

    @Test
    fun cssStyleExtendedWithColorModeClasses() {
        val BaseStyle = CssStyle.base { Modifier.color(if (colorMode.isLight) Colors.White else Colors.Black) }
        val BaseStyleName = "base-style"
        val SecondaryStyle = BaseStyle.extendedByBase {
            Modifier.color(if (colorMode.isLight) Colors.White else Colors.Black)
        }
        val SecondaryStyleName = "secondary-style"
        initSilk {
            with(it.theme) {
                registerStyle(BaseStyleName, BaseStyle)
                registerStyle(SecondaryStyleName, SecondaryStyle)
            }
        }

        callComposable {
            ColorMode.currentState.value = ColorMode.LIGHT
            ComparableAttrsScope<Element>().apply(BaseStyle.toModifier().toAttrs()).run {
                assertThat(classes).containsExactly(BaseStyleName, "${BaseStyleName}_light")
            }
            ComparableAttrsScope<Element>().apply(SecondaryStyle.toModifier().toAttrs()).run {
                assertThat(classes).containsExactly(
                    SecondaryStyleName, "${SecondaryStyleName}_light", BaseStyleName, "${BaseStyleName}_light"
                )
            }

            ColorMode.currentState.value = ColorMode.DARK
            ComparableAttrsScope<Element>().apply(BaseStyle.toModifier().toAttrs()).run {
                assertThat(classes).containsExactly(BaseStyleName, "${BaseStyleName}_dark")
            }
            ComparableAttrsScope<Element>().apply(SecondaryStyle.toModifier().toAttrs()).run {
                assertThat(classes).containsExactly(
                    SecondaryStyleName, "${SecondaryStyleName}_dark", BaseStyleName, "${BaseStyleName}_dark"
                )
            }
        }
    }
}
