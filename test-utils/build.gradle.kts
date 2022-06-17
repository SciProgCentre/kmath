plugins {
    id("ru.mipt.npm.gradle.mpp")
    id("ru.mipt.npm.gradle.native")
}

kotlin.sourceSets {
    commonMain {
        dependencies {
            api(projects.kmath.kmathCore)
            api(kotlin("test"))
        }
    }
}
