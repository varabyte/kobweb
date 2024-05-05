package com.varabyte.kobweb.silk.components.disclosure

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.calc
import com.varabyte.kobweb.compose.dom.ElementRefScope
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.BoxScope
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.silk.components.style.common.DisabledStyle
import com.varabyte.kobweb.silk.components.style.vars.animation.TransitionDurationVars
import com.varabyte.kobweb.silk.components.style.vars.color.BorderColorVar
import com.varabyte.kobweb.silk.style.ComponentKind
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.CssStyleVariant
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.selector.active
import com.varabyte.kobweb.silk.style.selector.ariaDisabled
import com.varabyte.kobweb.silk.style.selector.hover
import com.varabyte.kobweb.silk.style.selector.not
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.palette.tab
import com.varabyte.kobweb.silk.theme.colors.palette.toPalette
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLElement

object TabVars {
    val Color by StyleVariable<CSSColorValue>(prefix = "silk")
    val BorderColor by StyleVariable(prefix = "silk", defaultFallback = BorderColorVar.value())
    val BackgroundColor by StyleVariable<CSSColorValue>(prefix = "silk")
    val DisabledBackgroundColor by StyleVariable<CSSColorValue>(prefix = "silk")
    val HoverBackgroundColor by StyleVariable<CSSColorValue>(prefix = "silk")
    val PressedBackgroundColor by StyleVariable<CSSColorValue>(prefix = "silk")
    val BorderThickness by StyleVariable<CSSLengthNumericValue>(prefix = "silk", defaultFallback = 2.px)
    val ColorTransitionDuration by StyleVariable(
        prefix = "silk",
        defaultFallback = TransitionDurationVars.Normal.value()
    )
}

sealed interface TabsKind : ComponentKind {
    sealed interface TabRow : ComponentKind
    sealed interface Tab : ComponentKind
    sealed interface Panel : ComponentKind
}

val TabsStyle = CssStyle<TabsKind> {}

// TODO: should this take a variant? currently it's used without one
val TabsTabRowStyle = CssStyle.base<TabsKind.TabRow> {
    Modifier
        .fillMaxWidth()
        .borderBottom(TabVars.BorderThickness.value(), LineStyle.Solid, TabVars.BorderColor.value())
}
val TabsTabStyle = CssStyle<TabsKind.Tab>(extraModifier = { Modifier.tabIndex(0) }) {
    base {
        Modifier
            .cursor(Cursor.Pointer)
            .transition(
                *CSSTransition.group(
                    listOf("background-color", "color", "border-color"), TabVars.ColorTransitionDuration.value()
                )
            )
            .backgroundColor(TabVars.BackgroundColor.value())
            .color(TabVars.Color.value())
            .userSelect(UserSelect.None)
            .padding(0.5.cssRem)
            .margin(bottom = calc { -TabVars.BorderThickness.value() })
            .borderBottom(TabVars.BorderThickness.value(), LineStyle.Solid, TabVars.BorderColor.value())
    }

    ariaDisabled {
        Modifier.backgroundColor(TabVars.DisabledBackgroundColor.value()).cursor(Cursor.NotAllowed)
    }

    (hover + not(ariaDisabled)) {
        Modifier.backgroundColor(TabVars.HoverBackgroundColor.value())
    }

    (active + not(ariaDisabled)) {
        Modifier.backgroundColor(TabVars.PressedBackgroundColor.value())
    }
}

val TabsPanelStyle = CssStyle.base<TabsKind.Panel> {
    Modifier.padding(1.cssRem).fillMaxWidth().flexGrow(1).overflow { y(Overflow.Auto) }
}

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPEALIAS, AnnotationTarget.TYPE)
annotation class TabsScopeMarker

internal data class TabData(
    val modifier: Modifier = Modifier,
    val content: @Composable BoxScope.() -> Unit,
)

internal data class PanelData(
    val modifier: Modifier = Modifier,
    val content: @Composable BoxScope.() -> Unit,
)

internal data class TabPanelData(
    val enabled: Boolean = true,
    val isDefault: Boolean = false,
    val tab: TabData,
    val panel: PanelData
)

@TabsScopeMarker
class TabPanelScope {
    internal var tab: TabData? = null
    internal var panel: PanelData? = null

    @Suppress("FunctionName") // Composable style
    fun Tab(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
        check(tab == null) { "Attempting to define two tabs for a single TabPanel" }
        tab = TabData(modifier, content)
    }

    @Suppress("FunctionName") // Composable style
    fun Panel(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
        check(panel == null) { "Attempting to define two panels for a single TabPanel" }
        panel = PanelData(modifier, content)
    }
}

/**
 * Convenience method for creating a [Tab] that is just text.
 *
 * Using it looks like this:
 *
 * ```
 * TabPanel {
 *   Tab("Tab")
 *   Panel { /* ... */ }
 * }
 * ```
 */
@Suppress("FunctionName") // Composable style
fun TabPanelScope.Tab(text: String, modifier: Modifier = Modifier) {
    Tab(modifier) {
        Text(text)
    }
}

@TabsScopeMarker
class TabsScope {
    private val _tabPanels = mutableListOf<TabPanelData>()
    internal val tabPanels: List<TabPanelData> = _tabPanels

    @Suppress("FunctionName") // Composable style
    fun TabPanel(enabled: Boolean = true, isDefault: Boolean = false, block: TabPanelScope.() -> Unit) {
        val scope = TabPanelScope().apply(block)
        check(scope.tab != null) { "TabPanel did not declare Tab" }
        check(scope.panel != null) { "TabPanel did not declare Panel" }

        _tabPanels.add(TabPanelData(enabled, isDefault, scope.tab!!, scope.panel!!))
    }
}

/**
 * Convenience method for creating a tab panel whose tab is just text.
 *
 * Using it looks like this:
 *
 * ```
 * TabPanel("Tab") {
 *   /* ... panel definition here ... */
 * }
 * ```
 */
@Suppress("FunctionName") // Composable style
fun TabsScope.TabPanel(
    tabText: String,
    tabModifier: Modifier = Modifier,
    panelModifier: Modifier = Modifier,
    enabled: Boolean = true,
    isDefault: Boolean = false,
    content: @TabsScopeMarker @Composable BoxScope.() -> Unit
) {
    TabPanel(enabled, isDefault) {
        Tab(tabText, tabModifier)
        Panel(panelModifier, content)
    }
}

/**
 * A set of tabs, where each tab is associated with a single panel.
 *
 * A very basic tab declaration looks something like this:
 *
 * ```
 * Tabs {
 *     TabPanel {
 *         Tab { Text("Tab 1") }
 *         Panel { Text("Panel 1") }
 *     }
 *     TabPanel {
 *         Tab { Text("Tab 2") }
 *         Panel { Text("Panel 2") }
 *     }
 * }
 * ```
 *
 * In other words, `Tabs` is a collection of `TabPanel`s, and each `TabPanel` MUST define exactly one tab and one
 * associated panel.
 *
 * Each `TabPanel` can also be configured to be disabled, and/or to be the default tab that is selected when the widget
 * first composes.
 *
 * For example, here's a collection of three tabs, with the first disabled and the third set to be selected by default:
 *
 * ```
 * Tabs {
 *     TabPanel(enabled = false) {
 *         Tab { Text("Tab 1") }; Panel { Text("Panel 1") }
 *     }
 *     TabPanel {
 *         Tab { Text("Tab 2") }; Panel { Text("Panel 2") }
 *     }
 *     TabPanel(isDefault = true) {
 *         Tab { Text("Tab 3") }; Panel { Text("Panel 3") }
 *     }
 * }
 * ```
 *
 * When first composed, the initially active tab will be the first non-disabled tab that is marked as default, or the
 * first non-disabled tab if none are marked as default. (If there are no tabs or all tabs are disabled, an exception
 * will be thrown).
 *
 * While each `Tab` and `Panel` call can take their own individual modifiers, you can specify common modifiers that
 * apply across all of them.
 *
 * For example, if you want to have all your tabs stretch equally to fill all available space, you can do this:
 *
 * ```
 * Tabs(commonTabModifier = Modifier.flexGrow(1)) { /* ... */ }
 * ```
 *
 * and you can always exclude an individual tab using its individual modifier:
 *
 * ```
 * Tabs(commonTabModifier = Modifier.flexGrow(1)) {
 *   TabPanel { /* ... */ } // Tab 1
 *   TabPanel { /* ... */ } // Tab 2
 *   TabPanel { Tab(Modifier.flexGrow(0)) { Text("Tab 3") }; /* ... */ }
 * }
 * ```
 *
 * @param commonTabModifier A modifier that will get applied to all tabs. You can override and/or extend this on a
 *   per-tab basis using [TabPanelScope.Tab]'s `modifier` parameter.
 *
 * @param commonPanelModifier Like `commonTabModifier`, but for the panel sections.
 *
 * @param onTabSelected A callback that will be invoked when the user selects a tab. The callback will be passed the
 *   index of the selected tab.
 */
@Composable
fun Tabs(
    modifier: Modifier = Modifier,
    variant: CssStyleVariant<TabsKind>? = null,
    tabVariant: CssStyleVariant<TabsKind.Tab>? = null,
    panelVariant: CssStyleVariant<TabsKind.Panel>? = null,
    commonTabModifier: Modifier = Modifier,
    commonPanelModifier: Modifier = Modifier,
    onTabSelected: (Int) -> Unit = {},
    ref: ElementRefScope<HTMLElement>? = null,
    block: TabsScope.() -> Unit
) {
    val tabPanels = TabsScope().apply(block).tabPanels
    if (tabPanels.isEmpty()) {
        error("Tabs must declare at least one TabPanel")
    }
    var selectedTabIndex by remember(tabPanels) {
        mutableStateOf(
            tabPanels.indexOfFirst { it.isDefault && it.enabled }.takeIf { it >= 0 }
                ?: tabPanels.indexOfFirst { it.enabled }.takeIf { it >= 0 }
                ?: error("All tabs are disabled")
        )
    }

    Column(TabsStyle.toModifier(variant).then(modifier), ref = ref) {
        Row(TabsTabRowStyle.toModifier()) {
            val tabPalette = ColorMode.current.toPalette().tab
            tabPanels.forEachIndexed { i, tabPanel ->
                val isActive = (i == selectedTabIndex)

                fun selectTab() {
                    if (tabPanel.enabled) {
                        selectedTabIndex = i
                        onTabSelected(i)
                    }
                }

                Box(
                    Modifier
                        .thenIf(isActive) {
                            Modifier
                                .setVariable(TabVars.Color, tabPalette.selectedColor)
                                .setVariable(TabVars.BackgroundColor, tabPalette.selectedBackground)
                                .setVariable(TabVars.BorderColor, tabPalette.selectedBorder)
                        }
                        .then(TabsTabStyle.toModifier(tabVariant))
                        .then(commonTabModifier)
                        .then(tabPanel.tab.modifier)
                        .thenIf(!tabPanel.enabled, DisabledStyle.toModifier())
                        .onClick { selectTab() }
                        .onKeyDown { if (it.code == "Space") selectTab() },
                    contentAlignment = Alignment.Center,
                ) {
                    tabPanel.tab.content(this)
                }
            }
        }

        val currTabPanel = tabPanels[selectedTabIndex]
        Box(
            TabsPanelStyle.toModifier(panelVariant)
                .then(commonPanelModifier)
                .then(currTabPanel.panel.modifier)
        ) {
            currTabPanel.panel.content(this)
        }
    }
}
