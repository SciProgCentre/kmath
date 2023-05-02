plugins {
    id("space.kscience.gradle.mpp")
}

kscience{
    jvm()
    js()
    native()

    useContextReceivers()
    useSerialization()
    dependencies{
        api(projects.kmath.kmathComplex)
    }

    testDependencies {
        implementation(projects.testUtils)
    }

}

readme {
    maturity = space.kscience.gradle.Maturity.PROTOTYPE
}
