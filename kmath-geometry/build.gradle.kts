plugins {
    id("space.kscience.gradle.mpp")
}

kscience {
    jvm()
    js()
    native()
    wasmJs()

    useContextParameters()
    useSerialization()
    dependencies {
        api(projects.kmath.kmathComplex)
    }

    testDependencies {
        implementation(projects.testUtils)
    }

}

readme {
    maturity = space.kscience.gradle.Maturity.PROTOTYPE
}
