import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

buildscript {
    val kotlinVersion: String by rootProject.extra("1.3.21")
    val ioVersion: String by rootProject.extra("0.1.5")
    val coroutinesVersion: String by rootProject.extra("1.1.1")
    val atomicfuVersion: String by rootProject.extra("0.12.1")

    repositories {
        //maven("https://dl.bintray.com/kotlin/kotlin-eap")
        jcenter()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("org.jfrog.buildinfo:build-info-extractor-gradle:4+")
        classpath("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.4")
    }
}

plugins {
    id("com.jfrog.artifactory") version "4.9.1" apply false
}

allprojects {
    if (project.name.startsWith("kmath")) {
        apply(plugin = "maven-publish")
        apply(plugin = "com.jfrog.artifactory")
    }

    group = "scientifik"
    version = "0.1.0-dev"

    repositories {
        //maven("https://dl.bintray.com/kotlin/kotlin-eap")
        jcenter()
    }

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
    }

    extensions.findByType<PublishingExtension>()?.apply {
        val javadocJar by tasks.creating(Jar::class) {
            archiveClassifier.value("javadoc")
            // TODO: instead of a single empty Javadoc JAR, generate real documentation for each module
        }

        val sourcesJar by tasks.creating(Jar::class) {
            archiveClassifier.value("sources")
        }

        repositories {
            maven(uri("$buildDir/repo"))
        }
        publications.withType<MavenPublication>().all {
            artifact(javadocJar)
            artifact(sourcesJar)
        }
        //apply(from = "${rootProject.rootDir}/gradle/bintray.gradle")
    }
}

if (file("gradle/artifactory.gradle").exists()) {
    apply(from = "gradle/artifactory.gradle")
}
