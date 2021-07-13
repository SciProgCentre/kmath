plugins {
    id("ru.mipt.npm.gradle.project")
}

allprojects {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.google.com")
        maven("https://plugins.gradle.org/m2/")
    }
}
