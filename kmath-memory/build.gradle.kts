plugins {
    id("space.kscience.gradle.mpp")
}

kscience {
    jvm()
    js()
    native()
    wasm{
        browser {
            testTask {
                useKarma {
                    webpackConfig.experiments.add("topLevelAwait")
                    useChromeHeadless()
                }
            }
        }
    }

    wasmTest{
        dependencies {
            implementation(kotlin("test"))
        }
    }
}

readme {
    maturity = space.kscience.gradle.Maturity.DEVELOPMENT
    description = """
        An API and basic implementation for arranging objects in a continuous memory block.
    """.trimIndent()
}
