package com.varabyte.kobweb.project.frontend

import com.varabyte.truthish.assertThat
import org.junit.Test

class FrontendDataTest {
    private fun pagesErrors(vararg routes: Pair<String, String>): List<String> {
        val errors = mutableListOf<String>()
        FrontendData(pages = routes.map { (fqn, route) -> PageEntry(fqn, route) }).assertValid { errors.add(it) }
        return errors
    }

    @Test
    fun validPagesReportNoErrors() {
        assertThat(
            pagesErrors(
                "site.pages.index" to "/",
                "site.pages.about" to "/about",
                "site.pages.blog.post" to "/blog/{post}",
                "site.pages.docs.rest" to "/docs/{...rest}",
            )
        ).isEmpty()
    }

    @Test
    fun duplicateRoutesAreReported() {
        val errors = pagesErrors("site.pages.about" to "/about", "site.pages.other.about" to "/about")
        assertThat(errors.size).isEqualTo(1)
        assertThat(errors[0]).contains("site.pages.about")
        assertThat(errors[0]).contains("site.pages.other.about")
    }

    @Test
    fun dynamicRoutesWithDifferentNamesStillConflict() {
        assertThat(pagesErrors("a" to "/users/{id}", "b" to "/users/{name}").size).isEqualTo(1)
        assertThat(pagesErrors("a" to "/a/{b}/c/{d}", "b" to "/a/{x}/c/{y}").size).isEqualTo(1)
    }

    @Test
    fun apiPrefixIsReserved() {
        assertThat(pagesErrors("site.pages.api.index" to "/api/").size).isEqualTo(1)
        assertThat(pagesErrors("site.pages.api.users" to "/api/users").size).isEqualTo(1)

        // Only a whole "api" segment is reserved
        assertThat(pagesErrors("site.pages.apis" to "/apis")).isEmpty()
        assertThat(pagesErrors("site.pages.docs.api" to "/docs/api")).isEmpty()
    }

    @Test
    fun repeatedDynamicSegmentNamesAreReported() {
        val errors = pagesErrors("site.pages.users.user" to "/users/{user}/{user}")
        assertThat(errors.size).isEqualTo(1)
        assertThat(errors[0]).contains("\"user\"")

        // Catch-all and optional segments are read by name too
        assertThat(pagesErrors("a" to "/{id}/x/{...id}").size).isEqualTo(1)
        assertThat(pagesErrors("a" to "/{id}/x/{id?}").size).isEqualTo(1)

        // Static segments can repeat
        assertThat(pagesErrors("a" to "/a/{a}/a")).isEmpty()
    }

    @Test
    fun trailingOptionalSegmentConflictsWithParentRoute() {
        val errors = pagesErrors("site.pages.users.index" to "/users/", "site.pages.users.id" to "/users/{id?}")
        assertThat(errors.size).isEqualTo(1)
        assertThat(errors[0]).contains("\"{id?}\" -> \"{id}\"")

        // Without the static parent route, an optional segment is fine
        assertThat(pagesErrors("site.pages.users.id" to "/users/{id?}")).isEmpty()
    }
}
