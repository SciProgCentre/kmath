plugins {
    id("space.kscience.gradle.mpp")
}

kscience{
    native()
}

kotlin.sourceSets.commonMain {
    dependencies {
        api(projects.kmath.kmathComplex)
    }
}

kscience {
    withContextReceivers()
}

readme {
    maturity = space.kscience.gradle.Maturity.PROTOTYPE
}
