plugins {
    id("space.kscience.gradle.mpp")
    id("space.kscience.gradle.native")
}

kotlin.sourceSets {
    all {
        languageSettings.optIn("space.kscience.kmath.misc.UnstableKMathAPI")
    }

    commonMain {
        dependencies {
            api(project(":kmath-coroutines"))
            api(npmlibs.atomicfu)
        }
    }
}

readme {
    maturity = space.kscience.gradle.Maturity.EXPERIMENTAL
}
