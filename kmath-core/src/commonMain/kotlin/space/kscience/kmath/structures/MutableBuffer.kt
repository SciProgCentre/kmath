/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.structures

import kotlin.reflect.KClass

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


        /**
         * Create a boxing mutable buffer of given type
         */
        public inline fun <T> boxing(size: Int, initializer: (Int) -> T): MutableBuffer<T> =
            MutableListBuffer(MutableList(size, initializer))

        /**
         * Creates a [MutableBuffer] of given [type]. If the type is primitive, specialized buffers are used
         * ([Int32Buffer], [Float64Buffer], etc.), [ListBuffer] is returned otherwise.
         *
         * The [size] is specified, and each element is calculated by calling the specified [initializer] function.
         */
        @Suppress("UNCHECKED_CAST")
        public inline fun <T : Any> auto(type: KClass<out T>, size: Int, initializer: (Int) -> T): MutableBuffer<T> =
            when (type) {
                Double::class -> double(size) { initializer(it) as Double } as MutableBuffer<T>
                Short::class -> short(size) { initializer(it) as Short } as MutableBuffer<T>
                Int::class -> int(size) { initializer(it) as Int } as MutableBuffer<T>
                Float::class -> float(size) { initializer(it) as Float } as MutableBuffer<T>
                Long::class -> long(size) { initializer(it) as Long } as MutableBuffer<T>
                else -> boxing(size, initializer)
            }

        /**
         * Creates a [MutableBuffer] of given type [T]. If the type is primitive, specialized buffers are used
         * ([Int32Buffer], [Float64Buffer], etc.), [ListBuffer] is returned otherwise.
         *
         * The [size] is specified, and each element is calculated by calling the specified [initializer] function.
         */
        @Suppress("UNCHECKED_CAST")
        public inline fun <reified T : Any> auto(size: Int, initializer: (Int) -> T): MutableBuffer<T> =
            auto(T::class, size, initializer)
    }
}


public sealed interface PrimitiveBuffer<T>: MutableBuffer<T>