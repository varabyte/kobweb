package nekt.ui.components

import androidx.compose.runtime.Composable
import nekt.ui.config.Theme
import nekt.ui.css.withTransitionDefaults
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.ContentBuilder
import org.w3c.dom.HTMLAnchorElement
import nekt.core.components.Link as CoreLink

// TODO: Is there a way to do this without having to create a new link? CSS variables maybe?
@Composable
fun Link(
    href: String? = null,
    attrs: AttrBuilderContext<HTMLAnchorElement>? = null,
    content: ContentBuilder<HTMLAnchorElement>? = null,
) {
    val linkColor = Theme.colors.getActivePalette().link
    CoreLink(
        href,
        {
            if (attrs != null) {
                attrs()
            }
            styleBuilder.color(linkColor)
            styleBuilder.withTransitionDefaults("color")
        },
        content)
}