package com.varabyte.kobweb.compose.style

import com.varabyte.kobweb.compose.css.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.AlignSelf
import org.jetbrains.compose.web.css.JustifyContent

object KobwebComposeStyleSheet : StyleSheet() {
    init {
        initBox()
        initCol()
        initRow()
        initSpacer()
    }

    private fun initBox() {
        ".kobweb-box" {
            // The Compose "Box" concept means: all children should be stacked one of top of the other. We do this by
            // setting the current element to grid but then jam all of its children into its top-left (and only) cell.
            display(DisplayStyle.Grid)
            // See: https://github.com/w3c/csswg-drafts/issues/1777
            gridTemplateColumns(GridTrackSize.minmax(0.px, 1.fr))
            gridTemplateRows(GridTrackSize.minmax(0.px, 1.fr))
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

    private fun initRow() {
        ".kobweb-row" {
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Row)
        }

        // Default styles for children placement

        ".kobweb-row.kobweb-arrange-start" { justifyContent(JustifyContent.FlexStart) }
        ".kobweb-row.kobweb-arrange-center" { justifyContent(JustifyContent.Center) }
        ".kobweb-row.kobweb-arrange-end" { justifyContent(JustifyContent.FlexEnd) }
        ".kobweb-row.kobweb-arrange-space-evenly" { justifyContent(JustifyContent.SpaceEvenly) }
        ".kobweb-row.kobweb-arrange-space-between" { justifyContent(JustifyContent.SpaceBetween) }
        ".kobweb-row.kobweb-arrange-space-around" { justifyContent(JustifyContent.SpaceAround) }

        ".kobweb-row.kobweb-align-top" { alignItems(AlignItems.FlexStart) }
        ".kobweb-row.kobweb-align-center-vert" { alignItems(AlignItems.Center) }
        ".kobweb-row.kobweb-align-bottom" { alignItems(AlignItems.FlexEnd) }

        // Styles when the child wants to override the parent setting

        ".kobweb-row > .kobweb-align-top-self" { alignSelf(AlignSelf.FlexStart) }
        ".kobweb-row > .kobweb-align-center-vert-self" { alignSelf(AlignSelf.Center) }
        ".kobweb-row > .kobweb-align-bottom-self" { alignSelf(AlignSelf.FlexEnd) }
    }

    private fun initCol() {
        ".kobweb-col" {
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Column)
        }

        // Default styles for children placement

        ".kobweb-col.kobweb-arrange-top" { justifyContent(JustifyContent.FlexStart) }
        ".kobweb-col.kobweb-arrange-center" { justifyContent(JustifyContent.Center) }
        ".kobweb-col.kobweb-arrange-bottom" { justifyContent(JustifyContent.FlexEnd) }
        ".kobweb-col.kobweb-arrange-space-evenly" { justifyContent(JustifyContent.SpaceEvenly) }
        ".kobweb-col.kobweb-arrange-space-between" { justifyContent(JustifyContent.SpaceBetween) }
        ".kobweb-col.kobweb-arrange-space-around" { justifyContent(JustifyContent.SpaceAround) }

        ".kobweb-col.kobweb-align-start" { alignItems(AlignItems.FlexStart) }
        ".kobweb-col.kobweb-align-center-horiz" { alignItems(AlignItems.Center) }
        ".kobweb-col.kobweb-align-end" { alignItems(AlignItems.FlexEnd) }

        // Styles when the child wants to override the parent setting

        ".kobweb-col > .kobweb-align-start-self" { alignSelf(AlignSelf.FlexStart) }
        ".kobweb-col > .kobweb-align-center-horiz-self" { alignSelf(AlignSelf.Center) }
        ".kobweb-col > .kobweb-align-end-self" { alignSelf(AlignSelf.FlexEnd) }
    }

    private fun initSpacer() {
        ".kobweb-spacer" {
            flexGrow(1)
        }
    }
}
