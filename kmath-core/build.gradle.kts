plugins {
    kotlin("multiplatform")
    id("ru.mipt.npm.gradle.common")
    id("ru.mipt.npm.gradle.native")
//    id("com.xcporter.metaview") version "0.0.5"
}

kotlin.sourceSets {
    filter { it.name.contains("test", true) }
        .map(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::languageSettings)
        .forEach {
            it.optIn("space.kscience.kmath.misc.PerformancePitfall")
            it.optIn("space.kscience.kmath.misc.UnstableKMathAPI")
        }

    commonMain {
        dependencies {
        }
    }
}
