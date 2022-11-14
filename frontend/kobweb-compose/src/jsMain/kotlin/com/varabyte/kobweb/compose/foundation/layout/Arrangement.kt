package com.varabyte.kobweb.compose.foundation.layout

object Arrangement {
    sealed interface Horizontal
    sealed interface Vertical
    sealed interface HorizontalOrVertical : Horizontal, Vertical

    object End : Horizontal
    object Start : Horizontal
    object Top : Vertical
    object Bottom : Vertical
    object Center : HorizontalOrVertical
    object SpaceEvenly : HorizontalOrVertical
    object SpaceBetween : HorizontalOrVertical
    object SpaceAround : HorizontalOrVertical
}
