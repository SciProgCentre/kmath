import com.jfrog.bintray.gradle.tasks.BintrayUploadTask
import org.gradle.api.publish.maven.internal.artifact.FileBasedMavenArtifact
import org.jfrog.gradle.plugin.artifactory.task.ArtifactoryTask

plugins {
    kotlin("multiplatform")
    `maven-publish`
}


kotlin {
    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    js {
        compilations.all {
            kotlinOptions {
                metaInfo = true
                sourceMap = true
                sourceMapEmbedSources = "always"
                moduleKind = "commonjs"
            }
        }

        compilations.named("main") {
            kotlinOptions {
                main = "call"
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(kotlin("stdlib"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                api(kotlin("stdlib-jdk8"))
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }
        val jsMain by getting {
            dependencies {
                api(kotlin("stdlib-js"))
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }

    targets.all {
        sourceSets.all {
            languageSettings.progressiveMode = true
            languageSettings.enableLanguageFeature("InlineClasses")
        }
    }

    // Apply JS test configuration
    val runJsTests by ext(false)

    if (runJsTests) {
        apply(plugin = "js-test")
    }

}

//workaround for bintray and artifactory
project.tasks.filter { it is ArtifactoryTask || it is BintrayUploadTask }.forEach {
    it.doFirst {
        project.configure<PublishingExtension> {
            publications.filterIsInstance<MavenPublication>()
                .forEach { publication ->
                    val moduleFile = project.buildDir.resolve("publications/${publication.name}/module.json")
                    if (moduleFile.exists()) {
                        publication.artifact(object : FileBasedMavenArtifact(moduleFile) {
                            override fun getDefaultExtension() = "module"
                        })
                    }
                }
        }
    }
}
