package com.varabyte.kobweb.ksp.common

import com.varabyte.kobweb.ksp.testutil.RecordingLogger
import com.varabyte.kobweb.project.common.FqName
import com.varabyte.kobweb.project.common.PackageName
import com.varabyte.truthish.assertThat
import org.junit.Test

class DefaultLayoutProcessingTest {
    @Test
    fun packagesWithMultipleLayoutsAreReported() {
        val layouts = listOf(
            DefaultLayout(PackageName("site.pages.blog"), FqName("site.layouts.B"), "/src/blog/Layout2.kt"),
            DefaultLayout(PackageName("site.pages"), FqName("site.layouts.A"), "/src/Layout.kt"),
            DefaultLayout(PackageName("site.pages.blog"), FqName("site.layouts.C"), "/src/blog/Layout1.kt"),
        )

        val logger = RecordingLogger()
        layouts.reportPackagesWithMultipleLayouts(logger)

        assertThat(logger.errors).hasSize(1)
        // Files are listed in a stable order, regardless of processing order
        assertThat(logger.errors.single().lines().drop(1)).containsExactly(" - /src/blog/Layout1.kt", " - /src/blog/Layout2.kt").inOrder()
    }

    @Test
    fun noErrorsWhenEachPackageHasOneLayout() {
        val layouts = listOf(
            DefaultLayout(PackageName("site.pages"), FqName("site.layouts.A"), "/src/Layout.kt"),
            DefaultLayout(PackageName("site.pages.blog"), FqName("site.layouts.B"), "/src/blog/Layout.kt"),
        )

        val logger = RecordingLogger()
        layouts.reportPackagesWithMultipleLayouts(logger)

        assertThat(logger.errors).isEmpty()
    }
}
