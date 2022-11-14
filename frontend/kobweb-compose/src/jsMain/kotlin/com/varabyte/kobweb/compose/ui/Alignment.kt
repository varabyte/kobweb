package com.varabyte.kobweb.compose.ui

sealed interface Alignment {
    sealed interface Vertical : Alignment
    sealed interface Horizontal : Alignment

    object TopStart : Alignment
    object TopCenter : Alignment
    object TopEnd : Alignment
    object CenterStart : Alignment
    object Center : Alignment
    object CenterEnd : Alignment
    object BottomStart : Alignment
    object BottomCenter : Alignment
    object BottomEnd : Alignment

    object Top : Vertical
    object CenterVertically : Vertical
    object Bottom : Vertical

    object Start : Horizontal
    object CenterHorizontally : Horizontal
    object End : Horizontal
}