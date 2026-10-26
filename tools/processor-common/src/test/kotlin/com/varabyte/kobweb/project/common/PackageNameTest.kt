package com.varabyte.kobweb.project.common

import com.varabyte.truthish.assertThat
import kotlin.io.path.Path
import org.junit.Test

class PackageNameTest {
    private fun String.isSameOrSubpackageOf(other: String) = PackageName(this).isSameOrSubpackageOf(PackageName(other))

    @Test
    fun isSameOrSubpackageOf() {
        assertThat("a.b".isSameOrSubpackageOf("a.b")).isTrue()
        assertThat("a.b.c".isSameOrSubpackageOf("a.b")).isTrue()
        assertThat("a.b.c.d".isSameOrSubpackageOf("a.b")).isTrue()

        // Sharing a string prefix is not enough; whole segments must match
        assertThat("a.bc".isSameOrSubpackageOf("a.b")).isFalse()
        assertThat("a.b_old".isSameOrSubpackageOf("a.b")).isFalse()
        assertThat("a.b_old.c".isSameOrSubpackageOf("a.b")).isFalse()

        assertThat("a".isSameOrSubpackageOf("a.b")).isFalse()
        assertThat("x.b".isSameOrSubpackageOf("a.b")).isFalse()

        // Everything lives under the root package
        assertThat("a.b".isSameOrSubpackageOf("")).isTrue()
        assertThat("".isSameOrSubpackageOf("")).isTrue()
        assertThat("".isSameOrSubpackageOf("a")).isFalse()
    }

    @Test
    fun resolvePackageShortcuts() {
        val group = PackageName("org.example")
        assertThat(group.resolve(PackageShortcut(".pages"))).isEqualTo(PackageName("org.example.pages"))
        assertThat(group.resolve(PackageShortcut("."))).isEqualTo(group)
        assertThat(group.resolve(PackageShortcut("com.other.pages"))).isEqualTo(PackageName("com.other.pages"))

        val root = PackageName("")
        assertThat(root.resolve(PackageShortcut(".pages"))).isEqualTo(PackageName("pages"))
        assertThat(root.resolve(PackageShortcut("."))).isEqualTo(root)
    }

    @Test
    fun resolveFqNameShortcuts() {
        val group = PackageName("org.example")
        assertThat(group.resolve(FqNameShortcut(".components.layouts.PageLayout")))
            .isEqualTo(FqName("org.example.components.layouts.PageLayout"))
        assertThat(group.resolve(FqNameShortcut("com.other.PageLayout"))).isEqualTo(FqName("com.other.PageLayout"))

        val root = PackageName("")
        assertThat(root.resolve(FqNameShortcut(".PageLayout"))).isEqualTo(FqName("PageLayout"))
    }

    @Test
    fun toPath() {
        assertThat(PackageName("a.b.c").toPath()).isEqualTo(Path("a", "b", "c"))
        assertThat(PackageName("a").toPath()).isEqualTo(Path("a"))
        assertThat(PackageName("").toPath()).isEqualTo(Path(""))
    }
}
