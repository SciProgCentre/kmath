plugins {
    id("ru.mipt.npm.gradle.mpp")
    id("ru.mipt.npm.gradle.native")
}

kotlin.sourceSets {
    commonMain {
        dependencies {
            api(projects.kmathCore)
            api(projects.kmathFunctions)
            api(projects.testUtilsFunctions)
            api(projects.kmathPolynomialX)
            api(kotlin("test"))
        }
    }
}
