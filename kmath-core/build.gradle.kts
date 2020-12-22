plugins {
    id("ru.mipt.npm.mpp")
    id("ru.mipt.npm.native")
}

kotlin.sourceSets.commonMain {
    dependencies {
        api(project(":kmath-memory"))
    }
}

readme {
    description = "Core classes, algebra definitions, basic linear algebra"
    maturity = ru.mipt.npm.gradle.Maturity.DEVELOPMENT
    propertyByTemplate("artifact", rootProject.file("docs/templates/ARTIFACT-TEMPLATE.md"))

    feature(
        id = "algebras",
        description = "Algebraic structures: contexts and elements",
        ref = "src/commonMain/kotlin/kscience/kmath/operations/Algebra.kt"
    )

    feature(
        id = "nd",
        description = "Many-dimensional structures",
        ref = "src/commonMain/kotlin/kscience/kmath/structures/NDStructure.kt"
    )

    feature(
        id = "buffers",
        description = "One-dimensional structure",
        ref = "src/commonMain/kotlin/kscience/kmath/structures/Buffers.kt"
    )

    feature(
        id = "expressions",
        description = "Functional Expressions",
        ref = "src/commonMain/kotlin/kscience/kmath/expressions"
    )

    feature(
        id = "domains",
        description = "Domains",
        ref = "src/commonMain/kotlin/kscience/kmath/domains"
    )

    feature(
        id = "autodif",
        description = "Automatic differentiation",
        ref = "src/commonMain/kotlin/kscience/kmath/expressions/SimpleAutoDiff.kt"
    )
}
