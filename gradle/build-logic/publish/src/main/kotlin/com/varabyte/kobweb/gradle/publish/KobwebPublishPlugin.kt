package com.varabyte.kobweb.gradle.publish

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskContainer
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.jetbrains.dokka.gradle.tasks.DokkaGenerateTask
import javax.inject.Inject

abstract class KobwebPublicationConfig @Inject constructor(objects: ObjectFactory) {
    abstract class RelocationDetails {
        abstract val groupId: Property<String>
        abstract val artifactId: Property<String>
        abstract val message: Property<String>
    }

    /**
     * A human-readable display name for this artifact.
     */
    abstract val artifactName: Property<String>

    /**
     * Provide an artifact ID given the name of the publication.
     *
     * For multiplatform artifacts, this name will be automatically suffixed with the target name, e.g. "-js", "-jvm".
     */
    abstract val artifactId: Property<String>

    /**
     * A human-readable description for this artifact.
     *
     * If this publication represents a relocation, this will be used for the relocation's message.
     */
    abstract val description: Property<String>

    /**
     * The website URL to associate with this artifact.
     */
    abstract val site: Property<String>

    /**
     * Set to non-null if this publication represents a relocation to another artifact.
     *
     * Relocations are useful if an artifact coordinate has changed, but you don't want projects using the old
     * coordinate to fail to build. Relocations give the user a grace period during which they can migrate at their
     * convenience.
     *
     * @see <a href="https://maven.apache.org/pom.html#relocation">Maven Relocation</a>
     */
    val relocationDetails: RelocationDetails = objects.newInstance(RelocationDetails::class.java)

    fun relocationDetails(configure: RelocationDetails.() -> Unit) {
        relocationDetails.configure()
    }

    init {
        site.convention("https://github.com/varabyte/kobweb")
    }
}

private const val DOKKA_HTML_JAR_TASK_NAME = "dokkaHtmlJar"

val TaskContainer.dokkaHtmlJar: Provider<Jar>
    get() = named<Jar>(DOKKA_HTML_JAR_TASK_NAME)

/**
 * An internal plugin that helps configure and publish Varabyte artifacts to our maven repository.
 *
 * By default, this will only publish to mavenLocal, unless you have "kobweb.gcloud.publish" set to true and
 * "gcloud.artifact.registry.secret" set with the correct authentication key.
 *
 * Additionally, this will attempt to sign your files only if you have "kobweb.sign" set to true. If this is false,
 * then files will not be published to the cloud.
 */
class KobwebPublishPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.pluginManager.apply {
            apply("org.gradle.maven-publish")
            apply("org.gradle.signing")
            apply("org.jetbrains.dokka")
        }

        val dokkaHtmlTask = project.tasks.named<DokkaGenerateTask>("dokkaGeneratePublicationHtml")
        project.tasks.register<Jar>(DOKKA_HTML_JAR_TASK_NAME) {
            dependsOn(dokkaHtmlTask)
            from(dokkaHtmlTask.flatMap { it.outputDirectory })
            archiveClassifier.set("javadoc")
        }

        project.configureDokka()

        val config = project.extensions.create<KobwebPublicationConfig>("kobwebPublication", project.objects)
        project.afterEvaluate {
            // Configure after evaluating to allow the user to set extension values first
            configurePublishing(config)
        }
    }
}
