package kscience.kmath.gsl

import kotlinx.cinterop.CStructVar
import kotlinx.cinterop.DeferScope
import kscience.kmath.linear.Point

public abstract class GslVector<T, H : CStructVar> internal constructor(scope: DeferScope) :
    GslMemoryHolder<H>(scope), Point<T> {
    internal abstract operator fun set(index: Int, value: T)
    internal abstract fun copy(): GslVector<T, H>

    public final override fun iterator(): Iterator<T> = object : Iterator<T> {
        private var cursor = 0

        override fun hasNext(): Boolean = cursor < size

        override fun next(): T {
            cursor++
            return this@GslVector[cursor - 1]
        }
    }
}
