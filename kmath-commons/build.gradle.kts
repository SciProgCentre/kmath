plugins {
    id("space.kscience.gradle.jvm")
}

description = "Commons math binding for kmath"

dependencies {
    api(project(":kmath-core"))
    api(project(":kmath-complex"))
    api(project(":kmath-coroutines"))
    api(project(":kmath-optimization"))
    api(project(":kmath-stat"))
    api(project(":kmath-functions"))
    api("org.apache.commons:commons-math3:3.6.1")
}

readme {
    maturity = space.kscience.gradle.Maturity.EXPERIMENTAL
}