package com.varabyte.kobweb.icons.codegen.generate

import com.varabyte.kobweb.icons.codegen.fa.FaCodegen
import com.varabyte.kobweb.icons.codegen.lucide.LucideCodegen
import com.varabyte.truthish.assertThat
import org.junit.Test
import kotlin.io.path.createTempDirectory

class MetadataTest {
    private val outDir = createTempDirectory("metadata-test").toFile().also { it.deleteOnExit() }
    private val metadataFile = outDir.resolve("META-INF/kobweb/iconset.json")

    @Test
    fun metadataIncludesVersionAndStylesheet() {
        FaCodegen().writeMetadata("6.5.2", outDir)

        assertThat(metadataFile.readText()).isEqualTo(
            """{"id":"fa","version":"6.5.2","stylesheet":"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css"}"""
        )
    }

    @Test
    fun metadataLeavesOutStylesheetWhenThereIsNone() {
        LucideCodegen().writeMetadata("1.69.0", outDir)

        assertThat(metadataFile.readText()).isEqualTo("""{"id":"lucide","version":"1.69.0"}""")
    }
}
