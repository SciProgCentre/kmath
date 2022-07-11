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
            api(projects.kmathFunctions)
        }
    }
    commonTest {
        dependencies {
            api(projects.testUtilsFunctions)
            api(projects.testUtilsPolynomialX)
            api(kotlin("test"))
        }
    }
}

readme {
    maturity = ru.mipt.npm.gradle.Maturity.PROTOTYPE
    propertyByTemplate("artifact", rootProject.file("docs/templates/ARTIFACT-TEMPLATE.md"))

//    feature("TODO") { "TODO" }
}
