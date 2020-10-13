import ru.mipt.npm.gradle.KSciencePublishPlugin

plugins {
    id("ru.mipt.npm.project")
    id("ru.mipt.npm.publish") apply false
}

private val kmathVersion: String by extra("0.2.0-dev-2")
private val bintrayRepo: String by extra("kscience")
private val githubProject: String by extra("kmath")

allprojects {
    repositories {
        jcenter()
        maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
        maven(url = "https://dl.bintray.com/kotlin/kotlinx")
        maven(url = "https://dl.bintray.com/hotkeytlt/maven")
    }

    group = "kscience.kmath"
    version = kmathVersion
}

subprojects {
    if (name.startsWith("kmath")) apply<KSciencePublishPlugin>()

    ksciencePublish {
        spaceRepo = "https://maven.pkg.jetbrains.space/mipt-npm/p/sci/maven"
        spaceUser = System.getenv("SPACE_USER")
        spaceToken = System.getenv("SPACE_TOKEN")
    }
}

readme {
    readmeTemplate = file("docs/templates/README-TEMPLATE.md")
}
