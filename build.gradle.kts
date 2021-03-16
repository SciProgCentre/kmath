import ru.mipt.npm.gradle.KSciencePublishingPlugin

plugins {
    id("ru.mipt.npm.gradle.project")
}

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

    group = "space.kscience"
    version = "0.3.0-dev-3"
}

subprojects {
    if (name.startsWith("kmath")) apply<KSciencePublishingPlugin>()
}

readme {
    readmeTemplate = file("docs/templates/README-TEMPLATE.md")
}

ksciencePublish {
    github("kmath")
    space()
    sonatype()
}

apiValidation {
    nonPublicMarkers.add("space.kscience.kmath.misc.UnstableKMathAPI")
}
