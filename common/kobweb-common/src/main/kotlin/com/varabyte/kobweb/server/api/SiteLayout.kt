package com.varabyte.kobweb.server.api

import com.varabyte.kobweb.common.error.KobwebException
import com.varabyte.kobweb.project.conf.KobwebConf

private val PROPERTY_SITE_LAYOUT = "kobweb.site.layout"

enum class SiteLayout {
    /**
     * Layout which supports packaging both frontend and backend code.
     *
     * Files live in multiple roots as specified in the [KobwebConf] file.
     *
     * Furthermore, the files searched for are different in development and production versions.
     *
     * With this format, your pages, resources, scripts, and server jar are served from all potentially different
     * locations, with a fallback `index.html` that can be used for unknown URLs.
     *
     * This format is guaranteed to support all Kobweb features, although it will require you hosting your own server
     * which will be more expensive than if you use a static layout hosting provider.
     */
    FULLSTACK,

    /**
     * Layout for serving files in a straightforward manner.
     *
     * Files live in single root folder, from which they are served directly without any complex routing logic.
     *
     * This is ideal for any site that doesn't require any custom backend logic (which is a lot of them). External
     * hosting providers should be able to handle this layout, making it fast and cheap to deploy.
     *
     * Kobweb server features and dynamic routing are not supported with this format. However, a huge advantage for this
     * format is it can often be served fast and cheap.
     */
    STATIC;

    companion object {
        fun get(): SiteLayout {
            val envValue: String = System.getProperty(PROPERTY_SITE_LAYOUT) ?: FULLSTACK.name
            return SiteLayout.entries.firstOrNull { layout -> layout.name == envValue }
                ?: throw KobwebException(
                    "Invalid export layout: $envValue, expected one of [${
                        ServerEnvironment.entries.joinToString()
                    }]"
                )
        }
    }

    fun toSystemPropertyParam(): String = "-D${PROPERTY_SITE_LAYOUT}=${this.name}"

    val isStatic: Boolean get() = this == STATIC
    val isFullstack: Boolean get() = !isStatic
}
