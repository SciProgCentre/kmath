/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

plugins {
    id("ru.mipt.npm.gradle.mpp")
}

kscience {
    useAtomic()
}

kotlin.sourceSets {
    commonMain {
        dependencies {
            api(project(":kmath-core"))
        }
    }
    commonTest {
        dependencies {
            implementation(project(":kmath-for-real"))
        }
    }
}

readme {
    this.maturity = ru.mipt.npm.gradle.Maturity.PROTOTYPE
}
