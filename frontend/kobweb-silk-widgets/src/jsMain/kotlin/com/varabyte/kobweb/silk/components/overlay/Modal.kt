package com.varabyte.kobweb.silk.components.overlay

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.BoxScope
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.defer.deferRender
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.base
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.defer.renderWithDeferred
import com.varabyte.kobweb.silk.theme.toSilkPalette
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px

val ModalBackdropStyle = ComponentStyle.base("silk-modal-backdrop") {
    Modifier
        .position(Position.Absolute)
        .backgroundColor(colorMode.toSilkPalette().modal.backdrop)
        .top(0.px).bottom(0.px).left(0.px).right(0.px)
}

val ModalStyle = ComponentStyle.base("silk-modal") {
    Modifier
        .margin(top = 15.percent)
        .backgroundColor(colorMode.toSilkPalette().modal.background)
        .padding(20.px)
        .borderRadius(5.percent)
}

/**
 * Renders some content into a simple modal dialog UI.
 *
 * Note: For users who are only using silk widgets and not kobweb, then you must call [renderWithDeferred] yourself
 * first, as a parent method that this lives under. See the method for more details.
 */
@Composable
fun Modal(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    deferRender {
        Box(ModalBackdropStyle.toModifier(), contentAlignment = Alignment.TopCenter) {
            Box(ModalStyle.toModifier().then(modifier)) {
                content()
            }
        }
    }
}