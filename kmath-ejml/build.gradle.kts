plugins {
    id("space.kscience.gradle.jvm")
}

val ejmlVerision = "0.43.1"

dependencies {
    api("org.ejml:ejml-all:$ejmlVerision")
    api(projects.kmathCore)
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

//kotlin.sourceSets.main {
//    val codegen by tasks.creating {
//        ejmlCodegen(kotlin.srcDirs.first().absolutePath + "/space/kscience/kmath/ejml/_generated.kt")
//    }
//
//    kotlin.srcDirs(files().builtBy(codegen))
//}
