plugins {
    kotlin("multiplatform")
    id("ru.mipt.npm.gradle.common")
}

description = "Functions, integration and interpolation"

kotlin.sourceSets.commonMain {
    dependencies {
        api(project(":kmath-core"))
    }
}

readme {
    maturity = ru.mipt.npm.gradle.Maturity.EXPERIMENTAL
    propertyByTemplate("artifact", rootProject.file("docs/templates/ARTIFACT-TEMPLATE.md"))

    feature("piecewise", "src/commonMain/kotlin/space/kscience/kmath/functions/Piecewise.kt") {
        "Piecewise functions."
    }
    feature("polynomials", "src/commonMain/kotlin/space/kscience/kmath/functions/Polynomial.kt") {
        "Polynomial functions."
    }
    feature("linear interpolation", "src/commonMain/kotlin/space/kscience/kmath/interpolation/LinearInterpolator.kt") {
        "Linear XY interpolator."
    }
    feature("spline interpolation", "src/commonMain/kotlin/space/kscience/kmath/interpolation/SplineInterpolator.kt") {
        "Cubic spline XY interpolator."
    }
    feature("integration") {
        "Univariate and multivariate quadratures"
    }
}
