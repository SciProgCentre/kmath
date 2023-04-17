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
                    this.webpackConfig.experiments.add("topLevelAwait")
                    useChromeHeadless()
                    useConfigDirectory(project.projectDir.resolve("karma.config.d").resolve("wasm"))
                }
            }
        }
    }

    wasmTest{
        dependencies {
            implementation(kotlin("test"))
        }
    }

    dependencies {
        api(projects.kmathCore)
    }

    testDependencies {
        implementation(projects.testUtils)
    }
}

readme {
    description = "Complex numbers and quaternions."
    maturity = space.kscience.gradle.Maturity.PROTOTYPE
    propertyByTemplate("artifact", rootProject.file("docs/templates/ARTIFACT-TEMPLATE.md"))

    feature(
        id = "complex",
        ref = "src/commonMain/kotlin/space/kscience/kmath/complex/Complex.kt"
    ) {
        "Complex numbers operations"
    }

    feature(
        id = "quaternion",
        ref = "src/commonMain/kotlin/space/kscience/kmath/complex/Quaternion.kt"
    ) {
        "Quaternions and their composition"
    }
}
