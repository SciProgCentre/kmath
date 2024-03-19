/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.structures

import space.kscience.attributes.SafeType
import java.util.stream.Collectors
import java.util.stream.IntStream
import kotlin.reflect.typeOf

/**
 * A Java stream-based parallel version of [MutableBuffer].
 * There is no parallelization for [Int8], [Int16] and [Float32] types.
 * They are processed sequentially.
 */
@Suppress("UNCHECKED_CAST")
public fun <T> MutableBuffer.Companion.parallel(
    type: SafeType<T>,
    size: Int,
    initializer: (Int) -> T,
): MutableBuffer<T> = when (type.kType) {
    typeOf<Int8>() -> Int8Buffer(size) { initializer(it) as Int8 } as MutableBuffer<T>
    typeOf<Int16>() -> Int16Buffer(size) { initializer(it) as Int16 } as MutableBuffer<T>
    typeOf<Int32>() -> IntStream.range(0, size).parallel().map { initializer(it) as Int32 }.toArray()
        .asBuffer() as MutableBuffer<T>

    typeOf<Int64>() -> IntStream.range(0, size).parallel().mapToLong { initializer(it) as Int64 }.toArray()
        .asBuffer() as MutableBuffer<T>

    typeOf<Float>() -> Float32Buffer(size) { initializer(it) as Float } as MutableBuffer<T>
    typeOf<Double>() -> IntStream.range(0, size).parallel().mapToDouble { initializer(it) as Float64 }.toArray()
        .asBuffer() as MutableBuffer<T>
    //TODO add unsigned types
    else -> IntStream.range(0, size).parallel().mapToObj { initializer(it) }.collect(Collectors.toList<T>()).asMutableBuffer()
}

public class ParallelBufferFactory<T>(override val type: SafeType<T>) : MutableBufferFactory<T> {
    override fun invoke(size: Int, builder: (Int) -> T): MutableBuffer<T> {
        TODO("Not yet implemented")
    }

}

/**
 * A Java stream-based parallel alternative to [MutableBufferFactory].
 * See [MutableBuffer.Companion.parallel] for details.
 */
public fun <T> MutableBufferFactory.Companion.parallel(
    type: SafeType<T>,
): MutableBufferFactory<T> = object : MutableBufferFactory<T> {

    override val type: SafeType<T> = type

    override fun invoke(size: Int, builder: (Int) -> T): MutableBuffer<T> = MutableBuffer.parallel(type, size, builder)

}