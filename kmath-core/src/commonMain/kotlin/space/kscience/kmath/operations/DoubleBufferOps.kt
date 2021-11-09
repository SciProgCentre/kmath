/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package space.kscience.kmath.operations

import space.kscience.kmath.linear.Point
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.structures.*

import kotlin.math.*

/**
 * [ExtendedFieldOps] over [DoubleBuffer].
 */
public abstract class DoubleBufferOps : BufferAlgebra<Double, DoubleField>, ExtendedFieldOps<Buffer<Double>>,
    Norm<Buffer<Double>, Double> {

    override val elementAlgebra: DoubleField get() = DoubleField
    override val bufferFactory: BufferFactory<Double> get() = ::DoubleBuffer

    override fun Buffer<Double>.map(block: DoubleField.(Double) -> Double): DoubleBuffer =
        mapInline { DoubleField.block(it) }

    @UnstableKMathAPI
    override fun unaryOperationFunction(operation: String): (arg: Buffer<Double>) -> Buffer<Double> =
        super<ExtendedFieldOps>.unaryOperationFunction(operation)

    @UnstableKMathAPI
    override fun binaryOperationFunction(operation: String): (left: Buffer<Double>, right: Buffer<Double>) -> Buffer<Double> =
        super<ExtendedFieldOps>.binaryOperationFunction(operation)

    override fun Buffer<Double>.unaryMinus(): DoubleBuffer = mapInline { -it }

    override fun add(left: Buffer<Double>, right: Buffer<Double>): DoubleBuffer {
        require(right.size == left.size) {
            "The size of the first buffer ${left.size} should be the same as for second one: ${right.size} "
        }

        return if (left is DoubleBuffer && right is DoubleBuffer) {
            val aArray = left.array
            val bArray = right.array
            DoubleBuffer(DoubleArray(left.size) { aArray[it] + bArray[it] })
        } else DoubleBuffer(DoubleArray(left.size) { left[it] + right[it] })
    }

    override fun Buffer<Double>.plus(arg: Buffer<Double>): DoubleBuffer = add(this, arg)

    override fun Buffer<Double>.minus(arg: Buffer<Double>): DoubleBuffer {
        require(arg.size == this.size) {
            "The size of the first buffer ${this.size} should be the same as for second one: ${arg.size} "
        }

        return if (this is DoubleBuffer && arg is DoubleBuffer) {
            val aArray = this.array
            val bArray = arg.array
            DoubleBuffer(DoubleArray(this.size) { aArray[it] - bArray[it] })
        } else DoubleBuffer(DoubleArray(this.size) { this[it] - arg[it] })
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

    override fun multiply(left: Buffer<Double>, right: Buffer<Double>): DoubleBuffer {
        require(right.size == left.size) {
            "The size of the first buffer ${left.size} should be the same as for second one: ${right.size} "
        }

        return if (left is DoubleBuffer && right is DoubleBuffer) {
            val aArray = left.array
            val bArray = right.array
            DoubleBuffer(DoubleArray(left.size) { aArray[it] * bArray[it] })
        } else DoubleBuffer(DoubleArray(left.size) { left[it] * right[it] })
    }

    override fun divide(left: Buffer<Double>, right: Buffer<Double>): DoubleBuffer {
        require(right.size == left.size) {
            "The size of the first buffer ${left.size} should be the same as for second one: ${right.size} "
        }

        return if (left is DoubleBuffer && right is DoubleBuffer) {
            val aArray = left.array
            val bArray = right.array
            DoubleBuffer(DoubleArray(left.size) { aArray[it] / bArray[it] })
        } else DoubleBuffer(DoubleArray(left.size) { left[it] / right[it] })
    }

    override fun sin(arg: Buffer<Double>): DoubleBuffer = arg.mapInline(::sin)

    override fun cos(arg: Buffer<Double>): DoubleBuffer = arg.mapInline(::cos)

    override fun tan(arg: Buffer<Double>): DoubleBuffer = arg.mapInline(::tan)

    override fun asin(arg: Buffer<Double>): DoubleBuffer = arg.mapInline(::asin)

    override fun acos(arg: Buffer<Double>): DoubleBuffer = arg.mapInline(::acos)

    override fun atan(arg: Buffer<Double>): DoubleBuffer = arg.mapInline(::atan)

    override fun sinh(arg: Buffer<Double>): DoubleBuffer = arg.mapInline(::sinh)

    override fun cosh(arg: Buffer<Double>): DoubleBuffer = arg.mapInline(::cosh)

    override fun tanh(arg: Buffer<Double>): DoubleBuffer = arg.mapInline(::tanh)

    override fun asinh(arg: Buffer<Double>): DoubleBuffer = arg.mapInline(::asinh)

    override fun acosh(arg: Buffer<Double>): DoubleBuffer = arg.mapInline(::acosh)

    override fun atanh(arg: Buffer<Double>): DoubleBuffer = arg.mapInline(::atanh)

    override fun exp(arg: Buffer<Double>): DoubleBuffer = arg.mapInline(::exp)

    override fun ln(arg: Buffer<Double>): DoubleBuffer = arg.mapInline(::ln)

    override fun norm(arg: Buffer<Double>): Double = DoubleL2Norm.norm(arg)

    override fun scale(a: Buffer<Double>, value: Double): DoubleBuffer = a.mapInline { it * value }

    public companion object : DoubleBufferOps() {

        public inline fun Buffer<Double>.mapInline(block: (Double) -> Double): DoubleBuffer =
            if (this is DoubleBuffer) {
                DoubleArray(size) { block(array[it]) }.asBuffer()
            } else {
                DoubleArray(size) { block(get(it)) }.asBuffer()
            }
    }
}

public inline fun BufferAlgebra<Double, DoubleField>.buffer(size: Int, init: (Int) -> Double): DoubleBuffer {
    val res = DoubleArray(size)
    for (i in 0 until size) {
        res[i] = init(i)
    }
    return res.asBuffer()
}

public object DoubleL2Norm : Norm<Point<Double>, Double> {
    override fun norm(arg: Point<Double>): Double = sqrt(arg.fold(0.0) { acc: Double, d: Double -> acc + d.pow(2) })
}

