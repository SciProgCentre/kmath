plugins {
    id("ru.mipt.npm.gradle.project")
    kotlin("jupyter.api") apply false
}

allprojects {
    repositories {
        maven("https://clojars.org/repo")
        maven("https://jitpack.io")
        maven("http://logicrunch.research.it.uu.se/maven") {
            isAllowInsecureProtocol = true
        }
        maven("https://oss.sonatype.org/content/repositories/snapshots") {

        }
        mavenCentral()
    }

    group = "space.kscience"
    version = "0.3.0-dev-13"
}

subprojects {
    if (name.startsWith("kmath")) apply<MavenPublishPlugin>()

    afterEvaluate {
        tasks.withType<org.jetbrains.dokka.gradle.DokkaTaskPartial> {
            dependsOn(tasks.getByName("assemble"))

            dokkaSourceSets.all {
                val readmeFile = File(this@subprojects.projectDir, "README.md")
                if (readmeFile.exists()) includes.from(readmeFile.absolutePath)
                externalDocumentationLink("http://ejml.org/javadoc/")
                externalDocumentationLink("https://commons.apache.org/proper/commons-math/javadocs/api-3.6.1/")
                externalDocumentationLink("https://deeplearning4j.org/api/latest/")
                externalDocumentationLink("https://axelclk.bitbucket.io/symja/javadoc/")
                externalDocumentationLink("https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/")
                externalDocumentationLink("https://breandan.net/kotlingrad/kotlingrad/", "https://breandan.net/kotlingrad/kotlingrad/kotlingrad/package-list")
            }
        }
    }
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
