plugins {
    id("ru.mipt.npm.gradle.mpp")
}

kotlin.sourceSets {
    all {
        languageSettings.useExperimentalAnnotation("space.kscience.kmath.misc.UnstableKMathAPI")
    }
    commonMain {
        dependencies {
            api(project(":kmath-core"))
            implementation(project(":kmath-stat"))
        }
    }
}

tasks.dokkaHtml {
    dependsOn(tasks.build)
}

readme {
    maturity = ru.mipt.npm.gradle.Maturity.PROTOTYPE
    propertyByTemplate("artifact", rootProject.file("docs/templates/ARTIFACT-TEMPLATE.md"))

    feature(
        id = "tensor algebra",
        description = "Basic linear algebra operations on tensors (plus, dot, etc.)",
        ref = "src/commonMain/kotlin/space/kscience/kmath/tensors/api/TensorAlgebra.kt"
    )

    feature(
        id = "tensor algebra with broadcasting",
        description = "Basic linear algebra operations implemented with broadcasting.",
        ref = "src/commonMain/kotlin/space/kscience/kmath/tensors/core/algebras/BroadcastDoubleTensorAlgebra.kt"
    )

    feature(
        id = "linear algebra operations",
        description = "Advanced linear algebra operations like LU decomposition, SVD, etc.",
        ref = "src/commonMain/kotlin/space/kscience/kmath/tensors/api/LinearOpsTensorAlgebra.kt"
    )

}