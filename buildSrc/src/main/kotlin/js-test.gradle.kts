import com.moowork.gradle.node.NodeExtension
import com.moowork.gradle.node.npm.NpmTask
import com.moowork.gradle.node.task.NodeTask
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile

plugins {
    id("com.moowork.node")
    kotlin("multiplatform")
}

configure<NodeExtension> {
    nodeModulesDir = file("$buildDir/node_modules")
}

val compileKotlinJs by tasks.getting(Kotlin2JsCompile::class)
val compileTestKotlinJs by tasks.getting(Kotlin2JsCompile::class)

inline fun <reified T : Task> TaskContainer.registering(
    crossinline action: T.() -> Unit
): RegisteringDomainObjectDelegateProviderWithTypeAndAction<TaskContainer, T> =
    RegisteringDomainObjectDelegateProviderWithTypeAndAction.of(this, T::class, { action() })


configure<KotlinMultiplatformExtension> {

    val populateNodeModules by tasks.registering(Copy::class) {
        dependsOn(compileKotlinJs)
        from(compileKotlinJs.destinationDir)

        js().compilations["test"].runtimeDependencyFiles.forEach {
            if (it.exists() && !it.isDirectory) {
                from(zipTree(it.absolutePath).matching { include("*.js") })
            }
        }

        into("$buildDir/node_modules")
    }

    val installMocha by tasks.registering<NpmTask> {
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

