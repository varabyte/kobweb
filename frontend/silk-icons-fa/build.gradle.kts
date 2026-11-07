plugins {
    id("kobweb-compilation-settings")
    alias(libs.plugins.kotlin.multiplatform)
    id("kobweb-compose")
    id("com.varabyte.kobweb.internal.publish")
    id("kobweb-silk-icons")
}

group = "com.varabyte.kobwebx"
version = libs.versions.kobweb.get()

silkIcons {
    iconSet.set("fa")
    // Keep in sync with the Font Awesome stylesheet that KobwebGenerateSiteIndexTask links to
    version.set("6.5.2")
    iconsFile.set("fa-icon-list.txt")
}

kobwebPublication {
    artifactName.set("Kobweb Silk Icons (Font Awesome)")
    artifactId.set("silk-icons-fa")
    description.set("A collection of composables that directly wrap Font Awesome icons.")
}
