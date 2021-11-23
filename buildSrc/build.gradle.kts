plugins {
    `kotlin-dsl`
    kotlin("plugin.serialization") version "1.5.21"
}

repositories {
    maven("https://repo.kotlin.link")
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")
    api("ru.mipt.npm:gradle-tools:0.10.7")
    api("org.jetbrains.kotlinx:kotlinx-benchmark-plugin:0.3.1")
}

kotlin.sourceSets.all {
    languageSettings.useExperimentalAnnotation("kotlin.ExperimentalStdlibApi")
}
