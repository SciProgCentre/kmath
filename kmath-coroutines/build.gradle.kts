plugins {
    id("space.kscience.gradle.mpp")
}

kscience{
    native()
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
            api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${space.kscience.gradle.KScienceVersions.coroutinesVersion}")
        }
    }
}

readme {
    maturity = space.kscience.gradle.Maturity.EXPERIMENTAL
}