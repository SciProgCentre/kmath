/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.noa.memory

public abstract class NoaResource
internal constructor(internal val scope: NoaScope) {
    init {
        scope.add(::dispose)
    }

    protected abstract fun dispose(): Unit
}