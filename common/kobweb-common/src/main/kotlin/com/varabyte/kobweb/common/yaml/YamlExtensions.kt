package com.varabyte.kobweb.common.yaml

import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration

private val NonStrictYamlInstance by lazy {
    Yaml(
        configuration = YamlConfiguration(
            // Disable strict mode, because otherwise we'll have a bad time with backwards compatibility (that is, if a
            // user tries to pull down a new Kobweb project with an older version the Kobweb binary, we will fail to
            // parse it EVEN IF the field we added didn't really matter that much.
            strictMode = false,
            // Use a property-based discriminator (e.g., "type: TOKEN_BUCKET") for sealed class polymorphism,
            // instead of YAML tags (e.g., "!TOKEN_BUCKET"), which is far more intuitive for user-facing config.
            polymorphismStyle = PolymorphismStyle.Property,
        )
    )
}

/** A default Yaml configuration used by Kobweb */
val Yaml.Companion.nonStrictDefault get() = NonStrictYamlInstance