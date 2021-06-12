plugins {
    id("ru.mipt.npm.gradle.jvm")
}

dependencies {
    api(project(":kmath-core"))
    api("net.jafama:jafama:2.3.2")
}

repositories {
    mavenCentral()
}

readme {
    maturity = ru.mipt.npm.gradle.Maturity.PROTOTYPE
}

kotlin.sourceSets.all {
    languageSettings.useExperimentalAnnotation("space.kscience.kmath.misc.UnstableKMathAPI")
}
