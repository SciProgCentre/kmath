plugins {
    id("space.kscience.gradle.mpp")
}

description = "Ojalgo bindings for kmath"

kscience {
    jvm()
    jvmMain {
        api(projects.kmathCore)
//        api(projects.kmathComplex)
//        api(projects.kmathCoroutines)
//        api(projects.kmathOptimization)
//        api(projects.kmathStat)
//        api(projects.kmathFunctions)
        api(libs.ojalgo)
    }

    jvmTest{
        implementation(projects.testUtils)
    }
}

readme {
    maturity = space.kscience.gradle.Maturity.PROTOTYPE
}