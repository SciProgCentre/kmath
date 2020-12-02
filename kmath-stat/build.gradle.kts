plugins {
    id("ru.mipt.npm.mpp")
}

kotlin.sourceSets {
    all {
        with(languageSettings) {
            useExperimentalAnnotation("kotlinx.coroutines.FlowPreview")
            useExperimentalAnnotation("kotlinx.coroutines.ExperimentalCoroutinesApi")
            useExperimentalAnnotation("kotlinx.coroutines.ObsoleteCoroutinesApi")
        }
    }

    commonMain {
        dependencies {
            api(project(":kmath-coroutines"))
        }
    }

    jvmMain {
        dependencies {
            api("org.apache.commons:commons-rng-sampling:1.3")
            api("org.apache.commons:commons-rng-simple:1.3")
        }
    }
}
