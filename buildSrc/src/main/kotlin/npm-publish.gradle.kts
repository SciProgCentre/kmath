@file:Suppress("UnstableApiUsage")

import com.jfrog.bintray.gradle.tasks.BintrayUploadTask
import groovy.lang.GroovyObject
import org.gradle.api.publish.maven.internal.artifact.FileBasedMavenArtifact
import org.jfrog.gradle.plugin.artifactory.dsl.PublisherConfig
import org.jfrog.gradle.plugin.artifactory.dsl.ResolverConfig

// Old bintray.gradle script converted to real Gradle plugin (precompiled script plugin)
// It now has own dependencies and support type safe accessors
// Syntax is pretty close to what we had in Groovy
// (excluding Property.set and bintray dynamic configs)

plugins {
    `maven-publish`
    id("com.jfrog.bintray")
    id("com.jfrog.artifactory")
}

val vcs = "https://github.com/mipt-npm/kmath"
val bintrayRepo = "https://bintray.com/mipt-npm/scientifik"

// Configure publishing
publishing {
    repositories {
        maven(bintrayRepo)
    }

    // Process each publication we have in this project
    publications.filterIsInstance<MavenPublication>().forEach { publication ->

        // use type safe pom config GSL instead of old dynamic
        publication.pom {
            name.set(project.name)
            description.set(project.description)
            url.set(vcs)

            licenses {
                license {
                    name.set("The Apache Software License, Version 2.0")
                    url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    distribution.set("repo")
                }
            }
            developers {
                developer {
                    id.set("MIPT-NPM")
                    name.set("MIPT nuclear physics methods laboratory")
                    organization.set("MIPT")
                    organizationUrl.set("http://npm.mipt.ru")
                }

            }
            scm {
                url.set(vcs)
            }
        }
    }

}

bintray {
    user = findProperty("bintrayUser") as? String ?: System.getenv("BINTRAY_USER")
    key = findProperty("bintrayApiKey") as? String? ?: System.getenv("BINTRAY_API_KEY")
    publish = true
    override = true // for multi-platform Kotlin/Native publishing

    // We have to use delegateClosureOf because bintray supports only dynamic groovy syntax
    // this is a problem of this plugin
    pkg.apply {
        userOrg = "mipt-npm"
        repo = "scientifik"
        name = project.name
        issueTrackerUrl = "$vcs/issues"
        setLicenses("Apache-2.0")
        vcsUrl = vcs
        version.apply {
            name = project.version.toString()
            vcsTag = project.version.toString()
            released = java.util.Date().toString()
        }
    }

    //workaround bintray bug
    afterEvaluate {
        setPublications(*publishing.publications.names.toTypedArray())
    }

    tasks {
        bintrayUpload {
            dependsOn(publishToMavenLocal)
        }
    }
}

//workaround for bintray
tasks.withType<BintrayUploadTask> {
    doFirst {
        publishing.publications
            .filterIsInstance<MavenPublication>()
            .forEach { publication ->
                val moduleFile = buildDir.resolve("publications/${publication.name}/module.json")
                if (moduleFile.exists()) {
                    publication.artifact(object : FileBasedMavenArtifact(moduleFile) {
                        override fun getDefaultExtension() = "module"
                    })
                }
            }
    }
}

artifactory {
    val artifactoryUser: String? by project
    val artifactoryPassword: String? by project
    val artifactoryContextUrl = "http://npm.mipt.ru:8081/artifactory"

    setContextUrl(artifactoryContextUrl)//The base Artifactory URL if not overridden by the publisher/resolver
    publish(delegateClosureOf<PublisherConfig> {
        repository(delegateClosureOf<GroovyObject> {
            setProperty("repoKey", "gradle-dev-local")
            setProperty("username", artifactoryUser)
            setProperty("password", artifactoryPassword)
        })

        defaults(delegateClosureOf<GroovyObject> {
            invokeMethod("publications", arrayOf("jvm", "js", "kotlinMultiplatform", "metadata"))
        })
    })
    resolve(delegateClosureOf<ResolverConfig> {
        repository(delegateClosureOf<GroovyObject> {
            setProperty("repoKey", "gradle-dev")
            setProperty("username", artifactoryUser)
            setProperty("password", artifactoryPassword)
        })
    })
}
