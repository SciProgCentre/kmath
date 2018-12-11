package scientifik.kmath.structures

import java.nio.ByteBuffer


/**
 * A specification for serialization and deserialization objects to buffer
 */
interface BufferSpec<T : Any> {
    fun fromBuffer(buffer: ByteBuffer): T
    fun toBuffer(value: T): ByteBuffer
}

/**
 * A [BufferSpec] with fixed unit size. Allows storage of any object without boxing.
 */
interface FixedSizeBufferSpec<T : Any> : BufferSpec<T> {
    val unitSize: Int

    /**
     * Read an object from buffer in current position
     */
    fun ByteBuffer.readObject(): T {
        val buffer = ByteArray(unitSize)
        get(buffer)
        return fromBuffer(ByteBuffer.wrap(buffer))
    }

    /**
     * Read an object from buffer in given index (not buffer position
     */
    fun ByteBuffer.readObject(index: Int): T {
        val dup = duplicate()
        dup.position(index*unitSize)
        return dup.readObject()
    }

    /**
     * Write object to [ByteBuffer] in current buffer position
     */
    fun ByteBuffer.writeObject(obj: T) {
        val buffer = toBuffer(obj).apply { rewind() }
        assert(buffer.limit() == unitSize)
        put(buffer)
    }

    /**
     * Put an object in given index
     */
    fun ByteBuffer.writeObject(index: Int, obj: T) {
        val dup = duplicate()
        dup.position(index*unitSize)
        dup.writeObject(obj)
    }
}