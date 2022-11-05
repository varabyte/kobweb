package com.varabyte.kobweb.gradle.application

// NOTE: Unlike the `KOBWEB_METADATA_` constants from the core plugin, these values will go into the build folder, not
// generated resource folders. At the app level, it is not a goal for these files to be consumed upstream, so there's
// no need to package them into a jar at build time.

const val KOBWEB_APP_METADATA_SUBFOLDER = "kobweb/metadata"
const val KOBWEB_APP_METADATA_FRONTEND = "$KOBWEB_APP_METADATA_SUBFOLDER/frontend.json"
const val KOBWEB_APP_METADATA_BACKEND = "$KOBWEB_APP_METADATA_SUBFOLDER/backend.json"