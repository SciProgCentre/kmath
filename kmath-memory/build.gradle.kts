plugins { id("scientifik.mpp") }
kotlin.sourceSets.all { languageSettings.useExperimentalAnnotation("kotlin.contracts.ExperimentalContracts") }
