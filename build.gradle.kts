import org.jetbrains.dokka.gradle.DokkaTask
import ru.mipt.npm.gradle.KSciencePublishingPlugin
import java.net.URL

plugins {
    id("ru.mipt.npm.gradle.project")
    kotlin("jupyter.api") apply false
}

allprojects {
    repositories {
        jcenter()
        maven("https://clojars.org/repo")
        maven("https://dl.bintray.com/egor-bogomolov/astminer/")
        maven("https://dl.bintray.com/hotkeytlt/maven")
        maven("https://jitpack.io")
        maven("http://logicrunch.research.it.uu.se/maven/")
        maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
        mavenCentral()
    }

    group = "space.kscience"
    version = "0.3.0-dev-4"
}

subprojects {
    if (name.startsWith("kmath")) apply<KSciencePublishingPlugin>()

    afterEvaluate {
        tasks.withType<DokkaTask> {
            dokkaSourceSets.all {
                val readmeFile = File(this@subprojects.projectDir, "./README.md")
                if (readmeFile.exists())
                    includes.setFrom(includes + readmeFile.absolutePath)

                arrayOf(
                    "http://ejml.org/javadoc/",
                    "https://commons.apache.org/proper/commons-math/javadocs/api-3.6.1/",
                    "https://deeplearning4j.org/api/latest/"
                ).map { URL("${it}package-list") to URL(it) }.forEach { (a, b) ->
                    externalDocumentationLink {
                        packageListUrl.set(a)
                        url.set(b)
                    }
                }
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
