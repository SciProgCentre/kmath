plugins {
    id("ru.mipt.npm.gradle.mpp")
    id("ru.mipt.npm.gradle.native")
}

kscience {
    useAtomic()
}

kotlin.sourceSets {
    commonMain {
        dependencies {
            api(project(":kmath-coroutines"))
        }
    }
}

readme {
    maturity = ru.mipt.npm.gradle.Maturity.EXPERIMENTAL
}
