import com.moowork.gradle.node.NodeExtension
import com.moowork.gradle.node.npm.NpmTask
import com.moowork.gradle.node.task.NodeTask
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    val kotlinVersion: String by rootProject.extra("1.3.30")
    val ioVersion: String by rootProject.extra("0.1.5")
    val coroutinesVersion: String by rootProject.extra("1.1.1")
    val atomicfuVersion: String by rootProject.extra("0.12.1")
    val dokkaVersion: String by rootProject.extra("0.9.17")
    val serializationVersion: String by rootProject.extra("0.10.0")

    repositories {
        jcenter()
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("org.jfrog.buildinfo:build-info-extractor-gradle:4+")
        classpath("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.4")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:$dokkaVersion")
        //classpath("org.jetbrains.kotlin:kotlin-frontend-plugin:0.0.45")
        //classpath("org.openjfx:javafx-plugin:0.0.7")
    }
}

plugins {
    id("com.jfrog.artifactory") version "4.9.1" apply false
    id("com.moowork.node") version "1.3.1" apply false
}

val kmathVersion by extra("0.1.2-dev-1")

allprojects {
    apply(plugin = "maven")
    apply(plugin = "maven-publish")
    apply(plugin = "com.jfrog.artifactory")

    repositories {
        jcenter()
        maven("https://kotlin.bintray.com/kotlinx")
    }
    group = "scientifik"
    version = kmathVersion
}

subprojects {
    if(name.startsWith("kmath")) {
        // apply bintray configuration
        apply(from = "${rootProject.rootDir}/gradle/bintray.gradle")

        //apply artifactory configuration
        apply(from = "${rootProject.rootDir}/gradle/artifactory.gradle")
    }
    //    dokka {
//        outputFormat = "html"
//        outputDirectory = javadoc.destinationDir
//    }
//
//    task dokkaJar (type: Jar, dependsOn: dokka) {
//           from javadoc . destinationDir
//            classifier = "javadoc"
//    }

    // Create empty jar for sources classifier to satisfy maven requirements
    val stubSources by tasks.registering(Jar::class) {
        archiveClassifier.set("sources")
        //from(sourceSets.main.get().allSource)
    }

    // Create empty jar for javadoc classifier to satisfy maven requirements
    val stubJavadoc by tasks.registering(Jar::class) {
        archiveClassifier.set("javadoc")
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }


    afterEvaluate {
        extensions.findByType<KotlinMultiplatformExtension>()?.apply {
            jvm {
                compilations.all {
                    kotlinOptions {
                        jvmTarget = "1.8"
                    }
                }
            }

            js {
                compilations.all {
                    tasks.getByName(compileKotlinTaskName) {
                        kotlinOptions {
                            metaInfo = true
                            sourceMap = true
                            sourceMapEmbedSources = "always"
                            moduleKind = "commonjs"
                        }
                    }
                }

                configure(listOf(compilations["main"])) {
                    tasks.getByName(compileKotlinTaskName) {
                        kotlinOptions {
                            main = "call"
                        }
                    }
                }


                val runJsTests by ext(false)

                if(runJsTests) {
                    apply(plugin = "com.moowork.node")
                    configure<NodeExtension> {
                        nodeModulesDir = file("$buildDir/node_modules")
                    }

                    val compileKotlinJs by tasks.getting(Kotlin2JsCompile::class)
                    val compileTestKotlinJs by tasks.getting(Kotlin2JsCompile::class)

                    val populateNodeModules by tasks.registering(Copy::class) {
                        dependsOn(compileKotlinJs)
                        from(compileKotlinJs.destinationDir)

                        compilations["test"].runtimeDependencyFiles.forEach {
                            if (it.exists() && !it.isDirectory) {
                                from(zipTree(it.absolutePath).matching { include("*.js") })
                            }
                        }

                        into("$buildDir/node_modules")
                    }

                    val installMocha by tasks.registering(NpmTask::class) {
                        setWorkingDir(buildDir)
                        setArgs(listOf("install", "mocha"))
                    }

                    val runMocha by tasks.registering(NodeTask::class) {
                        dependsOn(compileTestKotlinJs, populateNodeModules, installMocha)
                        setScript(file("$buildDir/node_modules/mocha/bin/mocha"))
                        setArgs(listOf(compileTestKotlinJs.outputFile))
                    }

                    tasks["jsTest"].dependsOn(runMocha)
                }
            }

            sourceSets {

                val commonMain by getting {
                    dependencies {
                        api(kotlin("stdlib"))
                    }
                }
                val commonTest by getting {
                    dependencies {
                        implementation(kotlin("test-common"))
                        implementation(kotlin("test-annotations-common"))
                    }
                }
                val jvmMain by getting {
                    dependencies {
                        api(kotlin("stdlib-jdk8"))
                    }
                }
                val jvmTest by getting {
                    dependencies {
                        implementation(kotlin("test"))
                        implementation(kotlin("test-junit"))
                    }
                }
                val jsMain by getting {
                    dependencies {
                        api(kotlin("stdlib-js"))
                    }
                }
                val jsTest by getting {
                    dependencies {
                        implementation(kotlin("test-js"))
                    }
                }
            }

            targets.all {
                sourceSets.all {
                    languageSettings.progressiveMode = true
                    languageSettings.enableLanguageFeature("InlineClasses")
                    languageSettings.useExperimentalAnnotation("ExperimentalContracts")
                    //languageSettings.enableLanguageFeature("Contracts")
                }
            }

            configure<PublishingExtension> {

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
        }
    }

}