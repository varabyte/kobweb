plugins {
    alias(libs.plugins.dokka)
}

group = "com.varabyte.kobweb"
version = libs.versions.kobweb.get()

dokka {
    moduleName = "kobweb"
}

dependencies {
    dokka(project(":common:client-server-internal"))
    dokka(project(":common:framework-annotations"))
    dokka(project(":common:kobweb-common"))
    dokka(project(":common:kobweb-serialization"))
    dokka(project(":common:kobwebx-serialization-kotlinx"))
    dokka(project(":frontend:kobweb-core"))
    dokka(project(":frontend:kobweb-compose"))
    dokka(project(":frontend:kobweb-silk"))
    dokka(project(":frontend:kobweb-worker"))
    dokka(project(":frontend:kobweb-worker-interface"))
    dokka(project(":frontend:silk-foundation"))
    dokka(project(":frontend:silk-widgets"))
    dokka(project(":frontend:silk-widgets-kobweb"))
    dokka(project(":frontend:silk-icons-fa"))
    dokka(project(":frontend:silk-icons-mdi"))
    dokka(project(":frontend:kobwebx-markdown"))
    dokka(project(":frontend:compose-html-ext"))
    dokka(project(":frontend:browser-ext"))
    dokka(project(":backend:kobweb-api"))
    dokka(project(":backend:server"))
    dokka(project(":backend:server-plugin"))
    dokka(project(":tools:gradle-plugins:core"))
    dokka(project(":tools:gradle-plugins:library"))
    dokka(project(":tools:gradle-plugins:application"))
    dokka(project(":tools:gradle-plugins:worker"))
    dokka(project(":tools:gradle-plugins:extensions:markdown"))
    dokka(project(":tools:ksp:site-processors"))
    dokka(project(":tools:ksp:worker-processor"))
    dokka(project(":tools:ksp:ksp-ext"))
    dokka(project(":tools:processor-common"))
}
