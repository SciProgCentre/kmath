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

public class RMSpropOptimiser
internal constructor(scope: NoaScope, internal val optimiserHandle: OptimiserHandle)
    : NoaOptimiser(scope) {
    override fun dispose(): Unit = JNoa.disposeRmsOptim(optimiserHandle)
    override fun step(): Unit = JNoa.stepRmsOptim(optimiserHandle)
    override fun zeroGrad(): Unit = JNoa.zeroGradRmsOptim(optimiserHandle)
}


public class AdamWOptimiser
internal constructor(scope: NoaScope, internal val optimiserHandle: OptimiserHandle)
    : NoaOptimiser(scope) {
    override fun dispose(): Unit = JNoa.disposeAdamWOptim(optimiserHandle)
    override fun step(): Unit = JNoa.stepAdamWOptim(optimiserHandle)
    override fun zeroGrad(): Unit = JNoa.zeroGradAdamWOptim(optimiserHandle)
}

public class AdagradOptimiser
internal constructor(scope: NoaScope, internal val optimiserHandle: OptimiserHandle)
    : NoaOptimiser(scope) {
    override fun dispose(): Unit = JNoa.disposeAdagradOptim(optimiserHandle)
    override fun step(): Unit = JNoa.stepAdagradOptim(optimiserHandle)
    override fun zeroGrad(): Unit = JNoa.zeroGradAdagradOptim(optimiserHandle)
}

public class SgdOptimiser
internal constructor(scope: NoaScope, internal val optimiserHandle: OptimiserHandle)
    : NoaOptimiser(scope) {
    override fun dispose(): Unit = JNoa.disposeSgdOptim(optimiserHandle)
    override fun step(): Unit = JNoa.stepSgdOptim(optimiserHandle)
    override fun zeroGrad(): Unit = JNoa.zeroGradSgdOptim(optimiserHandle)
}
