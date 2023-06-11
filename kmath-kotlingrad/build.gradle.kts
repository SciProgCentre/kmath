plugins {
    id("space.kscience.gradle.jvm")
}

kotlin.sourceSets
    .filter { it.name.contains("test", true) }
    .map(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::languageSettings)
    .forEach { it.optIn("space.kscience.kmath.UnstableKMathAPI") }

description = "Kotlin∇ integration module"

dependencies {
    api("ai.hypergraph:kaliningraph:0.1.9")
    api("ai.hypergraph:kotlingrad:0.4.7")
    api(project(":kmath-core"))
    testImplementation(project(":kmath-ast"))
}

readme {
    maturity = space.kscience.gradle.Maturity.EXPERIMENTAL
    propertyByTemplate("artifact", rootProject.file("docs/templates/ARTIFACT-TEMPLATE.md"))

    feature(
        "differentiable-mst-expression",
        "src/main/kotlin/space/kscience/kmath/kotlingrad/KotlingradExpression.kt",
    ) {
        "MST based DifferentiableExpression."
    }

    feature(
        "scalars-adapters",
        "src/main/kotlin/space/kscience/kmath/kotlingrad/scalarsAdapters.kt",
    ) {
        "Conversions between Kotlin∇'s SFun and MST"
    }
}
