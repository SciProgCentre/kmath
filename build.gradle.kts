buildscript {
    extra["kotlinVersion"] = "1.3.10"

    val kotlinVersion: String by extra

    repositories {
        jcenter()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("org.jfrog.buildinfo:build-info-extractor-gradle:4+")
    }
}

plugins {
    id("com.jfrog.artifactory") version "4.8.1"  apply false
}

allprojects {
    apply(plugin = "maven-publish")
    apply(plugin = "com.jfrog.artifactory")

    group = "scientifik"
    version = "0.0.1-SNAPSHOT"
}

if(file("artifactory.gradle").exists()){
    apply(from = "artifactory.gradle")
}