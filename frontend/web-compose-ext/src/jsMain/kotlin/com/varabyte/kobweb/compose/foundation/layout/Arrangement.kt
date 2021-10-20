package com.varabyte.kobweb.compose.foundation.layout

interface Arrangement {
    interface Horizontal
    interface Vertical
    interface HorizontalOrVertical : Horizontal, Vertical

    companion object {
        val End = object : Horizontal {}
        val Start = object : Horizontal {}
        val Top = object : Vertical {}
        val Bottom = object : Vertical {}
        val Center = object : HorizontalOrVertical {}
    }
}