package com.varabyte.kobweb.project.common

import com.varabyte.truthish.assertThat
import org.junit.Test

class FqNameTest {
    @Test
    fun createFromPackageAndName() {
        assertThat(FqName(PackageName("com.example"), "PageLayout").value).isEqualTo("com.example.PageLayout")
        assertThat(FqName(PackageName("com.example"), "Outer.Inner").value).isEqualTo("com.example.Outer.Inner")
        assertThat(FqName(PackageName(""), "PageLayout").value).isEqualTo("PageLayout")
    }
}
