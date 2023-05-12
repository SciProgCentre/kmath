plugins {
    id("space.kscience.gradle.mpp")
}

kscience{
    jvm()
    js()
    native()
    wasm{
        browser {
            testTask {
                useKarma {
                    webpackConfig.experiments.add("topLevelAwait")
                    useChromeHeadless()
                }
            }
        }
    }

    wasmTest{
        dependencies {
            implementation(kotlin("test"))
        }
    }

    dependencies {
        api(projects.kmathMemory)
    }

    testDependencies {
        implementation(projects.testUtils)
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
    ) { "Basic linear algebra operations (sums, products, etc.), backed by the `Space` API. Advanced linear algebra operations like matrix inversion and LU decomposition." }

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
}
