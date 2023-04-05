plugins {
    id("space.kscience.gradle.mpp")
}

kscience {
    jvm()
    js()
    native()

    dependencies {
        api(projects.kmathCore)
    }

    testDependencies {
        implementation(projects.testUtils)
    }
}

readme {
    description = """
        Extension module that should be used to achieve numpy-like behavior.
        All operations are specialized to work with `Double` numbers without declaring algebraic contexts.
        One can still use generic algebras though.
        """.trimIndent()
    maturity = space.kscience.gradle.Maturity.EXPERIMENTAL
    propertyByTemplate("artifact", rootProject.file("docs/templates/ARTIFACT-TEMPLATE.md"))

    feature(
        id = "DoubleVector",
        ref = "src/commonMain/kotlin/space/kscience/kmath/real/DoubleVector.kt"
    ) {
        "Numpy-like operations for Buffers/Points"
    }

    feature(
        id = "DoubleMatrix",
        ref = "src/commonMain/kotlin/space/kscience/kmath/real/DoubleMatrix.kt"
    ) {
        "Numpy-like operations for 2d real structures"
    }

    feature(
        id = "grids",
        ref = "src/commonMain/kotlin/space/kscience/kmath/structures/grids.kt"
    ) {
        "Uniform grid generators"
    }
}
