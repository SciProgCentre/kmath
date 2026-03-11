plugins {
    id("space.kscience.gradle.mpp")
}

kscience {
    jvm()
    js()
    native()
    wasmJs()

    dependencies {
        api(projects.kmathCore)
    }
}

description = "Functions, integration and interpolation"

dependencies {
    dokkaPlugin("org.jetbrains.dokka:mathjax-plugin:${spclibs.versions.dokka.get()}")
}

readme {
    maturity = space.kscience.gradle.Maturity.EXPERIMENTAL
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
