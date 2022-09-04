plugins {
    id("space.kscience.gradle.mpp")
}

kscience{
    native()
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
