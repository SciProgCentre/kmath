plugins {
    kotlin("multiplatform")
    id("ru.mipt.npm.gradle.common")
    id("ru.mipt.npm.gradle.native")
}

readme {
    maturity = ru.mipt.npm.gradle.Maturity.DEVELOPMENT
    description = """
        An API and basic implementation for arranging objects in a continuous memory block.
    """.trimIndent()
}

tasks.jvmTest {
    jvmArgs("--add-modules", "jdk.incubator.foreign")
}
