plugins {
    `kotlin-dsl`
    `version-catalog`
    alias(miptNpmLibs.plugins.kotlin.plugin.serialization)
}

java.targetCompatibility = JavaVersion.VERSION_11

repositories {
    mavenLocal()
    maven("https://repo.kotlin.link")
    mavenCentral()
    gradlePluginPortal()
}

val toolsVersion: String by extra
val kotlinVersion = miptNpmLibs.versions.kotlin.asProvider().get()
val benchmarksVersion = miptNpmLibs.versions.kotlinx.benchmark.get()

dependencies {
    api("ru.mipt.npm:gradle-tools:$toolsVersion")
    //plugins form benchmarks
    api("org.jetbrains.kotlinx:kotlinx-benchmark-plugin:$benchmarksVersion")
    api("org.jetbrains.kotlin:kotlin-allopen:$kotlinVersion")
    //to be used inside build-script only
    implementation(miptNpmLibs.kotlinx.serialization.json)
}

kotlin.sourceSets.all {
    languageSettings.optIn("kotlin.OptIn")
}
