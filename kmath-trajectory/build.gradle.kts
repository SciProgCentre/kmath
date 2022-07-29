plugins {
    kotlin("multiplatform")
    id("space.kscience.gradle.common")
    id("space.kscience.gradle.native")
}

kotlin.sourceSets.commonMain {
    dependencies {
        api(projects.kmath.kmathGeometry)
    }
}

readme {
    description = "Path and trajectory optimization"
    maturity = space.kscience.gradle.Maturity.PROTOTYPE
    propertyByTemplate("artifact", rootProject.file("docs/templates/ARTIFACT-TEMPLATE.md"))
}
