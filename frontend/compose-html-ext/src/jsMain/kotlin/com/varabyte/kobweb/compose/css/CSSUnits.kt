package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.css.*
import kotlin.math.PI

// The type aliases below are a copy of Compose HTML's type aliases org.jetbrains.compose.web.css.CSSUnits.kt
// with the key difference that they are based on `CSSNumericValue` instead of `CSSSizeValue`.
// This means that in addition to including raw values, they also cover the result of calculations, css functions like
// `min()`, and StyleVariables. These are intended to almost entirely replace the Compose HTML versions, as in general,
// when a something like a "length" value is needed in CSS, it does not necessarily need to be a raw value.
// Eventually, we would like the values of Compose HTML's type aliases to be based on `CSSNumericValue` as well, at which
// time these would no longer be necessary.

/**
 * A CSS numeric value representing an angle.
 *
 * This should be preferred over [CSSAngleValue] when used as a value passed to a CSS property,
 * as it covers a broader range of valid values.
 */
typealias CSSAngleNumericValue = CSSNumericValue<out CSSUnitAngle>

/**
 * A CSS numeric value representing a length or percentage.
 *
 * This should be preferred over [CSSLengthOrPercentageValue] when used as a value passed to a CSS property,
 * as it covers a broader range of valid values.
 */
typealias CSSLengthOrPercentageNumericValue = CSSNumericValue<out CSSUnitLengthOrPercentage>

/**
 * A CSS numeric value representing a length.
 *
 * This should be preferred over [CSSLengthValue] when used as a value passed to a CSS property,
 * as it covers a broader range of valid values.
 */
typealias CSSLengthNumericValue = CSSNumericValue<out CSSUnitLength>

/**
 * A CSS numeric value representing a percentage.
 *
 * This should be preferred over [CSSPercentageValue] when used as a value passed to a CSS property,
 * as it covers a broader range of valid values.
 */
typealias CSSPercentageNumericValue = CSSNumericValue<out CSSUnitPercentage>

// these do not have corresponding non-numeric type aliases defined in Compose HTML, but are named for consistency

/** A CSS numeric value representing a flex value. */
typealias CSSFlexNumericValue = CSSNumericValue<out CSSUnitFlex>

/** A CSS numeric value representing a time value. */
typealias CSSTimeNumericValue = CSSNumericValue<out CSSUnitTime>

fun CSSAngleValue.toDegrees() = when (this.unit.toString()) {
    "deg" -> value
    "grad" -> (value * 0.9f)
    "rad" -> (value * 180f / PI.toFloat())
    "turn" -> (value * 360f)
    else -> error("Unexpected unit type ${this.unit}")
} % 360f

// We can't edit org.jetbrains.compose.web.css.CSSUnit, but maybe some day this can be upstreamed?
interface CSSUnitExt : CSSUnit {
    interface svb : CSSUnitRel
    interface svh : CSSUnitRel
    interface svi : CSSUnitRel
    interface svmax : CSSUnitRel
    interface svmin : CSSUnitRel
    interface svw : CSSUnitRel
    interface lvb : CSSUnitRel
    interface lvh : CSSUnitRel
    interface lvi : CSSUnitRel
    interface lvmax : CSSUnitRel
    interface lvmin : CSSUnitRel
    interface lvw : CSSUnitRel
    interface dvb : CSSUnitRel
    interface dvh : CSSUnitRel
    interface dvi : CSSUnitRel
    interface dvmax : CSSUnitRel
    interface dvmin : CSSUnitRel
    interface dvw : CSSUnitRel

    companion object {
        inline val svb get() = "svb".unsafeCast<svb>()
        inline val svh get() = "svh".unsafeCast<svh>()
        inline val svi get() = "svi".unsafeCast<svi>()
        inline val svmax get() = "svmax".unsafeCast<svmax>()
        inline val svmin get() = "svmin".unsafeCast<svmin>()
        inline val svw get() = "svw".unsafeCast<svw>()
        inline val lvb get() = "lvb".unsafeCast<lvb>()
        inline val lvh get() = "lvh".unsafeCast<lvh>()
        inline val lvi get() = "lvi".unsafeCast<lvi>()
        inline val lvmax get() = "lvmax".unsafeCast<lvmax>()
        inline val lvmin get() = "lvmin".unsafeCast<lvmin>()
        inline val lvw get() = "lvw".unsafeCast<lvw>()
        inline val dvb get() = "dvb".unsafeCast<dvb>()
        inline val dvh get() = "dvh".unsafeCast<dvh>()
        inline val dvi get() = "dvi".unsafeCast<dvi>()
        inline val dvmax get() = "dvmax".unsafeCast<dvmax>()
        inline val dvmin get() = "dvmin".unsafeCast<dvmin>()
        inline val dvw get() = "dvw".unsafeCast<dvw>()
    }
}

/**
 * Represents 1% of the viewport's block size.
 *
 * This is the same as [vw] when the document is using a horizontal writing mode or [vh] if vertical.
 *
 * This value may be affected by on-screen UI elements, but it generally lags behind them being shown or hidden. Use
 * [dvb] if you want something that reacts to UI elements in real time.
 *
 * @see svb
 * @see lvb
 * @see dvb
 */
val Number.vb get(): CSSSizeValue<CSSUnit.vb> = CSSUnitValueTyped(this.toFloat(), CSSUnit.vb)

/**
 * Represents 1% of the viewport's inline size.
 *
 * This is the same as [vh] when the document is using a horizontal writing mode or [vw] if vertical.
 *
 * This value may be affected by on-screen UI elements, but it generally lags behind them being shown or hidden. Use
 * [dvi] if you want something that reacts to UI elements in real time.
 *
 * @see svi
 * @see lvi
 * @see dvi
 */
val Number.vi get(): CSSSizeValue<CSSUnit.vi> = CSSUnitValueTyped(this.toFloat(), CSSUnit.vi)

/**
 * Represents 1% of the small viewport's block size.
 *
 * A small viewport means this is the smallest size of the viewport, ensuring an area that will always be visible even
 * if all dynamic UI elements are open.
 *
 * @see vb
 * @see lvb
 * @see dvb
 */
val Number.svb get(): CSSSizeValue<CSSUnitExt.svb> = CSSUnitValueTyped(this.toFloat(), CSSUnitExt.svb)

/**
 * Represents 1% of the viewport's block size.
 *
 * This value reacts to on-screen UI elements in real time, ensuring that the area will always be visible even if all
 * dynamic UI elements are open.
 *
 * @see vw
 * @see lvw
 * @see dvw
 */
val Number.svw get(): CSSSizeValue<CSSUnitExt.svw> = CSSUnitValueTyped(this.toFloat(), CSSUnitExt.svw)

/**
 * Represents 1% of the small viewport's height.
 *
 * A small viewport means this is the smallest size of the viewport, ensuring an area that will always be visible even
 * if all dynamic UI elements are open.
 *
 * @see vh
 * @see lvh
 * @see dvh
 */
val Number.svh get(): CSSSizeValue<CSSUnitExt.svh> = CSSUnitValueTyped(this.toFloat(), CSSUnitExt.svh)

/**
 * Represents 1% of the small viewport's inline size.
 *
 * A small viewport means this is the smallest size of the viewport, ensuring an area that will always be visible even
 * if all dynamic UI elements are open.
 *
 * @see vi
 * @see lvi
 * @see dvi
 */
val Number.svi get(): CSSSizeValue<CSSUnitExt.svi> = CSSUnitValueTyped(this.toFloat(), CSSUnitExt.svi)

/**
 * Represents 1% of the small viewport's smallest dimension.
 *
 * A small viewport means this is the smallest size of the viewport, ensuring an area that will always be visible even
 * if all dynamic UI elements are open.
 *
 * In other words, this will use the viewport's height if in landscape mode, or the width if in portrait mode.
 *
 * @see svw
 * @see svh
 */
val Number.svmin get(): CSSSizeValue<CSSUnitExt.svmin> = CSSUnitValueTyped(this.toFloat(), CSSUnitExt.svmin)

/**
 * Represents 1% of the small viewport's largest dimension.
 *
 * A small viewport means this is the smallest size of the viewport, ensuring an area that will always be visible even
 * if all dynamic UI elements are open.
 *
 * In other words, this will use the viewport's width if in landscape mode, or the height if in portrait mode.
 *
 * @see svw
 * @see svh
 */
val Number.svmax get(): CSSSizeValue<CSSUnitExt.svmax> = CSSUnitValueTyped(this.toFloat(), CSSUnitExt.svmax)

/**
 * Represents 1% of the large viewport's block size.
 *
 * A large viewport means this is the largest size of the viewport, ensuring an area that will always be visible only
 * if all dynamic UI elements are closed.
 *
 * @see vb
 * @see svb
 * @see dvb
 */
val Number.lvb get(): CSSSizeValue<CSSUnitExt.lvb> = CSSUnitValueTyped(this.toFloat(), CSSUnitExt.lvb)

/**
 * Represents 1% of the large viewport's width.
 *
 * A large viewport means this is the largest size of the viewport, ensuring an area that will always be visible only
 * if all dynamic UI elements are closed.
 *
 * @see vw
 * @see svw
 * @see dvw
 */
val Number.lvw get(): CSSSizeValue<CSSUnitExt.lvw> = CSSUnitValueTyped(this.toFloat(), CSSUnitExt.lvw)

/**
 * Represents 1% of the large viewport's height.
 *
 * A large viewport means this is the largest size of the viewport, ensuring an area that will always be visible only
 * if all dynamic UI elements are closed.
 *
 * @see vh
 * @see svh
 * @see dvh
 */
val Number.lvi get(): CSSSizeValue<CSSUnitExt.lvi> = CSSUnitValueTyped(this.toFloat(), CSSUnitExt.lvi)

/**
 * Represents 1% of the large viewport's inline size.
 *
 * A large viewport means this is the largest size of the viewport, ensuring an area that will always be visible only
 * if all dynamic UI elements are closed.
 *
 * @see vh
 * @see svh
 * @see dvh
 */
val Number.lvh get(): CSSSizeValue<CSSUnitExt.lvh> = CSSUnitValueTyped(this.toFloat(), CSSUnitExt.lvh)

/**
 * Represents 1% of the large viewport's smallest dimension.
 *
 * A large viewport means this is the largest size of the viewport, ensuring an area that will always be visible only
 * if all dynamic UI elements are closed.
 *
 * In other words, this will use the viewport's height if in landscape mode, or the width if in portrait mode.
 *
 * @see lvw
 * @see lvh
 */
val Number.lvmin get(): CSSSizeValue<CSSUnitExt.lvmin> = CSSUnitValueTyped(this.toFloat(), CSSUnitExt.lvmin)

/**
 * Represents 1% of the large viewport's largest dimension.
 *
 * A large viewport means this is the largest size of the viewport, ensuring an area that will always be visible only
 * if all dynamic UI elements are closed.
 *
 * In other words, this will use the viewport's width if in landscape mode, or the height if in portrait mode.
 *
 * @see lvw
 * @see lvh
 */
val Number.lvmax get(): CSSSizeValue<CSSUnitExt.lvmax> = CSSUnitValueTyped(this.toFloat(), CSSUnitExt.lvmax)

/**
 * Represents 1% of the dynamic viewport's block size.
 *
 * A dynamic viewport means this is the current size of the viewport visible around any open UI elements. This value
 * will update in real time as elements are shown or hidden.
 *
 * @see vb
 * @see svb
 * @see lvb
 */
val Number.dvb get(): CSSSizeValue<CSSUnitExt.dvb> = CSSUnitValueTyped(this.toFloat(), CSSUnitExt.dvb)

/**
 * Represents 1% of the dynamic viewport's width.
 *
 * A dynamic viewport means this is the current size of the viewport visible around any open UI elements. This value
 * will update in real time as elements are shown or hidden.
 *
 * @see vw
 * @see svw
 * @see lvw
 */
val Number.dvw get(): CSSSizeValue<CSSUnitExt.dvw> = CSSUnitValueTyped(this.toFloat(), CSSUnitExt.dvw)

/**
 * Represents 1% of the dynamic viewport's height.
 *
 * A dynamic viewport means this is the current size of the viewport visible around any open UI elements. This value
 * will update in real time as elements are shown or hidden.
 *
 * @see vh
 * @see svh
 * @see lvh
 */
val Number.dvh get(): CSSSizeValue<CSSUnitExt.dvh> = CSSUnitValueTyped(this.toFloat(), CSSUnitExt.dvh)

/**
 * Represents 1% of the dynamic viewport's inline size.
 *
 * A dynamic viewport means this is the current size of the viewport visible around any open UI elements. This value
 * will update in real time as elements are shown or hidden.
 *
 * @see vh
 * @see svh
 * @see lvh
 */
val Number.dvi get(): CSSSizeValue<CSSUnitExt.dvi> = CSSUnitValueTyped(this.toFloat(), CSSUnitExt.dvi)

/**
 * Represents 1% of the dynamic viewport's smallest dimension.
 *
 * A dynamic viewport means this is the current size of the viewport visible around any open UI elements. This value
 * will update in real time as elements are shown or hidden.
 *
 * In other words, this will use the viewport's height if in landscape mode, or the width if in portrait mode.
 *
 * @see dvw
 * @see dvh
 */
val Number.dvmin get(): CSSSizeValue<CSSUnitExt.dvmin> = CSSUnitValueTyped(this.toFloat(), CSSUnitExt.dvmin)

/**
 * Represents 1% of the dynamic viewport's largest dimension.
 *
 * A dynamic viewport means this is the current size of the viewport visible around any open UI elements. This value
 * will update in real time as elements are shown or hidden.
 *
 * In other words, this will use the viewport's width if in landscape mode, or the height if in portrait mode.
 *
 * @see dvw
 * @see dvh
 */
val Number.dvmax get(): CSSSizeValue<CSSUnitExt.dvmax> = CSSUnitValueTyped(this.toFloat(), CSSUnitExt.dvmax)
