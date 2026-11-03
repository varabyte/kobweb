package com.varabyte.kobweb.compose.ui.graphics

import com.varabyte.truthish.assertThat
import org.jetbrains.compose.web.css.*
import kotlin.test.Test

class ColorTest {
    @Test
    fun hslNormalizesOutOfRangeValuesLikeBrowsers() {
        assertThat(Color.hsl(-90, 1f, 0.5f)).isEqualTo(Color.hsl(270, 1f, 0.5f))
        assertThat(Color.hsl(450, 1f, 0.5f)).isEqualTo(Color.hsl(90, 1f, 0.5f))
        assertThat(Color.hsla(-90, 2f, -1f, 3f).toString()).isEqualTo("hsl(270, 100%, 0%)")
    }

    @Test
    fun hslWithNegativeHueWrapsAround() {
        assertThat(Color.hsl(-90, 1f, 0.5f).toRgb()).isEqualTo(Color.hsl(270, 1f, 0.5f).toRgb())
        assertThat(Color.hsl((-90).deg, 100.percent, 50.percent).toRgb()).isEqualTo(Color.hsl(270, 1f, 0.5f).toRgb())
        assertThat(Color.hsl(-90, 1f, 0.5f).inverted()).isEqualTo(Color.hsl(270, 1f, 0.5f).inverted())
    }

    @Test
    fun hslWithTinyNegativeHueWrapsToZero() {
        // -1e-20 mod 360 rounds up to exactly 360
        assertThat(Color.hsl(-1e-20f, 1f, 0.5f).hue).isEqualTo(0f)
        assertThat(Color.hsl(-1e-20f, 1f, 0.5f).toRgb()).isEqualTo(Color.rgb(255, 0, 0))
    }

    @Test
    fun hslClampsSaturationAndLightnessLikeBrowsers() {
        assertThat(Color.hsl(0, 2f, 0.25f).toRgb()).isEqualTo(Color.rgb(127, 0, 0))
        assertThat(Color.hsl(0, -1f, 0.5f).toRgb()).isEqualTo(Color.hsl(0, 0f, 0.5f).toRgb())
        assertThat(Color.hsl(0, 1f, 1.5f).toRgb()).isEqualTo(Color.rgb(255, 255, 255))
        assertThat(Color.hsl(0, 1f, -0.5f).toRgb()).isEqualTo(Color.rgb(0, 0, 0))
    }

    @Test
    fun rgbClampsOutOfRangeIntChannelsLikeBrowsers() {
        assertThat(Color.rgb(300, 0, 0)).isEqualTo(Color.rgb(255, 0, 0))
        assertThat(Color.rgb(-20, 0, 0)).isEqualTo(Color.rgb(0, 0, 0))
        assertThat(Color.rgba(0, 0, 0, 300)).isEqualTo(Color.rgba(0, 0, 0, 255))
    }
}
