plugins {
    kotlin("jvm")
    id("ru.mipt.npm.gradle.common")
}

kotlin.sourceSets
    .filter { it.name.contains("test", true) }
    .map(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::languageSettings)
    .forEach { it.optIn("space.kscience.kmath.misc.UnstableKMathAPI") }

description = "Kotlin∇ integration module"

dependencies {
    api("com.github.breandan:kaliningraph:0.1.6")
    api("com.github.breandan:kotlingrad:0.4.5")
    api(project(":kmath-core"))
    testImplementation(project(":kmath-ast"))
}

readme {
    maturity = ru.mipt.npm.gradle.Maturity.EXPERIMENTAL
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
