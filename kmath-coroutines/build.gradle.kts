/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

import ru.mipt.npm.gradle.Maturity

plugins {
    kotlin("multiplatform")
    id("ru.mipt.npm.gradle.common")
}

kotlin.sourceSets {
    all {
        with(languageSettings) {
            useExperimentalAnnotation("kotlinx.coroutines.InternalCoroutinesApi")
            useExperimentalAnnotation("kotlinx.coroutines.ExperimentalCoroutinesApi")
            useExperimentalAnnotation("kotlinx.coroutines.FlowPreview")
        }
    }

    commonMain {
        dependencies {
            api(project(":kmath-core"))
            api(project(":kmath-complex"))
            api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${ru.mipt.npm.gradle.KScienceVersions.coroutinesVersion}")
        }
    }
}

readme {
    maturity = Maturity.EXPERIMENTAL
}