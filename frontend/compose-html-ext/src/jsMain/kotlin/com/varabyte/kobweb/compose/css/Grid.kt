package com.varabyte.kobweb.compose.css

import org.jetbrains.compose.web.attributes.AutoComplete.Companion.name
import org.jetbrains.compose.web.css.*

@DslMarker
annotation class GridDslMarker

typealias CSSFlexValue = CSSSizeValue<out CSSUnitFlex>

/**
 * The base class for all values which can be used to configure a CSS grid's rows or columns.
 *
 * This allows a user to convert a CSS value like "1fr `[name]` repeat(3, 100px) 1fr" into a list of [GridEntry]s.
 */
sealed class GridEntry(private val value: String) {
    override fun toString() = value

    /**
     * Represents all possible size values that can be used to configure a CSS grid track.
     *
     * A track is the space between two grid lines -- it can be used for rows or columns based on context.
     */
    sealed class TrackSize(value: String) : GridEntry(value) {
        /** A size which tells the track to be as small as possible while still fitting all of its contents. */
        class FitContent internal constructor(value: CSSLengthOrPercentageValue) : TrackSize("fit-content($value)")

        /** A size which represents a range of values this track can be. */
        class MinMax internal constructor(internal val min: Inflexible, internal val max: TrackSize) :
            TrackSize("minmax($min, $max)")

        /** Represents a track size which is a flex value (e.g. `1fr`) */
        class Flex internal constructor(value: String) : TrackSize(value)

        /** Like [TrackSize] but excludes flex values (e.g. `1fr`). */
        sealed class Inflexible(value: String) : TrackSize(value)

        /** Represents a track size defined by a keyword (e.g. `auto`). */
        class Keyword internal constructor(value: String) : Inflexible(value)

        /** Represents a track size which is fixed, either a length or percentage value (e.g. `100px`, `40%`). */
        class Fixed internal constructor(value: String) : Inflexible(value)
    }

    /** Represents a repeated set of track sizes and line names for a CSS grid. */
    sealed class Repeat(value: Any, internal val entries: Array<out GridEntry>) :
        GridEntry("repeat($value, ${entries.toTrackListString()})") {

        /** A fixed count of repeated track sizes and line names. */
        class Track internal constructor(count: Int, vararg entries: GridEntry) : Repeat(count, entries)

        /**
         * An automatically-determined repetition of track sizes and line names.
         *
         * Note that this supports limited types of sizing values.
         */
        class Auto internal constructor(type: Type, vararg entries: GridEntry) : Repeat(type, entries) {
            enum class Type(private val value: String) {
                AutoFill("auto-fill"),
                AutoFit("auto-fit");

                override fun toString() = value
            }
        }
    }

    /** Represents a set of line names for a CSS grid line. */
    class LineNames internal constructor(internal vararg val names: String) :
        GridEntry(names.joinToString(" ", prefix = "[", postfix = "]"))

    companion object {
        val Auto get() = TrackSize.Keyword("auto")
        val MinContent get() = TrackSize.Keyword("min-content")
        val MaxContent get() = TrackSize.Keyword("max-content")

        fun size(value: CSSLengthOrPercentageValue) = TrackSize.Fixed(value.toString())
        fun size(value: CSSFlexValue) = TrackSize.Flex(value.toString())

        fun minmax(min: TrackSize.Inflexible, max: TrackSize) = TrackSize.MinMax(min, max)

        fun fitContent(value: CSSLengthOrPercentageValue) = TrackSize.FitContent(value)

        fun repeat(count: Int, vararg entries: GridEntry): Repeat = Repeat.Track(count, *entries)
        fun repeat(type: Repeat.Auto.Type, vararg entries: GridEntry): Repeat = Repeat.Auto(type, *entries)

        fun lineNames(vararg names: String) = LineNames(*names)
    }
}

private fun Array<out GridEntry>.toTrackListString(): String {
    val entries = this@toTrackListString.also { it.validate() }
    return entries.joinToString(" ")
}

private fun Array<out GridEntry>.validate() {
    val trackSizes = flatMap {
        when (it) {
            is GridEntry.LineNames -> emptyList()
            is GridEntry.TrackSize -> listOf(it)
            is GridEntry.Repeat -> it.entries.filterIsInstance<GridEntry.TrackSize>()
                .ifEmpty { error("repeat() must contain at least one track size") }
        }
    }

    check(trackSizes.isNotEmpty()) { "You must specify at least one track size" }

    val autoRepeatCount = this.count { it is GridEntry.Repeat.Auto }
    if (autoRepeatCount == 0) return

    check(autoRepeatCount == 1) { "Only one auto-repeat call is allowed per track list" }

    trackSizes.forEach {
        when (it) {
            is GridEntry.TrackSize.Fixed -> {} // OK
            is GridEntry.TrackSize.Flex -> error("Cannot use flex values with auto-repeat")
            is GridEntry.TrackSize.Keyword -> error("Cannot use keywords with auto-repeat")
            is GridEntry.TrackSize.FitContent -> error("Cannot use fit-content with auto-repeat")
            is GridEntry.TrackSize.MinMax -> {
                check(it.min is GridEntry.TrackSize.Fixed || it.max is GridEntry.TrackSize.Fixed) {
                    "Cannot use minmax with auto-repeat unless at least one of the values is a fixed value (a length or percentage)"
                }
            }
        }
    }
}

private fun List<GridEntry>.toTrackListString() = toTypedArray().toTrackListString()

@GridDslMarker
abstract class GridTrackBuilderInRepeat {
    val auto get() = GridEntry.Auto
    val minContent get() = GridEntry.MinContent
    val maxContent get() = GridEntry.MaxContent
    val autoFit get() = GridEntry.Repeat.Auto.Type.AutoFit
    val autoFill get() = GridEntry.Repeat.Auto.Type.AutoFill

    internal val tracks = mutableListOf<GridEntry>()

    fun size(track: GridEntry) {
        tracks.add(track)
    }

    fun size(value: CSSLengthOrPercentageValue) = size(GridEntry.size(value))

    fun size(value: CSSFlexValue) = size(GridEntry.size(value))

    fun fitContent(value: CSSLengthOrPercentageValue) = size(GridEntry.fitContent(value))

    fun minmax(min: GridEntry.TrackSize.Inflexible, max: GridEntry.TrackSize) =
        size(GridEntry.minmax(min, max))

    fun minmax(min: GridEntry.TrackSize.Fixed, max: GridEntry.TrackSize) =
        size(GridEntry.minmax(min, max))

    fun minmax(min: GridEntry.TrackSize.Inflexible, max: CSSFlexValue) = minmax(min, GridEntry.size(max))

    fun minmax(min: GridEntry.TrackSize.Inflexible, max: CSSLengthOrPercentageValue) = minmax(min, GridEntry.size(max))

    fun minmax(min: CSSLengthOrPercentageValue, max: GridEntry.TrackSize) = minmax(GridEntry.size(min), max)

    fun minmax(min: CSSLengthOrPercentageValue, max: CSSLengthOrPercentageValue) =
        minmax(GridEntry.size(min), GridEntry.size(max))

    fun minmax(min: CSSLengthOrPercentageValue, max: CSSFlexValue) = minmax(GridEntry.size(min), GridEntry.size(max))

    fun lineNames(vararg names: String) {
        // combine with previous line names if there is no track specified between them
        val prev = tracks.lastOrNull()
        if (prev is GridEntry.LineNames) {
            tracks[tracks.lastIndex] = GridEntry.lineNames(*(prev.names + names))
            return
        }
        tracks.add(GridEntry.lineNames(*names))
    }
}

/**
 * A builder for simplifying the creation of grid track lists.
 */
class GridTrackBuilder : GridTrackBuilderInRepeat() {
    fun repeat(count: Int, block: GridTrackBuilderInRepeat.() -> Unit) {
        val repeatTracks = GridTrackBuilder().apply(block).tracks.toTypedArray()
        size(GridEntry.repeat(count, *repeatTracks))
    }

    fun repeat(type: GridEntry.Repeat.Auto.Type, block: GridTrackBuilderInRepeat.() -> Unit) {
        val repeatTracks = GridTrackBuilder().apply(block).tracks.toTypedArray()
        size(GridEntry.repeat(type, *repeatTracks))
    }
}

sealed class GridAuto private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    class Keyword internal constructor(value: String) : GridAuto(value)

    companion object {
        // Keywords
        val None get() = Keyword("none")

        // Global values
        val Inherit get() = Keyword("inherit")
        val Initial get() = Keyword("initial")
        val Revert get() = Keyword("revert")
        val Unset get() = Keyword("unset")
    }
}

fun StyleScope.gridAutoColumns(gridAutoColumns: GridAuto.Keyword) {
    gridAutoColumns(gridAutoColumns.toString())
}

fun StyleScope.gridAutoColumns(vararg gridAutoColumns: GridEntry) {
    gridAutoColumns(gridAutoColumns.toTrackListString())
}

fun StyleScope.gridAutoColumns(block: GridTrackBuilder.() -> Unit) {
    gridAutoColumns(GridTrackBuilder().apply(block).tracks.toTrackListString())
}

fun StyleScope.gridAutoRows(gridAutoRows: GridAuto.Keyword) {
    gridAutoRows(gridAutoRows.toString())
}

fun StyleScope.gridAutoRows(vararg gridAutoRows: GridEntry) {
    gridAutoRows(gridAutoRows.toTrackListString())
}

fun StyleScope.gridAutoRows(block: GridTrackBuilder.() -> Unit) {
    gridAutoRows(GridTrackBuilder().apply(block).tracks.toTrackListString())
}

/**
 * Represents all possible values that can be passed into a CSS grid property.
 *
 * Note: "subgrid" and "masonry" purposely excluded as they are not widely supported
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_grid_layout/Subgrid
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_grid_layout/Masonry_layout
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/CSS/grid-template
 */
sealed class GridTemplate private constructor(private val value: String) : StylePropertyValue {
    override fun toString() = value

    class Keyword internal constructor(value: String) : GridTemplate(value)
    companion object {
        // Keywords
        val None get() = Keyword("none")

        // Global
        val Initial get() = Keyword("initial")
        val Inherit get() = Keyword("inherit")
        val Revert get() = Keyword("revert")
        val Unset get() = Keyword("unset")
    }
}

fun StyleScope.gridTemplateColumns(gridTemplateColumns: GridTemplate.Keyword) {
    gridTemplateColumns(gridTemplateColumns.toString())
}

fun StyleScope.gridTemplateColumns(vararg gridTemplateColumns: GridEntry) {
    gridTemplateColumns(gridTemplateColumns.toTrackListString())
}

fun StyleScope.gridTemplateColumns(block: GridTrackBuilder.() -> Unit) {
    gridTemplateColumns(GridTrackBuilder().apply(block).tracks.toTrackListString())
}

fun StyleScope.gridTemplateRows(gridTemplateRows: GridTemplate.Keyword) {
    gridTemplateRows(gridTemplateRows.toString())
}

fun StyleScope.gridTemplateRows(vararg gridTemplateRows: GridEntry) {
    gridTemplateRows(gridTemplateRows.toTrackListString())
}

fun StyleScope.gridTemplateRows(block: GridTrackBuilder.() -> Unit) {
    gridTemplateRows(GridTrackBuilder().apply(block).tracks.toTrackListString())
}

@GridDslMarker
abstract class GridBuilderInAuto {
    protected var cols: List<GridEntry>? = null
    protected var rows: List<GridEntry>? = null
    protected var autoBuilder: GridBuilder? = null

    fun col(value: CSSLengthOrPercentageValue) {
        cols = GridTrackBuilder().apply { size(value) }.tracks
    }

    fun col(value: CSSFlexValue) {
        cols = GridTrackBuilder().apply { size(value) }.tracks
    }

    fun row(value: CSSLengthOrPercentageValue) {
        rows = GridTrackBuilder().apply { size(value) }.tracks
    }

    fun row(value: CSSFlexValue) {
        rows = GridTrackBuilder().apply { size(value) }.tracks
    }

    fun cols(block: GridTrackBuilder.() -> Unit) {
        cols = GridTrackBuilder().apply(block).tracks
    }

    fun rows(block: GridTrackBuilder.() -> Unit) {
        rows = GridTrackBuilder().apply(block).tracks
    }

    internal fun buildInto(scope: StyleScope) {
        scope.display(DisplayStyle.Grid)
        cols?.let { scope.gridTemplateColumns(it.toTrackListString()) }
        rows?.let { scope.gridTemplateRows(it.toTrackListString()) }
        autoBuilder?.let { autoBuilder ->
            autoBuilder.cols?.let { scope.gridAutoColumns(it.toTrackListString()) }
            autoBuilder.rows?.let { scope.gridAutoRows(it.toTrackListString()) }
        }
    }
}

/**
 * A class which allows for a more concise way of declaring a grid.
 *
 * For example:
 *
 * ```
 * // Without the builder
 * Modifier.
 *  gridTemplateColumns(
 *     GridEntry.size(40.px),
 *     GridEntry.size(1.fr),
 *     GridEntry.repeat(3, GridEntry.size(200.px))
 *  )
 *  gridTemplateRows(
 *    GridEntry.size(1.fr),
 *    GridEntry.size(1.fr),
 *  )
 *  gridAutoColumns(GridEntry.size(50.px))
 *
 * // With the builder
 * Modifier.grid {
 *   cols { size(40.px); size(1.fr); repeat(3) { size(200.px) } }
 *   rows { size(1.fr); size(1.fr) }
 *   auto { col(50.px) }
 * }
 * ```
 */
class GridBuilder : GridBuilderInAuto() {
    fun auto(block: GridBuilderInAuto.() -> Unit) {
        autoBuilder = GridBuilder().apply(block)
    }
}

fun StyleScope.grid(block: GridBuilder.() -> Unit) {
    GridBuilder().apply(block).buildInto(this)
}
