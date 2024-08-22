package com.varabyte.kobweb.compose.style

import com.varabyte.kobweb.compose.css.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.AlignSelf
import org.jetbrains.compose.web.css.JustifyContent

/**
 * The name of the CSS layer used by Kobweb Compose widgets.
 *
 * These values are wrapped into a layer so that users, if they really want to and know what they are doing, can define
 * their own styles that override some of these values.
 */
const val KOBWEB_COMPOSE_LAYER = "kobweb-compose"

object KobwebComposeStyleSheet : StyleSheet() {
    init {
        layer(KOBWEB_COMPOSE_LAYER) {
            initBox()
            initCol()
            initRow()
            initSpacer()
            initArrangeSpacedByStyle()
        }
    }

    private fun GenericStyleSheetBuilder<CSSStyleRuleBuilder>.initBox() {
        ".kobweb-box" {
            // The Compose "Box" concept means: all children should be stacked one of top of the other. We do this by
            // setting the current element to grid but then jam all of its children into its top-left (and only) cell.
            grid {
                // Why minmax? See: https://github.com/w3c/csswg-drafts/issues/1777
                columns { minmax(0.px, 1.fr) }
                rows { minmax(0.px, 1.fr) }
            }
        }

        // justify in grid means "row" while align means "col"

        // Default styles for children placement

        ".kobweb-box > *" {
            gridArea("1", "1")
        }

        ".kobweb-box.kobweb-align-top-start" {
            alignItems(AlignItems.Start)
            justifyItems(JustifyItems.Start)
        }
        ".kobweb-box.kobweb-align-top-center" {
            alignItems(AlignItems.Start)
            justifyItems(JustifyItems.Center)
        }
        ".kobweb-box.kobweb-align-top-end" {
            alignItems(AlignItems.Start)
            justifyItems(JustifyItems.End)
        }
        ".kobweb-box.kobweb-align-center-start" {
            alignItems(AlignItems.Center)
            justifyItems(JustifyItems.Start)
        }
        ".kobweb-box.kobweb-align-center" {
            alignItems(AlignItems.Center)
            justifyItems(JustifyItems.Center)
        }
        ".kobweb-box.kobweb-align-center-end" {
            alignItems(AlignItems.Center)
            justifyItems(JustifyItems.End)
        }
        ".kobweb-box.kobweb-align-bottom-start" {
            alignItems(AlignItems.End)
            justifyItems(JustifyItems.Start)
        }
        ".kobweb-box.kobweb-align-bottom-center" {
            alignItems(AlignItems.End)
            justifyItems(JustifyItems.Center)
        }
        ".kobweb-box.kobweb-align-bottom-end" {
            alignItems(AlignItems.End)
            justifyItems(JustifyItems.End)
        }

        // Styles when the child wants to override the parent setting

        ".kobweb-box > .kobweb-align-top-start-self" {
            alignSelf(AlignSelf.Start)
            justifySelf(JustifySelf.Start)
        }
        ".kobweb-box > .kobweb-align-top-center-self" {
            alignSelf(AlignSelf.Start)
            justifySelf(JustifySelf.Center)
        }
        ".kobweb-box > .kobweb-align-top-end-self" {
            alignSelf(AlignSelf.Start)
            justifySelf(JustifySelf.End)
        }
        ".kobweb-box > .kobweb-align-center-start-self" {
            alignSelf(AlignSelf.Center)
            justifySelf(JustifySelf.Start)
        }
        ".kobweb-box > .kobweb-align-center-self" {
            alignSelf(AlignSelf.Center)
            justifySelf(JustifySelf.Center)
        }
        ".kobweb-box > .kobweb-align-center-end-self" {
            justifySelf(JustifySelf.End)
            alignSelf(AlignSelf.Center)
        }
        ".kobweb-box > .kobweb-align-bottom-start-self" {
            justifySelf(JustifySelf.Start)
            alignSelf(AlignSelf.End)
        }
        ".kobweb-box > .kobweb-align-bottom-center-self" {
            justifySelf(JustifySelf.Center)
            alignSelf(AlignSelf.End)
        }
        ".kobweb-box > .kobweb-align-bottom-end-self" {
            justifySelf(JustifySelf.End)
            alignSelf(AlignSelf.End)
        }
    }

    private fun GenericStyleSheetBuilder<CSSStyleRuleBuilder>.initRow() {
        ".kobweb-row" {
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Row)
        }

        // Default styles for children placement

        ".kobweb-row.$KOBWEB_ARRANGE_START" { justifyContent(JustifyContent.FlexStart) }
        ".kobweb-row.$KOBWEB_ARRANGE_CENTER" { justifyContent(JustifyContent.Center) }
        ".kobweb-row.$KOBWEB_ARRANGE_END" { justifyContent(JustifyContent.FlexEnd) }
        ".kobweb-row.$KOBWEB_ARRANGE_SPACE_EVENLY" { justifyContent(JustifyContent.SpaceEvenly) }
        ".kobweb-row.$KOBWEB_ARRANGE_SPACE_BETWEEN" { justifyContent(JustifyContent.SpaceBetween) }
        ".kobweb-row.$KOBWEB_ARRANGE_SPACE_AROUND" { justifyContent(JustifyContent.SpaceAround) }

        ".kobweb-row.kobweb-align-top" { alignItems(AlignItems.FlexStart) }
        ".kobweb-row.kobweb-align-center-vert" { alignItems(AlignItems.Center) }
        ".kobweb-row.kobweb-align-bottom" { alignItems(AlignItems.FlexEnd) }

        // Styles when the child wants to override the parent setting

        ".kobweb-row > .kobweb-align-top-self" { alignSelf(AlignSelf.FlexStart) }
        ".kobweb-row > .kobweb-align-center-vert-self" { alignSelf(AlignSelf.Center) }
        ".kobweb-row > .kobweb-align-bottom-self" { alignSelf(AlignSelf.FlexEnd) }
    }

    private fun GenericStyleSheetBuilder<CSSStyleRuleBuilder>.initCol() {
        ".kobweb-col" {
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Column)
        }

        // Default styles for children placement

        ".kobweb-col.$KOBWEB_ARRANGE_TOP" { justifyContent(JustifyContent.FlexStart) }
        ".kobweb-col.$KOBWEB_ARRANGE_CENTER" { justifyContent(JustifyContent.Center) }
        ".kobweb-col.$KOBWEB_ARRANGE_BOTTOM" { justifyContent(JustifyContent.FlexEnd) }
        ".kobweb-col.$KOBWEB_ARRANGE_SPACE_EVENLY" { justifyContent(JustifyContent.SpaceEvenly) }
        ".kobweb-col.$KOBWEB_ARRANGE_SPACE_BETWEEN" { justifyContent(JustifyContent.SpaceBetween) }
        ".kobweb-col.$KOBWEB_ARRANGE_SPACE_AROUND" { justifyContent(JustifyContent.SpaceAround) }

        ".kobweb-col.kobweb-align-start" { alignItems(AlignItems.FlexStart) }
        ".kobweb-col.kobweb-align-center-horiz" { alignItems(AlignItems.Center) }
        ".kobweb-col.kobweb-align-end" { alignItems(AlignItems.FlexEnd) }

        // Styles when the child wants to override the parent setting

        ".kobweb-col > .kobweb-align-start-self" { alignSelf(AlignSelf.FlexStart) }
        ".kobweb-col > .kobweb-align-center-horiz-self" { alignSelf(AlignSelf.Center) }
        ".kobweb-col > .kobweb-align-end-self" { alignSelf(AlignSelf.FlexEnd) }
    }

    private fun GenericStyleSheetBuilder<CSSStyleRuleBuilder>.initSpacer() {
        ".kobweb-spacer" {
            flexGrow(1)
        }
    }
}
