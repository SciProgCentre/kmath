plugins {
    id("space.kscience.gradle.mpp")
}

kscience {
    jvm()
    js()
    native()
    wasm()

    dependencies {
        api(projects.kmathCore)
    }
}

readme {
    maturity = space.kscience.gradle.Maturity.DEVELOPMENT
    description = """
        An API and basic implementation for arranging objects in a continuous memory block.
    """.trimIndent()
}

//rootProject.the<NodeJsRootExtension>().versions.webpack.version = "5.76.2"
//rootProject.the<NodeJsRootExtension>().nodeVersion = "20.8.0"
