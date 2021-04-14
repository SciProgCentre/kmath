plugins {
    kotlin("jvm")
    id("ru.mipt.npm.gradle.common")
}
description = "Commons math binding for kmath"

dependencies {
    api(project(":kmath-core"))
    api(project(":kmath-complex"))
    api(project(":kmath-coroutines"))
    api(project(":kmath-stat"))
    api(project(":kmath-functions"))
    api("org.apache.commons:commons-math3:3.6.1")
}

readme {
    maturity = ru.mipt.npm.gradle.Maturity.EXPERIMENTAL
}