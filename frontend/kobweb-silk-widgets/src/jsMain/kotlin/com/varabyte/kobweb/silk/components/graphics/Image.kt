package com.varabyte.kobweb.silk.components.graphics

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributesBuilder
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.ComponentVariant
import com.varabyte.kobweb.silk.components.style.addBaseVariant
import com.varabyte.kobweb.silk.components.style.toModifier
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Img

val ImageStyle = ComponentStyle("silk-image") {}

val FitWidthImageVariant = ImageStyle.addBaseVariant("fit") {
    Modifier
        .styleModifier {
            property("width", 100.percent)
            property("object-fit", "scale-down")
        }
}

/**
 * A Silk-styleable [Img] tag.
 *
 * @param desc An optional description which gets used as alt-text for the image. This is useful to include for
 *   accessibility tools.
 */
@Composable
fun Image(
    src: String,
    desc: String = "",
    modifier: Modifier = Modifier,
    variant: ComponentVariant? = null,
) {
    Img(src, desc, attrs = ImageStyle.toModifier(variant).then(modifier).asAttributesBuilder())
}