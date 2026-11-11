plugins {
    id("kobweb-compilation-settings")
    alias(libs.plugins.kotlin.multiplatform)
    id("kobweb-compose")
    id("com.varabyte.kobweb.internal.publish")
    id("kobweb-silk-icons")
}

group = "com.varabyte.kobwebx"
version = libs.versions.kobweb.framework.get()

silkIcons {
    iconSet.set("mdi")
    // A commit in https://github.com/google/material-design-icons, which has no release tags
    version.set("49d4db35df873165d6bd6ba09b063c7dafbac2f4")
    iconsFile.set("md-icon-list.txt")
}

kobwebPublication {
    artifactName.set("Kobweb Silk Icons (Material Design Icons)")
    artifactId.set("silk-icons-mdi")
    description.set("A collection of composables that directly wrap Material Design Icons.")
}
