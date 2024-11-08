package com.varabyte.kobweb.silk.components.graphics

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.dom.ElementRefScope
import com.varabyte.kobweb.compose.dom.registerRefScope
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.navigation.BasePath
import com.varabyte.kobweb.silk.style.ComponentKind
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.CssStyleVariant
import com.varabyte.kobweb.silk.style.addVariantBase
import com.varabyte.kobweb.silk.style.toModifier
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Img
import org.w3c.dom.HTMLImageElement

sealed interface ImageKind : ComponentKind

val ImageStyle = CssStyle<ImageKind> {}

val FitWidthImageVariant = ImageStyle.addVariantBase {
    Modifier
        .width(100.percent)
        .objectFit(ObjectFit.ScaleDown)
}

/**
 * An [Img] tag with a more Silk-like API.
 *
 * @param width The width, in pixels, of the image. If not specified, the image will be displayed at its natural size.
 *   However, it's better to specify the width and height if known so that the browser can reserve the space for the
 *   image.
 *
 * @param height See docs for [width], except this applies to the height of the image in pixels.
 *
 * @param alt An optional description which gets used as alt text for the image. This is useful to include for
 *   accessibility tools.
 */
@Composable
fun Image(
    src: String,
    modifier: Modifier = Modifier,
    variant: CssStyleVariant<ImageKind>? = null,
    width: Int? = null,
    height: Int? = null,
    alt: String = "",
    ref: ElementRefScope<HTMLImageElement>? = null,
) {
    if (ref != null) {
        Div(Modifier.display(DisplayStyle.None).toAttrs()) {
            registerRefScope(ref) { it.nextSibling as HTMLImageElement }
        }
    }
    Img(BasePath.prepend(src), alt, attrs = ImageStyle.toModifier(variant).then(modifier).toAttrs {
        if (width != null) attr("width", width.toString())
        if (height != null) attr("height", height.toString())
    })
}

/**
 * Convenience version of `Image` where the alt description is not optional.
 *
 * We provide this convenience method since it is strongly encouraged to include a description with your
 * images for accessibility reasons.
 *
 * Note that the parameter here is called `description` instead of `alt` to avoid ambiguity issues with the other
 * `Image` method. In other words, because of this decision, you can write this code:
 * ```
 * Image(
 * "/my-image.png",
 * alt = "My image description",
 * modifier = Modifier.stuff()
 * ```
 * and the compiler won't complain about getting confused between which method you're trying to call.
 */
@Composable
fun Image(
    src: String,
    description: String,
    modifier: Modifier = Modifier,
    variant: CssStyleVariant<ImageKind>? = null,
    width: Int? = null,
    height: Int? = null,
    ref: ElementRefScope<HTMLImageElement>? = null,
) {
    Image(src, modifier, variant, width, height, description, ref)
}
