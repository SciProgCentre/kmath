/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.operations

import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.BufferFactory
import space.kscience.kmath.structures.DoubleBuffer

/**
 * An algebra over [Buffer]
 */
@UnstableKMathAPI
public interface BufferAlgebra<T, A : Algebra<T>> : Algebra<Buffer<T>> {
    public val bufferFactory: BufferFactory<T>
    public val elementAlgebra: A
    public val size: Int

    public fun buffer(vararg elements: T): Buffer<T> {
        require(elements.size == size) { "Expected $size elements but found ${elements.size}" }
        return bufferFactory(size) { elements[it] }
    }

    //TODO move to multi-receiver inline extension
    public fun Buffer<T>.map(block: (T) -> T): Buffer<T> = bufferFactory(size) { block(get(it)) }

    public fun Buffer<T>.zip(other: Buffer<T>, block: (left: T, right: T) -> T): Buffer<T> {
        require(size == other.size) { "Incompatible buffer sizes. left: $size, right: ${other.size}" }
        return bufferFactory(size) { block(this[it], other[it]) }
    }

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

@UnstableKMathAPI
public fun <T> BufferField<T, *>.buffer(initializer: (Int) -> T): Buffer<T> {
    return bufferFactory(size, initializer)
}

@UnstableKMathAPI
public fun <T, A : TrigonometricOperations<T>> BufferAlgebra<T, A>.sin(arg: Buffer<T>): Buffer<T> =
    arg.map(elementAlgebra::sin)

@UnstableKMathAPI
public fun <T, A : TrigonometricOperations<T>> BufferAlgebra<T, A>.cos(arg: Buffer<T>): Buffer<T> =
    arg.map(elementAlgebra::cos)

@UnstableKMathAPI
public fun <T, A : TrigonometricOperations<T>> BufferAlgebra<T, A>.tan(arg: Buffer<T>): Buffer<T> =
    arg.map(elementAlgebra::tan)

@UnstableKMathAPI
public fun <T, A : TrigonometricOperations<T>> BufferAlgebra<T, A>.asin(arg: Buffer<T>): Buffer<T> =
    arg.map(elementAlgebra::asin)

@UnstableKMathAPI
public fun <T, A : TrigonometricOperations<T>> BufferAlgebra<T, A>.acos(arg: Buffer<T>): Buffer<T> =
    arg.map(elementAlgebra::acos)

@UnstableKMathAPI
public fun <T, A : TrigonometricOperations<T>> BufferAlgebra<T, A>.atan(arg: Buffer<T>): Buffer<T> =
    arg.map(elementAlgebra::atan)

@UnstableKMathAPI
public fun <T, A : ExponentialOperations<T>> BufferAlgebra<T, A>.exp(arg: Buffer<T>): Buffer<T> =
    arg.map(elementAlgebra::exp)

@UnstableKMathAPI
public fun <T, A : ExponentialOperations<T>> BufferAlgebra<T, A>.ln(arg: Buffer<T>): Buffer<T> =
    arg.map(elementAlgebra::ln)

@UnstableKMathAPI
public fun <T, A : ExponentialOperations<T>> BufferAlgebra<T, A>.sinh(arg: Buffer<T>): Buffer<T> =
    arg.map(elementAlgebra::sinh)

@UnstableKMathAPI
public fun <T, A : ExponentialOperations<T>> BufferAlgebra<T, A>.cosh(arg: Buffer<T>): Buffer<T> =
    arg.map(elementAlgebra::cosh)

@UnstableKMathAPI
public fun <T, A : ExponentialOperations<T>> BufferAlgebra<T, A>.tanh(arg: Buffer<T>): Buffer<T> =
    arg.map(elementAlgebra::tanh)

@UnstableKMathAPI
public fun <T, A : ExponentialOperations<T>> BufferAlgebra<T, A>.asinh(arg: Buffer<T>): Buffer<T> =
    arg.map(elementAlgebra::asinh)

@UnstableKMathAPI
public fun <T, A : ExponentialOperations<T>> BufferAlgebra<T, A>.acosh(arg: Buffer<T>): Buffer<T> =
    arg.map(elementAlgebra::acosh)

@UnstableKMathAPI
public fun <T, A : ExponentialOperations<T>> BufferAlgebra<T, A>.atanh(arg: Buffer<T>): Buffer<T> =
    arg.map(elementAlgebra::atanh)

@UnstableKMathAPI
public fun <T, A : PowerOperations<T>> BufferAlgebra<T, A>.pow(arg: Buffer<T>, pow: Number): Buffer<T> =
    with(elementAlgebra) { arg.map { power(it, pow) } }


@UnstableKMathAPI
public class BufferField<T, A : Field<T>>(
    override val bufferFactory: BufferFactory<T>,
    override val elementAlgebra: A,
    override val size: Int
) : BufferAlgebra<T, A>, Field<Buffer<T>> {

    override val zero: Buffer<T> = bufferFactory(size) { elementAlgebra.zero }
    override val one: Buffer<T> = bufferFactory(size) { elementAlgebra.one }


    override fun add(a: Buffer<T>, b: Buffer<T>): Buffer<T> = a.zip(b, elementAlgebra::add)
    override fun multiply(a: Buffer<T>, b: Buffer<T>): Buffer<T> = a.zip(b, elementAlgebra::multiply)
    override fun divide(a: Buffer<T>, b: Buffer<T>): Buffer<T> = a.zip(b, elementAlgebra::divide)

    override fun scale(a: Buffer<T>, value: Double): Buffer<T> = with(elementAlgebra) { a.map { scale(it, value) } }
    override fun Buffer<T>.unaryMinus(): Buffer<T> = with(elementAlgebra) { map { -it } }

    override fun unaryOperationFunction(operation: String): (arg: Buffer<T>) -> Buffer<T> {
        return super<BufferAlgebra>.unaryOperationFunction(operation)
    }

    override fun binaryOperationFunction(operation: String): (left: Buffer<T>, right: Buffer<T>) -> Buffer<T> {
        return super<BufferAlgebra>.binaryOperationFunction(operation)
    }
}

//Double buffer specialization

@UnstableKMathAPI
public fun BufferField<Double, *>.buffer(vararg elements: Number): Buffer<Double> {
    require(elements.size == size) { "Expected $size elements but found ${elements.size}" }
    return bufferFactory(size) { elements[it].toDouble() }
}

@UnstableKMathAPI
public fun <T, A : Field<T>> A.bufferAlgebra(bufferFactory: BufferFactory<T>, size: Int): BufferField<T, A> =
    BufferField(bufferFactory, this, size)

@UnstableKMathAPI
public fun DoubleField.bufferAlgebra(size: Int): BufferField<Double, DoubleField> =
    BufferField(::DoubleBuffer, DoubleField, size)

