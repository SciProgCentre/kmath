import ru.mipt.npm.gradle.Maturity

plugins {
    id("ru.mipt.npm.gradle.jvm")
    kotlin("jupyter.api")
}

dependencies {
    api(project(":kmath-ast"))
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.3")
}

readme {
    maturity = Maturity.PROTOTYPE
}
