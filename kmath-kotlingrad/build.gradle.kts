plugins {
    id("ru.mipt.npm.jvm")
}

dependencies {
    implementation("com.github.breandan:kaliningraph:0.1.4")
    implementation("com.github.breandan:kotlingrad:0.4.0")
    api(project(":kmath-ast"))
}

readme{
    maturity = ru.mipt.npm.gradle.Maturity.PROTOTYPE
}