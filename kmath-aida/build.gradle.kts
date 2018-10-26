plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
    maven ("http://dl.bintray.com/kotlin/kotlin-eap")
    maven("http://java.freehep.org/maven2/")
}

dependencies {
    implementation(kotlin("stdlib"))
    api(project(":kmath-core"))
    api(group = "org.freehep", name = "freehep-jaida", version = "3.4.13")
}