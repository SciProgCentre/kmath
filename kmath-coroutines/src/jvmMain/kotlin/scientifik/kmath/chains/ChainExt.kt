package scientifik.kmath.chains

import kotlinx.coroutines.runBlocking
import sicentifik.kmath.chains.Chain
import kotlin.sequences.Sequence

/**
 * Represent a chain as regular iterator (uses blocking calls)
 */
operator fun <R> Chain<R>.iterator() = object : Iterator<R> {
    override fun hasNext(): Boolean = true

    override fun next(): R = runBlocking { next() }
}

/**
 * Represent a chain as a sequence
 */
fun <R> Chain<R>.asSequence(): Sequence<R> = object : Sequence<R> {
    override fun iterator(): Iterator<R> = this@asSequence.iterator()
}


/**
 * Map the chain result using suspended transformation. Initial chain result can no longer be safely consumed
 * since mapped chain consumes tokens. Accepts suspending transformation function.
 */
fun <T, R> Chain<T>.map(func: suspend (T) -> R): Chain<R> {
    val parent = this;
    return object : Chain<R> {
        override val value: R? get() = runBlocking { parent.value?.let { func(it) } }

        override suspend fun next(): R {
            return func(parent.next())
        }

        override fun fork(): Chain<R> {
            return parent.fork().map(func)
        }
    }
}