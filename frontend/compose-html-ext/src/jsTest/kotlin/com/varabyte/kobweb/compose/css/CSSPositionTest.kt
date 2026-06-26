package com.varabyte.kobweb.compose.css

import com.varabyte.truthish.assertThat
import com.varabyte.truthish.assertWithMessage
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.vh
import kotlin.test.Test

class CSSPositionTest {
    private class CSSPositionAsserter(private val pos: CSSPosition) {
        fun isEqualTo(expected: String) {
            val posStr = pos.toString()
            assertThat(posStr).isEqualTo(expected)

            // In addition to verifying raw text, make sure the output is valid and accepted
            assertWithMessage("CSSPosition [$posStr] was rejected by the HTML engine (using object-position)")
                .that(CssTestUtils.isValidCss { property("object-position", posStr) }).isTrue()

            // And since radial gradient doesn't support 3-arg formats, and we want CSSPosition to work with that,
            // verify that works as well.
            assertWithMessage("CSSPosition [$posStr] was rejected by the HTML engine (using radial-gradient)")
                .that(CssTestUtils.isValidCss { property("background", "radial-gradient(circle at $posStr, blue, green)") }).isTrue()
        }
    }

    private fun assertThat(pos: CSSPosition) = CSSPositionAsserter(pos)

    @Test
    fun simplePositionsWork() {
        assertThat(CSSPosition(0.px, 0.px)).isEqualTo("0px 0px")
        assertThat(CSSPosition(10.percent, 90.percent)).isEqualTo("10% 90%")
        assertThat(CSSPosition(50.percent, 90.percent)).isEqualTo("50% 90%")
        assertThat(CSSPosition(Edge.CenterX, 90.percent)).isEqualTo("center 90%")
        assertThat(CSSPosition(10.percent, Edge.CenterY)).isEqualTo("10% center")
    }

    @Test
    fun cardinalDirectionsBecomeExpectedTextValues() {
        assertThat(CSSPosition.Top).isEqualTo("top")
        assertThat(CSSPosition.TopRight).isEqualTo("right top")
        assertThat(CSSPosition.Right).isEqualTo("right")
        assertThat(CSSPosition.BottomRight).isEqualTo("right bottom")
        assertThat(CSSPosition.Bottom).isEqualTo("bottom")
        assertThat(CSSPosition.BottomLeft).isEqualTo("left bottom")
        assertThat(CSSPosition.Left).isEqualTo("left")
        assertThat(CSSPosition.TopLeft).isEqualTo("left top")
        assertThat(CSSPosition.Center).isEqualTo("center")
    }

    @Test
    fun anchorsBecomeExpectedTextValues() {
        // Note: CSSPosition only supports 1-, 2-, or 4-arg formats, which is why some of these might seem more complex
        // than they should be (e.g. "left 10px top 50%" vs "left 10px center")

        assertThat(CSSPosition(Edge.Left)).isEqualTo("left")
        assertThat(CSSPosition(Edge.Bottom)).isEqualTo("bottom")

        // Drop zero offset when we can
        assertThat(CSSPosition(Edge.Left(0.px))).isEqualTo("left")
        assertThat(CSSPosition(Edge.Bottom(0.percent))).isEqualTo("bottom")
        assertThat(CSSPosition(Edge.Left(0.em), Edge.Bottom(0.vh))).isEqualTo("left bottom")

        assertThat(CSSPosition(Edge.Left(10.px))).isEqualTo("left 10px top 50%")
        assertThat(CSSPosition(Edge.Bottom(20.em))).isEqualTo("left 50% bottom 20em")
        assertThat(CSSPosition(Edge.Left, Edge.Bottom)).isEqualTo("left bottom")

        assertThat(CSSPosition(Edge.Left(25.percent), Edge.Bottom(75.percent))).isEqualTo("left 25% bottom 75%")
        assertThat(CSSPosition(Edge.Left, Edge.Bottom(75.percent))).isEqualTo("left 0% bottom 75%")
        assertThat(CSSPosition(Edge.Left(25.percent), Edge.Bottom)).isEqualTo("left 25% bottom 0%")

        // Do not drop 0 offset if it needs to be part of the final output
        assertThat(CSSPosition(Edge.Left(0.em), Edge.Bottom(75.percent))).isEqualTo("left 0em bottom 75%")
        assertThat(CSSPosition(Edge.Left(25.percent), Edge.Bottom(0.px))).isEqualTo("left 25% bottom 0px")

        assertThat(CSSPosition(Edge.CenterX)).isEqualTo("center")
        assertThat(CSSPosition(Edge.CenterY)).isEqualTo("center")
        assertThat(CSSPosition(Edge.Left, Edge.CenterY)).isEqualTo("left center")
        assertThat(CSSPosition(Edge.CenterX, Edge.Bottom)).isEqualTo("center bottom")
        assertThat(CSSPosition(Edge.CenterX, Edge.CenterY)).isEqualTo("center")
    }

    private val dummyPercentValue by StyleVariable<CSSPercentageNumericValue>()

    // We do an internal "as" check against the passed-in numeric value. We want to make sure that this logic doesn't
    // fail when we counter a variable!
    @Test
    fun variablesWork() {
        assertThat(CSSPosition(Edge.Left(dummyPercentValue.value()))).isEqualTo("left var(--css-position-test-dummy-percent-value) top 50%")
    }
}
