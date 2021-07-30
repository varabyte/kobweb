package nekt.ui.components.layout

import FlexBasis
import androidx.compose.runtime.Composable
import flexBasis
import org.jetbrains.compose.web.attributes.AttrsBuilder
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement

// See also: https://css-tricks.com/snippets/css/a-guide-to-flexbox/

data class FlexParams(
    val direction: FlexDirection = FlexDirection.Row,
    val wrap: FlexWrap = FlexWrap.Nowrap,
    val alignItems: AlignItems = AlignItems.Stretch,
    val alignContent: AlignContent = AlignContent.Unset,
    val justifyContent: JustifyContent = JustifyContent.FlexStart,
)

data class FlexItemParams(
    val order: Int = 0,
    val grow: Int = 0,
    val shrink: Int = 0,
    val flexBasis: FlexBasis = FlexBasis.Auto,
    val alignSelf: AlignSelf = AlignSelf.Unset
)

/**
 * Call on a target attrs builder to set this component to a flex container.
 *
 * For example, you could write:
 *
 * ```
 * Div({
 *   attrs = {
 *     setFlex()
 *   }
 * }) {
 *   ...
 * }
 * ```
 *
 * As this is relatively common to do, a helper [Flex] method is provided as well which does this for you.
 */
fun <E : Element> AttrsBuilder<E>.setAsFlexContainer(params: FlexParams = FlexParams()) {
    style {
        display(DisplayStyle.Flex)
        flexDirection(params.direction)
        flexWrap(params.wrap)
        alignItems(params.alignItems)
        alignContent(params.alignContent)
        justifyContent(params.justifyContent)
    }
}

/**
 * Call on a target attrs builder to set this component to a flex container.
 *
 * For example, you could write:
 *
 * ```
 * Div({
 *   attrs = {
 *     setFlex()
 *   }
 * }) {
 *   Text({
 *     attrs = {
 *       setFlexItem()
 *     }
 *   })
 * }
 * ```
 *
 * As this is relatively common to do, a helper [Flex] method is provided as well which does this for you.
 */
fun <E : Element> AttrsBuilder<E>.setAsFlexItem(params: FlexItemParams = FlexItemParams()) {
    style {
        order(params.order)
        flexGrow(params.grow)
        flexShrink(params.shrink)
        flexBasis(params.flexBasis)
        alignSelf(params.alignSelf)
    }
}

/**
 * A convenient element for creating an element that parents a flex layout.
 *
 * Semantically a `<div>`
 */
@Composable
fun Flex(
    params: FlexParams = FlexParams(),
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
    content: ContentBuilder<HTMLDivElement>,
) {
    Div(
        attrs = {
            if (attrs != null) {
                attrs()
            }
            setAsFlexContainer(params)
        },
        content
    )
}

/**
 * A convenience element which grows to consume all remaining space in a flex row or column.
 */
@Composable
fun Spacer() {
    Div(
        attrs = {
            setAsFlexItem(FlexItemParams(grow = 1))
        }
    )
}

