plugins { id("scientifik.jvm") }
description = "Commons math binding for kmath"

dependencies {
    api(project(":kmath-core"))
    api(project(":kmath-coroutines"))
    api(project(":kmath-prob"))
    api(project(":kmath-functions"))
    api("org.apache.commons:commons-math3:3.6.1")
}

kotlin.sourceSets.all { languageSettings.useExperimentalAnnotation("kotlin.contracts.ExperimentalContracts") }
