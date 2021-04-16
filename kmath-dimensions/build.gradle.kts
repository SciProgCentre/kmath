/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

plugins {
    kotlin("multiplatform")
    id("ru.mipt.npm.gradle.common")
    id("ru.mipt.npm.gradle.native")
}

description = "A proof of concept module for adding type-safe dimensions to structures"

kotlin.sourceSets {
    commonMain {
        dependencies {
            api(project(":kmath-core"))
        }
    }

    jvmMain {
        dependencies {
            api(kotlin("reflect"))
        }
    }
}

readme {
    maturity = ru.mipt.npm.gradle.Maturity.PROTOTYPE
}
