import space.kscience.gradle.isInDevelopment
import space.kscience.gradle.useApache2Licence
import space.kscience.gradle.useSPCTeam

plugins {
    id("space.kscience.gradle.project")
    id("org.jetbrains.kotlinx.kover") version "0.6.0"
}

allprojects {
    repositories {
        maven("https://repo.kotlin.link")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        mavenCentral()
    }

    group = "space.kscience"
    version = "0.3.1"
}

subprojects {
    if (name.startsWith("kmath")) apply<MavenPublishPlugin>()

    plugins.withId("org.jetbrains.dokka") {
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
                        java.net.URL("https://github.com/SciProgCentre/kmath/tree/master/${this@subprojects.name}/$kotlinDirPath")
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
    pom("https://github.com/SciProgCentre/kmath") {
        useApache2Licence()
        useSPCTeam()
    }
    github("kmath", "SciProgCentre")
    space(
        if (isInDevelopment) {
            "https://maven.pkg.jetbrains.space/spc/p/sci/dev"
        } else {
            "https://maven.pkg.jetbrains.space/spc/p/sci/maven"
        }
    )
    sonatype()
}

apiValidation.nonPublicMarkers.add("space.kscience.kmath.UnstableKMathAPI")

val multikVersion by extra("0.2.0")
