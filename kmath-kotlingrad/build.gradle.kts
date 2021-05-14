plugins {
    kotlin("jvm")
    id("ru.mipt.npm.gradle.common")
}

dependencies {
    api("com.github.breandan:kaliningraph:0.1.6")
    api("com.github.breandan:kotlingrad:0.4.5")
    api(project(":kmath-ast"))
}

readme {
    description = "Functions, integration and interpolation"
    maturity = ru.mipt.npm.gradle.Maturity.EXPERIMENTAL
    propertyByTemplate("artifact", rootProject.file("docs/templates/ARTIFACT-TEMPLATE.md"))

    feature(
        "differentiable-mst-expression",
        "src/main/kotlin/space/kscience/kmath/kotlingrad/DifferentiableMstExpression.kt",
    ) {
        "MST based DifferentiableExpression."
    }

    feature(
        "differentiable-mst-expression",
        "src/main/kotlin/space/kscience/kmath/kotlingrad/DifferentiableMstExpression.kt",
    ) {
        "Conversions between Kotlin∇'s SFun and MST"
    }
}
