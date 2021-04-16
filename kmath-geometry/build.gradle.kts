/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

import ru.mipt.npm.gradle.Maturity.PROTOTYPE

plugins {
    kotlin("multiplatform")
    id("ru.mipt.npm.gradle.common")
}

kotlin.sourceSets.commonMain {
    dependencies {
        api(project(":kmath-core"))
    }
}

readme {
    maturity = Maturity.PROTOTYPE
}
