package com.varabyte.kobweb.silk.style.layer

class LayerListBuilder {
    // Map of "after" layer to custom layers
    // The null key (no "after" layer) means that the custom layers should just be appended to the end of the layer list
    private val customLayers = mutableMapOf<String?, MutableList<String>>()

    private val silkLayers = SilkLayer.entries.map { it.layerName }

    /**
     * Register one or more custom layers.
     *
     * The order that layers are added will be in increased precedence. By default, the most recent layers added will
     * take precedence over any layers added earlier. However, the user can specify an optional [after] parameter which,
     * if present, can be used to specify some layers at earlier (and therefore lower) precedence positions.
     *
     * For example:
     * ```
     * cssLayers.add("utilities", "overrides")
     * cssLayers.add("thirdparty", after = SilkLayer.BASE)
     * ```
     *
     * will result in this final layer order: `reset`, `base`, `thirdparty`, `component-styles`, `component-variants`,
     * `restricted-styles`, `general-styles`, `utilities`, and finally `overrides`
     *
     * @see SilkLayer
     */
    fun add(vararg layerNames: String, after: String? = null) {
        if (layerNames.isEmpty()) return

        val existingLayerNames = (silkLayers + customLayers.values.flatten()).toSet()
        layerNames.forEach { layerName ->
            require(!existingLayerNames.contains(layerName)) {
                "Cannot register layer name \"$layerName\" as a layer with that name was already registered."
            }
        }
        if (after != null) {
            require(existingLayerNames.contains(after)) {
                "Cannot register layer names (${layerNames.joinToString { "\"$it\"" }}) as the requested layer they should follow (\"$after\") does not exist."
            }
        }
        customLayers.getOrPut(after) { mutableListOf() }.addAll(layerNames.toList())
    }

    internal fun build(): List<String> {
        val layersToProcess = (silkLayers + customLayers[null].orEmpty()).toMutableList()
        return buildList {
            while (layersToProcess.isNotEmpty()) {
                val currLayer = layersToProcess.removeAt(0)
                add(currLayer)
                customLayers[currLayer]?.let { layersToProcess.addAll(0, it) }
            }
        }
    }
}

fun LayerListBuilder.add(vararg layerNames: String, after: SilkLayer) {
    add(*layerNames, after = after.layerName)
}
