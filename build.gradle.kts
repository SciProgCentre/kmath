import ru.mipt.npm.gradle.KSciencePublishPlugin

plugins {
    id("ru.mipt.npm.project")
}

internal val kmathVersion: String by extra("0.2.0-dev-4")
internal val bintrayRepo: String by extra("kscience")
internal val githubProject: String by extra("kmath")

allprojects {
    repositories {
        jcenter()
        maven("https://clojars.org/repo")
        maven("https://dl.bintray.com/egor-bogomolov/astminer/")
        maven("https://dl.bintray.com/hotkeytlt/maven")
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
        maven("https://dl.bintray.com/kotlin/kotlinx")
        maven("https://dl.bintray.com/mipt-npm/dev")
        maven("https://dl.bintray.com/mipt-npm/kscience")
        maven("https://jitpack.io")
        maven("http://logicrunch.research.it.uu.se/maven/")
        mavenCentral()
    }

    group = "kscience.kmath"
    version = kmathVersion
}

subprojects {
    if (name.startsWith("kmath")) apply<KSciencePublishPlugin>()
}

readme {
    readmeTemplate = file("docs/templates/README-TEMPLATE.md")
}

apiValidation {
    validationDisabled = true
}
