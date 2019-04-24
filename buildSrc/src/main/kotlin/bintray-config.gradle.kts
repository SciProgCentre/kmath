@file:Suppress("UnstableApiUsage")

import com.jfrog.bintray.gradle.BintrayExtension.PackageConfig
import com.jfrog.bintray.gradle.BintrayExtension.VersionConfig

// Old bintray.gradle script converted to real Gradle plugin (precompiled script plugin)
// It now has own dependencies and support type safe accessors
// Syntax is pretty close to what we had in Groovy
// (excluding Property.set and bintray dynamic configs)

plugins {
    id("com.jfrog.bintray")
    `maven-publish`
}

val vcs = "https://github.com/mipt-npm/kmath"

// Configure publishing
publishing {
    repositories {
        maven("https://bintray.com/mipt-npm/scientifik")
    }

    // Process each publication we have in this project
    publications.filterIsInstance<MavenPublication>().forEach { publication ->

        // use type safe pom config GSL insterad of old dynamic
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
    // delegates for runtime properties
    val bintrayUser: String? by project
    val bintrayApiKey: String? by project
    user = bintrayUser ?: System.getenv("BINTRAY_USER")
    key = bintrayApiKey ?: System.getenv("BINTRAY_API_KEY")
    publish = true
    override = true // for multi-platform Kotlin/Native publishing

    // We have to use delegateClosureOf because bintray supports only dynamic groovy syntax
    // this is a problem of this plugin
    pkg(delegateClosureOf<PackageConfig> {
        userOrg = "mipt-npm"
        repo = "scientifik"
        name = "scientifik.kmath"
        issueTrackerUrl = "https://github.com/mipt-npm/kmath/issues"
        setLicenses("Apache-2.0")
        vcsUrl = vcs
        version(delegateClosureOf<VersionConfig> {
            name = project.version.toString()
            vcsTag = project.version.toString()
            released = java.util.Date().toString()
        })
    })

    tasks {
        bintrayUpload {
            dependsOn(publishToMavenLocal)
            doFirst {
                setPublications(project.publishing.publications
                    .filterIsInstance<MavenPublication>()
                    .filter { !it.name.contains("-test") && it.name != "kotlinMultiplatform" }
                    .map {
                        println("""Uploading artifact "${it.groupId}:${it.artifactId}:${it.version}" from publication "${it.name}""")
                        it.name //https://github.com/bintray/gradle-bintray-plugin/issues/256
                    })
            }
        }

    }
}
