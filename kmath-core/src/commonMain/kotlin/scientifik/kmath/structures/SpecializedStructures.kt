package scientifik.kmath.structures

/**
 * A structure that is guaranteed to be one-dimensional
 */
interface Structure1D<T> : NDStructure<T>, Buffer<T> {
    override val dimension: Int get() = 1

    override fun get(index: IntArray): T {
        if (index.size != 1) error("Index dimension mismatch. Expected 1 but found ${index.size}")
        return get(index[0])
    }

    override fun iterator(): Iterator<T> = (0 until size).asSequence().map { get(it) }.iterator()
}

/**
 * A 1D wrapper for nd-structure
 */
private inline class Structure1DWrapper<T>(val structure: NDStructure<T>) : Structure1D<T> {

    override val shape: IntArray get() = structure.shape
    override val size: Int get() = structure.shape[0]

    override fun get(index: Int): T = structure[index]

    override fun elements(): Sequence<Pair<IntArray, T>> = structure.elements()
}

/**
 * Represent a [NDStructure] as [Structure1D]. Throw error in case of dimension mismatch
 */
fun <T> NDStructure<T>.as1D(): Structure1D<T> = if (shape.size == 1) {
    Structure1DWrapper(this)
} else {
    error("Can't create 1d-structure from ${shape.size}d-structure")
}

fun <T> NDBuffer<T>.as1D(): Structure1D<T> = if (shape.size == 1) {
    Buffer1DWrapper(this.buffer)
} else {
    error("Can't create 1d-structure from ${shape.size}d-structure")
}

/**
 * A structure wrapper for buffer
 */
private inline class Buffer1DWrapper<T>(val buffer: Buffer<T>) : Structure1D<T> {
    override val shape: IntArray get() = intArrayOf(buffer.size)

    override val size: Int get() = buffer.size

    override fun elements(): Sequence<Pair<IntArray, T>> =
        asSequence().mapIndexed { index, value -> intArrayOf(index) to value }

    override fun get(index: Int): T = buffer.get(index)
}

/**
 * Represent this buffer as 1D structure
 */
fun <T> Buffer<T>.asND(): Structure1D<T> = Buffer1DWrapper(this)

/**
 * A structure that is guaranteed to be two-dimensional
 */
interface Structure2D<T> : NDStructure<T> {
    operator fun get(i: Int, j: Int): T

    override fun get(index: IntArray): T {
        if (index.size != 2) error("Index dimension mismatch. Expected 2 but found ${index.size}")
        return get(index[0], index[1])
    }
}

/**
 * A 2D wrapper for nd-structure
 */
private inline class Structure2DWrapper<T>(val structure: NDStructure<T>) : Structure2D<T> {
    override fun get(i: Int, j: Int): T = structure[i, j]

    override val shape: IntArray get() = structure.shape

    override fun elements(): Sequence<Pair<IntArray, T>> = structure.elements()
}

/**
 * Represent a [NDStructure] as [Structure1D]. Throw error in case of dimension mismatch
 */
fun <T> NDStructure<T>.as2D(): Structure2D<T> = if (shape.size == 2) {
    Structure2DWrapper(this)
} else {
    error("Can't create 2d-structure from ${shape.size}d-structure")
}