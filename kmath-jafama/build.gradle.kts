plugins {
    id("space.kscience.gradle.jvm")
}

description = "Jafama integration module"

dependencies {
    api(project(":kmath-core"))
    api("net.jafama:jafama:2.3.2")
}

repositories {
    mavenCentral()
}

readme {
    maturity = space.kscience.gradle.Maturity.PROTOTYPE
    propertyByTemplate("artifact", rootProject.file("docs/templates/ARTIFACT-TEMPLATE.md"))

    feature("jafama-double", "src/main/kotlin/space/kscience/kmath/jafama/") {
        "Double ExtendedField implementations based on Jafama"
    }
}