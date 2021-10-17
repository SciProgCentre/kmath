/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package space.kscience.kmath.operations

import space.kscience.kmath.linear.Point
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.DoubleBuffer

import kotlin.math.*

/**
 * [ExtendedFieldOps] over [DoubleBuffer].
 */
public abstract class DoubleBufferOps : ExtendedFieldOps<Buffer<Double>>, Norm<Buffer<Double>, Double> {
    override fun Buffer<Double>.unaryMinus(): DoubleBuffer = if (this is DoubleBuffer) {
        DoubleBuffer(size) { -array[it] }
    } else {
        DoubleBuffer(size) { -get(it) }
    }

    override fun add(a: Buffer<Double>, b: Buffer<Double>): DoubleBuffer {
        require(b.size == a.size) {
            "The size of the first buffer ${a.size} should be the same as for second one: ${b.size} "
        }

        return if (a is DoubleBuffer && b is DoubleBuffer) {
            val aArray = a.array
            val bArray = b.array
            DoubleBuffer(DoubleArray(a.size) { aArray[it] + bArray[it] })
        } else DoubleBuffer(DoubleArray(a.size) { a[it] + b[it] })
    }

    override fun Buffer<Double>.plus(b: Buffer<Double>): DoubleBuffer = add(this, b)

    override fun Buffer<Double>.minus(b: Buffer<Double>): DoubleBuffer {
        require(b.size == this.size) {
            "The size of the first buffer ${this.size} should be the same as for second one: ${b.size} "
        }

        return if (this is DoubleBuffer && b is DoubleBuffer) {
            val aArray = this.array
            val bArray = b.array
            DoubleBuffer(DoubleArray(this.size) { aArray[it] - bArray[it] })
        } else DoubleBuffer(DoubleArray(this.size) { this[it] - b[it] })
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

    override fun multiply(a: Buffer<Double>, b: Buffer<Double>): DoubleBuffer {
        require(b.size == a.size) {
            "The size of the first buffer ${a.size} should be the same as for second one: ${b.size} "
        }

        return if (a is DoubleBuffer && b is DoubleBuffer) {
            val aArray = a.array
            val bArray = b.array
            DoubleBuffer(DoubleArray(a.size) { aArray[it] * bArray[it] })
        } else
            DoubleBuffer(DoubleArray(a.size) { a[it] * b[it] })
    }

    override fun divide(a: Buffer<Double>, b: Buffer<Double>): DoubleBuffer {
        require(b.size == a.size) {
            "The size of the first buffer ${a.size} should be the same as for second one: ${b.size} "
        }

        return if (a is DoubleBuffer && b is DoubleBuffer) {
            val aArray = a.array
            val bArray = b.array
            DoubleBuffer(DoubleArray(a.size) { aArray[it] / bArray[it] })
        } else DoubleBuffer(DoubleArray(a.size) { a[it] / b[it] })
    }

    override fun sin(arg: Buffer<Double>): DoubleBuffer = if (arg is DoubleBuffer) {
        val array = arg.array
        DoubleBuffer(DoubleArray(arg.size) { sin(array[it]) })
    } else DoubleBuffer(DoubleArray(arg.size) { sin(arg[it]) })

    override fun cos(arg: Buffer<Double>): DoubleBuffer = if (arg is DoubleBuffer) {
        val array = arg.array
        DoubleBuffer(DoubleArray(arg.size) { cos(array[it]) })
    } else DoubleBuffer(DoubleArray(arg.size) { cos(arg[it]) })

    override fun tan(arg: Buffer<Double>): DoubleBuffer = if (arg is DoubleBuffer) {
        val array = arg.array
        DoubleBuffer(DoubleArray(arg.size) { tan(array[it]) })
    } else DoubleBuffer(DoubleArray(arg.size) { tan(arg[it]) })

    override fun asin(arg: Buffer<Double>): DoubleBuffer = if (arg is DoubleBuffer) {
        val array = arg.array
        DoubleBuffer(DoubleArray(arg.size) { asin(array[it]) })
    } else
        DoubleBuffer(DoubleArray(arg.size) { asin(arg[it]) })

    override fun acos(arg: Buffer<Double>): DoubleBuffer = if (arg is DoubleBuffer) {
        val array = arg.array
        DoubleBuffer(DoubleArray(arg.size) { acos(array[it]) })
    } else
        DoubleBuffer(DoubleArray(arg.size) { acos(arg[it]) })

    override fun atan(arg: Buffer<Double>): DoubleBuffer = if (arg is DoubleBuffer) {
        val array = arg.array
        DoubleBuffer(DoubleArray(arg.size) { atan(array[it]) })
    } else
        DoubleBuffer(DoubleArray(arg.size) { atan(arg[it]) })

    override fun sinh(arg: Buffer<Double>): DoubleBuffer = if (arg is DoubleBuffer) {
        val array = arg.array
        DoubleBuffer(DoubleArray(arg.size) { sinh(array[it]) })
    } else
        DoubleBuffer(DoubleArray(arg.size) { sinh(arg[it]) })

    override fun cosh(arg: Buffer<Double>): DoubleBuffer = if (arg is DoubleBuffer) {
        val array = arg.array
        DoubleBuffer(DoubleArray(arg.size) { cosh(array[it]) })
    } else
        DoubleBuffer(DoubleArray(arg.size) { cosh(arg[it]) })

    override fun tanh(arg: Buffer<Double>): DoubleBuffer = if (arg is DoubleBuffer) {
        val array = arg.array
        DoubleBuffer(DoubleArray(arg.size) { tanh(array[it]) })
    } else
        DoubleBuffer(DoubleArray(arg.size) { tanh(arg[it]) })

    override fun asinh(arg: Buffer<Double>): DoubleBuffer = if (arg is DoubleBuffer) {
        val array = arg.array
        DoubleBuffer(DoubleArray(arg.size) { asinh(array[it]) })
    } else
        DoubleBuffer(DoubleArray(arg.size) { asinh(arg[it]) })

    override fun acosh(arg: Buffer<Double>): DoubleBuffer = if (arg is DoubleBuffer) {
        val array = arg.array
        DoubleBuffer(DoubleArray(arg.size) { acosh(array[it]) })
    } else
        DoubleBuffer(DoubleArray(arg.size) { acosh(arg[it]) })

    override fun atanh(arg: Buffer<Double>): DoubleBuffer = if (arg is DoubleBuffer) {
        val array = arg.array
        DoubleBuffer(DoubleArray(arg.size) { atanh(array[it]) })
    } else
        DoubleBuffer(DoubleArray(arg.size) { atanh(arg[it]) })

    override fun power(arg: Buffer<Double>, pow: Number): DoubleBuffer = if (arg is DoubleBuffer) {
        val array = arg.array
        DoubleBuffer(DoubleArray(arg.size) { array[it].pow(pow.toDouble()) })
    } else
        DoubleBuffer(DoubleArray(arg.size) { arg[it].pow(pow.toDouble()) })

    override fun exp(arg: Buffer<Double>): DoubleBuffer = if (arg is DoubleBuffer) {
        val array = arg.array
        DoubleBuffer(DoubleArray(arg.size) { exp(array[it]) })
    } else DoubleBuffer(DoubleArray(arg.size) { exp(arg[it]) })

    override fun ln(arg: Buffer<Double>): DoubleBuffer = if (arg is DoubleBuffer) {
        val array = arg.array
        DoubleBuffer(DoubleArray(arg.size) { ln(array[it]) })
    } else {
        DoubleBuffer(DoubleArray(arg.size) { ln(arg[it]) })
    }

    override fun norm(arg: Buffer<Double>): Double = DoubleL2Norm.norm(arg)

    override fun scale(a: Buffer<Double>, value: Double): DoubleBuffer = if (a is DoubleBuffer) {
        val aArray = a.array
        DoubleBuffer(DoubleArray(a.size) { aArray[it] * value })
    } else DoubleBuffer(DoubleArray(a.size) { a[it] * value })

    public companion object : DoubleBufferOps()
}

public object DoubleL2Norm : Norm<Point<Double>, Double> {
    override fun norm(arg: Point<Double>): Double = sqrt(arg.fold(0.0) { acc: Double, d: Double -> acc + d.pow(2) })
}

