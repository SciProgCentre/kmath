import space.kscience.gradle.useApache2Licence
import space.kscience.gradle.useSPCTeam

plugins {
    alias(spclibs.plugins.kscience.project)
    alias(spclibs.plugins.kotlinx.kover)
}

allprojects {
    repositories {
        maven("https://repo.kotlin.link")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        mavenCentral()
    }

    group = "space.kscience"
    version = "0.4.3-dev-1"
}

dependencies {
    subprojects.forEach {
        dokka(it)
    }
}

subprojects {
    if (name.startsWith("kmath")) apply<MavenPublishPlugin>()

    plugins.withId("org.jetbrains.dokka") {
        dokka {
            dokkaSourceSets.configureEach {
                val readmeFile = projectDir.resolve("README.md")
                if (readmeFile.exists()) includes.from(readmeFile)
                val kotlinDirPath = "src/$name/kotlin"
                val kotlinDir = file(kotlinDirPath)

                if (kotlinDir.exists()) sourceLink {
                    localDirectory.set(kotlinDir)
                    remoteUrl(
                        "https://github.com/SciProgCentre/kmath/tree/master/${name}/$kotlinDirPath"
                    )
                }

                fun externalDocumentationLink(url: String, packageListUrl: String? = null){
                    externalDocumentationLinks.register(url) {
                        url(url)
                        packageListUrl?.let {
                            packageListUrl(it)
                        }
                    }
                }

                externalDocumentationLink("https://commons.apache.org/proper/commons-math/javadocs/api-3.6.1/")
                externalDocumentationLink("https://deeplearning4j.org/api/latest/")
                externalDocumentationLink("https://axelclk.bitbucket.io/symja/javadoc/")

                externalDocumentationLink(
                    "https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/",
                    "https://kotlin.github.io/kotlinx.coroutines/package-list",
                )

                externalDocumentationLink(
                    "https://breandan.net/kotlingrad/kotlingrad",
                    "https://breandan.net/kotlingrad/package-list",
                )
            }
        }

    }
}

readme.readmeTemplate = file("docs/templates/README-TEMPLATE.md")

ksciencePublish {
    pom("https://github.com/SciProgCentre/kmath") {
        useApache2Licence()
        useSPCTeam()
    }
    repository("spc", "https://maven.sciprog.center/kscience")
    central()
}

apiValidation.nonPublicMarkers.add("space.kscience.kmath.UnstableKMathAPI")
