package com.varabyte.kobweb.silk.components.graphics

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.dom.ElementRefScope
import com.varabyte.kobweb.compose.dom.registerRefScope
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.navigation.RoutePrefix
import com.varabyte.kobweb.navigation.prependIf
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.ComponentVariant
import com.varabyte.kobweb.silk.components.style.addVariantBase
import com.varabyte.kobweb.silk.components.style.toModifier
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Img
import org.w3c.dom.HTMLImageElement

val ImageStyle by ComponentStyle(prefix = "silk") {}

val FitWidthImageVariant by ImageStyle.addVariantBase {
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
 *
 * @param autoPrefix If true AND if a route prefix is configured for this site, auto-affix it to the front. You usually
 *   want this to be true, unless you are intentionally linking outside this site's root folder while still staying in
 *   the same domain.
 */
@Composable
fun Image(
    src: String,
    modifier: Modifier = Modifier,
    variant: ComponentVariant? = null,
    width: Int? = null,
    height: Int? = null,
    alt: String = "",
    autoPrefix: Boolean = true,
    ref: ElementRefScope<HTMLImageElement>? = null,
) {
    if (ref != null) {
        Div(Modifier.display(DisplayStyle.None).toAttrs()) {
            registerRefScope(ref) { it.nextSibling as HTMLImageElement }
        }
    }
    Img(RoutePrefix.prependIf(autoPrefix, src), alt, attrs = ImageStyle.toModifier(variant).then(modifier).toAttrs {
        if (width != null) attr("width", width.toString())
        if (height != null) attr("height", height.toString())
    })
}

/**
 * Convenience version of [Image] with a non-optional [alt] parameter.
 *
 * Setting alt text is a common and encouraged use-case.
 */
@Composable
fun Image(
    src: String,
    alt: String,
    modifier: Modifier = Modifier,
    variant: ComponentVariant? = null,
    width: Int? = null,
    height: Int? = null,
    autoPrefix: Boolean = true,
    ref: ElementRefScope<HTMLImageElement>? = null,
) {
    Image(src, modifier, variant, width, height, alt, autoPrefix, ref)
}
