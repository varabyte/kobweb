package com.varabyte.kobweb.ksp.common

private const val KOBWEB_FQN_PREFIX = "com.varabyte.kobweb."
private const val KOBWEB_CORE_FQN_PREFIX = "${KOBWEB_FQN_PREFIX}core."
private const val KOBWEB_SILK_FQN_PREFIX = "${KOBWEB_FQN_PREFIX}silk."
private const val KOBWEB_API_FQN_PREFIX = "${KOBWEB_FQN_PREFIX}api."

const val INIT_API_FQN = "${KOBWEB_API_FQN_PREFIX}init.InitApi"
const val PACKAGE_MAPPING_API_FQN = "${KOBWEB_API_FQN_PREFIX}PackageMapping"
const val API_FQN = "${KOBWEB_API_FQN_PREFIX}Api"
const val API_STREAM_SIMPLE_NAME = "ApiStream"
const val API_STREAM_FQN = "${KOBWEB_API_FQN_PREFIX}stream.$API_STREAM_SIMPLE_NAME"

const val APP_FQN = "${KOBWEB_CORE_FQN_PREFIX}App"
const val INIT_KOBWEB_FQN = "${KOBWEB_CORE_FQN_PREFIX}init.InitKobweb"
const val INIT_SILK_FQN = "${KOBWEB_SILK_FQN_PREFIX}init.InitSilk"
const val PACKAGE_MAPPING_PAGE_FQN = "${KOBWEB_CORE_FQN_PREFIX}PackageMapping"
const val PAGE_FQN = "${KOBWEB_CORE_FQN_PREFIX}Page"
const val COMPONENT_STYLE_SIMPLE_NAME = "ComponentStyle"
const val COMPONENT_STYLE_FQN = "${KOBWEB_SILK_FQN_PREFIX}components.style.$COMPONENT_STYLE_SIMPLE_NAME"
const val COMPONENT_VARIANT_SIMPLE_NAME = "ComponentVariant"
const val COMPONENT_VARIANT_FQN = "${KOBWEB_SILK_FQN_PREFIX}components.style.$COMPONENT_VARIANT_SIMPLE_NAME"
const val KEYFRAMES_SIMPLE_NAME = "Keyframes"
const val KEYFRAMES_FQN = "${KOBWEB_SILK_FQN_PREFIX}components.animation.$KEYFRAMES_SIMPLE_NAME"
