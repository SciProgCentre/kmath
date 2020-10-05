package kscience.kmath.gsl.codegen

import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.com.intellij.openapi.extensions.ExtensionPoint
import org.jetbrains.kotlin.com.intellij.openapi.extensions.Extensions
import org.jetbrains.kotlin.com.intellij.openapi.util.UserDataHolderBase
import org.jetbrains.kotlin.com.intellij.pom.PomModel
import org.jetbrains.kotlin.com.intellij.pom.PomModelAspect
import org.jetbrains.kotlin.com.intellij.pom.PomTransaction
import org.jetbrains.kotlin.com.intellij.pom.impl.PomTransactionBase
import org.jetbrains.kotlin.com.intellij.pom.tree.TreeAspect
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.TreeCopyHandler
import org.jetbrains.kotlin.config.CompilerConfiguration
import sun.reflect.ReflectionFactory

internal fun createProject(): MockProject {
    val project = KotlinCoreEnvironment.createForProduction(
        {},
        CompilerConfiguration().apply { put(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE) },
        EnvironmentConfigFiles.JVM_CONFIG_FILES
    ).project as MockProject

    val extensionPoint = "org.jetbrains.kotlin.com.intellij.treeCopyHandler"

    arrayOf(project.extensionArea, Extensions.getRootArea())
        .asSequence()
        .filterNot { it.hasExtensionPoint(extensionPoint) }
        .forEach {
            it.registerExtensionPoint(extensionPoint, TreeCopyHandler::class.java.name, ExtensionPoint.Kind.INTERFACE)
        }

    project.registerService(PomModel::class.java, object : UserDataHolderBase(), PomModel {
        override fun runTransaction(transaction: PomTransaction) = (transaction as PomTransactionBase).run()

        @Suppress("UNCHECKED_CAST")
        override fun <T : PomModelAspect> getModelAspect(aspect: Class<T>): T? {
            if (aspect == TreeAspect::class.java) {
                val constructor = ReflectionFactory.getReflectionFactory().newConstructorForSerialization(
                    aspect,
                    Any::class.java.getDeclaredConstructor(*arrayOfNulls(0))
                )

                return constructor.newInstance() as T
            }

            return null
        }
    })

    return project
}

internal operator fun PsiElement.plusAssign(e: PsiElement) {
    add(e)
}

internal fun fn(pattern: String, type: String): String {
    if (type == "double") return pattern.replace("R", "_")
    return pattern.replace("R", "_${type}_")
}

internal fun sn(pattern: String, type: String): String {
    if (type == "double") return pattern.replace("R", "")
    return pattern.replace("R", "_$type")
}
