/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.torchscript.compiler

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment

public class TorchscriptPluginComponentRegistrar : ComponentRegistrar {
    public override fun registerProjectComponents(project: MockProject, configuration: CompilerConfiguration): Unit =
        registerExtensions(project)

    private companion object {
        private fun registerExtensions(project: MockProject) {
            IrGenerationExtension.registerExtension(project, TorchscriptExtension)
        }
    }
}

public object TorchscriptExtension : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        TODO()
    }
}
