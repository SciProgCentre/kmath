import org.gradle.kotlin.dsl.*

plugins {
    kotlin("multiplatform")
    `maven-publish`
}


kotlin {
    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
                // This was used in kmath-koma, but probably if we need it better to apply it for all modules
                freeCompilerArgs = listOf("-progressive")
            }
        }
    }

    js {
        compilations.all {
            kotlinOptions {
                metaInfo = true
                sourceMap = true
                sourceMapEmbedSources = "always"
                moduleKind = "commonjs"
            }
        }

        compilations.named("main") {
            kotlinOptions {
                main = "call"
            }
        }
    }

    sourceSets.invoke {
        commonMain {
            dependencies {
                api(kotlin("stdlib"))
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        "jvmMain" {
            dependencies {
                api(kotlin("stdlib-jdk8"))
            }
        }
        "jvmTest" {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }
        "jsMain" {
            dependencies {
                api(kotlin("stdlib-js"))
            }
        }
        "jsTest" {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }

    targets.all {
        sourceSets.all {
            languageSettings.progressiveMode = true
            languageSettings.enableLanguageFeature("InlineClasses")
        }
    }

    // Create empty jar for sources classifier to satisfy maven requirements
    val stubSources by tasks.registering(Jar::class){
        archiveClassifier.set("sources")
        //from(sourceSets.main.get().allSource)
    }

    // Create empty jar for javadoc classifier to satisfy maven requirements
    val stubJavadoc by tasks.registering(Jar::class) {
        archiveClassifier.set("javadoc")
    }


    publishing {

        publications.filterIsInstance<MavenPublication>().forEach { publication ->
            if (publication.name == "kotlinMultiplatform") {
                // for our root metadata publication, set artifactId with a package and project name
                publication.artifactId = project.name
            } else {
                // for targets, set artifactId with a package, project name and target name (e.g. iosX64)
                publication.artifactId = "${project.name}-${publication.name}"
            }
        }

        targets.all {
            val publication = publications.findByName(name) as MavenPublication

            // Patch publications with fake javadoc
            publication.artifact(stubJavadoc.get())
        }
    }

    // Apply JS test configuration
    val runJsTests by ext(false)

    if (runJsTests) {
        apply(plugin = "js-test")
    }

}
