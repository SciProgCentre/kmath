plugins {
    id("ru.mipt.npm.base")
    id("org.jetbrains.changelog") version "0.4.0"
}

val kmathVersion by extra("0.2.0-dev-1")
val bintrayRepo by extra("kscience")
val githubProject by extra("kmath")

allprojects {
    repositories {
        jcenter()
        maven("https://dl.bintray.com/kotlin/kotlinx")
        maven("https://dl.bintray.com/hotkeytlt/maven")
    }

    group = "kscience.kmath"
    version = kmathVersion
}

subprojects {
    if (name.startsWith("kmath")) apply<ru.mipt.npm.gradle.KSciencePublishPlugin>()
}

/**
 * TODO move to base plugin
 */
val generateReadme by tasks.creating {
    group = "documentation"

    fun List<Map<String, Any?>>.generateFeatureString(pathPrefix: String): String = buildString {
        this@generateFeatureString.forEach { feature ->
            val id by feature
            val description by feature
            val ref by feature
            appendln(" - [$id]($pathPrefix$ref) : $description")
        }
    }

    doLast {
        val reader = groovy.json.JsonSlurper()
        val projects = HashMap<String, Map<String, Any?>>()

        project.subprojects {
            var properties: Map<String, Any?> = mapOf(
                "name" to this.name,
                "group" to this.group,
                "version" to this.version
            )

            val projectProperties = this.file("docs/kscience-module.json")

            @Suppress("UNCHECKED_CAST")
            if (projectProperties.exists()) {
                val customProperties: Map<String, Any?> =
                    (reader.parse(projectProperties) as? Map<String, Any?> ?: emptyMap()).withDefault { null }
                val features: List<Map<String, Any?>>? by customProperties
                val featureString = features?.generateFeatureString("")
                properties = customProperties + properties + ("featuresString" to featureString)
            }

            projects[name] = properties.withDefault { null }

            val readmeStub = this.file("docs/README-STUB.md")
            if (readmeStub.exists()) {
                val readmeFile = this.file("README.md")
                readmeFile.writeText(
                    groovy.text.SimpleTemplateEngine().createTemplate(readmeStub).make(properties).toString()
                )
            }
        }

        val rootReadmeStub = project.file("docs/README-STUB.md")

        val modulesString = buildString {
            projects.filter { it.key.startsWith("kmath") }.forEach { (name, properties) ->
                appendln("### [$name]($name)")
                val features: List<Map<String, Any?>>? by properties
                if (features != null) {
                    appendln(features!!.generateFeatureString("$name/"))
                }
            }
        }

        val rootReadmeProperties: Map<String, Any> = mapOf(
            "name" to project.name,
            "group" to project.group,
            "version" to project.version,
            "modulesString" to modulesString
        )

        if (rootReadmeStub.exists()) {
            val readmeFile = project.file("README.md")
            readmeFile.writeText(
                groovy.text.SimpleTemplateEngine().createTemplate(rootReadmeStub).make(rootReadmeProperties).toString()
            )
        }

    }
}