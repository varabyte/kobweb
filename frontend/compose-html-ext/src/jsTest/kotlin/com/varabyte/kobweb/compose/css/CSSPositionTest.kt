package com.varabyte.kobweb.compose.css

import com.varabyte.truthish.assertThat
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import kotlin.test.Test

class CSSPositionTest {
    @Test
    fun simplePositionsWork() {
        assertThat(CSSPosition(0.px, 0.px).toString()).isEqualTo("0px 0px")
        assertThat(CSSPosition(10.percent, 90.percent).toString()).isEqualTo("10% 90%")
        assertThat(CSSPosition(50.percent, 90.percent).toString()).isEqualTo("50% 90%")
        assertThat(CSSPosition(Edge.CenterX, 90.percent).toString()).isEqualTo("center 90%")
        assertThat(CSSPosition(10.percent, Edge.CenterY).toString()).isEqualTo("10% center")
    }

    @Test
    fun cardinalDirectionsBecomeExpectedTextValues() {
        assertThat(CSSPosition.Top.toString()).isEqualTo("top")
        assertThat(CSSPosition.TopRight.toString()).isEqualTo("right top")
        assertThat(CSSPosition.Right.toString()).isEqualTo("right")
        assertThat(CSSPosition.BottomRight.toString()).isEqualTo("right bottom")
        assertThat(CSSPosition.Bottom.toString()).isEqualTo("bottom")
        assertThat(CSSPosition.BottomLeft.toString()).isEqualTo("left bottom")
        assertThat(CSSPosition.Left.toString()).isEqualTo("left")
        assertThat(CSSPosition.TopLeft.toString()).isEqualTo("left top")
        assertThat(CSSPosition.Center.toString()).isEqualTo("center")
    }

    @Test
    fun edgesBecomeExpectedTextValues() {
        assertThat(CSSPosition(Edge.Left(10.px)).toString()).isEqualTo("left 10px")
        assertThat(CSSPosition(Edge.Bottom(20.em)).toString()).isEqualTo("bottom 20em")
        assertThat(CSSPosition(Edge.Left(0.px)).toString()).isEqualTo("left")
        assertThat(CSSPosition(Edge.Bottom(0.percent)).toString()).isEqualTo("bottom")
        assertThat(CSSPosition(Edge.Left).toString()).isEqualTo("left")
        assertThat(CSSPosition(Edge.Bottom).toString()).isEqualTo("bottom")
        assertThat(CSSPosition(Edge.Left(25.percent), Edge.Bottom(75.percent)).toString()).isEqualTo("left 25% bottom 75%")
        assertThat(CSSPosition(Edge.Left, Edge.Bottom(75.percent)).toString()).isEqualTo("left bottom 75%")
        assertThat(CSSPosition(Edge.Left(25.percent), Edge.Bottom).toString()).isEqualTo("left 25% bottom")
        assertThat(CSSPosition(Edge.Left, Edge.Bottom).toString()).isEqualTo("left bottom")
    }

    private val dummyPercentValue by StyleVariable<CSSPercentageNumericValue>()

    // We do an internal "as" check against the passed-in numeric value. We want to make sure that this logic doesn't
    // fail when we counter a variable!
    @Test
    fun variablesWork() {
        assertThat(CSSPosition(Edge.Left(dummyPercentValue.value())).toString()).isEqualTo("left var(--css-position-test-dummy-percent-value)")
    }
}
