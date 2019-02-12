package scientifik.kmath.structures

import java.nio.ByteBuffer


/**
 * A specification for serialization and deserialization objects to buffer
 */
interface BufferSpec<T : Any> {
    /**
     * Read an object from buffer in current position
     */
    fun ByteBuffer.readObject(): T

    /**
     * Write object to [ByteBuffer] in current buffer position
     */
    fun ByteBuffer.writeObject(value: T)
}

/**
 * A [BufferSpec] with fixed unit size. Allows storage of any object without boxing.
 */
interface FixedSizeBufferSpec<T : Any> : BufferSpec<T> {
    val unitSize: Int


    /**
     * Read an object from buffer in given index (not buffer position
     */
    fun ByteBuffer.readObject(index: Int): T {
        position(index * unitSize)
        return readObject()
    }


    /**
     * Put an object in given index
     */
    fun ByteBuffer.writeObject(index: Int, obj: T) {
        position(index * unitSize)
        writeObject(obj)
    }
}
