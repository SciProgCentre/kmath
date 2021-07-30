/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.noa

import space.kscience.kmath.noa.memory.NoaResource
import space.kscience.kmath.noa.memory.NoaScope

internal typealias JitModuleHandle = Long

public class NoaJitModule
internal constructor(scope: NoaScope, internal val jitModuleHandle: JitModuleHandle)
    : NoaResource(scope){
    override fun dispose(): Unit = JNoa.disposeJitModule(jitModuleHandle)

    public fun save(path: String): Unit = JNoa.saveJitModule(jitModuleHandle, path)
}