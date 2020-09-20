import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import scientifik.ScientifikPublishPlugin

plugins {
    id("scientifik.publish") apply false
    id("org.jetbrains.changelog") version "0.4.0"
}

val kmathVersion by extra("0.1.4")
val bintrayRepo by extra("scientifik")
val githubProject by extra("kmath")

allprojects {
    repositories {
        jcenter()
        maven("https://dl.bintray.com/kotlin/kotlinx")
        maven("https://dl.bintray.com/hotkeytlt/maven")
    }

    group = "kscience.kmath"
    version = kmathVersion

    afterEvaluate {
        extensions.findByType<KotlinProjectExtension>()?.run {
            sourceSets.all {
                languageSettings.useExperimentalAnnotation("kotlin.contracts.ExperimentalContracts")
            }
        }
    }
}

subprojects {
    if (name.startsWith("kmath"))
        apply<ScientifikPublishPlugin>()
}