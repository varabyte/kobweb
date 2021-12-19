package com.varabyte.kobweb.compose.style

import org.jetbrains.compose.web.css.*

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
            gridTemplateColumns("1fr")
            gridTemplateRows("1fr")
        }

        // justify in grid means "row" while align means "col"

        // Default styles for children placement

        ".kobweb-box.kobweb-top-start" {
            alignItems(AlignItems.Start)
            justifyItems(AlignItems.Start.value)
        }
        ".kobweb-box.kobweb-top" {
            alignItems(AlignItems.Start)
            justifyItems(AlignItems.Center.value)
        }
        ".kobweb-box.kobweb-top-end" {
            alignItems(AlignItems.Start)
            justifyItems(AlignItems.End.value)
        }
        ".kobweb-box.kobweb-center-start" {
            alignItems(AlignItems.Center)
            justifyItems(AlignItems.Start.value)
        }
        ".kobweb-box.kobweb-center" {
            alignItems(AlignItems.Center)
            justifyItems(AlignItems.Center.value)
        }
        ".kobweb-box.kobweb-center-end" {
            justifyItems(AlignItems.End.value)
            alignItems(AlignItems.Center)
        }
        ".kobweb-box.kobweb-bottom-start" {
            justifyItems(AlignItems.Start.value)
            alignItems(AlignItems.End)
        }
        ".kobweb-box.kobweb-bottom" {
            justifyItems(AlignItems.Center.value)
            alignItems(AlignItems.End)
        }
        ".kobweb-box.kobweb-bottom-end" {
            justifyItems(AlignItems.End.value)
            alignItems(AlignItems.End)
        }

        // Styles when the child wants to override the parent setting

        ".kobweb-box > .kobweb-top-start" {
            alignSelf(AlignSelf.Start)
            justifySelf(AlignSelf.Start.value)
        }
        ".kobweb-box > .kobweb-top" {
            alignSelf(AlignSelf.Start)
            justifySelf(AlignSelf.Center.value)
        }
        ".kobweb-box > .kobweb-top-end" {
            alignSelf(AlignSelf.Start)
            justifySelf(AlignSelf.End.value)
        }
        ".kobweb-box > .kobweb-center-start" {
            alignSelf(AlignSelf.Center)
            justifySelf(AlignSelf.Start.value)
        }
        ".kobweb-box > .kobweb-center" {
            alignSelf(AlignSelf.Center)
            justifySelf(AlignSelf.Center.value)
        }
        ".kobweb-box > .kobweb-center-end" {
            justifySelf(AlignSelf.End.value)
            alignSelf(AlignSelf.Center)
        }
        ".kobweb-box > .kobweb-bottom-start" {
            justifySelf(AlignSelf.Start.value)
            alignSelf(AlignSelf.End)
        }
        ".kobweb-box > .kobweb-bottom" {
            justifySelf(AlignSelf.Center.value)
            alignSelf(AlignSelf.End)
        }
        ".kobweb-box > .kobweb-bottom-end" {
            justifySelf(AlignSelf.End.value)
            alignSelf(AlignSelf.End)
        }
    }

    private fun initRow() {
        ".kobweb-row" {
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Row)
            flexWrap(FlexWrap.Wrap)
        }

        // Default styles for children placement

        ".kobweb-row.kobweb-start" { justifyContent(JustifyContent.FlexStart) }
        ".kobweb-row.kobweb-center" { justifyContent(JustifyContent.Center) }
        ".kobweb-row.kobweb-end" { justifyContent(JustifyContent.FlexEnd) }

        ".kobweb-row.kobweb-top" { alignItems(AlignItems.FlexStart) }
        ".kobweb-row.kobweb-center-vert" { alignItems(AlignItems.Center) }
        ".kobweb-row.kobweb-bottom" { alignItems(AlignItems.FlexEnd) }

        // Styles when the child wants to override the parent setting

        ".kobweb-row > .kobweb-top" { alignSelf(AlignSelf.FlexStart) }
        ".kobweb-row > .kobweb-center-vert" { alignSelf(AlignSelf.Center) }
        ".kobweb-row > .kobweb-bottom" { alignSelf(AlignSelf.FlexEnd) }
    }

    private fun initCol() {
        ".kobweb-col" {
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Column)
        }

        // Default styles for children placement

        ".kobweb-col.kobweb-top" { justifyContent(JustifyContent.FlexStart) }
        ".kobweb-col.kobweb-center" { justifyContent(JustifyContent.Center) }
        ".kobweb-col.kobweb-bottom" { justifyContent(JustifyContent.FlexEnd) }

        ".kobweb-col.kobweb-start" { alignItems(AlignItems.FlexStart) }
        ".kobweb-col.kobweb-center-horiz" { alignItems(AlignItems.Center) }
        ".kobweb-col.kobweb-end" { alignItems(AlignItems.FlexEnd) }

        // Styles when the child wants to override the parent setting

        ".kobweb-col > .kobweb-start" { alignSelf(AlignSelf.FlexStart) }
        ".kobweb-col > .kobweb-center-horiz" { alignSelf(AlignSelf.Center) }
        ".kobweb-col > .kobweb-end" { alignSelf(AlignSelf.FlexEnd) }
    }

    private fun initSpacer() {
        ".kobweb-spacer" {
            flexGrow(1)
        }
    }
}