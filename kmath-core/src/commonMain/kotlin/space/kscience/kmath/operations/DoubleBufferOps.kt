/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.operations

import space.kscience.kmath.linear.Point
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.BufferFactory
import space.kscience.kmath.structures.DoubleBuffer
import space.kscience.kmath.structures.asBuffer

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

    override fun unaryOperationFunction(operation: String): (arg: Buffer<Double>) -> Buffer<Double> =
        super<ExtendedFieldOps>.unaryOperationFunction(operation)

    override fun binaryOperationFunction(operation: String): (left: Buffer<Double>, right: Buffer<Double>) -> Buffer<Double> =
        super<ExtendedFieldOps>.binaryOperationFunction(operation)

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

    override fun negate(arg: Buffer<Double>): DoubleBuffer = arg.mapInline { -it }

    override fun subtract(left: Buffer<Double>, right: Buffer<Double>): DoubleBuffer {
        require(left.size == right.size) {
            "The size of the first buffer ${left.size} should be the same as for second one: ${right.size} "
        }

        return if (left is DoubleBuffer && right is DoubleBuffer)
            DoubleBuffer(DoubleArray(left.size) { left.array[it] - right.array[it] })
        else
            DoubleBuffer(DoubleArray(left.size) { left[it] - right[it] })
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

public object DoubleL2Norm : Norm<Point<Double>, Double> {
    override fun norm(arg: Point<Double>): Double = sqrt(arg.fold(0.0) { acc: Double, d: Double -> acc + d.pow(2) })
}

