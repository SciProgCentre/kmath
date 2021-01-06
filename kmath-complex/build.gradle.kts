plugins {
    id("ru.mipt.npm.mpp")
    id("ru.mipt.npm.native")
}

kotlin.sourceSets.commonMain {
    dependencies {
        api(project(":kmath-core"))
    }
}

readme {
    description = "Complex numbers and quaternions."
    maturity = ru.mipt.npm.gradle.Maturity.DEVELOPMENT
    propertyByTemplate("artifact", rootProject.file("docs/templates/ARTIFACT-TEMPLATE.md"))

    feature(
        id = "complex",
        description = "Complex Numbers",
        ref = "src/commonMain/kotlin/kscience/kmath/operations/Algebra.kt"
    )

    feature(
        id = "quaternion",
        description = "Quaternions",
        ref = "src/commonMain/kotlin/kscience/kmath/structures/NDStructure.kt"
    )
}
