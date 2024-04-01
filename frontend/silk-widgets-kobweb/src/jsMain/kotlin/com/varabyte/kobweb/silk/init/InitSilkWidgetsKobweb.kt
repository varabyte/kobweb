package com.varabyte.kobweb.silk.init

import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.silk.components.document.TocBorderedVariant
import com.varabyte.kobweb.silk.components.document.TocStyle
import com.varabyte.kobweb.silk.components.graphics.FitWidthImageVariant
import com.varabyte.kobweb.silk.components.graphics.ImageStyle
import com.varabyte.kobweb.silk.components.navigation.AlwaysUnderlinedLinkVariant
import com.varabyte.kobweb.silk.components.navigation.LinkStyle
import com.varabyte.kobweb.silk.components.navigation.UncoloredLinkVariant
import com.varabyte.kobweb.silk.components.navigation.UndecoratedLinkVariant
import com.varabyte.kobweb.silk.theme.colors.palette.link

// Note: This expects to be called after `initSilkWidgets` is called first.
fun initSilkWidgetsKobweb(ctx: InitSilkContext) {
    val mutableTheme = ctx.theme

    mutableTheme.palettes.apply {
        light.apply {
            link.set(
                default = Colors.Blue,
                visited = Colors.Purple,
            )
        }
        dark.apply {
            link.set(
                default = Colors.Cyan,
                visited = Colors.Violet,
            )
        }
    }

    // TODO: add modifyCssStyle
//    mutableTheme.modifyComponentStyleBase(SilkColorsStyle) {
//        val palette = colorMode.toPalette()
//        Modifier
//            .setVariable(LinkVars.DefaultColor, palette.link.default)
//            .setVariable(LinkVars.VisitedColor, palette.link.visited)
//    }

    // TODO: Automate the creation of this list (with a Gradle task?)

    mutableTheme.registerComponentStyle(ImageStyle)
    mutableTheme.registerComponentVariants(FitWidthImageVariant)

    mutableTheme.registerComponentStyle(LinkStyle)
    mutableTheme.registerComponentVariants(
        UncoloredLinkVariant,
        UndecoratedLinkVariant,
        AlwaysUnderlinedLinkVariant,
    )

    mutableTheme.registerComponentStyle(TocStyle)
    mutableTheme.registerComponentVariants(TocBorderedVariant)
}
