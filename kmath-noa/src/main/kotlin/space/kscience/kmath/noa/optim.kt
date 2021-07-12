/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.noa

import space.kscience.kmath.noa.memory.NoaResource
import space.kscience.kmath.noa.memory.NoaScope

internal typealias OptimiserHandle = Long

public abstract class NoaOptimiser
internal constructor(scope: NoaScope) : NoaResource(scope) {
    public abstract fun step(): Unit
    public abstract fun zeroGrad(): Unit
}

public class AdamOptimiser
internal constructor(scope: NoaScope, internal val optimiserHandle: OptimiserHandle)
    : NoaOptimiser(scope) {
    override fun dispose(): Unit = JNoa.disposeAdamOptim(optimiserHandle)
    override fun step(): Unit = JNoa.stepAdamOptim(optimiserHandle)
    override fun zeroGrad(): Unit = JNoa.zeroGradAdamOptim(optimiserHandle)
}