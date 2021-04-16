/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

import ru.mipt.npm.gradle.Maturity

plugins {
    kotlin("jvm")
    id("ru.mipt.npm.gradle.common")
}

dependencies {
    api("org.ejml:ejml-simple:0.40")
    api(project(":kmath-core"))
}

readme {
    maturity = Maturity.PROTOTYPE
    propertyByTemplate("artifact", rootProject.file("docs/templates/ARTIFACT-TEMPLATE.md"))

    feature(
        id = "ejml-vector",
        description = "The Point implementation using SimpleMatrix.",
        ref = "src/main/kotlin/space/kscience/kmath/ejml/EjmlVector.kt"
    )

    feature(
        id = "ejml-matrix",
        description = "The Matrix implementation using SimpleMatrix.",
        ref = "src/main/kotlin/space/kscience/kmath/ejml/EjmlMatrix.kt"
    )

    feature(
        id = "ejml-linear-space",
        description = "The LinearSpace implementation using SimpleMatrix.",
        ref = "src/main/kotlin/space/kscience/kmath/ejml/EjmlLinearSpace.kt"
    )
}
