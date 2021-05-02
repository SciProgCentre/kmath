plugins {
    id("ru.mipt.npm.gradle.jvm")
    kotlin("jupyter.api")
}

dependencies {
    api(project(":kmath-ast"))
    api(project(":kmath-complex"))
    api(project(":kmath-for-real"))
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.3")
}

readme {
    maturity = ru.mipt.npm.gradle.Maturity.PROTOTYPE
}

kotlin.sourceSets.all {
    languageSettings.useExperimentalAnnotation("space.kscience.kmath.misc.UnstableKMathAPI")
}
