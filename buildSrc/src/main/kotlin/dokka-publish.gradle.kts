import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    id("org.jetbrains.dokka")
    `maven-publish`
}

afterEvaluate {

    extensions.findByType<KotlinMultiplatformExtension>()?.apply{
        val dokka by tasks.getting(DokkaTask::class) {
            outputFormat = "html"
            outputDirectory = "$buildDir/javadoc"
            jdkVersion = 8

            kotlinTasks {
                // dokka fails to retrieve sources from MPP-tasks so we only define the jvm task
                listOf(tasks.getByPath("compileKotlinJvm"))
            }
            sourceRoot {
                // assuming only single source dir
                path = sourceSets["commonMain"].kotlin.srcDirs.first().toString()
                platforms = listOf("Common")
            }
            // although the JVM sources are now taken from the task,
            // we still define the jvm source root to get the JVM marker in the generated html
            sourceRoot {
                // assuming only single source dir
                path = sourceSets["jvmMain"].kotlin.srcDirs.first().toString()
                platforms = listOf("JVM")
            }
        }

        val kdocJar by tasks.registering(Jar::class) {
            group = JavaBasePlugin.DOCUMENTATION_GROUP
            dependsOn(dokka)
            archiveClassifier.set("javadoc")
            from("$buildDir/javadoc")
        }

        configure<PublishingExtension> {

            targets.all {
                val publication = publications.findByName(name) as MavenPublication

                // Patch publications with fake javadoc
                publication.artifact(kdocJar.get())
            }
        }
    }


    extensions.findByType<KotlinJvmProjectExtension>()?.apply{
        val dokka by tasks.getting(DokkaTask::class) {
            outputFormat = "html"
            outputDirectory = "$buildDir/javadoc"
            jdkVersion = 8
        }

        val kdocJar by tasks.registering(Jar::class) {
            group = JavaBasePlugin.DOCUMENTATION_GROUP
            dependsOn(dokka)
            archiveClassifier.set("javadoc")
            from("$buildDir/javadoc")
        }

        configure<PublishingExtension> {
            publications.filterIsInstance<MavenPublication>().forEach { publication ->
                publication.artifact(kdocJar.get())
            }
        }
    }
}