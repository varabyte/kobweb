package com.varabyte.kobweb.icons.codegen.fetch

import com.varabyte.truthish.assertThat
import org.junit.Test

class GitHubRepoTest {
    @Test
    fun fileUriEncodesPath() {
        val uri = GitHubRepo("google/material-design-icons")
            .fileUri("abc123", "variablefont/MaterialSymbolsOutlined[FILL,GRAD,opsz,wght].codepoints")

        assertThat(uri.toString()).isEqualTo(
            "https://raw.githubusercontent.com/google/material-design-icons/abc123/variablefont/MaterialSymbolsOutlined%5BFILL,GRAD,opsz,wght%5D.codepoints"
        )
    }
}
