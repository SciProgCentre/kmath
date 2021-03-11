pluginManagement {
    repositories {
        maven("https://repo.kotlin.link")
        mavenLocal()
        gradlePluginPortal()
        jcenter()
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
        maven("https://dl.bintray.com/kotlin/kotlinx")
    }

    val toolsVersion = "0.9.1"
    val kotlinVersion = "1.4.31"

    plugins {
        id("kotlinx.benchmark") version "0.2.0-dev-20"
        id("ru.mipt.npm.gradle.project") version toolsVersion
        id("ru.mipt.npm.gradle.mpp") version toolsVersion
        id("ru.mipt.npm.gradle.jvm") version toolsVersion
        id("ru.mipt.npm.gradle.publish") version toolsVersion
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.allopen") version kotlinVersion
    }
}

rootProject.name = "kmath"

include(
    ":kmath-memory",
    ":kmath-complex",
    ":kmath-core",
    ":kmath-coroutines",
    ":kmath-functions",
    ":kmath-histograms",
    ":kmath-commons",
    ":kmath-viktor",
    ":kmath-stat",
    ":kmath-nd4j",
    ":kmath-dimensions",
    ":kmath-for-real",
    ":kmath-geometry",
    ":kmath-ast",
    ":kmath-ejml",
    ":kmath-kotlingrad",
    ":examples"
)
