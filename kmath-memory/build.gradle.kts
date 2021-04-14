plugins {
    kotlin("multiplatform")
    id("ru.mipt.npm.gradle.common")
    id("ru.mipt.npm.gradle.native")
}

readme {
    maturity = ru.mipt.npm.gradle.Maturity.DEVELOPMENT
    description = """
        An API and basic implementation for arranging objects in a continous memory block.
    """.trimIndent()
}