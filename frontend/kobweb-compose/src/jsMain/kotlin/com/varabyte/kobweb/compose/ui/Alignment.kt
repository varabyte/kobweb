package com.varabyte.kobweb.compose.ui

interface Alignment {
    interface Vertical
    interface Horizontal

    companion object {
        val TopStart = object : Alignment {}
        val TopCenter = object : Alignment {}
        val TopEnd = object : Alignment {}
        val CenterStart = object : Alignment {}
        val Center = object : Alignment {}
        val CenterEnd = object : Alignment {}
        val BottomStart = object : Alignment {}
        val BottomCenter = object : Alignment {}
        val BottomEnd = object : Alignment {}

        val Top = object : Alignment.Vertical {}
        val CenterVertically = object : Alignment.Vertical {}
        val Bottom = object : Alignment.Vertical {}

        val Start = object : Alignment.Horizontal {}
        val CenterHorizontally = object : Alignment.Horizontal {}
        val End = object : Alignment.Horizontal {}
    }
}