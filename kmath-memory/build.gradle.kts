plugins {
    id("ru.mipt.npm.mpp")
    id("ru.mipt.npm.native")
}

readme{
    description = """
        An API and basic implementation for arranging objects in a continous memory block.
    """.trimIndent()
    maturity = ru.mipt.npm.gradle.Maturity.DEVELOPMENT
}