plugins {
    id("space.kscience.gradle.mpp")
}

kscience {
    jvm()
    js()
    native()
//    wasm()
}

readme {
    maturity = space.kscience.gradle.Maturity.DEVELOPMENT
    description = """
        An API and basic implementation for arranging objects in a continuous memory block.
    """.trimIndent()
}
