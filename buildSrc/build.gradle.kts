plugins {
    `kotlin-dsl`
    kotlin("plugin.serialization") version "1.4.31"
}

repositories {
    maven("https://repo.kotlin.link")
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0")
    api("ru.mipt.npm:gradle-tools:0.10.2")
    api("org.jetbrains.kotlinx:kotlinx-benchmark-plugin:0.3.1")
}

kotlin.sourceSets.all {
    languageSettings.useExperimentalAnnotation("kotlin.ExperimentalStdlibApi")
}
