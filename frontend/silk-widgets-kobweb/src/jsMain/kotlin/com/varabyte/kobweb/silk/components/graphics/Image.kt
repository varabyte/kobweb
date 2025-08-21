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
 * Providing a hint to the user agent as to how to best schedule the loading of the image to optimize page performance.
 *
 * @param Eager The default behavior, eager tells the browser to load the image as soon as the <img> element is processed.
 * @param Lazy Tells the user agent to hold off on loading the image until the browser estimates that it will be needed imminently.
 */
enum class ImageLoading(private val value: String) {
    Eager("eager"),
    Lazy("lazy");

    override fun toString(): String {
        return value
    }
}

/**
 * Provides a hint to the browser as to whether it should perform image decoding along with other tasks in a single step [Sync], or allow other content to be rendered before this completes [Async].
 *
 * @param Sync Decode the image synchronously for atomic presentation with other content.
 * @param Async Decode the image asynchronously and allow other content to be rendered before this completes.
 * @param Auto No preference for the decoding mode; the browser decides what is best for the user.
 */
enum class ImageDecoding(private val value: String) {
    Sync("sync"),
    Async("async"),
    Auto("auto");

    override fun toString(): String {
        return value
    }
}

/**
 * Indicating how the browser should prioritize fetching a particular image relative to other images.
 *
 * @param High Fetch the image at a high priority relative to other images with the same internal prioritization.
 * @param Low Fetch the image at a low priority relative to other images with the same internal prioritization.
 * @param Auto Don't set a user preference for the fetch priority.
 */
enum class ImageFetchPriority(private val value: String) {
    High("high"),
    Low("low"),
    Auto("auto");

    override fun toString(): String {
        return value
    }
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
    loading: ImageLoading? = null,
    decoding: ImageDecoding? = null,
    fetchPriority: ImageFetchPriority? = null
) {
    if (ref != null) {
        Div(Modifier.display(DisplayStyle.None).toAttrs()) {
            registerRefScope(ref) { it.nextSibling as HTMLImageElement }
        }
    }
    Img(BasePath.prependTo(src), alt, attrs = ImageStyle.toModifier(variant).then(modifier).toAttrs {
        if (width != null) attr("width", width.toString())
        if (height != null) attr("height", height.toString())
        if (loading != null) attr("loading", loading.toString())
        if (decoding != null) attr("decoding", decoding.toString())
        if (fetchPriority != null) attr("fetchpriority", fetchPriority.toString())
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
    loading: ImageLoading? = null,
    decoding: ImageDecoding? = null,
    fetchPriority: ImageFetchPriority? = null
) {
    Image(
        src = src,
        modifier = modifier,
        variant = variant,
        width = width,
        height = height,
        alt = description,
        ref = ref,
        loading = loading,
        decoding = decoding,
        fetchPriority = fetchPriority
    )
}
