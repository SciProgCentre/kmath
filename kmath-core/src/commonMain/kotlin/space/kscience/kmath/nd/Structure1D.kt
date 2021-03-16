package space.kscience.kmath.nd

import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.MutableBuffer
import space.kscience.kmath.structures.asMutableBuffer
import space.kscience.kmath.structures.asSequence

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
 * A mutable structure that is guaranteed to be one-dimensional
 */
public interface MutableStructure1D<T> : Structure1D<T>, MutableNDStructure<T>, MutableBuffer<T> {
    public override operator fun set(index: IntArray, value: T) {
        require(index.size == 1) { "Index dimension mismatch. Expected 1 but found ${index.size}" }
        set(index[0], value)
    }
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
 * A 1D wrapper for a mutable nd-structure
 */
private inline class MutableStructure1DWrapper<T>(val structure: MutableNDStructure<T>) : MutableStructure1D<T> {
    override val shape: IntArray get() = structure.shape
    override val size: Int get() = structure.shape[0]
    override fun elements(): Sequence<Pair<IntArray, T>> {
        TODO("Not yet implemented")
    }

    override fun get(index: Int): T = structure[index]
    override fun set(index: Int, value: T) {
        structure[intArrayOf(index)] = value
    }

    override fun copy(): MutableBuffer<T> =
        structure.elements().map { it.second }.toMutableList().asMutableBuffer()
}


/**
 * A structure wrapper for buffer
 */
private inline class Buffer1DWrapper<T>(val buffer: Buffer<T>) : Structure1D<T> {
    override val shape: IntArray get() = intArrayOf(buffer.size)
    override val size: Int get() = buffer.size

    override fun elements(): Sequence<Pair<IntArray, T>> =
        buffer.asSequence().mapIndexed { index, value -> intArrayOf(index) to value }

    override operator fun get(index: Int): T = buffer[index]
}

private inline class MutableBuffer1DWrapper<T>(val buffer: MutableBuffer<T>) : MutableStructure1D<T> {
    override val shape: IntArray get() = intArrayOf(buffer.size)
    override val size: Int get() = buffer.size

    override fun elements(): Sequence<Pair<IntArray, T>> =
        buffer.asSequence().mapIndexed { index, value -> intArrayOf(index) to value }

    override operator fun get(index: Int): T = buffer[index]
    override fun set(index: Int, value: T) {
        buffer[index] = value
    }

    override fun copy(): MutableBuffer<T> = buffer.copy()
}

/**
 * Represent a [NDStructure] as [Structure1D]. Throw error in case of dimension mismatch
 */
public fun <T> NDStructure<T>.as1D(): Structure1D<T> = this as? Structure1D<T> ?: if (shape.size == 1) {
    when (this) {
        is NDBuffer -> Buffer1DWrapper(this.buffer)
        else -> Structure1DWrapper(this)
    }
} else error("Can't create 1d-structure from ${shape.size}d-structure")

public fun <T> MutableNDStructure<T>.as1D(): MutableStructure1D<T> =
    this as? MutableStructure1D<T> ?: if (shape.size == 1) {
        when (this) {
            is MutableNDBuffer -> MutableBuffer1DWrapper(this.buffer)
            else -> MutableStructure1DWrapper(this)
        }
    } else error("Can't create 1d-structure from ${shape.size}d-structure")

/**
 * Represent this buffer as 1D structure
 */
public fun <T> Buffer<T>.asND(): Structure1D<T> = Buffer1DWrapper(this)

public fun <T> MutableBuffer<T>.asND(): MutableStructure1D<T> = MutableBuffer1DWrapper(this)
