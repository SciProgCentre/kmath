plugins {
    id("scientifik.mpp")
    //id("scientifik.atomic")
}

kotlin.sourceSets {
    all {
        with(languageSettings) {
            useExperimentalAnnotation("kotlin.contracts.ExperimentalContracts")
            useExperimentalAnnotation("kotlinx.coroutines.InternalCoroutinesApi")
            useExperimentalAnnotation("kotlinx.coroutines.ExperimentalCoroutinesApi")
            useExperimentalAnnotation("kotlinx.coroutines.FlowPreview")
        }
    }

    commonMain {
        dependencies {
            api(project(":kmath-core"))
            api("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:${Scientifik.coroutinesVersion}")
        }
    }

    jvmMain {
        dependencies { api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Scientifik.coroutinesVersion}") }
    }

    jsMain {
        dependencies { api("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:${Scientifik.coroutinesVersion}") }
    }
}
