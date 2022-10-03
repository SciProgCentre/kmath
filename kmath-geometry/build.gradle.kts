plugins {
    id("space.kscience.gradle.mpp")
}

kscience{
    native()
    withContextReceivers()
    dependencies{
        api(projects.kmath.kmathComplex)
    }
}

readme {
    maturity = space.kscience.gradle.Maturity.PROTOTYPE
}
