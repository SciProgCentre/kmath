plugins {
    id("space.kscience.gradle.mpp")
    id("space.kscience.gradle.native")
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
