/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

plugins {
    id("space.kscience.gradle.mpp")
}

description = "Symja integration module"

kscience{
    jvm()

    jvmMain {
        api("org.matheclipse:matheclipse-core:3.0.0")

        api(project(":kmath-core"))
    }


}


readme {
    maturity = space.kscience.gradle.Maturity.PROTOTYPE
}
