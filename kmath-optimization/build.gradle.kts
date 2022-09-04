plugins {
    id("space.kscience.gradle.mpp")
}

kscience{
    native()
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
