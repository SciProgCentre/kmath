plugins {
    kotlin("jvm") version "1.7.0"
    `kotlin-dsl`
    `version-catalog`
    alias(npmlibs.plugins.kotlin.plugin.serialization)
}

java.targetCompatibility = JavaVersion.VERSION_11

repositories {
    mavenLocal()
    maven("https://repo.kotlin.link")
    mavenCentral()
    gradlePluginPortal()
}

val toolsVersion = npmlibs.versions.tools.get()
val kotlinVersion = npmlibs.versions.kotlin.asProvider().get()
val benchmarksVersion = npmlibs.versions.kotlinx.benchmark.get()

dependencies {
    api("ru.mipt.npm:gradle-tools:$toolsVersion")
    api(npmlibs.atomicfu.gradle)
    //plugins form benchmarks
    api("org.jetbrains.kotlinx:kotlinx-benchmark-plugin:$benchmarksVersion")
    api("org.jetbrains.kotlin:kotlin-allopen:$kotlinVersion")
    //to be used inside build-script only
    implementation(npmlibs.kotlinx.serialization.json)
}

kotlin.sourceSets.all {
    languageSettings.optIn("kotlin.OptIn")
}
