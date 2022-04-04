plugins {
    kotlin("multiplatform")
    id("ru.mipt.npm.gradle.common")
//    id("ru.mipt.npm.gradle.native")
}

kotlin.sourceSets {
    all {
        with(languageSettings) {
            optIn("kotlinx.coroutines.InternalCoroutinesApi")
            optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
            optIn("kotlinx.coroutines.FlowPreview")
        }
    }

    commonMain {
        dependencies {
            api(project(":kmath-core"))
            api(project(":kmath-complex"))
            api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${ru.mipt.npm.gradle.KScienceVersions.coroutinesVersion}")
        }
    }
}

readme {
    maturity = ru.mipt.npm.gradle.Maturity.EXPERIMENTAL
}

// Testing multi-receiver!
tasks.withType<org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile> {
    enabled = false
}
