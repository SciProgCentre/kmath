/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

plugins {
    id("ru.mipt.npm.gradle.jvm")
}

dependencies {
    implementation("com.github.breandan:kaliningraph:0.1.4")
    implementation("com.github.breandan:kotlingrad:0.4.0")
    api(project(":kmath-ast"))
}

readme{
    maturity = ru.mipt.npm.gradle.Maturity.PROTOTYPE
}