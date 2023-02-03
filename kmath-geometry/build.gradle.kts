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
}

readme {
    maturity = space.kscience.gradle.Maturity.PROTOTYPE
}
