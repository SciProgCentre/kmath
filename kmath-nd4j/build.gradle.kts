plugins {
    id("space.kscience.gradle.mpp")
}

description = "ND4J NDStructure implementation and according NDAlgebra classes"


kscience {
    jvm()

    jvmMain {
        api(project(":kmath-tensors"))
        api("org.nd4j:nd4j-api:1.0.0-M2.1")
    }

    jvmTest{
        implementation("org.nd4j:nd4j-native-platform:1.0.0-M1")
    }
}

readme {
    maturity = space.kscience.gradle.Maturity.DEPRECATED
    propertyByTemplate("artifact", rootProject.file("docs/templates/ARTIFACT-TEMPLATE.md"))
    feature(id = "nd4jarraystructure") { "NDStructure wrapper for INDArray" }
    feature(id = "nd4jarrayrings") { "Rings over Nd4jArrayStructure of Int and Long" }
    feature(id = "nd4jarrayfields") { "Fields over Nd4jArrayStructure of Float and Double" }
}
