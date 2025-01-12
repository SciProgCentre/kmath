plugins {
    id("space.kscience.gradle.mpp")
}

description = "Commons math binding for kmath"

kscience {
    jvm()
    jvmMain {
        api(projects.kmathCore)
        api(projects.kmathComplex)
        api(projects.kmathCoroutines)
        api(projects.kmathOptimization)
        api(projects.kmathStat)
        api(projects.kmathFunctions)
        api(libs.commons.math)
    }
}

readme {
    maturity = space.kscience.gradle.Maturity.EXPERIMENTAL
}