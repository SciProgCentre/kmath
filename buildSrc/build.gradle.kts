plugins {
    `kotlin-dsl`
    `version-catalog`
}

java.targetCompatibility = JavaVersion.VERSION_11

repositories {
    mavenLocal()
    maven("https://repo.kotlin.link")
    mavenCentral()
    gradlePluginPortal()
}

val toolsVersion = spclibs.versions.tools.get()
val kotlinVersion = spclibs.versions.kotlin.asProvider().get()
val benchmarksVersion = spclibs.versions.kotlinx.benchmark.get()

dependencies {
    api("space.kscience:gradle-tools:$toolsVersion")
    //plugins form benchmarks
    api("org.jetbrains.kotlinx:kotlinx-benchmark-plugin:$benchmarksVersion")
    //api("org.jetbrains.kotlin:kotlin-allopen:$kotlinVersion")
    //to be used inside build-script only
    //implementation(spclibs.kotlinx.serialization.json)
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.+")
}

kotlin.sourceSets.all {
    languageSettings.optIn("kotlin.OptIn")
}
