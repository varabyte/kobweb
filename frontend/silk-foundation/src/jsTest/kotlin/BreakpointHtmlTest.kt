import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.SilkFoundationStyles
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.between
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.style.breakpoint.BreakpointSizes
import com.varabyte.kobweb.silk.style.breakpoint.BreakpointValues
import com.varabyte.kobweb.silk.style.breakpoint.displayBetween
import com.varabyte.kobweb.silk.style.breakpoint.displayIfAtLeast
import com.varabyte.kobweb.silk.style.breakpoint.displayUntil
import com.varabyte.kobweb.silk.style.toAttrs
import com.varabyte.kobweb.silk.style.until
import com.varabyte.kobweb.silk.theme.ImmutableSilkTheme
import com.varabyte.kobweb.silk.theme.MutableSilkTheme
import com.varabyte.kobweb.silk.theme.SilkTheme
import com.varabyte.kobweb.silk.theme._SilkTheme
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.cssClass
import com.varabyte.kobweb.test.compose.computedStyle
import com.varabyte.kobweb.test.compose.runComposeTest
import com.varabyte.truthish.assertThat
import kotlinx.browser.window
import kotlinx.dom.addClass
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.Element
import kotlin.test.AfterTest
import kotlin.test.Test

@Suppress("LocalVariableName")
class BreakpointHtmlTest {
    private fun <T : Element> AttrsScope<T>.autoRef(effect: DisposableEffectScope.(T) -> Unit) = ref {
        effect(it)
        onDispose { }
    }

    private fun breakpointsWithCurrentSizeSetToMd(): BreakpointValues<*> {
        val clientWidth = window.innerWidth.px
        return BreakpointSizes(
            sm = clientWidth - 1.px,
            md = clientWidth,
            lg = clientWidth + 1.px,
            xl = clientWidth + 2.px,
            xxl = clientWidth + 3.px,
        )
    }

    @AfterTest
    fun tearDown() {
        _SilkTheme = null
    }

    @Test
    fun breakpointAwareStyles() = runComposeTest {
        val stylesheet = StyleSheet()
        val BreakpointInvokeStyle = CssStyle {
            base { Modifier.width(10.px).height(20.px).margin(30.px) }
            Breakpoint.SM { Modifier.width(40.px).height(50.px) }
            Breakpoint.MD { Modifier.width(60.px) }
            Breakpoint.LG { Modifier.width(70.px) }
        }
        val BreakpointRangeStyle = com.varabyte.kobweb.silk.style.CssStyle {
            (Breakpoint.ZERO until Breakpoint.MD) { Modifier.opacity(0.5) }
            (Breakpoint.SM..Breakpoint.MD) { Modifier.width(10.px) }
            (Breakpoint.SM..<Breakpoint.MD) { Modifier.height(20.px) }
            (Breakpoint.MD..Breakpoint.LG) { Modifier.margin(30.px) }
            (Breakpoint.LG..<Breakpoint.XL) { Modifier.padding(40.px) }
        }
        val BreakpointConvenienceStyle = com.varabyte.kobweb.silk.style.CssStyle {
            until(Breakpoint.MD) { Modifier.opacity(0.5) }
            until(Breakpoint.LG) { Modifier.width(10.px) }
            between(Breakpoint.SM, Breakpoint.MD) { Modifier.height(20.px) }
            between(Breakpoint.MD, Breakpoint.LG) { Modifier.margin(30.px) }
            between(Breakpoint.LG, Breakpoint.XL) { Modifier.padding(40.px) }
        }

        _SilkTheme = MutableSilkTheme().apply {
            breakpoints = breakpointsWithCurrentSizeSetToMd()
            registerStyle("invoke-style", BreakpointInvokeStyle)
            registerStyle("range-style", BreakpointRangeStyle)
            registerStyle("convenience-style", BreakpointConvenienceStyle)
        }.let(::ImmutableSilkTheme)
        SilkTheme.registerStylesInto(stylesheet)

        root.addClass(ColorMode.LIGHT.cssClass) // CssStyles need a parent color mode class to take effect
        composition {
            Style(stylesheet)

            Div(BreakpointInvokeStyle.toAttrs {
                autoRef {
                    assertThat(it.computedStyle.width).isEqualTo("60px")
                    assertThat(it.computedStyle.height).isEqualTo("50px")
                    assertThat(it.computedStyle.margin).isEqualTo("30px")
                }
            })
            Div(BreakpointRangeStyle.toAttrs {
                autoRef {
                    assertThat(it.computedStyle.opacity).isEqualTo("1")
                    assertThat(it.computedStyle.width).isEqualTo("10px")
                    assertThat(it.computedStyle.height).isEqualTo("0px")
                    assertThat(it.computedStyle.margin).isEqualTo("30px")
                    assertThat(it.computedStyle.padding).isEqualTo("0px")
                }
            })
            Div(BreakpointConvenienceStyle.toAttrs {
                autoRef {
                    assertThat(it.computedStyle.opacity).isEqualTo("1")
                    assertThat(it.computedStyle.width).isEqualTo("10px")
                    assertThat(it.computedStyle.height).isEqualTo("0px")
                    assertThat(it.computedStyle.margin).isEqualTo("30px")
                    assertThat(it.computedStyle.padding).isEqualTo("0px")
                }
            })
        }
    }

    @Test
    fun modifierDisplayBreakpoint() = runComposeTest {
        composition {
            SilkFoundationStyles(initSilk = { ctx ->
                ctx.theme.breakpoints = breakpointsWithCurrentSizeSetToMd()
            })
            Div(Modifier.displayIfAtLeast(Breakpoint.SM).toAttrs {
                autoRef {
                    assertThat(it.computedStyle.display).isEqualTo("block")
                }
            })
            Div(Modifier.displayIfAtLeast(Breakpoint.MD).toAttrs {
                autoRef {
                    assertThat(it.computedStyle.display).isEqualTo("block")
                }
            })
            Div(Modifier.displayIfAtLeast(Breakpoint.LG).toAttrs {
                autoRef { el ->
                    assertThat(el.computedStyle.display).isEqualTo("none")
                }
            })

            Div(Modifier.displayUntil(Breakpoint.SM).toAttrs {
                autoRef {
                    assertThat(it.computedStyle.display).isEqualTo("none")
                }
            })
            Div(Modifier.displayUntil(Breakpoint.MD).toAttrs {
                autoRef {
                    assertThat(it.computedStyle.display).isEqualTo("none")
                }
            })
            Div(Modifier.displayUntil(Breakpoint.LG).toAttrs {
                autoRef {
                    assertThat(it.computedStyle.display).isEqualTo("block")
                }
            })

            Div(Modifier.displayBetween(Breakpoint.SM, Breakpoint.MD).toAttrs {
                autoRef {
                    assertThat(it.computedStyle.display).isEqualTo("none")
                }
            })
            Div(Modifier.displayBetween(Breakpoint.MD, Breakpoint.LG).toAttrs {
                autoRef {
                    assertThat(it.computedStyle.display).isEqualTo("block")
                }
            })
            Div(Modifier.displayBetween(Breakpoint.LG, Breakpoint.XL).toAttrs {
                autoRef {
                    assertThat(it.computedStyle.display).isEqualTo("none")
                }
            })
        }
    }
}
