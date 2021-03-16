plugins {
    kotlin("jvm")
    kotlin("jupyter.api")
    id("ru.mipt.npm.gradle.common")
}

dependencies {
    api(project(":kmath-ast"))
    api(project(":kmath-complex"))
    api(project(":kmath-for-real"))
}

kscience.useHtml()
readme.maturity = ru.mipt.npm.gradle.Maturity.PROTOTYPE

kotlin.sourceSets.all {
    languageSettings.useExperimentalAnnotation("space.kscience.kmath.misc.UnstableKMathAPI")
}
