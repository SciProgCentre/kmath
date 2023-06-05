/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
rootProject.name = "kmath"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    val projectProperties = java.util.Properties()
    file("../gradle.properties").inputStream().use {
        projectProperties.load(it)
    }

    projectProperties.forEach { key, value ->
        extra.set(key.toString(), value)
    }


    val toolsVersion: String = projectProperties["toolsVersion"].toString()

    repositories {
        mavenLocal()
        maven("https://repo.kotlin.link")
        mavenCentral()
        gradlePluginPortal()
    }

    versionCatalogs {
        create("spclibs") {
            from("space.kscience:version-catalog:$toolsVersion")
        }
    }
}
