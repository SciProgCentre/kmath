package scientifik.kmath.structures

/**
 * A structure that is guaranteed to be two-dimensional
 */
interface Structure2D<T> : NDStructure<T> {
    val rowNum: Int get() = shape[0]
    val colNum: Int get() = shape[1]

    operator fun get(i: Int, j: Int): T

    override operator fun get(index: IntArray): T {
        require(index.size == 2) { "Index dimension mismatch. Expected 2 but found ${index.size}" }
        return get(index[0], index[1])
    }

    val rows: Buffer<Buffer<T>>
        get() = VirtualBuffer(rowNum) { i ->
            VirtualBuffer(colNum) { j -> get(i, j) }
        }

    val columns: Buffer<Buffer<T>>
        get() = VirtualBuffer(colNum) { j ->
            VirtualBuffer(rowNum) { i -> get(i, j) }
        }

    override fun elements(): Sequence<Pair<IntArray, T>> = sequence {
        for (i in (0 until rowNum)) {
            for (j in (0 until colNum)) {
                yield(intArrayOf(i, j) to get(i, j))
            }
        }
    }

    companion object
}

/**
 * A 2D wrapper for nd-structure
 */
private inline class Structure2DWrapper<T>(val structure: NDStructure<T>) : Structure2D<T> {
    override val shape: IntArray get() = structure.shape

    override operator fun get(i: Int, j: Int): T = structure[i, j]

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

typealias Matrix<T> = Structure2D<T>
