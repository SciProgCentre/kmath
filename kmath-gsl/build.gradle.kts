@file:Suppress("UNUSED_VARIABLE")

import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id("ru.mipt.npm.native")
}

kotlin {
    targets.withType<KotlinNativeTarget> {
        compilations["main"].cinterops {
            val libgsl by creating {
                defFile("src/nativeInterop/cinterop/libgsl.def")
                includeDirs { allHeaders("./src/nativeMain/resources/") }
            }
        }
    }

    sourceSets.commonMain {
        dependencies {
            api(project(":kmath-core"))
        }
    }
}
