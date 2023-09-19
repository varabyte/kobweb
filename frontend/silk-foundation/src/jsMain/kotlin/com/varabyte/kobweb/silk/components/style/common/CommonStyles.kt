package com.varabyte.kobweb.silk.components.style.common

import com.varabyte.kobweb.silk.components.style.StyleModifiers
import com.varabyte.kobweb.silk.components.style.ariaDisabled
import com.varabyte.kobweb.silk.components.style.ariaInvalid
import com.varabyte.kobweb.silk.components.style.ariaRequired

/**
 * A way to select elements that have been tagged with an `aria-disabled` attribute.
 *
 * This is different from the `:disabled` pseudo-class selector! There are various reasons to use the ARIA version over
 * the HTML version; for example, some elements don't support `disabled` and also `disabled` elements don't fire
 * mouse events, which can be useful e.g. when implementing tooltips.
 */
@Deprecated("Use com.varabyte.kobweb.silk.components.style.ariaDisabled instead.",
    ReplaceWith("ariaDisabled", "com.varabyte.kobweb.silk.components.style.ariaDisabled")
)
val StyleModifiers.ariaDisabled get() = ariaDisabled

/**
 * A way to select elements that have been tagged with an `aria-invalid` attribute.
 *
 * This is different from the `:invalid` pseudo-class selector! The `invalid` pseudo-class is useful when the standard
 * widget supports a general invalidation algorithm (like an email type input with an invalid email address), but the
 * `ariaInvalid` version can be used to support custom invalidation strategies.
 */
@Deprecated("Use com.varabyte.kobweb.silk.components.style.ariaInvalid instead.",
    ReplaceWith("ariaInvalid", "com.varabyte.kobweb.silk.components.style.ariaInvalid")
)
val StyleModifiers.ariaInvalid get() = ariaInvalid

/**
 * A way to select elements that have been tagged with an `aria-required` attribute.
 *
 * This is different from the `:required` pseudo-class selector! It can be useful to use the `ariaRequired` tag when
 * using elements that don't support the `required` attribute, like elements created from divs, as a way to communicate
 * their required state to accessibility readers.
 */
@Deprecated("Use com.varabyte.kobweb.silk.components.style.ariaRequired instead.",
    ReplaceWith("ariaRequired", "com.varabyte.kobweb.silk.components.style.ariaRequired")
)
val StyleModifiers.ariaRequired get() = ariaRequired
