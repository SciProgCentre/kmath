/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

plugins {
    id("ru.mipt.npm.gradle.mpp")
    id("ru.mipt.npm.gradle.native")
}

readme{
    description = """
        An API and basic implementation for arranging objects in a continous memory block.
    """.trimIndent()
    maturity = ru.mipt.npm.gradle.Maturity.DEVELOPMENT
}