/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.noa.memory

private typealias Disposable = () -> Unit

public class NoaScope {

    internal val disposables: ArrayDeque<Disposable> = ArrayDeque(0)

    public fun disposeAll() {
        disposables.forEach(Disposable::invoke)
        disposables.clear()
    }

    internal inline fun add(crossinline disposable: Disposable) {
        disposables += {
            try {
                disposable()
            } catch (e: Throwable) {
            }
        }
    }

    internal fun addAll(scope: NoaScope) {
        disposables.addAll(scope.disposables)
    }
}

internal inline fun <R> withNoaScope(block: NoaScope.() -> R): R? {
    val noaScope = NoaScope()
    val result = try { noaScope.block() } catch (e: Throwable) { null }
    noaScope.disposeAll()
    return result
}

internal inline fun <R> withNoaScope(scope: NoaScope, block: NoaScope.() -> R): R? {
    val noaScope = NoaScope()
    val result = try { noaScope.block() } catch (e: Throwable) { null }
    if (result == null){
        noaScope.disposeAll()
    } else {
        scope.addAll(noaScope)
    }
    return result
}