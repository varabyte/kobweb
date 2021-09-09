package com.varabyte.kobweb.cli.common

import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.exists

private val TEMPLATE_FILE = "kobweb.template.yaml"

object KobwebUtils {
    fun getTemplateFileIn(path: Path) = Paths.get(path.toString(), TEMPLATE_FILE).takeIf { it.exists() }
}