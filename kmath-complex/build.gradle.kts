plugins {
    kotlin("multiplatform")
    id("ru.mipt.npm.gradle.common")
    id("ru.mipt.npm.gradle.native")
}

kotlin.sourceSets {
    commonMain {
        dependencies {
            api(project(":kmath-core"))
        }
    }
}

readme {
    description = "Complex numbers and quaternions."
    maturity = ru.mipt.npm.gradle.Maturity.PROTOTYPE
    propertyByTemplate("artifact", rootProject.file("docs/templates/ARTIFACT-TEMPLATE.md"))

    feature(
        id = "complex",
        ref = "src/commonMain/kotlin/space/kscience/kmath/complex/Complex.kt"
    ){
        "Complex numbers operations"
    }

    feature(
        id = "quaternion",
        ref = "src/commonMain/kotlin/space/kscience/kmath/complex/Quaternion.kt"
    ){
        "Quaternions and their composition"
    }
}
