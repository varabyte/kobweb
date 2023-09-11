package playground.pages

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.dom.ElementTarget
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.BoxScope
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.silk.components.disclosure.Tabs
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.forms.ButtonSize
import com.varabyte.kobweb.silk.components.forms.Checkbox
import com.varabyte.kobweb.silk.components.forms.CheckboxIconScope
import com.varabyte.kobweb.silk.components.forms.CheckboxSize
import com.varabyte.kobweb.silk.components.forms.CheckedState
import com.varabyte.kobweb.silk.components.forms.FilledInputVariant
import com.varabyte.kobweb.silk.components.forms.FlushedInputVariant
import com.varabyte.kobweb.silk.components.forms.Input
import com.varabyte.kobweb.silk.components.forms.InputGroup
import com.varabyte.kobweb.silk.components.forms.InputSize
import com.varabyte.kobweb.silk.components.forms.OutlinedInputVariant
import com.varabyte.kobweb.silk.components.forms.Switch
import com.varabyte.kobweb.silk.components.forms.SwitchShape
import com.varabyte.kobweb.silk.components.forms.SwitchSize
import com.varabyte.kobweb.silk.components.forms.TextInput
import com.varabyte.kobweb.silk.components.forms.TriCheckbox
import com.varabyte.kobweb.silk.components.forms.UnstyledInputVariant
import com.varabyte.kobweb.silk.components.icons.CheckIcon
import com.varabyte.kobweb.silk.components.icons.ChevronDownIcon
import com.varabyte.kobweb.silk.components.icons.ChevronLeftIcon
import com.varabyte.kobweb.silk.components.icons.ChevronRightIcon
import com.varabyte.kobweb.silk.components.icons.ChevronUpIcon
import com.varabyte.kobweb.silk.components.icons.CircleIcon
import com.varabyte.kobweb.silk.components.icons.IndeterminateIcon
import com.varabyte.kobweb.silk.components.icons.MinusIcon
import com.varabyte.kobweb.silk.components.icons.PlusIcon
import com.varabyte.kobweb.silk.components.icons.SquareIcon
import com.varabyte.kobweb.silk.components.icons.fa.FaBolt
import com.varabyte.kobweb.silk.components.icons.fa.FaCheck
import com.varabyte.kobweb.silk.components.icons.fa.FaDollarSign
import com.varabyte.kobweb.silk.components.icons.fa.FaUser
import com.varabyte.kobweb.silk.components.icons.fa.IconStyle
import com.varabyte.kobweb.silk.components.overlay.Tooltip
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.base
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.colors.ColorSchemes
import com.varabyte.kobweb.silk.theme.toSilkPalette
import org.jetbrains.compose.web.attributes.AutoComplete
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Hr
import org.jetbrains.compose.web.dom.Text
import playground.components.layouts.PageLayout
import playground.components.widgets.GoHomeLink

val WidgetSectionStyle by ComponentStyle.base {
    Modifier
        .fillMaxWidth()
        .border(1.px, LineStyle.Solid, colorMode.toSilkPalette().border)
        .position(Position.Relative)
}

val WidgetPaddingStyle by ComponentStyle.base {
    Modifier
        .fillMaxSize()
        .padding(1.cssRem)
}

val WidgetLabelStyle by ComponentStyle.base {
    Modifier
        .position(Position.Relative)
        .fontSize(0.8.cssRem)
        .left(0.3.cssRem)
        .top((-.7).cssRem)
        .padding(0.2.cssRem)
        .backgroundColor(colorMode.toSilkPalette().background)
}

val IconContainerStyle by ComponentStyle.base {
    Modifier
        .padding(0.2.cssRem)
        .border(1.px, LineStyle.Solid, colorMode.toSilkPalette().border)
        .borderRadius(3.px)
        .cursor(Cursor.Help)
}

@Composable
fun WidgetSection(title: String, content: @Composable BoxScope.() -> Unit) {
    Box(WidgetSectionStyle.toModifier()) {
        Box(WidgetLabelStyle.toModifier()) {
            SpanText(title)
        }
        Box(WidgetPaddingStyle.toModifier()) {
            content()
        }
    }
}

@Page
@Composable
fun WidgetsPage() {
    PageLayout("WIDGETS") {
        Column(
            Modifier.gap(2.cssRem).fillMaxWidth().padding(2.cssRem).maxWidth(800.px),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WidgetSection("Button") {
                Column(Modifier.gap(0.5.cssRem)) {
                    listOf(null, ColorSchemes.Red, ColorSchemes.Blue, ColorSchemes.Green).forEach { colorScheme ->
                        Row(Modifier.gap(1.cssRem), verticalAlignment = Alignment.CenterVertically) {
                            listOf(ButtonSize.XS, ButtonSize.SM, ButtonSize.MD, ButtonSize.LG).forEach { size ->
                                Button(onClick = {}, size = size, colorScheme = colorScheme) { Text("Button") }
                            }
                        }
                    }
                }
            }

            WidgetSection("Checkbox") {
                Column(Modifier.gap(1.cssRem)) {
                    Row(Modifier.gap(1.cssRem), verticalAlignment = Alignment.CenterVertically) {
                        listOf(CheckboxSize.SM, CheckboxSize.MD, CheckboxSize.LG).forEach { size ->
                            var checked by remember { mutableStateOf(true) }
                            Checkbox(
                                checked,
                                onCheckedChange = { checked = it },
                                size = size,
                            ) { Text("Checkbox") }
                        }
                    }

                    Row(Modifier.gap(1.cssRem), verticalAlignment = Alignment.CenterVertically) {
                        listOf(ColorSchemes.Red, ColorSchemes.Green, ColorSchemes.Orange).forEach { colorScheme ->
                            var checked by remember { mutableStateOf(true) }
                            Checkbox(
                                checked,
                                onCheckedChange = { checked = it },
                                colorScheme = colorScheme
                            ) { Text("Checkbox") }
                        }
                    }

                    run {
                        var checked by remember { mutableStateOf(true) }
                        Checkbox(checked, onCheckedChange = { checked = it }, enabled = false) { Text("Disabled") }
                    }

                    Row(Modifier.gap(1.cssRem), verticalAlignment = Alignment.CenterVertically) {
                        // Note: We could have also used `CheckedIcon { PlusIcon() }` etc. below, but we don't ever care
                        // about the indeterminate state so it doesn't matter.
                        val iconOverrides = mapOf<String, @Composable CheckboxIconScope.() -> Unit>(
                            "Plus" to { PlusIcon() },
                            "Square" to { SquareIcon() },
                            "Circle" to { CircleIcon() },
                            "Font Awesome" to { FaBolt() }
                        )

                        for ((iconLabel, iconProvider) in iconOverrides) {
                            var checked by remember { mutableStateOf(true) }
                            Checkbox(
                                checked,
                                onCheckedChange = { checked = it },
                                icon = iconProvider
                            ) { Text(iconLabel) }
                        }
                    }

                    Column(Modifier.gap(0.2.cssRem)) {
                        var child1Checked by remember { mutableStateOf(false) }
                        var child2Checked by remember { mutableStateOf(false) }

                        TriCheckbox(
                            CheckedState.from(child1Checked, child2Checked),
                            onCheckedChange = {
                                val checked = it.toBoolean()
                                child1Checked = checked
                                child2Checked = checked
                            },
                        ) { Text("Parent") }
                        Column(Modifier.margin(left = 1.cssRem).gap(0.2.cssRem)) {
                            Checkbox(child1Checked, onCheckedChange = { child1Checked = it }) { Text("Child 1") }
                            Checkbox(child2Checked, onCheckedChange = { child2Checked = it }) { Text("Child 2") }
                        }
                    }
                }
            }

            WidgetSection("Icons") {
                val icons = mapOf<String, @Composable () -> Unit>(
                    "Check" to { CheckIcon() },
                    "Chevron Down" to { ChevronDownIcon() },
                    "Chevron Left" to { ChevronLeftIcon() },
                    "Chevron Right" to { ChevronRightIcon() },
                    "Chevron Up" to { ChevronUpIcon() },
                    "Circle" to { CircleIcon() },
                    "Indeterminate" to { IndeterminateIcon() },
                    "Minus" to { MinusIcon() },
                    "Plus" to { PlusIcon() },
                    "Square" to { SquareIcon() },
                )

                Row(
                    Modifier.gap(0.5.cssRem).flexWrap(FlexWrap.Wrap).fontSize(0.8.cssRem),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    icons.forEach { (iconName, iconMethod) ->
                        Box(IconContainerStyle.toModifier()) { iconMethod() }
                        Tooltip(ElementTarget.PreviousSibling, iconName)
                    }
                }
            }

            WidgetSection("Input") {
                var text by remember { mutableStateOf("") }
                Column(Modifier.gap(0.5.cssRem).fillMaxWidth()) {
                    Row(Modifier.fillMaxWidth()) {
                        Column(Modifier.gap(0.5.cssRem)) {
                            TextInput(
                                text,
                                placeholder = "extra small size",
                                size = InputSize.XS,
                                onTextChanged = { text = it })
                            TextInput(
                                text,
                                placeholder = "small size",
                                size = InputSize.SM,
                                onTextChanged = { text = it })
                            TextInput(
                                text,
                                placeholder = "medium size",
                                size = InputSize.MD,
                                onTextChanged = { text = it })
                            TextInput(
                                text,
                                placeholder = "large size",
                                size = InputSize.LG,
                                onTextChanged = { text = it })
                        }

                        Spacer()

                        Column(Modifier.gap(0.5.cssRem)) {
                            TextInput(
                                text,
                                placeholder = "outlined",
                                variant = OutlinedInputVariant,
                                onTextChanged = { text = it })
                            TextInput(
                                text,
                                placeholder = "filled",
                                variant = FilledInputVariant,
                                onTextChanged = { text = it })
                            TextInput(
                                text,
                                placeholder = "flushed",
                                variant = FlushedInputVariant,
                                onTextChanged = { text = it })
                            TextInput(
                                text,
                                placeholder = "unstyled",
                                variant = UnstyledInputVariant,
                                onTextChanged = { text = it })
                        }
                    }

                    Hr(Modifier.fillMaxWidth().toAttrs())

                    Row(Modifier.gap(0.5.cssRem).fillMaxWidth().flexWrap(FlexWrap.Wrap)) {
                        Column(Modifier.gap(0.5.cssRem)) {
                            var telNum by remember { mutableStateOf("") }
                            InputGroup {
                                LeftAddon { Text("+1") }
                                Input(
                                    InputType.Tel,
                                    telNum,
                                    placeholder = "phone number",
                                    autoComplete = AutoComplete.telNational,
                                    onValueChanged = { telNum = it })
                            }

                            var url by remember { mutableStateOf("") }
                            InputGroup(size = InputSize.SM) {
                                LeftAddon { Text("https://") }
                                TextInput(url, placeholder = "url", onTextChanged = { url = it })
                                RightAddon { Text(".com") }
                            }

                            var dateTime by remember { mutableStateOf("") }
                            Input(InputType.DateTimeLocal, dateTime, onValueChanged = { dateTime = it })
                        }

                        Spacer()

                        Column(Modifier.gap(0.5.cssRem)) {
                            var username by remember { mutableStateOf("") }
                            InputGroup {
                                LeftInset { FaUser(style = IconStyle.FILLED) }
                                TextInput(
                                    username,
                                    placeholder = "username",
                                    onTextChanged = { username = it })
                            }

                            val dollarRegex = Regex("""^(\d{1,3}(,\d{3})*|(\d+))(\.\d{2})?$""")
                            var amount by remember { mutableStateOf("") }
                            InputGroup(size = InputSize.SM) {
                                LeftInset { FaDollarSign() }
                                TextInput(
                                    amount,
                                    placeholder = "amount",
                                    onTextChanged = { amount = it })
                                RightInset {
                                    if (dollarRegex.matches(amount)) {
                                        FaCheck(Modifier.color(ColorSchemes.Green._500))
                                    }
                                }
                            }

                            var showPassword by remember { mutableStateOf(false) }
                            var password by remember { mutableStateOf("") }
                            InputGroup(Modifier.width(230.px)) {
                                TextInput(
                                    password,
                                    password = !showPassword,
                                    onTextChanged = { password = it })
                                RightInset(width = 4.5.cssRem) {
                                    Button(
                                        onClick = { showPassword = !showPassword },
                                        Modifier.width(3.5.cssRem).height(1.75.cssRem),
                                        size = ButtonSize.SM,
                                    ) {
                                        Text(if (showPassword) "Hide" else "Show")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            WidgetSection("Switch") {
                Column(Modifier.fillMaxWidth().gap(1.cssRem)) {
                    SwitchShape.entries.forEach { shape ->
                        Row(Modifier.gap(1.cssRem), verticalAlignment = Alignment.CenterVertically) {
                            listOf(SwitchSize.SM, SwitchSize.MD, SwitchSize.LG).forEach { size ->
                                var checked by remember { mutableStateOf(false) }
                                Switch(
                                    checked,
                                    onCheckedChange = { checked = it },
                                    size = size,
                                    shape = shape
                                )
                            }
                        }
                    }
                }
            }

            WidgetSection("Tabs") {
                Tabs {
                    TabPanel {
                        Tab { Text("Tab 1") }; Panel { Text("Panel 1") }
                    }
                    TabPanel {
                        Tab { Text("Tab 2") }; Panel { Text("Panel 2") }
                    }
                    TabPanel {
                        Tab { Text("Tab 3") }; Panel { Text("Panel 3") }
                    }
                }
            }

            GoHomeLink()
        }
    }
}
