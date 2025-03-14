plugins {
    alias(libs.plugins.dokka)
}

group = "com.varabyte.kobweb"
version = libs.versions.kobweb.get()

dokka {
    moduleName = "kobweb"
}

dependencies {
    // (*) indicates dependencies we explicitly excluded as their API is meant to be internal only; no need to make the
    // docs more complicated by including them.

    // dokka(projects.common.clientServerInternal) (*)
    dokka(projects.common.frameworkAnnotations)
    dokka(projects.common.kobwebCommon)
    dokka(projects.common.kobwebSerialization)
    dokka(projects.common.kobwebxSerializationKotlinx)
    dokka(projects.frontend.kobwebCore)
    dokka(projects.frontend.kobwebCompose)
    dokka(projects.frontend.kobwebSilk)
    dokka(projects.frontend.kobwebWorker)
    dokka(projects.frontend.kobwebWorkerInterface)
    dokka(projects.frontend.silkFoundation)
    dokka(projects.frontend.silkWidgets)
    dokka(projects.frontend.silkWidgetsKobweb)
    dokka(projects.frontend.silkIconsFa)
    dokka(projects.frontend.silkIconsMdi)
    dokka(projects.frontend.kobwebxMarkdown)
    dokka(projects.frontend.composeHtmlExt)
    dokka(projects.frontend.browserExt)
    dokka(projects.backend.kobwebApi)
    dokka(projects.backend.serverPlugin)
    // dokka(projects.backend.server) (*)
    dokka(projects.tools.gradlePlugins.core)
    dokka(projects.tools.gradlePlugins.library)
    dokka(projects.tools.gradlePlugins.application)
    dokka(projects.tools.gradlePlugins.worker)
    dokka(projects.tools.gradlePlugins.extensions.markdown)
    // dokka(projects.tools.ksp.siteProcessors) (*)
    // dokka(projects.tools.ksp.workerProcessor) (*)
    // dokka(projects.tools.ksp.kspExt) (*)
    // dokka(projects.tools.processorCommon) (*)
}
