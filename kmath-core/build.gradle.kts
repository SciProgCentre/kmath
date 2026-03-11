plugins {
    id("space.kscience.gradle.mpp")
}

kscience {
    jvm()
    js()
    native()
    wasmJs()

    dependencies {
        api(libs.attributes)
    }

    testDependencies {
        implementation(projects.testUtils)
    }
}

kotlin.sourceSets {
    filter { it.name.contains("test", true) }
        .map(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::languageSettings)
        .forEach {
            it.optIn("space.kscience.kmath.PerformancePitfall")
            it.optIn("space.kscience.kmath.UnstableKMathAPI")
        }
}

readme {
    description = "Core classes, algebra definitions, basic linear algebra"
    maturity = space.kscience.gradle.Maturity.DEVELOPMENT
    propertyByTemplate("artifact", rootProject.file("docs/templates/ARTIFACT-TEMPLATE.md"))

    feature(
        id = "algebras",
        ref = "src/commonMain/kotlin/space/kscience/kmath/operations/Algebra.kt",
    ) { "Algebraic structures like rings, spaces and fields." }

    feature(
        id = "nd",
        ref = "src/commonMain/kotlin/space/kscience/kmath/structures/StructureND.kt",
    ) { "Many-dimensional structures and operations on them." }

    feature(
        id = "linear",
        ref = "src/commonMain/kotlin/space/kscience/kmath/operations/Algebra.kt",
    ) {
        """
            Basic linear algebra operations (sums, products, etc.), backed by the `Space` API. 
            Advanced linear algebra operations like matrix inversion and LU decomposition.
        """.trimIndent()
    }

    feature(
        id = "buffers",
        ref = "src/commonMain/kotlin/space/kscience/kmath/structures/Buffers.kt",
    ) { "One-dimensional structure" }

    feature(
        id = "expressions",
        ref = "src/commonMain/kotlin/space/kscience/kmath/expressions"
    ) {
        """
            By writing a single mathematical expression once, users will be able to apply different types of
            objects to the expression by providing a context. Expressions can be used for a wide variety of purposes from high 
            performance calculations to code generation.
        """.trimIndent()
    }

    feature(
        id = "domains",
        ref = "src/commonMain/kotlin/space/kscience/kmath/domains",
    ) { "Domains" }

    feature(
        id = "autodiff",
        ref = "src/commonMain/kotlin/space/kscience/kmath/expressions/SimpleAutoDiff.kt"
    ) { "Automatic differentiation" }

    feature(
        id = "Parallel linear algebra"
    ) {
        """
            Parallel implementation for `LinearAlgebra`
        """.trimIndent()
    }
}
