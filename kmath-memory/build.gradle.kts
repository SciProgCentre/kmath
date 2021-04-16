/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

plugins {
    kotlin("multiplatform")
    id("ru.mipt.npm.gradle.common")
    id("ru.mipt.npm.gradle.native")
}

readme {
    maturity = ru.mipt.npm.gradle.Maturity.DEVELOPMENT
    description = """
        An API and basic implementation for arranging objects in a continous memory block.
    """.trimIndent()
}