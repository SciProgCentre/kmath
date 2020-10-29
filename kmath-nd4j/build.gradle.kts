import ru.mipt.npm.gradle.Maturity

plugins {
    id("ru.mipt.npm.jvm")
}

dependencies {
    api(project(":kmath-core"))
    api("org.nd4j:nd4j-api:1.0.0-beta7")
    testImplementation("org.deeplearning4j:deeplearning4j-core:1.0.0-beta7")
    testImplementation("org.nd4j:nd4j-native-platform:1.0.0-beta7")
    testImplementation("org.slf4j:slf4j-simple:1.7.30")
}

readme {
    description = "ND4J NDStructure implementation and according NDAlgebra classes"
    maturity = Maturity.EXPERIMENTAL
    propertyByTemplate("artifact", rootProject.file("docs/templates/ARTIFACT-TEMPLATE.md"))

    feature(
        id = "nd4jarraystructure",
        description = "NDStructure wrapper for INDArray",
        ref = "src/commonMain/kotlin/kscience/kmath/operations/Algebra.kt"
    )

    feature(
        id = "nd4jarrayrings",
        description = "Rings over Nd4jArrayStructure of Int and Long",
        ref = "src/commonMain/kotlin/kscience/kmath/structures/NDStructure.kt"
    )

    feature(
        id = "nd4jarrayfields",
        description = "Fields over Nd4jArrayStructure of Float and Double",
        ref = "src/commonMain/kotlin/kscience/kmath/structures/Buffers.kt"
    )
}
