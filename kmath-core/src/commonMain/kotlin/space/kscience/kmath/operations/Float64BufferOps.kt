/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.operations

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.linear.Point
import space.kscience.kmath.structures.*
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * [ExtendedFieldOps] over [Float64Buffer].
 */
public abstract class Float64BufferOps : BufferAlgebra<Double, Float64Field>, ExtendedFieldOps<Buffer<Double>>,
    Norm<Buffer<Double>, Double> {

    override val elementAlgebra: Float64Field get() = Float64Field

    override val elementBufferFactory: MutableBufferFactory<Double> get() = elementAlgebra.bufferFactory

    @Suppress("OVERRIDE_BY_INLINE")
    @OptIn(UnstableKMathAPI::class)
    final override inline fun Buffer<Double>.map(block: Float64Field.(Double) -> Double): Float64Buffer =
        DoubleArray(size) { Float64Field.block(getDouble(it)) }.asBuffer()


    @OptIn(UnstableKMathAPI::class)
    @Suppress("OVERRIDE_BY_INLINE")
    final override inline fun Buffer<Double>.mapIndexed(block: Float64Field.(index: Int, arg: Double) -> Double): Float64Buffer =
        Float64Buffer(size) { Float64Field.block(it, getDouble(it)) }

    @OptIn(UnstableKMathAPI::class)
    @Suppress("OVERRIDE_BY_INLINE")
    final override inline fun Buffer<Double>.zip(
        other: Buffer<Double>,
        block: Float64Field.(left: Double, right: Double) -> Double,
    ): Float64Buffer {
        require(size == other.size) { "Incompatible buffer sizes. left: ${size}, right: ${other.size}" }
        return Float64Buffer(size) { Float64Field.block(getDouble(it), other.getDouble(it)) }
    }

    override fun unaryOperationFunction(operation: String): (arg: Buffer<Double>) -> Buffer<Double> =
        super<ExtendedFieldOps>.unaryOperationFunction(operation)

    override fun binaryOperationFunction(operation: String): (left: Buffer<Double>, right: Buffer<Double>) -> Buffer<Double> =
        super<ExtendedFieldOps>.binaryOperationFunction(operation)

    override fun Buffer<Double>.unaryMinus(): Float64Buffer = map { -it }

    override fun add(left: Buffer<Double>, right: Buffer<Double>): Float64Buffer {
        require(right.size == left.size) {
            "The size of the first buffer ${left.size} should be the same as for second one: ${right.size} "
        }

        return if (left is Float64Buffer && right is Float64Buffer) {
            val aArray = left.array
            val bArray = right.array
            Float64Buffer(DoubleArray(left.size) { aArray[it] + bArray[it] })
        } else Float64Buffer(DoubleArray(left.size) { left[it] + right[it] })
    }

    override fun Buffer<Double>.plus(arg: Buffer<Double>): Float64Buffer = add(this, arg)

    override fun Buffer<Double>.minus(arg: Buffer<Double>): Float64Buffer {
        require(arg.size == this.size) {
            "The size of the first buffer ${this.size} should be the same as for second one: ${arg.size} "
        }

        return if (this is Float64Buffer && arg is Float64Buffer) {
            val aArray = this.array
            val bArray = arg.array
            Float64Buffer(DoubleArray(this.size) { aArray[it] - bArray[it] })
        } else Float64Buffer(DoubleArray(this.size) { this[it] - arg[it] })
    }

    //
//    override fun multiply(a: Buffer<Double>, k: Number): RealBuffer {
//        val kValue = k.toDouble()
//
//        return if (a is RealBuffer) {
//            val aArray = a.array
//            RealBuffer(DoubleArray(a.size) { aArray[it] * kValue })
//        } else RealBuffer(DoubleArray(a.size) { a[it] * kValue })
//    }
//
//    override fun divide(a: Buffer<Double>, k: Number): RealBuffer {
//        val kValue = k.toDouble()
//
//        return if (a is RealBuffer) {
//            val aArray = a.array
//            RealBuffer(DoubleArray(a.size) { aArray[it] / kValue })
//        } else RealBuffer(DoubleArray(a.size) { a[it] / kValue })
//    }

    @UnstableKMathAPI
    override fun multiply(left: Buffer<Double>, right: Buffer<Double>): Float64Buffer {
        require(right.size == left.size) {
            "The size of the first buffer ${left.size} should be the same as for second one: ${right.size} "
        }

        return if (left is Float64Buffer && right is Float64Buffer) {
            val aArray = left.array
            val bArray = right.array
            Float64Buffer(DoubleArray(left.size) { aArray[it] * bArray[it] })
        } else Float64Buffer(DoubleArray(left.size) { left[it] * right[it] })
    }

    override fun divide(left: Buffer<Double>, right: Buffer<Double>): Float64Buffer {
        require(right.size == left.size) {
            "The size of the first buffer ${left.size} should be the same as for second one: ${right.size} "
        }

        return if (left is Float64Buffer && right is Float64Buffer) {
            val aArray = left.array
            val bArray = right.array
            Float64Buffer(DoubleArray(left.size) { aArray[it] / bArray[it] })
        } else Float64Buffer(DoubleArray(left.size) { left[it] / right[it] })
    }

    override fun sin(arg: Buffer<Double>): Float64Buffer = arg.map { sin(it) }

    override fun cos(arg: Buffer<Double>): Float64Buffer = arg.map { cos(it) }

    override fun tan(arg: Buffer<Double>): Float64Buffer = arg.map { tan(it) }

    override fun asin(arg: Buffer<Double>): Float64Buffer = arg.map { asin(it) }

    override fun acos(arg: Buffer<Double>): Float64Buffer = arg.map { acos(it) }

    override fun atan(arg: Buffer<Double>): Float64Buffer = arg.map { atan(it) }

    override fun sinh(arg: Buffer<Double>): Float64Buffer = arg.map { sinh(it) }

    override fun cosh(arg: Buffer<Double>): Float64Buffer = arg.map { cosh(it) }

    override fun tanh(arg: Buffer<Double>): Float64Buffer = arg.map { tanh(it) }

    override fun asinh(arg: Buffer<Double>): Float64Buffer = arg.map { asinh(it) }

    override fun acosh(arg: Buffer<Double>): Float64Buffer = arg.map { acosh(it) }

    override fun atanh(arg: Buffer<Double>): Float64Buffer = arg.map { atanh(it) }

    override fun exp(arg: Buffer<Double>): Float64Buffer = arg.map { exp(it) }

    override fun ln(arg: Buffer<Double>): Float64Buffer = arg.map { ln(it) }

    override fun norm(arg: Buffer<Double>): Double = Float64L2Norm.norm(arg)

    override fun scale(a: Buffer<Double>, value: Double): Float64Buffer = a.map { it * value }

    override fun power(arg: Buffer<Double>, pow: Number): Buffer<Double> = if (pow is Int) {
        arg.map { it.pow(pow) }
    } else {
        arg.map { it.pow(pow.toDouble()) }
    }

    public companion object : Float64BufferOps()
}

public object Float64L2Norm : Norm<Point<Double>, Double> {
    override fun norm(arg: Point<Double>): Double = sqrt(arg.fold(0.0) { acc: Double, d: Double -> acc + d.pow(2) })
}

public fun Float64BufferOps.sum(buffer: Buffer<Double>): Double = buffer.reduce(Double::plus)

/**
 * Sum of elements using given [conversion]
 */
public inline fun <T> Float64BufferOps.sumOf(buffer: Buffer<T>, conversion: (T) -> Double): Double =
    buffer.fold(0.0) { acc, value -> acc + conversion(value) }

public fun Float64BufferOps.average(buffer: Buffer<Double>): Double = sum(buffer) / buffer.size

/**
 * Average of elements using given [conversion]
 */
public inline fun <T> Float64BufferOps.averageOf(buffer: Buffer<T>, conversion: (T) -> Double): Double =
    sumOf(buffer, conversion) / buffer.size

public fun Float64BufferOps.dispersion(buffer: Buffer<Double>): Double {
    val av = average(buffer)
    return buffer.fold(0.0) { acc, value -> acc + (value - av).pow(2) } / buffer.size
}

public fun Float64BufferOps.std(buffer: Buffer<Double>): Double = sqrt(dispersion(buffer))

public fun Float64BufferOps.covariance(x: Buffer<Double>, y: Buffer<Double>): Double {
    require(x.size == y.size) { "Expected buffers of the same size, but x.size == ${x.size} and y.size == ${y.size}" }
    val xMean = average(x)
    val yMean = average(y)
    var sum = 0.0
    x.indices.forEach {
        sum += (x[it] - xMean) * (y[it] - yMean)
    }
    return sum / (x.size - 1)
}


