package kscience.kmath.structures

/**
 * A structure that is guaranteed to be one-dimensional
 */
public interface Structure1D<T> : NDStructure<T>, Buffer<T> {
    public override val dimension: Int get() = 1

    public override operator fun get(index: IntArray): T {
        require(index.size == 1) { "Index dimension mismatch. Expected 1 but found ${index.size}" }
        return get(index[0])
    }

    public override operator fun iterator(): Iterator<T> = (0 until size).asSequence().map(::get).iterator()
}

/**
 * A 1D wrapper for nd-structure
 */
private inline class Structure1DWrapper<T>(val structure: NDStructure<T>) : Structure1D<T> {
    override val shape: IntArray get() = structure.shape
    override val size: Int get() = structure.shape[0]

    override operator fun get(index: Int): T = structure[index]
    override fun elements(): Sequence<Pair<IntArray, T>> = structure.elements()
}


/**
 * A structure wrapper for buffer
 */
private inline class Buffer1DWrapper<T>(val buffer: Buffer<T>) : Structure1D<T> {
    override val shape: IntArray get() = intArrayOf(buffer.size)
    override val size: Int get() = buffer.size

    override fun elements(): Sequence<Pair<IntArray, T>> =
        asSequence().mapIndexed { index, value -> intArrayOf(index) to value }

    override operator fun get(index: Int): T = buffer[index]
}

/**
 * Represent a [NDStructure] as [Structure1D]. Throw error in case of dimension mismatch
 */
public fun <T> NDStructure<T>.as1D(): Structure1D<T> = if (shape.size == 1) {
    if (this is NDBuffer) Buffer1DWrapper(this.buffer) else Structure1DWrapper(this)
} else
    error("Can't create 1d-structure from ${shape.size}d-structure")

/**
 * Represent this buffer as 1D structure
 */
public fun <T> Buffer<T>.asND(): Structure1D<T> = Buffer1DWrapper(this)
