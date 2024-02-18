plugins {
    id("space.kscience.gradle.mpp")
}

version = "0.1.0"

kscience {
    jvm()
    js()
    native()
    wasm()
}

readme {
    maturity = space.kscience.gradle.Maturity.DEVELOPMENT
    description = """
        An API and basic implementation for arranging objects in a continuous memory block.
    """.trimIndent()
}
