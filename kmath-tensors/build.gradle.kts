plugins {
    id("ru.mipt.npm.gradle.mpp")
}

kotlin.sourceSets {
    commonMain {
        dependencies {
            api(project(":kmath-core"))
            api(project(":kmath-stat"))
        }
    }
}

readme {
    maturity = ru.mipt.npm.gradle.Maturity.EXPERIMENTAL
}