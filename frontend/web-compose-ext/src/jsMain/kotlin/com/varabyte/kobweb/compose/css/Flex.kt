import org.jetbrains.compose.web.css.CSSNumeric
import org.jetbrains.compose.web.css.StyleBuilder

// See: https://developer.mozilla.org/en-US/docs/Web/CSS/flex-basis
sealed interface FlexBasis {
    companion object {
        val Fill get() = StringFlexBasis("fill")
        val MaxContent get() = StringFlexBasis("max-content")
        val MinContent get() = StringFlexBasis("min-content")
        val FitContent get() = StringFlexBasis("fit-content")

        val Content get() = StringFlexBasis("content")

        val Auto get() = StringFlexBasis("auto")
        val Inherit get() = StringFlexBasis("inherit")
        val Initial get() = StringFlexBasis("initial")
        val Revert get() = StringFlexBasis("revert")
        val Unset get() = StringFlexBasis("unset")
    }
}

class StringFlexBasis(val value: String) : FlexBasis
class NumericFlexBasis(val value: CSSNumeric) : FlexBasis

fun StyleBuilder.flexBasis(flexBasis: FlexBasis) {
    when (flexBasis) {
        is StringFlexBasis -> property("flex-basis", flexBasis.value)
        is NumericFlexBasis -> property("flex-basis", flexBasis.value)
    }
}