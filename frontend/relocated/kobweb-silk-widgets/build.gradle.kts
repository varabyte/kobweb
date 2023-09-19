plugins {
    id("com.varabyte.kobweb.internal.publish")
}

group = "com.varabyte.kobweb"
version = libs.versions.kobweb.libs.get()

kobwebPublication {
    relocationDetails {
        artifactId.set("silk-widgets")
        message.set("All kobweb-silk-* artifacts have been renamed to silk-*, to emphasize that Silk can be used separately from Kobweb.")
    }

    description.set("[RELOCATED] The subset of Silk that doesn't depend on Kobweb at all, extracted into its own library in case projects want to use it without Kobweb")
}
