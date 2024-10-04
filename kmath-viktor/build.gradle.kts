plugins {
    id("space.kscience.gradle.mpp")
}

description = "Binding for https://github.com/JetBrains-Research/viktor"

kscience{
    jvm()
    jvmMain{
        api(project(":kmath-core"))
        api("org.jetbrains.bio:viktor:1.2.0")
    }
}

readme {
    maturity = space.kscience.gradle.Maturity.DEPRECATED
}
