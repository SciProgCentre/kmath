plugins {
    id("ru.mipt.npm.gradle.mpp")
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

    feature("piecewise", "src/commonMain/kotlin/space/kscience/kmath/functions/Piecewise.kt", "Piecewise functions.")
    feature("polynomials", "src/commonMain/kotlin/space/kscience/kmath/functions/Polynomial.kt", "Polynomial functions.")
    feature("linear interpolation",
        "src/commonMain/kotlin/space/kscience/kmath/interpolation/LinearInterpolator.kt",
        "Linear XY interpolator.")
    feature("spline interpolation",
        "src/commonMain/kotlin/space/kscience/kmath/interpolation/SplineInterpolator.kt",
        "Cubic spline XY interpolator.")
}