/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.structures

import space.kscience.attributes.SafeType
import space.kscience.attributes.safeTypeOf
import kotlin.reflect.typeOf

/**
 * A generic mutable random-access structure for both primitives and objects.
 *
 * @param T the type of elements contained in the buffer.
 */
public interface MutableBuffer<T> : Buffer<T> {
    /**
     * Sets the array element at the specified [index] to the specified [value].
     */
    public operator fun set(index: Int, value: T)

    /**
     * Returns a shallow copy of the buffer.
     */
    public fun copy(): MutableBuffer<T>

    public companion object {
        /**
         * Creates a [Float64Buffer] with the specified [size], where each element is calculated by calling the specified
         * [initializer] function.
         */
        public inline fun double(size: Int, initializer: (Int) -> Double): Float64Buffer =
            Float64Buffer(size, initializer)

        /**
         * Creates a [Int16Buffer] with the specified [size], where each element is calculated by calling the specified
         * [initializer] function.
         */
        public inline fun short(size: Int, initializer: (Int) -> Short): Int16Buffer =
            Int16Buffer(size, initializer)

        /**
         * Creates a [Int32Buffer] with the specified [size], where each element is calculated by calling the specified
         * [initializer] function.
         */
        public inline fun int(size: Int, initializer: (Int) -> Int): Int32Buffer =
            Int32Buffer(size, initializer)

        /**
         * Creates a [Int64Buffer] with the specified [size], where each element is calculated by calling the specified
         * [initializer] function.
         */
        public inline fun long(size: Int, initializer: (Int) -> Long): Int64Buffer =
            Int64Buffer(size, initializer)


        /**
         * Creates a [Float32Buffer] with the specified [size], where each element is calculated by calling the specified
         * [initializer] function.
         */
        public inline fun float(size: Int, initializer: (Int) -> Float): Float32Buffer =
            Float32Buffer(size, initializer)
    }
}


/**
 * Creates a [MutableBuffer] of given [type]. If the type is primitive, specialized buffers are used
 * ([Int32Buffer], [Float64Buffer], etc.), [ListBuffer] is returned otherwise.
 *
 * The [size] is specified, and each element is calculated by calling the specified [initializer] function.
 */
@Suppress("UNCHECKED_CAST")
public inline fun <T> MutableBuffer(
    type: SafeType<T>,
    size: Int,
    initializer: (Int) -> T,
): MutableBuffer<T> = when (type.kType) {
    typeOf<Boolean>() -> TODO()
    typeOf<Int8>() -> Int8Buffer(size) { initializer(it) as Int8 } as MutableBuffer<T>
    typeOf<Int16>() -> MutableBuffer.short(size) { initializer(it) as Int16 } as MutableBuffer<T>
    typeOf<Int32>() -> MutableBuffer.int(size) { initializer(it) as Int32 } as MutableBuffer<T>
    typeOf<Int64>() -> MutableBuffer.long(size) { initializer(it) as Int64 } as MutableBuffer<T>
    typeOf<Float>() -> MutableBuffer.float(size) { initializer(it) as Float } as MutableBuffer<T>
    typeOf<Double>() -> MutableBuffer.double(size) { initializer(it) as Double } as MutableBuffer<T>
    //TODO add unsigned types
    else -> MutableListBuffer(type, MutableList(size, initializer))
}

/**
 * Creates a [MutableBuffer] of given type [T]. If the type is primitive, specialized buffers are used
 * ([Int32Buffer], [Float64Buffer], etc.), [ListBuffer] is returned otherwise.
 *
 * The [size] is specified, and each element is calculated by calling the specified [initializer] function.
 */
public inline fun <reified T> MutableBuffer(size: Int, initializer: (Int) -> T): MutableBuffer<T> =
    MutableBuffer(safeTypeOf<T>(), size, initializer)


public sealed interface PrimitiveBuffer<T> : MutableBuffer<T>