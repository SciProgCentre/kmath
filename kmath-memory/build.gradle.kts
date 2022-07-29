plugins {
    id("space.kscience.gradle.mpp")
    id("space.kscience.gradle.native")
}

readme {
    maturity = space.kscience.gradle.Maturity.DEVELOPMENT
    description = """
        An API and basic implementation for arranging objects in a continuous memory block.
    """.trimIndent()
}
