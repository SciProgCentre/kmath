plugins {
    id("ru.mipt.npm.gradle.jvm")
}

dependencies {
    implementation("org.ejml:ejml-simple:0.39")
    implementation(project(":kmath-core"))
}

readme{
    maturity = ru.mipt.npm.gradle.Maturity.PROTOTYPE
}