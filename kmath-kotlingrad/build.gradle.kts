plugins {
    id("space.kscience.gradle.mpp")
}

kotlin.sourceSets
    .filter { it.name.contains("test", true) }
    .map(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::languageSettings)
    .forEach { it.optIn("space.kscience.kmath.UnstableKMathAPI") }

description = "Kotlin∇ integration module"

kscience{
    jvm()

    jvmMain{
        api("ai.hypergraph:kaliningraph:0.1.9")
        api("ai.hypergraph:kotlingrad:0.4.7")
        api(project(":kmath-core"))
    }

    jvmTest{
        implementation(project(":kmath-ast"))
    }
}

readme {
    maturity = space.kscience.gradle.Maturity.EXPERIMENTAL
    propertyByTemplate("artifact", rootProject.file("docs/templates/ARTIFACT-TEMPLATE.md"))

    feature(
        "differentiable-mst-expression",
        "src/jvmMain/kotlin/space/kscience/kmath/kotlingrad/KotlingradExpression.kt",
    ) {
        "MST based DifferentiableExpression."
    }

    feature(
        "scalars-adapters",
        "src/jvmMain/kotlin/space/kscience/kmath/kotlingrad/scalarsAdapters.kt",
    ) {
        "Conversions between Kotlin∇'s SFun and MST"
    }
}
