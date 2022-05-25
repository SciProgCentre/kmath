/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.operations

import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.BufferFactory
import space.kscience.kmath.structures.DoubleBuffer
import space.kscience.kmath.structures.ShortBuffer

public interface WithSize {
    public val size: Int
}

/**
 * An algebra over [Buffer]
 */
public interface BufferAlgebra<T, out A : Algebra<T>> : Algebra<Buffer<T>> {
    public val elementAlgebra: A
    public val bufferFactory: BufferFactory<T>

    public fun buffer(size: Int, vararg elements: T): Buffer<T> {
        require(elements.size == size) { "Expected $size elements but found ${elements.size}" }
        return bufferFactory(size) { elements[it] }
    }

    //TODO move to multi-receiver inline extension
    public fun Buffer<T>.map(block: A.(T) -> T): Buffer<T> = mapInline(this, block)

    public fun Buffer<T>.mapIndexed(block: A.(index: Int, arg: T) -> T): Buffer<T> = mapIndexedInline(this, block)

    public fun Buffer<T>.zip(other: Buffer<T>, block: A.(left: T, right: T) -> T): Buffer<T> =
        zipInline(this, other, block)

    override fun unaryOperationFunction(operation: String): (arg: Buffer<T>) -> Buffer<T> {
        val operationFunction = elementAlgebra.unaryOperationFunction(operation)
        return { arg -> bufferFactory(arg.size) { operationFunction(arg[it]) } }
    }

    override fun binaryOperationFunction(operation: String): (left: Buffer<T>, right: Buffer<T>) -> Buffer<T> {
        val operationFunction = elementAlgebra.binaryOperationFunction(operation)
        return { left, right ->
            bufferFactory(left.size) { operationFunction(left[it], right[it]) }
        }
    }
}

/**
 * Inline map
 */
private inline fun <T, A : Algebra<T>> BufferAlgebra<T, A>.mapInline(
    buffer: Buffer<T>,
    crossinline block: A.(T) -> T,
): Buffer<T> = bufferFactory(buffer.size) { elementAlgebra.block(buffer[it]) }

/**
 * Inline map
 */
private inline fun <T, A : Algebra<T>> BufferAlgebra<T, A>.mapIndexedInline(
    buffer: Buffer<T>,
    crossinline block: A.(index: Int, arg: T) -> T,
): Buffer<T> = bufferFactory(buffer.size) { elementAlgebra.block(it, buffer[it]) }

/**
 * Inline zip
 */
private inline fun <T, A : Algebra<T>> BufferAlgebra<T, A>.zipInline(
    l: Buffer<T>,
    r: Buffer<T>,
    crossinline block: A.(l: T, r: T) -> T,
): Buffer<T> {
    require(l.size == r.size) { "Incompatible buffer sizes. left: ${l.size}, right: ${r.size}" }
    return bufferFactory(l.size) { elementAlgebra.block(l[it], r[it]) }
}

public fun <T> BufferAlgebra<T, *>.buffer(size: Int, initializer: (Int) -> T): Buffer<T> {
    return bufferFactory(size, initializer)
}

public fun <T, A> A.buffer(initializer: (Int) -> T): Buffer<T> where A : BufferAlgebra<T, *>, A : WithSize {
    return bufferFactory(size, initializer)
}

public fun <T, A : TrigonometricOperations<T>> BufferAlgebra<T, A>.sin(arg: Buffer<T>): Buffer<T> =
    mapInline(arg) { sin(it) }

public fun <T, A : TrigonometricOperations<T>> BufferAlgebra<T, A>.cos(arg: Buffer<T>): Buffer<T> =
    mapInline(arg) { cos(it) }

public fun <T, A : TrigonometricOperations<T>> BufferAlgebra<T, A>.tan(arg: Buffer<T>): Buffer<T> =
    mapInline(arg) { tan(it) }

public fun <T, A : TrigonometricOperations<T>> BufferAlgebra<T, A>.asin(arg: Buffer<T>): Buffer<T> =
    mapInline(arg) { asin(it) }

public fun <T, A : TrigonometricOperations<T>> BufferAlgebra<T, A>.acos(arg: Buffer<T>): Buffer<T> =
    mapInline(arg) { acos(it) }

public fun <T, A : TrigonometricOperations<T>> BufferAlgebra<T, A>.atan(arg: Buffer<T>): Buffer<T> =
    mapInline(arg) { atan(it) }

public fun <T, A : ExponentialOperations<T>> BufferAlgebra<T, A>.exp(arg: Buffer<T>): Buffer<T> =
    mapInline(arg) { exp(it) }

public fun <T, A : ExponentialOperations<T>> BufferAlgebra<T, A>.ln(arg: Buffer<T>): Buffer<T> =
    mapInline(arg) { ln(it) }

public fun <T, A : ExponentialOperations<T>> BufferAlgebra<T, A>.sinh(arg: Buffer<T>): Buffer<T> =
    mapInline(arg) { sinh(it) }

public fun <T, A : ExponentialOperations<T>> BufferAlgebra<T, A>.cosh(arg: Buffer<T>): Buffer<T> =
    mapInline(arg) { cosh(it) }

public fun <T, A : ExponentialOperations<T>> BufferAlgebra<T, A>.tanh(arg: Buffer<T>): Buffer<T> =
    mapInline(arg) { tanh(it) }

public fun <T, A : ExponentialOperations<T>> BufferAlgebra<T, A>.asinh(arg: Buffer<T>): Buffer<T> =
    mapInline(arg) { asinh(it) }

public fun <T, A : ExponentialOperations<T>> BufferAlgebra<T, A>.acosh(arg: Buffer<T>): Buffer<T> =
    mapInline(arg) { acosh(it) }

public fun <T, A : ExponentialOperations<T>> BufferAlgebra<T, A>.atanh(arg: Buffer<T>): Buffer<T> =
    mapInline(arg) { atanh(it) }

public fun <T, A : PowerOperations<T>> BufferAlgebra<T, A>.pow(arg: Buffer<T>, pow: Number): Buffer<T> =
    mapInline(arg) { it.pow(pow) }


public open class BufferRingOps<T, A : Ring<T>>(
    override val elementAlgebra: A,
    override val bufferFactory: BufferFactory<T>,
) : BufferAlgebra<T, A>, RingOps<Buffer<T>> {

    override fun add(left: Buffer<T>, right: Buffer<T>): Buffer<T> = zipInline(left, right) { l, r -> l + r }
    override fun multiply(left: Buffer<T>, right: Buffer<T>): Buffer<T> = zipInline(left, right) { l, r -> l * r }
    override fun Buffer<T>.unaryMinus(): Buffer<T> = map { -it }

    override fun unaryOperationFunction(operation: String): (arg: Buffer<T>) -> Buffer<T> =
        super<BufferAlgebra>.unaryOperationFunction(operation)

    override fun binaryOperationFunction(operation: String): (left: Buffer<T>, right: Buffer<T>) -> Buffer<T> =
        super<BufferAlgebra>.binaryOperationFunction(operation)
}

public val ShortRing.bufferAlgebra: BufferRingOps<Short, ShortRing>
    get() = BufferRingOps(ShortRing, ::ShortBuffer)

public open class BufferFieldOps<T, A : Field<T>>(
    elementAlgebra: A,
    bufferFactory: BufferFactory<T>,
) : BufferRingOps<T, A>(elementAlgebra, bufferFactory), BufferAlgebra<T, A>, FieldOps<Buffer<T>>,
    ScaleOperations<Buffer<T>> {

//    override fun add(left: Buffer<T>, right: Buffer<T>): Buffer<T> = zipInline(left, right) { l, r -> l + r }
//    override fun multiply(left: Buffer<T>, right: Buffer<T>): Buffer<T> = zipInline(left, right) { l, r -> l * r }
    override fun divide(left: Buffer<T>, right: Buffer<T>): Buffer<T> = zipInline(left, right) { l, r -> l / r }

    override fun scale(a: Buffer<T>, value: Double): Buffer<T> = a.map { scale(it, value) }
    override fun Buffer<T>.unaryMinus(): Buffer<T> = map { -it }

    override fun binaryOperationFunction(operation: String): (left: Buffer<T>, right: Buffer<T>) -> Buffer<T> =
        super<BufferRingOps>.binaryOperationFunction(operation)
}

public class BufferField<T, A : Field<T>>(
    elementAlgebra: A,
    bufferFactory: BufferFactory<T>,
    override val size: Int,
) : BufferFieldOps<T, A>(elementAlgebra, bufferFactory), Field<Buffer<T>>, WithSize {

    override val zero: Buffer<T> = bufferFactory(size) { elementAlgebra.zero }
    override val one: Buffer<T> = bufferFactory(size) { elementAlgebra.one }
}

/**
 * Generate full buffer field from given buffer operations
 */
public fun <T, A : Field<T>> BufferFieldOps<T, A>.withSize(size: Int): BufferField<T, A> =
    BufferField(elementAlgebra, bufferFactory, size)

//Double buffer specialization

public fun BufferField<Double, *>.buffer(vararg elements: Number): Buffer<Double> {
    require(elements.size == size) { "Expected $size elements but found ${elements.size}" }
    return bufferFactory(size) { elements[it].toDouble() }
}

public fun <T, A : Field<T>> A.bufferAlgebra(bufferFactory: BufferFactory<T>): BufferFieldOps<T, A> =
    BufferFieldOps(this, bufferFactory)

public val DoubleField.bufferAlgebra: BufferFieldOps<Double, DoubleField>
    get() = BufferFieldOps(DoubleField, ::DoubleBuffer)

