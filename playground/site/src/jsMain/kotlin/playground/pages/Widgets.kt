package playground.pages

import androidx.compose.runtime.*
import com.varabyte.kobweb.browser.dom.ElementTarget
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.BoxScope
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.navigation.OpenLinkStrategy
import com.varabyte.kobweb.silk.components.disclosure.Tabs
import com.varabyte.kobweb.silk.components.display.Callout
import com.varabyte.kobweb.silk.components.display.CalloutType
import com.varabyte.kobweb.silk.components.display.LeftBorderedFilledCalloutVariant
import com.varabyte.kobweb.silk.components.display.OutlinedCalloutVariant
import com.varabyte.kobweb.silk.components.display.OutlinedFilledCalloutVariant
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
import com.varabyte.kobweb.silk.components.icons.ArrowBackIcon
import com.varabyte.kobweb.silk.components.icons.ArrowDownIcon
import com.varabyte.kobweb.silk.components.icons.ArrowForwardIcon
import com.varabyte.kobweb.silk.components.icons.ArrowUpIcon
import com.varabyte.kobweb.silk.components.icons.AttachmentIcon
import com.varabyte.kobweb.silk.components.icons.CheckIcon
import com.varabyte.kobweb.silk.components.icons.ChevronDownIcon
import com.varabyte.kobweb.silk.components.icons.ChevronLeftIcon
import com.varabyte.kobweb.silk.components.icons.ChevronRightIcon
import com.varabyte.kobweb.silk.components.icons.ChevronUpIcon
import com.varabyte.kobweb.silk.components.icons.CircleIcon
import com.varabyte.kobweb.silk.components.icons.CloseIcon
import com.varabyte.kobweb.silk.components.icons.DownloadIcon
import com.varabyte.kobweb.silk.components.icons.ExclaimIcon
import com.varabyte.kobweb.silk.components.icons.HamburgerIcon
import com.varabyte.kobweb.silk.components.icons.IndeterminateIcon
import com.varabyte.kobweb.silk.components.icons.InfoIcon
import com.varabyte.kobweb.silk.components.icons.LightbulbIcon
import com.varabyte.kobweb.silk.components.icons.MinusIcon
import com.varabyte.kobweb.silk.components.icons.MoonIcon
import com.varabyte.kobweb.silk.components.icons.PlusIcon
import com.varabyte.kobweb.silk.components.icons.QuestionIcon
import com.varabyte.kobweb.silk.components.icons.QuoteIcon
import com.varabyte.kobweb.silk.components.icons.SquareIcon
import com.varabyte.kobweb.silk.components.icons.StopIcon
import com.varabyte.kobweb.silk.components.icons.SunIcon
import com.varabyte.kobweb.silk.components.icons.WarningIcon
import com.varabyte.kobweb.silk.components.icons.fa.FaBolt
import com.varabyte.kobweb.silk.components.icons.fa.FaCheck
import com.varabyte.kobweb.silk.components.icons.fa.FaCloud
import com.varabyte.kobweb.silk.components.icons.fa.FaDollarSign
import com.varabyte.kobweb.silk.components.icons.fa.FaGithub
import com.varabyte.kobweb.silk.components.icons.fa.FaHouse
import com.varabyte.kobweb.silk.components.icons.fa.FaStar
import com.varabyte.kobweb.silk.components.icons.fa.FaUser
import com.varabyte.kobweb.silk.components.icons.mdi.MdiClose
import com.varabyte.kobweb.silk.components.icons.mdi.MdiHome
import com.varabyte.kobweb.silk.components.icons.mdi.MdiMenu
import com.varabyte.kobweb.silk.components.icons.mdi.MdiSearch
import com.varabyte.kobweb.silk.components.layout.SimpleGrid
import com.varabyte.kobweb.silk.components.layout.numColumns
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.overlay.KeepPopupOpenStrategy
import com.varabyte.kobweb.silk.components.overlay.Popover
import com.varabyte.kobweb.silk.components.overlay.PopupPlacement
import com.varabyte.kobweb.silk.components.overlay.Tooltip
import com.varabyte.kobweb.silk.components.overlay.manual
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.theme.colors.ColorPalettes
import com.varabyte.kobweb.silk.theme.colors.palette.background
import com.varabyte.kobweb.silk.theme.colors.palette.border
import com.varabyte.kobweb.silk.theme.colors.palette.toPalette
import org.jetbrains.compose.web.attributes.AutoComplete
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Hr
import org.jetbrains.compose.web.dom.Text
import playground.components.layouts.PageLayoutData
import playground.components.widgets.GoHomeLink
import com.varabyte.kobweb.silk.components.icons.fa.IconStyle as FaIconStyle
import com.varabyte.kobweb.silk.components.icons.mdi.IconStyle as MdiIconStyle

val WidgetSectionStyle = CssStyle.base {
    Modifier
        .fillMaxWidth()
        .border(1.px, LineStyle.Solid, colorMode.toPalette().border)
        .position(Position.Relative)
}

val WidgetPaddingStyle = CssStyle.base {
    Modifier
        .fillMaxSize()
        .padding(1.cssRem)
}

val WidgetLabelStyle = CssStyle.base {
    Modifier
        .position(Position.Relative)
        .fontSize(0.8.cssRem)
        .left(0.3.cssRem)
        .top((-.7).cssRem)
        .padding(0.2.cssRem)
        .backgroundColor(colorMode.toPalette().background)
}

val IconContainerStyle = CssStyle.base {
    Modifier
        .padding(0.2.cssRem)
        .border(1.px, LineStyle.Solid, colorMode.toPalette().border)
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

@InitRoute
fun initPostPage(ctx: InitRouteContext) {
    ctx.data.add(PageLayoutData("WIDGETS"))
}

@Page
@Composable
fun WidgetsPage() {
    Column(
        Modifier.gap(2.cssRem).fillMaxWidth().padding(2.cssRem).maxWidth(800.px),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        WidgetSection("Button") {
            Column(Modifier.gap(0.5.cssRem)) {
                listOf(null, ColorPalettes.Red, ColorPalettes.Blue, ColorPalettes.Green).forEach { colorPalette ->
                    Row(Modifier.gap(1.cssRem), verticalAlignment = Alignment.CenterVertically) {
                        listOf(ButtonSize.XS, ButtonSize.SM, ButtonSize.MD, ButtonSize.LG).forEach { size ->
                            Button(onClick = {}, size = size, colorPalette = colorPalette) { Text("Button") }
                        }
                    }
                }
            }
        }

        WidgetSection("Callout") {
            Div {
                listOf(
                    CalloutType.NOTE,
                    CalloutType.TIP,
                    CalloutType.IMPORTANT,
                    CalloutType.WARNING,
                ).forEach { type ->
                    Callout(type, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.")
                    Callout(type, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.", variant = LeftBorderedFilledCalloutVariant)
                    Callout(type, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.", variant = OutlinedFilledCalloutVariant)
                    Callout(type, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.", variant = OutlinedCalloutVariant)
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
                    listOf(ColorPalettes.Red, ColorPalettes.Green, ColorPalettes.Orange).forEach { colorPalette ->
                        var checked by remember { mutableStateOf(true) }
                        Checkbox(
                            checked,
                            onCheckedChange = { checked = it },
                            colorPalette = colorPalette
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
            Column(Modifier.fillMaxWidth().gap(1.cssRem).padding(top = 0.5.cssRem)) {
                WidgetSection("SVG") {
                    val icons = mapOf<String, @Composable () -> Unit>(
                        "Arrow Back" to { ArrowBackIcon() },
                        "Arrow Down" to { ArrowDownIcon() },
                        "Arrow Forward" to { ArrowForwardIcon() },
                        "Arrow Up" to { ArrowUpIcon() },
                        "Attachment" to { AttachmentIcon() },
                        "Check" to { CheckIcon() },
                        "Chevron Down" to { ChevronDownIcon() },
                        "Chevron Left" to { ChevronLeftIcon() },
                        "Chevron Right" to { ChevronRightIcon() },
                        "Chevron Up" to { ChevronUpIcon() },
                        "Circle" to { CircleIcon() },
                        "Close" to { CloseIcon() },
                        "Download" to { DownloadIcon() },
                        "Exclaim" to { ExclaimIcon() },
                        "Hamburger" to { HamburgerIcon() },
                        "Indeterminate" to { IndeterminateIcon() },
                        "Info" to { InfoIcon() },
                        "Lightbulb" to { LightbulbIcon() },
                        "Minus" to { MinusIcon() },
                        "Moon" to { MoonIcon() },
                        "Plus" to { PlusIcon() },
                        "Question" to { QuestionIcon() },
                        "Quote" to { QuoteIcon() },
                        "Square" to { SquareIcon() },
                        "Stop" to { StopIcon() },
                        "Sun" to { SunIcon() },
                        "Warning" to { WarningIcon() },
                    )

                    Row(
                        Modifier.gap(0.5.cssRem).flexWrap(FlexWrap.Wrap),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        icons.forEach { (iconName, iconMethod) ->
                            Box(IconContainerStyle.toModifier()) { iconMethod() }
                            Tooltip(ElementTarget.PreviousSibling, iconName)
                        }
                    }
                }

                WidgetSection("Web - Font Awesome") {
                    val faIcons = mapOf<String, @Composable () -> Unit>(
                        "Home" to { FaHouse() },
                        "Cloud" to { FaCloud() },
                        "Star" to { FaStar(style = FaIconStyle.FILLED) },
                        "GitHub" to { FaGithub() }
                    )

                    Row(
                        Modifier.gap(0.5.cssRem).flexWrap(FlexWrap.Wrap),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        faIcons.forEach { (iconName, iconMethod) ->
                            Box(IconContainerStyle.toModifier()) { iconMethod() }
                            Tooltip(ElementTarget.PreviousSibling, iconName)
                        }
                    }
                }

                WidgetSection("Web - Google Material Design") {
                    val mdiIcons = mapOf<String, @Composable () -> Unit>(
                        "Search" to { MdiSearch() },
                        "Home" to { MdiHome(style = MdiIconStyle.OUTLINED) },
                        "Menu" to { MdiMenu() },
                        "Close" to { MdiClose() },
                    )

                    Row(
                        Modifier.gap(0.5.cssRem).flexWrap(FlexWrap.Wrap),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        mdiIcons.forEach { (iconName, iconMethod) ->
                            Box(IconContainerStyle.toModifier()) { iconMethod() }
                            Tooltip(ElementTarget.PreviousSibling, iconName)
                        }
                    }
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
                            onTextChange = { text = it })
                        TextInput(
                            text,
                            placeholder = "small size",
                            size = InputSize.SM,
                            onTextChange = { text = it })
                        TextInput(
                            text,
                            placeholder = "medium size",
                            size = InputSize.MD,
                            onTextChange = { text = it })
                        TextInput(
                            text,
                            placeholder = "large size",
                            size = InputSize.LG,
                            onTextChange = { text = it })
                    }

                    Spacer()

                    Column(Modifier.gap(0.5.cssRem)) {
                        TextInput(
                            text,
                            placeholder = "outlined",
                            variant = OutlinedInputVariant,
                            onTextChange = { text = it })
                        TextInput(
                            text,
                            placeholder = "filled",
                            variant = FilledInputVariant,
                            onTextChange = { text = it })
                        TextInput(
                            text,
                            placeholder = "flushed",
                            variant = FlushedInputVariant,
                            onTextChange = { text = it })
                        TextInput(
                            text,
                            placeholder = "unstyled",
                            variant = UnstyledInputVariant,
                            onTextChange = { text = it })
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
                                onValueChange = { telNum = it })
                        }

                        var url by remember { mutableStateOf("") }
                        InputGroup(size = InputSize.SM) {
                            LeftAddon { Text("https://") }
                            TextInput(url, placeholder = "url", onTextChange = { url = it })
                            RightAddon { Text(".com") }
                        }

                        var dateTime by remember { mutableStateOf("") }
                        Input(InputType.DateTimeLocal, dateTime, onValueChange = { dateTime = it })
                    }

                    Spacer()

                    Column(Modifier.gap(0.5.cssRem)) {
                        var username by remember { mutableStateOf("") }
                        InputGroup {
                            LeftInset { FaUser(style = FaIconStyle.FILLED) }
                            TextInput(
                                username,
                                placeholder = "username",
                                onTextChange = { username = it })
                        }

                        val dollarRegex = Regex("""^(\d{1,3}(,\d{3})*|(\d+))(\.\d{2})?$""")
                        var amount by remember { mutableStateOf("") }
                        InputGroup(size = InputSize.SM) {
                            LeftInset { FaDollarSign() }
                            TextInput(
                                amount,
                                placeholder = "amount",
                                onTextChange = { amount = it })
                            RightInset {
                                if (dollarRegex.matches(amount)) {
                                    FaCheck(Modifier.color(ColorPalettes.Green._500))
                                }
                            }
                        }

                        var showPassword by remember { mutableStateOf(false) }
                        var password by remember { mutableStateOf("") }
                        InputGroup(Modifier.width(230.px)) {
                            TextInput(
                                password,
                                password = !showPassword,
                                onTextChange = { password = it })
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

        WidgetSection("Popovers and Tooltips") {
            Column(Modifier.gap(1.cssRem).padding(0.5.cssRem)) {
                SimpleGrid(numColumns(3), Modifier.gap(0.5.cssRem)) {
                    for (y in -1..1) {
                        for (x in -1..1) {
                            Box(
                                Modifier.size(150.px).backgroundColor(Colors.Blue)
                                    .borderRadius(5.px),
                            )
                            if (y != 0 || x != 0) {
                                val placement = when {
                                    y == -1 && x == -1 -> PopupPlacement.TopLeft
                                    y == -1 && x == 0 -> PopupPlacement.Top
                                    y == -1 && x == 1 -> PopupPlacement.TopRight
                                    y == 0 && x == -1 -> PopupPlacement.Left
                                    y == 0 && x == 1 -> PopupPlacement.Right
                                    y == 1 && x == -1 -> PopupPlacement.BottomLeft
                                    y == 1 && x == 0 -> PopupPlacement.Bottom
                                    y == 1 && x == 1 -> PopupPlacement.BottomRight
                                    else -> error("Unexpected coordinates ($x, $y)")
                                }

                                Popover(
                                    ElementTarget.PreviousSibling,
                                    placement = placement,
                                ) {
                                    Box(Modifier.backgroundColor(Colors.Green).padding(0.3.cssRem)) {
                                        Text(placement.name)
                                    }
                                }
                            }
                        }
                    }
                }

                Column { // Conditional popup
                    Row(Modifier.gap(0.3.cssRem)) {
                        var count by remember { mutableStateOf(0) }
                        Button({ count++ }) { Text("Click to increment popup value") }
                        if (count < 10) {
                            Tooltip(
                                ElementTarget.PreviousSibling,
                                "Lorem ipsum $count",
                            )
                        }
                        Button({ count = 0 }, enabled = count > 0) { Text("Reset") }
                    }
                }

                Column { // Should still show even if already mouse over
                    var showTooltipOnClick by remember { mutableStateOf(false) }
                    Button({ showTooltipOnClick = true },
                        Modifier.onMouseOut { showTooltipOnClick = false }
                            .onFocusOut { showTooltipOnClick = false }) { Text("Click to show tooltip") }
                    if (showTooltipOnClick) {
                        Tooltip(
                            ElementTarget.PreviousSibling,
                            "You clicked me!",
                            placement = PopupPlacement.Right,
                        )
                    }
                }

                Column(Modifier.gap(1.cssRem)) { // Translate + margin popup
                    val manualStrat = remember { KeepPopupOpenStrategy.manual() }
                    Button(onClick = { manualStrat.shouldKeepOpen = false }) {
                        Text("Click to close tooltip")
                    }
                    Tooltip(
                        ElementTarget.PreviousSibling,
                        "tooltip",
                        placement = PopupPlacement.Right,
                        keepOpenStrategy = manualStrat
                    )
                }

                Column(Modifier.gap(1.cssRem)) { // Should close after switching back to the tab
                    Link(
                        "https://example.com",
                        "Click to open a new tab",
                        openExternalLinksStrategy = OpenLinkStrategy.IN_NEW_TAB
                    )
                    Tooltip(
                        ElementTarget.PreviousSibling,
                        "This tooltip should not remain open after coming back to this page",
                        placement = PopupPlacement.Right
                    )
                }

                Box(
                    Modifier.border(1.px, LineStyle.Solid, Colors.Black).borderRadius(5.px).padding(0.5.cssRem)
                ) { // changing target / placement while popup is up
                    var targetId by remember { mutableStateOf("a") }
                    var placementId by remember { mutableStateOf("c") }
                    Column(Modifier.gap(0.5.cssRem)) {
                        SpanText(
                            "Mouse over the target element (\"Element $targetId\") to show the popup, which will be associated with the placement element (\"Element $placementId\")",
                            Modifier.textAlign(TextAlign.Left).fontStyle(FontStyle.Italic)
                        )
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Box(
                                Modifier.id("a").thenIf(targetId == "a", Modifier.fontWeight(FontWeight.Bold))
                            ) { SpanText("Element a") }
                            Box(
                                Modifier.id("b").thenIf(targetId == "b", Modifier.fontWeight(FontWeight.Bold))
                            ) { SpanText("Element b") }
                            Box(
                                Modifier.id("c").thenIf(placementId == "c", Modifier.fontStyle(FontStyle.Italic))
                            ) { SpanText("Element c") }
                            Box(
                                Modifier.id("d").thenIf(placementId == "d", Modifier.fontStyle(FontStyle.Italic))
                            ) { SpanText("Element d") }
                        }
                        Row(Modifier.gap(0.3.cssRem)) {
                            Button({
                                targetId = if (targetId == "a") "b" else "a"
                            }) { Text("Toggle Target ($targetId)") }
                            Button({
                                placementId = if (placementId == "c") "d" else "c"
                            }) { Text("Toggle Placement ($placementId)") }
                        }
                    }
                    Tooltip(
                        ElementTarget.withId(targetId),
                        "Popup",
                        placement = PopupPlacement.Top,
                        placementTarget = ElementTarget.withId(placementId),
                    )
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
