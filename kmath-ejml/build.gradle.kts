plugins {
    id("space.kscience.gradle.mpp")
}

val ejmlVerision = "0.43.1"

kscience {
    jvm()
    jvmMain {
        api(projects.kmathCore)
        api(projects.kmathComplex)
        api("org.ejml:ejml-all:$ejmlVerision")
    }

    jvmTest {
        implementation(projects.testUtils)
    }
}

readme {
    maturity = space.kscience.gradle.Maturity.PROTOTYPE
    propertyByTemplate("artifact", rootProject.file("docs/templates/ARTIFACT-TEMPLATE.md"))

    feature(
        id = "ejml-vector",
        ref = "src/main/kotlin/space/kscience/kmath/ejml/EjmlVector.kt"
    ) { "Point implementations." }

    feature(
        id = "ejml-matrix",
        ref = "src/main/kotlin/space/kscience/kmath/ejml/EjmlMatrix.kt"
    ) { "Matrix implementation." }

    feature(
        id = "ejml-linear-space",
        ref = "src/main/kotlin/space/kscience/kmath/ejml/EjmlLinearSpace.kt"
    ) { "LinearSpace implementations." }
}