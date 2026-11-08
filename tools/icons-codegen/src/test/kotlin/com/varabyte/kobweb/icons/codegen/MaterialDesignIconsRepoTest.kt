package com.varabyte.kobweb.icons.codegen

import com.varabyte.truthish.assertThat
import com.varabyte.truthish.assertThrows
import org.junit.Test

class MaterialDesignIconsRepoTest {
    @Test
    fun exactCommitResolvesToItself() {
        val commit = "49d4db35df873165d6bd6ba09b063c7dafbac2f4"
        assertThat(MaterialDesignIconsRepo.resolveCommit(VersionSpec(commit))).isEqualTo(commit)
    }

    @Test
    fun prefixSpecIsRejected() {
        val e = assertThrows<IllegalArgumentException> {
            MaterialDesignIconsRepo.resolveCommit(VersionSpec("1.+"))
        }
        assertThat(e.message!!).contains("Use a commit SHA or \"latest\" instead.")
    }
}
