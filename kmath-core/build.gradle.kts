plugins { id("scientifik.mpp") }

kotlin.sourceSets {
    all { languageSettings.useExperimentalAnnotation("kotlin.contracts.ExperimentalContracts") }
    commonMain { dependencies { api(project(":kmath-memory")) } }
}
