buildscript {
    extra["kotlinVersion"] = "1.3.20-eap-52"
    extra["ioVersion"] = "0.1.2"
    extra["coroutinesVersion"] = "1.1.0"

    val kotlinVersion: String by extra
    val ioVersion: String by extra
    val coroutinesVersion: String by extra

    repositories {
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
        jcenter()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("org.jfrog.buildinfo:build-info-extractor-gradle:4+")
    }
}

plugins {
    id("com.jfrog.artifactory") version "4.8.1" apply false
//    id("org.jetbrains.kotlin.multiplatform") apply false
}

allprojects {
    apply(plugin = "maven-publish")
    apply(plugin = "com.jfrog.artifactory")

    group = "scientifik"
    version = "0.0.3-dev-1"

    repositories {
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
        jcenter()
    }
}

if (file("artifactory.gradle").exists()) {
    apply(from = "artifactory.gradle")
}