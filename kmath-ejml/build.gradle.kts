plugins {
    id("ru.mipt.npm.jvm")
}

dependencies {
    implementation("org.ejml:ejml-simple:0.39")
    implementation(project(":kmath-core"))
}
