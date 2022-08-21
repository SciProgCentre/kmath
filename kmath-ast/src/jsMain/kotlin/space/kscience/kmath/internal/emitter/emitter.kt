/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.internal.emitter

internal open external class Emitter {
    constructor(obj: Any)
    constructor()

    open fun on(event: String, fn: () -> Unit)
    open fun off(event: String, fn: () -> Unit)
    open fun once(event: String, fn: () -> Unit)
    open fun emit(event: String, vararg any: Any)
    open fun listeners(event: String): Array<() -> Unit>
    open fun hasListeners(event: String): Boolean
}
