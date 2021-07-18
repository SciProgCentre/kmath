/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.structures

import space.kscience.kmath.operations.ExtendedField
import space.kscience.kmath.operations.ExtendedFieldOperations
import kotlin.math.*

/**
 * [ExtendedFieldOperations] over [DoubleBuffer].
 */
public object DoubleBufferFieldOperations : ExtendedFieldOperations<Buffer<Double>> {
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
    } else
        DoubleBuffer(DoubleArray(arg.size) { ln(arg[it]) })
}

/**
 * [ExtendedField] over [DoubleBuffer].
 *
 * @property size the size of buffers to operate on.
 */
public class DoubleBufferField(public val size: Int) : ExtendedField<Buffer<Double>> {
    override val zero: Buffer<Double> by lazy { DoubleBuffer(size) { 0.0 } }
    override val one: Buffer<Double> by lazy { DoubleBuffer(size) { 1.0 } }

    override fun number(value: Number): Buffer<Double> = DoubleBuffer(size) { value.toDouble() }

    override fun Buffer<Double>.unaryMinus(): Buffer<Double> = DoubleBufferFieldOperations.run {
        -this@unaryMinus
    }

    override fun add(a: Buffer<Double>, b: Buffer<Double>): DoubleBuffer {
        require(a.size == size) { "The buffer size ${a.size} does not match context size $size" }
        return DoubleBufferFieldOperations.add(a, b)
    }

    override fun scale(a: Buffer<Double>, value: Double): DoubleBuffer {
        require(a.size == size) { "The buffer size ${a.size} does not match context size $size" }

        return if (a is DoubleBuffer) {
            val aArray = a.array
            DoubleBuffer(DoubleArray(a.size) { aArray[it] * value })
        } else DoubleBuffer(DoubleArray(a.size) { a[it] * value })
    }

    override fun multiply(a: Buffer<Double>, b: Buffer<Double>): DoubleBuffer {
        require(a.size == size) { "The buffer size ${a.size} does not match context size $size" }
        return DoubleBufferFieldOperations.multiply(a, b)
    }

    override fun divide(a: Buffer<Double>, b: Buffer<Double>): DoubleBuffer {
        require(a.size == size) { "The buffer size ${a.size} does not match context size $size" }
        return DoubleBufferFieldOperations.divide(a, b)
    }

    override fun sin(arg: Buffer<Double>): DoubleBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return DoubleBufferFieldOperations.sin(arg)
    }

    override fun cos(arg: Buffer<Double>): DoubleBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return DoubleBufferFieldOperations.cos(arg)
    }

    override fun tan(arg: Buffer<Double>): DoubleBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return DoubleBufferFieldOperations.tan(arg)
    }

    override fun asin(arg: Buffer<Double>): DoubleBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return DoubleBufferFieldOperations.asin(arg)
    }

    override fun acos(arg: Buffer<Double>): DoubleBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return DoubleBufferFieldOperations.acos(arg)
    }

    override fun atan(arg: Buffer<Double>): DoubleBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return DoubleBufferFieldOperations.atan(arg)
    }

    override fun sinh(arg: Buffer<Double>): DoubleBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return DoubleBufferFieldOperations.sinh(arg)
    }

    override fun cosh(arg: Buffer<Double>): DoubleBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return DoubleBufferFieldOperations.cosh(arg)
    }

    override fun tanh(arg: Buffer<Double>): DoubleBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return DoubleBufferFieldOperations.tanh(arg)
    }

    override fun asinh(arg: Buffer<Double>): DoubleBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return DoubleBufferFieldOperations.asinh(arg)
    }

    override fun acosh(arg: Buffer<Double>): DoubleBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return DoubleBufferFieldOperations.acosh(arg)
    }

    override fun atanh(arg: Buffer<Double>): DoubleBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return DoubleBufferFieldOperations.atanh(arg)
    }

    override fun power(arg: Buffer<Double>, pow: Number): DoubleBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return DoubleBufferFieldOperations.power(arg, pow)
    }

    override fun exp(arg: Buffer<Double>): DoubleBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return DoubleBufferFieldOperations.exp(arg)
    }

    override fun ln(arg: Buffer<Double>): DoubleBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return DoubleBufferFieldOperations.ln(arg)
    }
}
