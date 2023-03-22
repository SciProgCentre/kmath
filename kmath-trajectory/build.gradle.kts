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
    description = "Path and trajectory optimization (to be moved to a separate project)"
    maturity = space.kscience.gradle.Maturity.DEPRECATED
    propertyByTemplate("artifact", rootProject.file("docs/templates/ARTIFACT-TEMPLATE.md"))
}
