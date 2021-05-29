/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

plugins {
    kotlin("jvm")
    id("ru.mipt.npm.gradle.common")
}

description = "Symja integration module"

dependencies {
    api("org.matheclipse:matheclipse-core:2.0.0-SNAPSHOT") {
        // Incorrect transitive dependency org.apfloat:apfloat:1.10.0-SNAPSHOT
        exclude("org.apfloat", "apfloat")
    }

    // Replace for org.apfloat:apfloat:1.10.0-SNAPSHOT
    api("org.apfloat:apfloat:1.10.0")

    api(project(":kmath-core"))
    testImplementation("org.slf4j:slf4j-simple:1.7.30")
}

readme {
    maturity = ru.mipt.npm.gradle.Maturity.PROTOTYPE
}
