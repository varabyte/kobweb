package com.varabyte.kobweb.silk.components.graphics

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.navigation.RoutePrefix
import com.varabyte.kobweb.navigation.prependIf
import com.varabyte.kobweb.silk.components.style.ComponentVariant
import com.varabyte.kobweb.silk.components.style.toModifier
import org.jetbrains.compose.web.dom.Img

/**
 * A Silk-styleable [Img] tag.
 *
 * @param desc An optional description which gets used as alt-text for the image. This is useful to include for
 *   accessibility tools.
 *
 * @param autoPrefix If true AND if a route prefix is configured for this site, auto-affix it to the front. You usually
 *   want this to be true, unless you are intentionally linking outside this site's root folder while still staying in
 *   the same domain.
 */
@Composable
fun Image(
    src: String,
    desc: String = "",
    modifier: Modifier = Modifier,
    autoPrefix: Boolean = true,
    variant: ComponentVariant? = null,
) {
    Img(RoutePrefix.prependIf(autoPrefix, src), desc, attrs = ImageStyle.toModifier(variant).then(modifier).toAttrs())
}
