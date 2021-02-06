plugins {
    id("ru.mipt.npm.mpp")
}

kotlin.sourceSets.commonMain {
    dependencies {
        api(project(":kmath-core"))
    }
}

readme {
    description = "Functions and interpolation"
    maturity = ru.mipt.npm.gradle.Maturity.PROTOTYPE
    propertyByTemplate("artifact", rootProject.file("docs/templates/ARTIFACT-TEMPLATE.md"))

    feature("piecewise", "src/commonMain/kotlin/kscience/kmath/functions/Piecewise.kt", "Piecewise functions.")
    feature("polynomials", "src/commonMain/kotlin/kscience/kmath/functions/Polynomial.kt", "Polynomial functions.")
    feature("linear interpolation", "src/commonMain/kotlin/kscience/kmath/interpolation/LinearInterpolator.kt", "Linear XY interpolator.")
    feature("spline interpolation", "src/commonMain/kotlin/kscience/kmath/interpolation/SplineInterpolator.kt", "Cubic spline XY interpolator.")
}