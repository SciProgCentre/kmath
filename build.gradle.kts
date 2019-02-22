import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

buildscript {
    val kotlinVersion: String by rootProject.extra("1.3.21")
    val ioVersion: String by rootProject.extra("0.1.5")
    val coroutinesVersion: String by rootProject.extra("1.1.1")
    val atomicfuVersion: String by rootProject.extra("0.12.1")
    val dokkaVersion: String by rootProject.extra("0.9.17")

    repositories {
        //maven("https://dl.bintray.com/kotlin/kotlin-eap")
        jcenter()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("org.jfrog.buildinfo:build-info-extractor-gradle:4+")
        classpath("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.4")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:$dokkaVersion")
    }
}

plugins {
    id("com.jfrog.artifactory") version "4.9.1" apply false
}

val kmathVersion by extra("0.1.0")

allprojects {
    group = "scientifik"
    version = kmathVersion

    repositories {
        //maven("https://dl.bintray.com/kotlin/kotlin-eap")
        jcenter()
    }

    apply(plugin = "maven")
    apply(plugin = "maven-publish")

    // apply bintray configuration
    apply(from = "${rootProject.rootDir}/gradle/bintray.gradle")

    //apply artifactory configuration
    apply(from = "${rootProject.rootDir}/gradle/artifactory.gradle")
}

subprojects {
    if (!name.startsWith("kmath")) return@subprojects


    extensions.findByType<KotlinMultiplatformExtension>()?.apply {
        jvm {
            compilations.all {
                kotlinOptions {
                    jvmTarget = "1.8"
                }
            }
        }
        targets.all {
            sourceSets.all {
                languageSettings.progressiveMode = true
            }
        }


        extensions.findByType<PublishingExtension>()?.apply {
            publications.filterIsInstance<MavenPublication>().forEach { publication ->
                if (publication.name == "kotlinMultiplatform") {
                    // for our root metadata publication, set artifactId with a package and project name
                    publication.artifactId = project.name
                } else {
                    // for targets, set artifactId with a package, project name and target name (e.g. iosX64)
                    publication.artifactId = "${project.name}-${publication.name}"
                }
            }

            // Create empty jar for sources classifier to satisfy maven requirements
            val stubSources by tasks.registering(Jar::class) {
                archiveClassifier.set("sources")
                //from(sourceSets.main.get().allSource)
            }

            // Create empty jar for javadoc classifier to satisfy maven requirements
            val stubJavadoc by tasks.registering(Jar::class) {
                archiveClassifier.set("javadoc")
            }

            extensions.findByType<KotlinMultiplatformExtension>()?.apply {

                targets.forEach { target ->
                    val publication = publications.findByName(target.name) as MavenPublication

                    // Patch publications with fake javadoc
                    publication.artifact(stubJavadoc)

                    // Remove gradle metadata publishing from all targets which are not native
//                if (target.platformType.name != "native") {
//                    publication.gradleModuleMetadataFile = null
//                    tasks.matching { it.name == "generateMetadataFileFor${name.capitalize()}Publication" }.all {
//                        onlyIf { false }
//                    }
//                }
                }
            }
        }
    }
}


