package com.varabyte.kobweb.cli.common

import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.exists

private val KOBWEB_TEMPLATE_FILENAME = "kobweb.template.yaml"

object KobwebUtils {
    fun getTemplateFileIn(path: Path) = Paths.get(path.toString(), KOBWEB_TEMPLATE_FILENAME).takeIf { it.exists() }
    fun isTemplateFileIn(path: Path) = getTemplateFileIn(path) != null
}