package com.varabyte.kobweb.silk

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.style.toAttrs
import com.varabyte.kobweb.silk.theme.ImmutableSilkTheme
import com.varabyte.kobweb.silk.theme.MutableSilkTheme
import com.varabyte.kobweb.silk.theme.SilkTheme
import com.varabyte.kobweb.silk.theme._SilkTheme
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.cssClass
import com.varabyte.kobweb.test.compose.computedStyle
import com.varabyte.kobweb.test.compose.runComposeTest
import com.varabyte.truthish.assertThat
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.Element
import kotlin.test.AfterTest
import kotlin.test.Test

@Suppress("LocalVariableName")
class CssStyleColorModeHtmlTest {
    private fun <T : Element> AttrsScope<T>.autoRef(effect: DisposableEffectScope.(T) -> Unit) = ref {
        effect(it)
        onDispose { }
    }

    @AfterTest
    fun tearDown() {
        _SilkTheme = null
    }

    @Test
    fun colorModeAwareStyles() = runComposeTest {
        val stylesheet = StyleSheet()
        val ColorModeAgnosticStyle = CssStyle.base { Modifier.width(10.px) }
        val ColorModeAwareStyle = CssStyle.base { Modifier.width(if (colorMode.isLight) 10.px else 50.px) }
        val LightOnlyStyle = CssStyle.base { Modifier.thenIf(colorMode.isLight, Modifier.margin(30.px)) }
        val DarkOnlyStyle = CssStyle.base { Modifier.thenIf(colorMode.isDark, Modifier.margin(40.px)) }

        _SilkTheme = MutableSilkTheme().apply {
            registerStyle("agnostic-style", ColorModeAgnosticStyle)
            registerStyle("aware-style", ColorModeAwareStyle)
            registerStyle("light-only-style", LightOnlyStyle)
            registerStyle("dark-only-style", DarkOnlyStyle)
        }.let(::ImmutableSilkTheme)
        SilkTheme.registerStylesInto(stylesheet)

        composition {
            Style(stylesheet)

            Div({ classes(ColorMode.LIGHT.cssClass) }) {
                Div(ColorModeAgnosticStyle.toAttrs {
                    autoRef {
                        assertThat(it.computedStyle.width).isEqualTo("10px")
                    }
                })

                Div(ColorModeAwareStyle.toAttrs {
                    autoRef {
                        assertThat(it.computedStyle.width).isEqualTo("10px")
                    }
                })

                Div(LightOnlyStyle.toAttrs {
                    autoRef {
                        assertThat(it.computedStyle.margin).isEqualTo("30px")
                    }
                })

                Div(DarkOnlyStyle.toAttrs {
                    autoRef {
                        assertThat(it.computedStyle.margin).isEqualTo("0px")
                    }
                })
            }

            Div({ classes(ColorMode.DARK.cssClass) }) {
                Div(ColorModeAgnosticStyle.toAttrs {
                    autoRef {
                        assertThat(it.computedStyle.width).isEqualTo("10px")
                    }
                })

                Div(ColorModeAwareStyle.toAttrs {
                    autoRef {
                        assertThat(it.computedStyle.width).isEqualTo("50px")
                    }
                })

                Div(LightOnlyStyle.toAttrs {
                    autoRef {
                        assertThat(it.computedStyle.margin).isEqualTo("0px")
                    }
                })

                Div(DarkOnlyStyle.toAttrs {
                    autoRef {
                        assertThat(it.computedStyle.margin).isEqualTo("40px")
                    }
                })
            }
        }
    }

    @Test
    fun colorModeOverrides() = runComposeTest {
        val stylesheet = StyleSheet()
        val BaseStyle = CssStyle.base {
            Modifier
                .width(if (colorMode.isLight) 10.px else 50.px)
                .thenIf(colorMode.isLight, Modifier.margin(20.px))
        }

        _SilkTheme = MutableSilkTheme().apply {
            registerStyle("base-style", BaseStyle)
        }.let(::ImmutableSilkTheme)
        SilkTheme.registerStylesInto(stylesheet)

        composition {
            Style(stylesheet)

            Div(BaseStyle.toAttrs {
                classes(ColorMode.LIGHT.cssClass)
                autoRef {
                    assertThat(it.computedStyle.width).isEqualTo("10px")
                    assertThat(it.computedStyle.margin).isEqualTo("20px")
                }
            }) {
                Div(BaseStyle.toAttrs {
                    autoRef {
                        assertThat(it.computedStyle.width).isEqualTo("10px")
                        assertThat(it.computedStyle.margin).isEqualTo("20px")
                    }
                })
                Div(BaseStyle.toAttrs {
                    classes(ColorMode.DARK.cssClass)
                    autoRef {
                        assertThat(it.computedStyle.width).isEqualTo("50px")
                        assertThat(it.computedStyle.margin).isEqualTo("0px")
                    }
                }) {
                    Div(BaseStyle.toAttrs {
                        classes(ColorMode.LIGHT.cssClass)
                        autoRef {
                            assertThat(it.computedStyle.width).isEqualTo("10px")
                            assertThat(it.computedStyle.margin).isEqualTo("20px")
                        }
                    })
                }
            }
            Div(BaseStyle.toAttrs {
                classes(ColorMode.DARK.cssClass)
                autoRef {
                    assertThat(it.computedStyle.width).isEqualTo("50px")
                    assertThat(it.computedStyle.margin).isEqualTo("0px")
                }
            }) {
                Div(BaseStyle.toAttrs {
                    autoRef {
                        assertThat(it.computedStyle.width).isEqualTo("50px")
                        assertThat(it.computedStyle.margin).isEqualTo("0px")
                    }
                })
                Div(BaseStyle.toAttrs {
                    classes(ColorMode.LIGHT.cssClass)
                    autoRef {
                        assertThat(it.computedStyle.width).isEqualTo("10px")
                        assertThat(it.computedStyle.margin).isEqualTo("20px")
                    }
                }) {
                    Div(BaseStyle.toAttrs {
                        classes(ColorMode.DARK.cssClass)
                        autoRef {
                            assertThat(it.computedStyle.width).isEqualTo("50px")
                            assertThat(it.computedStyle.margin).isEqualTo("0px")
                        }
                    })
                }
            }
        }
    }


    @Test
    fun colorModeAwareAndAgnosticMediaQueries() = runComposeTest {
        val stylesheet = StyleSheet()
        val AgnosticMediaStyle = CssStyle {
            base {
                Modifier.width(if (colorMode.isLight) 10.px else 20.px)
            }
            Breakpoint.ZERO { // Intentionally always apply, but still use a media query
                Modifier.width(30.px)
            }
        }
        val AwareMediaStyle = CssStyle {
            base {
                Modifier.width(30.px)
            }
            Breakpoint.ZERO { // Intentionally always apply, but still use a media query
                Modifier.width(if (colorMode.isLight) 10.px else 20.px)
            }
        }

        _SilkTheme = MutableSilkTheme().apply {
            registerStyle("agnostic-media-style", AgnosticMediaStyle)
            registerStyle("aware-media-style", AwareMediaStyle)
        }.let(::ImmutableSilkTheme)
        SilkTheme.registerStylesInto(stylesheet)

        composition {
            Style(stylesheet)

            Div(AgnosticMediaStyle.toAttrs {
                classes(ColorMode.LIGHT.cssClass)
                autoRef {
                    assertThat(it.computedStyle.width).isEqualTo("30px")
                }
            }) {
                Div(AgnosticMediaStyle.toAttrs {
                    autoRef {
                        assertThat(it.computedStyle.width).isEqualTo("30px")
                    }
                })

                Div(AwareMediaStyle.toAttrs {
                    autoRef {
                        assertThat(it.computedStyle.width).isEqualTo("10px")
                    }
                })
            }

            Div(AwareMediaStyle.toAttrs {
                classes(ColorMode.DARK.cssClass)
                autoRef {
                    assertThat(it.computedStyle.width).isEqualTo("20px")
                }
            }) {
                Div(AgnosticMediaStyle.toAttrs {
                    autoRef {
                        assertThat(it.computedStyle.width).isEqualTo("30px")
                    }
                })

                Div(AwareMediaStyle.toAttrs {
                    autoRef {
                        assertThat(it.computedStyle.width).isEqualTo("20px")
                    }
                })
            }
        }
    }

    @Test
    fun consistentSelectorPriority() = runComposeTest {
        val stylesheet = object : StyleSheet() {
            init {
                // This style should take precedence over `SimpleStyle`, both for elements with the silk-light/dark class
                // and their children. This works because the `@scope` based selector uses the 0-specificity `:where()` selector
                ".simple-style.simple-style" {
                    width(20.px)
                }
            }
        }
        val SimpleStyle = CssStyle.base {
            Modifier.width(10.px)
        }

        _SilkTheme = MutableSilkTheme().apply {
            registerStyle("simple-style", SimpleStyle)
        }.let(::ImmutableSilkTheme)
        SilkTheme.registerStylesInto(stylesheet)

        composition {
            Style(stylesheet)

            Div(SimpleStyle.toAttrs {
                classes(ColorMode.LIGHT.cssClass)
                autoRef {
                    assertThat(it.computedStyle.width).isEqualTo("20px")
                }
            }) {
                Div(SimpleStyle.toAttrs {
                    autoRef {
                        assertThat(it.computedStyle.width).isEqualTo("20px")
                    }
                })
            }
        }
    }
}
