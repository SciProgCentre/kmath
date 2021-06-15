plugins {
    `kotlin-dsl`
    kotlin("plugin.serialization") version "1.4.31"
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0")
    api("ru.mipt.npm:gradle-tools:0.9.10")
    api("org.jetbrains.kotlinx:kotlinx-benchmark-plugin:0.3.1")
}

kotlin.sourceSets.all {
    languageSettings.useExperimentalAnnotation("kotlin.ExperimentalStdlibApi")
}
