plugins {
    kotlin("jvm")
    id("ru.mipt.npm.gradle.common")
}

dependencies {
    api("com.github.breandan:kaliningraph:0.1.4")
    api("com.github.breandan:kotlingrad:0.4.5")
    api(project(":kmath-ast"))
}

readme {
    maturity = ru.mipt.npm.gradle.Maturity.PROTOTYPE
}
