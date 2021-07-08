/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.noa.memory

private typealias Disposable = () -> Unit

public class NoaScope {

    private val disposables: ArrayDeque<Disposable> = ArrayDeque(0)

    public fun disposeAll() {
        disposables.forEach(Disposable::invoke)
        disposables.clear()
    }

    internal inline fun add(crossinline disposable: Disposable) {
        disposables += {
            try {
                disposable()
            } catch (ignored: Throwable) {
            }
        }
    }
}

internal inline fun <R> withNoaScope(block: NoaScope.() -> R): R {
    val noaScope = NoaScope()
    val result = noaScope.block()
    noaScope.disposeAll()
    return result
}