plugins {
    id("space.kscience.gradle.mpp")
}

kscience{
    jvm()
    js()
    native()

    useContextReceivers()
    useSerialization()
    dependencies {
        api(projects.kmath.kmathGeometry)
    }
}

readme {
    description = "Path and trajectory optimization"
    maturity = space.kscience.gradle.Maturity.PROTOTYPE
    propertyByTemplate("artifact", rootProject.file("docs/templates/ARTIFACT-TEMPLATE.md"))
}
