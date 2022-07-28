plugins {
    kotlin("multiplatform")
    id("ru.mipt.npm.gradle.common")
    id("ru.mipt.npm.gradle.native")
}

kotlin.sourceSets.commonMain {
    dependencies {
        api(projects.kmath.kmathGeometry)
    }
}

readme {
    description = "Path and trajectory optimization"
    maturity = ru.mipt.npm.gradle.Maturity.PROTOTYPE
    propertyByTemplate("artifact", rootProject.file("docs/templates/ARTIFACT-TEMPLATE.md"))
}
