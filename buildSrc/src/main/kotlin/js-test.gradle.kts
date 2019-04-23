import com.moowork.gradle.node.npm.NpmTask
import com.moowork.gradle.node.task.NodeTask
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile

plugins {
    id("com.moowork.node")
    kotlin("multiplatform")
}

node {
    nodeModulesDir = file("$buildDir/node_modules")
}

val compileKotlinJs by tasks.getting(Kotlin2JsCompile::class)
val compileTestKotlinJs by tasks.getting(Kotlin2JsCompile::class)

val populateNodeModules by tasks.registering(Copy::class) {
    dependsOn(compileKotlinJs)
    from(compileKotlinJs.destinationDir)

    kotlin.js().compilations["test"].runtimeDependencyFiles.forEach {
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


