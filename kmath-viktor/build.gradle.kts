/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

plugins {
    id("ru.mipt.npm.gradle.jvm")
}

description = "Binding for https://github.com/JetBrains-Research/viktor"

dependencies {
    api(project(":kmath-core"))
    api("org.jetbrains.bio:viktor:1.0.1")
}

readme{
    maturity = ru.mipt.npm.gradle.Maturity.DEVELOPMENT
}