@file:Suppress("FunctionName")

package com.varabyte.kobwebx.gradle.markdown.util

import com.varabyte.kobwebx.frontmatter.FrontMatterElement
import com.varabyte.truthish.assertThat
import org.junit.Test
import java.io.File

class RouteUtilsTest {
    @Test
    fun `slug is the file name in kebab case`() {
        assertThat(RouteUtils.getSlug(File("blog/BlogPost.md"))).isEqualTo("blog-post")
    }

    @Test
    fun `index file has an empty slug`() {
        assertThat(RouteUtils.getSlug(File("blog/Index.md"))).isEqualTo("")
    }

    @Test
    fun `route is the folder plus the slug`() {
        assertThat(RouteUtils.getRoute(File("blog/BlogPost.md"), FrontMatterElement.EmptyMap)).isEqualTo("/blog/blog-post")
        assertThat(RouteUtils.getRoute(File("About.md"), FrontMatterElement.EmptyMap)).isEqualTo("/about")
    }

    @Test
    fun `index route ends with a slash`() {
        assertThat(RouteUtils.getRoute(File("blog/index.md"), FrontMatterElement.EmptyMap)).isEqualTo("/blog/")
        assertThat(RouteUtils.getRoute(File("index.md"), FrontMatterElement.EmptyMap)).isEqualTo("/")
    }

    @Test
    fun `relative route override replaces the slug`() {
        val frontMatter = FrontMatterElement.ValueMap(mapOf("routeOverride" to FrontMatterElement.Scalar("first-post")))

        assertThat(RouteUtils.getRoute(File("blog/BlogPost.md"), frontMatter)).isEqualTo("/blog/first-post")
    }

    @Test
    fun `absolute route override replaces the whole route`() {
        val frontMatter = FrontMatterElement.ValueMap(mapOf("routeOverride" to FrontMatterElement.Scalar("/articles/first-post")))

        assertThat(RouteUtils.getRoute(File("blog/BlogPost.md"), frontMatter)).isEqualTo("/articles/first-post")
    }

    @Test
    fun `route override ending in a slash keeps the slug`() {
        val frontMatter = FrontMatterElement.ValueMap(mapOf("routeOverride" to FrontMatterElement.Scalar("posts/")))

        assertThat(RouteUtils.getRoute(File("blog/BlogPost.md"), frontMatter)).isEqualTo("/blog/posts/blog-post")
    }

    @Test
    fun `route override ending in index becomes a folder route`() {
        val frontMatter = FrontMatterElement.ValueMap(mapOf("routeOverride" to FrontMatterElement.Scalar("posts/index")))

        assertThat(RouteUtils.getRoute(File("blog/BlogPost.md"), frontMatter)).isEqualTo("/blog/posts/")
    }
}
