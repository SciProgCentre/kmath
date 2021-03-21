import ru.mipt.npm.gradle.Maturity

plugins {
    id("ru.mipt.npm.gradle.mpp")
    id("ru.mipt.npm.gradle.native")
}

kotlin.sourceSets {
    commonMain {
        dependencies {
            api(project(":kmath-memory"))
        }
    }
}

readme {
    description = "Core classes, algebra definitions, basic linear algebra"
    maturity = Maturity.DEVELOPMENT
    propertyByTemplate("artifact", rootProject.file("docs/templates/ARTIFACT-TEMPLATE.md"))

    feature(
        id = "algebras",
        description = """
            Algebraic structures like rings, spaces and fields.
        """.trimIndent(),
        ref = "src/commonMain/kotlin/space/kscience/kmath/operations/Algebra.kt"
    )

    feature(
        id = "nd",
        description = "Many-dimensional structures and operations on them.",
        ref = "src/commonMain/kotlin/space/kscience/kmath/structures/StructureND.kt"
    )

    feature(
        id = "linear",
        description = """
            Basic linear algebra operations (sums, products, etc.), backed by the `Space` API. Advanced linear algebra operations like matrix inversion and LU decomposition.
        """.trimIndent(),
        ref = "src/commonMain/kotlin/space/kscience/kmath/operations/Algebra.kt"
    )

    feature(
        id = "buffers",
        description = "One-dimensional structure",
        ref = "src/commonMain/kotlin/space/kscience/kmath/structures/Buffers.kt"
    )

    feature(
        id = "expressions",
        description = """
            By writing a single mathematical expression once, users will be able to apply different types of 
            objects to the expression by providing a context. Expressions can be used for a wide variety of purposes from high 
            performance calculations to code generation.
        """.trimIndent(),
        ref = "src/commonMain/kotlin/space/kscience/kmath/expressions"
    )

    feature(
        id = "domains",
        description = "Domains",
        ref = "src/commonMain/kotlin/space/kscience/kmath/domains"
    )

    feature(
        id = "autodif",
        description = "Automatic differentiation",
        ref = "src/commonMain/kotlin/space/kscience/kmath/expressions/SimpleAutoDiff.kt"
    )
}
