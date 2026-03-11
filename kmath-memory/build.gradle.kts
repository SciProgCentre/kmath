plugins {
    id("space.kscience.gradle.mpp")
}

kscience {
    jvm()
    js()
    native()
    wasmJs()

    dependencies {
        api(projects.kmathCore)
    }

    wasmJsMain{
        api(spclibs.kotlinx.browser)
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
