import java.net.URL

plugins {
    id("ru.mipt.npm.gradle.project")
    id("org.jetbrains.kotlinx.kover") version "0.4.1"
    kotlin("jupyter.api") apply false
}

allprojects {
    repositories {
        maven("https://clojars.org/repo")
        maven("https://jitpack.io")

        maven("http://logicrunch.research.it.uu.se/maven") {
            isAllowInsecureProtocol = true
        }

        maven("https://oss.sonatype.org/content/repositories/snapshots")
        mavenCentral()
    }

    group = "space.kscience"
    version = "0.3.0-dev-17"
}

subprojects {
    if (name.startsWith("kmath")) apply<MavenPublishPlugin>()

    afterEvaluate {
        tasks.withType<org.jetbrains.dokka.gradle.DokkaTaskPartial> {
            dependsOn(tasks["assemble"])

            dokkaSourceSets.all {
                val readmeFile = this@subprojects.projectDir.resolve("README.md")
                if (readmeFile.exists()) includes.from(readmeFile)
                val kotlinDirPath = "src/$name/kotlin"
                val kotlinDir = file(kotlinDirPath)

                if (kotlinDir.exists()) sourceLink {
                    localDirectory.set(kotlinDir)

                    remoteUrl.set(
                        URL("https://github.com/mipt-npm/${rootProject.name}/tree/master/${this@subprojects.name}/$kotlinDirPath")
                    )
                }

                externalDocumentationLink("https://commons.apache.org/proper/commons-math/javadocs/api-3.6.1/")
                externalDocumentationLink("https://deeplearning4j.org/api/latest/")
                externalDocumentationLink("https://axelclk.bitbucket.io/symja/javadoc/")

                externalDocumentationLink(
                    "https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/",
                    "https://kotlin.github.io/kotlinx.coroutines/package-list",
                )

                externalDocumentationLink(
                    "https://breandan.net/kotlingrad/kotlingrad/",
                    "https://breandan.net/kotlingrad/kotlingrad/kotlingrad/package-list",
                )
            }
        }
    }
}

readme.readmeTemplate = file("docs/templates/README-TEMPLATE.md")

ksciencePublish {
    vcs("https://github.com/mipt-npm/kmath")
    space(publish = true)
    sonatype(publish = true)
}

apiValidation.nonPublicMarkers.add("space.kscience.kmath.misc.UnstableKMathAPI")