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
