package com.varabyte.kobweb.server.api

import com.varabyte.kobweb.common.error.KobwebException
import com.varabyte.kobweb.project.conf.KobwebConf

private val PROPERTY_SITE_LAYOUT = "kobweb.site.layout"

enum class SiteLayout {
    /**
     * Files live in multiple roots as specified in the [KobwebConf] file.
     *
     * Furthermore, the files searched for are different in developement and production versions.
     *
     * With this format, your pages, resources, scripts, and server jar are served from all potentially different
     * locations, with a fallback `index.html` that can be used for unknown URLs.
     *
     * This format is guaranteed to support all Kobweb features, although it will require you hosting your own server
     * which might be more expensive than a static layout.
     */
    KOBWEB,

    /**
     * Files live in single root folder, from which they are served directly without any complex routing logic.
     *
     * This is ideal for serving simple static blog sites. External hosting providers should be able to handle this
     * layout.
     *
     * Kobweb server features and dynamic routing are not supported with this format. However, a huge advantage for this
     * format is it can often be served fast and cheap.
     */
    STATIC;

    companion object {
        fun get(): SiteLayout {
            val envValue: String = System.getProperty(PROPERTY_SITE_LAYOUT) ?: KOBWEB.name
            return SiteLayout.values().firstOrNull { layout -> layout.name == envValue }
                ?: throw KobwebException("Invalid export layout: $envValue, expected one of [${ServerEnvironment.values().joinToString()}]")
        }
    }

    fun toSystemPropertyParam(): String = "-D${PROPERTY_SITE_LAYOUT}=${this.name}"
}