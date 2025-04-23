package com.varabyte.kobweb.silk

import com.varabyte.kobweb.compose.css.CSSScopeRuleDeclaration
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.init.initSilk
import com.varabyte.kobweb.silk.style.ComponentKind
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.addVariant
import com.varabyte.kobweb.silk.style.addVariantBase
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.extendedBy
import com.varabyte.kobweb.silk.style.extendedByBase
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.testutils.flattenCssRules
import com.varabyte.kobweb.silk.testutils.toTestAttrs
import com.varabyte.kobweb.silk.theme.ImmutableSilkTheme
import com.varabyte.kobweb.silk.theme.MutableSilkTheme
import com.varabyte.kobweb.silk.theme.SilkTheme
import com.varabyte.kobweb.silk.theme._SilkTheme
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.name
import com.varabyte.kobweb.test.compose.callComposable
import com.varabyte.truthish.assertThat
import org.jetbrains.compose.web.css.*
import kotlin.test.AfterTest
import kotlin.test.Test

@Suppress("LocalVariableName")
class CssStyleTest {
    sealed interface TestKind : ComponentKind

    // See CssStyle#withColorModeScope for more details about this style selector syntax
    private fun inScopeSelector(className: String) = ":where(&).$className, .$className"

    @AfterTest
    fun tearDown() {
        _SilkTheme = null
    }

    @Test
    fun componentVariantStylesSheetOrder() {
        val stylesheet = StyleSheet()
        // Styles must be non-empty to be registered
        val BaseStyle = CssStyle.base<ComponentKind> { Modifier.color(Colors.Red) }
        val Variant = BaseStyle.addVariantBase { Modifier.color(Colors.Red) }
        _SilkTheme = MutableSilkTheme().apply {
            // Intentionally register out of order
            registerVariant("variant-style", Variant)
            registerStyle("base-style", BaseStyle)
        }.let(::ImmutableSilkTheme)
        SilkTheme.registerStylesInto(stylesheet)

        assertThat(stylesheet.flattenCssRules().map { it.header })
            .containsExactly(inScopeSelector(BaseStyle.name), inScopeSelector(Variant.name))
            .inOrder()
    }

    @Test
    fun cssStyleExtendedStylesheetOrder() {
        val stylesheet = StyleSheet()
        // Styles must be non-empty to be registered
        val BaseStyle = CssStyle.base { Modifier.color(Colors.Red) }
        val SecondaryStyle = BaseStyle.extendedByBase { Modifier.color(Colors.Red) }
        val TertiaryStyle = SecondaryStyle.extendedByBase { Modifier.color(Colors.Red) }
        _SilkTheme = MutableSilkTheme().apply {
            // Intentionally register out of order
            registerStyle("tertiary-style", TertiaryStyle)
            registerStyle("base-style", BaseStyle)
            registerStyle("secondary-style", SecondaryStyle)
        }.let(::ImmutableSilkTheme)
        SilkTheme.registerStylesInto(stylesheet)

        assertThat(stylesheet.flattenCssRules().map { it.header })
            .containsExactly(
                inScopeSelector(BaseStyle.name),
                inScopeSelector(SecondaryStyle.name),
                inScopeSelector(TertiaryStyle.name)
            )
            .inOrder()
    }


    @Test
    fun cssStyleEmptyLayerNameResultsInNoLayer() {
        val stylesheet = StyleSheet()
        // Styles must be non-empty to be registered
        val BaseStyle = CssStyle.base<ComponentKind> { Modifier.color(Colors.Red) }
        _SilkTheme = MutableSilkTheme().apply {
            registerStyle("base-style", BaseStyle, layer = "")
        }.let(::ImmutableSilkTheme)
        SilkTheme.registerStylesInto(stylesheet)
        // All styles get wrapped in a parent scope automatically
        val scopeRule = stylesheet.cssRules.first() as CSSScopeRuleDeclaration
        val styleRule = scopeRule.rules.first() as CSSStyleRuleDeclaration
        assertThat(styleRule.header).isEqualTo(inScopeSelector(BaseStyle.name))
    }

    @Test
    fun cssStyleExtendedClasses() {
        val BaseStyle = CssStyle { }
        val SecondaryStyle = BaseStyle.extendedBy { }
        val TertiaryStyle = SecondaryStyle.extendedBy { }
        initSilk {
            with(it.theme) {
                registerStyle("base-style", BaseStyle)
                registerStyle("secondary-style", TertiaryStyle)
                registerStyle("tertiary-style", SecondaryStyle)
            }
        }

        callComposable {
            assertThat(BaseStyle.toModifier().toTestAttrs().classes).containsExactly(BaseStyle.name)
            assertThat(SecondaryStyle.toModifier().toTestAttrs().classes)
                .containsExactly(SecondaryStyle.name, BaseStyle.name)
            assertThat(TertiaryStyle.toModifier().toTestAttrs().classes)
                .containsExactly(TertiaryStyle.name, SecondaryStyle.name, BaseStyle.name)
        }
    }

    @Test
    fun cssStyleExtendedWithColorModeClasses() {
        val BaseStyle = CssStyle.base { Modifier.color(if (colorMode.isLight) Colors.White else Colors.Black) }
        val SecondaryStyle = BaseStyle.extendedByBase {
            Modifier.color(if (colorMode.isLight) Colors.White else Colors.Black)
        }
        initSilk {
            with(it.theme) {
                registerStyle("base-style", BaseStyle)
                registerStyle("secondary-style", SecondaryStyle)
            }
        }

        callComposable {
            ColorMode.currentState.value = ColorMode.LIGHT
            assertThat(BaseStyle.toModifier().toTestAttrs().classes)
                .containsExactly(BaseStyle.name, "${BaseStyle.name}_light")
            assertThat(SecondaryStyle.toModifier().toTestAttrs().classes).containsExactly(
                SecondaryStyle.name, "${SecondaryStyle.name}_light", BaseStyle.name, "${BaseStyle.name}_light"
            )

            ColorMode.currentState.value = ColorMode.DARK
            assertThat(BaseStyle.toModifier().toTestAttrs().classes)
                .containsExactly(BaseStyle.name, "${BaseStyle.name}_dark")
            assertThat(SecondaryStyle.toModifier().toTestAttrs().classes).containsExactly(
                SecondaryStyle.name, "${SecondaryStyle.name}_dark", BaseStyle.name, "${BaseStyle.name}_dark"
            )
        }
    }

    @Test
    fun cssStyleExtendedVariants() {

        val TestStyle = CssStyle<TestKind> { }
        val BaseTestVariant = TestStyle.addVariant { }
        val PrimaryBaseTestVariant = BaseTestVariant.extendedBy { }
        val SecondaryBaseTestVariant = PrimaryBaseTestVariant.extendedBy { }
        val TertiaryBaseTestVariant = SecondaryBaseTestVariant.extendedBy { }
        initSilk {
            with(it.theme) {
                registerStyle("test", TestStyle)
                registerVariant("-base", BaseTestVariant)
                registerVariant("-primary-base", PrimaryBaseTestVariant)
                registerVariant("-secondary-base", SecondaryBaseTestVariant)
                registerVariant("-tertiary-base", TertiaryBaseTestVariant)
            }
        }

        callComposable {
            assertThat(TestStyle.toModifier(TertiaryBaseTestVariant).toTestAttrs().classes).containsExactly(
                TestStyle.name,
                BaseTestVariant.name,
                PrimaryBaseTestVariant.name,
                SecondaryBaseTestVariant.name,
                TertiaryBaseTestVariant.name
            )
        }
    }

    @Test
    fun cssStyleNameFor() {
        val BaseStyle = CssStyle { }
        val BaseStyleName = "base-style"
        val SecondaryStyle = BaseStyle.extendedBy { }
        val SecondaryStyleName = "secondary-style"
        val ComponentStyle = CssStyle<ComponentKind> { }
        val ComponentStyleName = "component-style"
        val VariantStyle = ComponentStyle.addVariant { }
        val VariantStyleName = "variant-style"
        initSilk {
            with(it.theme) {
                registerStyle(BaseStyleName, BaseStyle)
                registerStyle(SecondaryStyleName, SecondaryStyle)
                registerStyle(ComponentStyleName, ComponentStyle)
                registerVariant(VariantStyleName, VariantStyle)
            }
        }

        assertThat(BaseStyle.name).isEqualTo(BaseStyleName)
        assertThat(SecondaryStyle.name).isEqualTo(SecondaryStyleName)
        assertThat(ComponentStyle.name).isEqualTo(ComponentStyleName)
        assertThat(VariantStyle.name).isEqualTo(VariantStyleName)
    }
}
