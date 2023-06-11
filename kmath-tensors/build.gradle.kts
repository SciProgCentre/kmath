plugins {
    id("space.kscience.gradle.mpp")
}

kscience{
    jvm()
    js()
    native()

    dependencies {
        api(projects.kmathCore)
        api(projects.kmathStat)
    }
}

kotlin.sourceSets {

    commonMain {
        dependencies {
            api(project(":kmath-core"))
            api(project(":kmath-stat"))
        }
    }

    commonTest{
        dependencies{
            implementation(projects.testUtils)
        }
    }
}

readme {
    maturity = space.kscience.gradle.Maturity.PROTOTYPE
    propertyByTemplate("artifact", rootProject.file("docs/templates/ARTIFACT-TEMPLATE.md"))

    feature(
        id = "tensor algebra",
        ref = "src/commonMain/kotlin/space/kscience/kmath/tensors/api/TensorAlgebra.kt"
    ) { "Basic linear algebra operations on tensors (plus, dot, etc.)" }

    feature(
        id = "tensor algebra with broadcasting",
        ref = "src/commonMain/kotlin/space/kscience/kmath/tensors/core/BroadcastDoubleTensorAlgebra.kt"
    ) { "Basic linear algebra operations implemented with broadcasting." }

    feature(
        id = "linear algebra operations",
        ref = "src/commonMain/kotlin/space/kscience/kmath/tensors/api/LinearOpsTensorAlgebra.kt"
    ) { "Advanced linear algebra operations like LU decomposition, SVD, etc." }
}
