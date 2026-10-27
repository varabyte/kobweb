package com.varabyte.kobweb.project.conf

import com.charleskorn.kaml.Yaml
import com.varabyte.kobweb.common.yaml.nonStrictDefault
import com.varabyte.truthish.assertThat
import com.varabyte.truthish.assertThrows
import org.junit.Test

class KobwebConfTest {
    private fun decodeServer(extraYaml: String) = Yaml.nonStrictDefault.decodeFromString(
        Server.serializer(),
        """
        files:
          dev:
            contentRoot: "content"
            script: "dev.js"
          prod:
            script: "prod.js"
        """.trimIndent() + "\n" + extraYaml.trimIndent()
    )

    @Test
    fun portMustBeInRange() {
        assertThat(decodeServer("port: 1").port).isEqualTo(1)
        assertThat(decodeServer("port: 65535").port).isEqualTo(65535)

        assertThrows<IllegalArgumentException> { decodeServer("port: 0") }
        assertThrows<IllegalArgumentException> { decodeServer("port: 65536") }
        assertThrows<IllegalArgumentException> { decodeServer("port: -1") }
    }

    @Test
    fun maxFileCountMustNotBeNegative() {
        assertThat(decodeServer(
            """
            logging:
              maxFileCount: 0
            """.trimIndent()
        ).logging.maxFileCount).isEqualTo(0)
        assertThat(decodeServer(
            """
            logging:
              maxFileCount: null    
            """.trimIndent()
        ).logging.maxFileCount).isNull()

        assertThrows<IllegalArgumentException> {
            decodeServer(
                """
                logging:
                  maxFileCount: -1
                """.trimIndent()
            )
        }
    }
}
