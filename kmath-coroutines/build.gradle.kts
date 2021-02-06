plugins { id("ru.mipt.npm.mpp") }

kotlin.sourceSets {
    all {
        with(languageSettings) {
            useExperimentalAnnotation("kotlinx.coroutines.InternalCoroutinesApi")
            useExperimentalAnnotation("kotlinx.coroutines.ExperimentalCoroutinesApi")
            useExperimentalAnnotation("kotlinx.coroutines.FlowPreview")
        }
    }

    commonMain {
        dependencies {
            api(project(":kmath-core"))
            api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${ru.mipt.npm.gradle.KScienceVersions.coroutinesVersion}")
        }
    }
}

readme{
    maturity = ru.mipt.npm.gradle.Maturity.EXPERIMENTAL
}