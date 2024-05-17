package com.varabyte.kobweb.ksp

const val KSP_PAGES_PACKAGE_KEY = "kobweb.pagesPackage"
const val KSP_API_PACKAGE_KEY = "kobweb.apiPackage"
const val KSP_PROCESSOR_MODE_KEY = "kobweb.mode"
const val KSP_DEFAULT_CSS_PREFIX_KEY = "kobweb.defaultCssPrefix"
const val KSP_WORKER_OUTPUT_PATH_KEY = "kobweb.worker.outputPath"
const val KSP_WORKER_FQCN_KEY = "kobweb.worker.fqcn"

// Metadata files that will be bundled as resources in JS / JVM artifacts.

const val KOBWEB_METADATA_SUBFOLDER = "META-INF/kobweb"
const val KOBWEB_METADATA_MODULE = "$KOBWEB_METADATA_SUBFOLDER/module.json"

@Deprecated("Migrated to KOBWEB_METADATA_INDEX")
const val KOBWEB_METADATA_INDEX = "$KOBWEB_METADATA_SUBFOLDER/index.json"
const val KOBWEB_METADATA_FRONTEND = "$KOBWEB_METADATA_SUBFOLDER/frontend.json"
const val KOBWEB_METADATA_BACKEND = "$KOBWEB_METADATA_SUBFOLDER/backend.json"

// Top-level metadata files that identify what sort of Kobweb artifact this is

const val KOBWEB_METADATA_LIBRARY = "$KOBWEB_METADATA_SUBFOLDER/library.json"
const val KOBWEB_METADATA_WORKER = "$KOBWEB_METADATA_SUBFOLDER/worker.json"

// Constants for Kobweb workers

const val KOBWEB_METADATA_WORKER_SUBFOLDER = "${KOBWEB_METADATA_SUBFOLDER}/worker"
// The root resource folder under the "resources/public" folder which all worker JS files will be copied to. This
// significantly reduces the chance that generated worker data will collide with user resources.
const val KOBWEB_PUBLIC_WORKER_ROOT = "_kobweb/workers"

// NOTE: Unlike the `KOBWEB_METADATA_` constants, these values will go into the build folder, not
// generated resource folders. At the app level, it is not a goal for these files to be consumed upstream, so there's
// no need to package them into a jar at build time.

const val KOBWEB_APP_METADATA_SUBFOLDER = "kobweb/metadata"
const val KOBWEB_APP_METADATA_FRONTEND = "$KOBWEB_APP_METADATA_SUBFOLDER/frontend.json"
const val KOBWEB_APP_METADATA_BACKEND = "$KOBWEB_APP_METADATA_SUBFOLDER/backend.json"
