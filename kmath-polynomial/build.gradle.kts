plugins {
    kotlin("multiplatform")
    id("ru.mipt.npm.gradle.common")
    id("ru.mipt.npm.gradle.native")
}

description = "Polynomial extra utilities and rational functions"

kotlin.sourceSets {
    commonMain {
        dependencies {
            api(projects.kmathCore)
        }
    }
}

dependencies {
    dokkaPlugin("org.jetbrains.dokka:mathjax-plugin:${versionCatalogs.named("npmlibs").findVersion("dokka").get().requiredVersion}")
}

readme {
    maturity = ru.mipt.npm.gradle.Maturity.PROTOTYPE
    propertyByTemplate("artifact", rootProject.file("docs/templates/ARTIFACT-TEMPLATE.md"))

    feature("polynomial abstraction", "src/commonMain/kotlin/space/kscience/kmath/functions/Polynomial.kt") {
        "Abstraction for polynomial spaces."
    }
    feature("rational function abstraction", "src/commonMain/kotlin/space/kscience/kmath/functions/RationalFunction.kt") {
        "Abstraction for rational functions spaces."
    }
    feature("\"list\" polynomials", "src/commonMain/kotlin/space/kscience/kmath/functions/ListRationalFunction.kt") {
        "List implementation of univariate polynomials."
    }
    feature("\"list\" rational functions", "src/commonMain/kotlin/space/kscience/kmath/functions/ListPolynomial.kt") {
        "List implementation of univariate rational functions."
    }
    feature("\"list\" polynomials and rational functions constructors", "src/commonMain/kotlin/space/kscience/kmath/functions/listConstructors.kt") {
        "Constructors for list polynomials and rational functions."
    }
    feature("\"list\" polynomials and rational functions utilities", "src/commonMain/kotlin/space/kscience/kmath/functions/listUtil.kt") {
        "Utilities for list polynomials and rational functions."
    }
    feature("\"numbered\" polynomials", "src/commonMain/kotlin/space/kscience/kmath/functions/NumberedRationalFunction.kt") {
        "Numbered implementation of multivariate polynomials."
    }
    feature("\"numbered\" rational functions", "src/commonMain/kotlin/space/kscience/kmath/functions/NumberedPolynomial.kt") {
        "Numbered implementation of multivariate rational functions."
    }
    feature("\"numbered\" polynomials and rational functions constructors", "src/commonMain/kotlin/space/kscience/kmath/functions/numberedConstructors.kt") {
        "Constructors for numbered polynomials and rational functions."
    }
    feature("\"numbered\" polynomials and rational functions utilities", "src/commonMain/kotlin/space/kscience/kmath/functions/numberedUtil.kt") {
        "Utilities for numbered polynomials and rational functions."
    }
    feature("\"labeled\" polynomials", "src/commonMain/kotlin/space/kscience/kmath/functions/LabeledRationalFunction.kt") {
        "Labeled implementation of multivariate polynomials."
    }
    feature("\"labeled\" rational functions", "src/commonMain/kotlin/space/kscience/kmath/functions/LabeledPolynomial.kt") {
        "Labeled implementation of multivariate rational functions."
    }
    feature("\"labeled\" polynomials and rational functions constructors", "src/commonMain/kotlin/space/kscience/kmath/functions/labeledConstructors.kt") {
        "Constructors for labeled polynomials and rational functions."
    }
    feature("\"labeled\" polynomials and rational functions utilities", "src/commonMain/kotlin/space/kscience/kmath/functions/labeledUtil.kt") {
        "Utilities for labeled polynomials and rational functions."
    }
}
