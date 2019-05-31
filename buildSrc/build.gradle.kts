plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    jcenter()
}

val kotlinVersion = "1.3.31"

// Add plugins used in buildSrc as dependencies, also we should specify version only here
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    implementation("org.jfrog.buildinfo:build-info-extractor-gradle:4.9.6")
    implementation("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.4")
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:0.9.18")
    implementation("com.moowork.gradle:gradle-node-plugin:1.3.1")
    implementation("org.openjfx:javafx-plugin:0.0.7")
}
