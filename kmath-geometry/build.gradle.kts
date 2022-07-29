plugins {
    kotlin("multiplatform")
    id("space.kscience.gradle.common")
    id("space.kscience.gradle.native")
}

kotlin.sourceSets.commonMain {
    dependencies {
        api(projects.kmath.kmathComplex)
    }
}

readme {
    maturity = space.kscience.gradle.Maturity.PROTOTYPE
}
