package scientifik.memory

/**
 * A specification to read or write custom objects with fixed size in bytes
 */
interface MemorySpec<T : Any> {
    /**
     * Size of [T] in bytes after serialization
     */
    val objectSize: Int

    fun MemoryReader.read(offset: Int): T
    //TODO consider thread safety
    fun MemoryWriter.write(offset: Int, value: T)
}

fun <T : Any> MemoryReader.read(spec: MemorySpec<T>, offset: Int): T = spec.run { read(offset) }
fun <T : Any> MemoryWriter.write(spec: MemorySpec<T>, offset: Int, value: T) = spec.run { write(offset, value) }

inline fun <reified T : Any> MemoryReader.readArray(spec: MemorySpec<T>, offset: Int, size: Int) =
    Array(size) { i ->
        spec.run {
            read(offset + i * objectSize)
        }
    }

fun <T : Any> MemoryWriter.writeArray(spec: MemorySpec<T>, offset: Int, array: Array<T>) {
    spec.run {
        for (i in array.indices) {
            write(offset + i * objectSize, array[i])
        }
    }
}

//TODO It is possible to add elastic MemorySpec with unknown object size