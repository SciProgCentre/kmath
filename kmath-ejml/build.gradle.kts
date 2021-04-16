/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

plugins {
    id("ru.mipt.npm.gradle.jvm")
}

dependencies {
    implementation("org.ejml:ejml-simple:0.39")
    implementation(project(":kmath-core"))
}

readme{
    maturity = ru.mipt.npm.gradle.Maturity.PROTOTYPE
}