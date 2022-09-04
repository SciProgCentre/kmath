/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

plugins {
    id("space.kscience.gradle.jvm")
}

description = "Symja integration module"

dependencies {
    api("org.matheclipse:matheclipse-core:2.0.0-SNAPSHOT") {
        // Incorrect transitive dependencies
        exclude("org.apfloat", "apfloat")
        exclude("org.hipparchus", "hipparchus-clustering")
        exclude("org.hipparchus", "hipparchus-core")
        exclude("org.hipparchus", "hipparchus-fft")
        exclude("org.hipparchus", "hipparchus-fitting")
        exclude("org.hipparchus", "hipparchus-ode")
        exclude("org.hipparchus", "hipparchus-optim")
        exclude("org.hipparchus", "hipparchus-stat")
    }

    // Replaces for incorrect transitive dependencies
    api("org.apfloat:apfloat:1.10.0")
    api("org.hipparchus:hipparchus-clustering:1.8")
    api("org.hipparchus:hipparchus-core:1.8")
    api("org.hipparchus:hipparchus-fft:1.8")
    api("org.hipparchus:hipparchus-fitting:1.8")
    api("org.hipparchus:hipparchus-ode:1.8")
    api("org.hipparchus:hipparchus-optim:1.8")
    api("org.hipparchus:hipparchus-stat:1.8")

    api(project(":kmath-core"))
    testImplementation("org.slf4j:slf4j-simple:1.7.31")
}

readme {
    maturity = space.kscience.gradle.Maturity.PROTOTYPE
}
