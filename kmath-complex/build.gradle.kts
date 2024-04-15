plugins {
    id("space.kscience.gradle.mpp")
}

kscience {
    jvm()
    js()
    native()
    wasm()

    dependencies {
        api(projects.kmathCore)
        api(projects.kmathMemory)
    }

    testDependencies {
        implementation(projects.testUtils)
    }
}

readme {
    description = "Complex numbers and quaternions."
    maturity = space.kscience.gradle.Maturity.PROTOTYPE
    propertyByTemplate("artifact", rootProject.file("docs/templates/ARTIFACT-TEMPLATE.md"))

    feature(
        id = "complex",
        ref = "src/commonMain/kotlin/space/kscience/kmath/complex/Complex.kt"
    ) {
        "Complex numbers operations"
    }

    feature(
        id = "quaternion",
        ref = "src/commonMain/kotlin/space/kscience/kmath/complex/Quaternion.kt"
    ) {
        "Quaternions and their composition"
    }
}
