package com.varabyte.kobweb.ksp

const val KSP_PAGES_PACKAGE_KEY = "kobweb.pagesPackage"
const val KSP_API_PACKAGE_KEY = "kobweb.apiPackage"
const val KSP_PROCESSOR_MODE_KEY = "kobweb.mode"

const val KOBWEB_METADATA_SUBFOLDER = "META-INF/kobweb"
const val KOBWEB_METADATA_FRONTEND = "$KOBWEB_METADATA_SUBFOLDER/frontend.json"
const val KOBWEB_METADATA_BACKEND = "$KOBWEB_METADATA_SUBFOLDER/backend.json"

// NOTE: Unlike the `KOBWEB_METADATA_` constants, these values will go into the build folder, not
// generated resource folders. At the app level, it is not a goal for these files to be consumed upstream, so there's
// no need to package them into a jar at build time.

const val KOBWEB_APP_METADATA_SUBFOLDER = "kobweb/metadata"
const val KOBWEB_APP_METADATA_FRONTEND = "$KOBWEB_APP_METADATA_SUBFOLDER/frontend.json"
const val KOBWEB_APP_METADATA_BACKEND = "$KOBWEB_APP_METADATA_SUBFOLDER/backend.json"
