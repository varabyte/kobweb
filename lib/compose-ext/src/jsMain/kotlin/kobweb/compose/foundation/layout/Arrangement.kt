package kobweb.compose.foundation.layout

interface Arrangement {
    interface Horizontal
    interface Vertical

    companion object {
        val End = object : Horizontal {}
        val Start = object : Horizontal {}
        val Top = object : Vertical {}
        val Bottom = object : Vertical {}
    }
}
