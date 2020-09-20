package kscience.kmath.structures

import kscience.kmath.operations.ExtendedField
import kscience.kmath.operations.ExtendedFieldOperations
import kotlin.math.*

/**
 * [ExtendedFieldOperations] over [RealBuffer].
 */
public object RealBufferFieldOperations : ExtendedFieldOperations<Buffer<Double>> {
    public override fun add(a: Buffer<Double>, b: Buffer<Double>): RealBuffer {
        require(b.size == a.size) {
            "The size of the first buffer ${a.size} should be the same as for second one: ${b.size} "
        }

        return if (a is RealBuffer && b is RealBuffer) {
            val aArray = a.array
            val bArray = b.array
            RealBuffer(DoubleArray(a.size) { aArray[it] + bArray[it] })
        } else RealBuffer(DoubleArray(a.size) { a[it] + b[it] })
    }

    public override fun multiply(a: Buffer<Double>, k: Number): RealBuffer {
        val kValue = k.toDouble()

        return if (a is RealBuffer) {
            val aArray = a.array
            RealBuffer(DoubleArray(a.size) { aArray[it] * kValue })
        } else RealBuffer(DoubleArray(a.size) { a[it] * kValue })
    }

    public override fun multiply(a: Buffer<Double>, b: Buffer<Double>): RealBuffer {
        require(b.size == a.size) {
            "The size of the first buffer ${a.size} should be the same as for second one: ${b.size} "
        }

        return if (a is RealBuffer && b is RealBuffer) {
            val aArray = a.array
            val bArray = b.array
            RealBuffer(DoubleArray(a.size) { aArray[it] * bArray[it] })
        } else
            RealBuffer(DoubleArray(a.size) { a[it] * b[it] })
    }

    public override fun divide(a: Buffer<Double>, b: Buffer<Double>): RealBuffer {
        require(b.size == a.size) {
            "The size of the first buffer ${a.size} should be the same as for second one: ${b.size} "
        }

        return if (a is RealBuffer && b is RealBuffer) {
            val aArray = a.array
            val bArray = b.array
            RealBuffer(DoubleArray(a.size) { aArray[it] / bArray[it] })
        } else RealBuffer(DoubleArray(a.size) { a[it] / b[it] })
    }

    public override fun sin(arg: Buffer<Double>): RealBuffer = if (arg is RealBuffer) {
        val array = arg.array
        RealBuffer(DoubleArray(arg.size) { sin(array[it]) })
    } else RealBuffer(DoubleArray(arg.size) { sin(arg[it]) })

    public override fun cos(arg: Buffer<Double>): RealBuffer = if (arg is RealBuffer) {
        val array = arg.array
        RealBuffer(DoubleArray(arg.size) { cos(array[it]) })
    } else RealBuffer(DoubleArray(arg.size) { cos(arg[it]) })

    public override fun tan(arg: Buffer<Double>): RealBuffer = if (arg is RealBuffer) {
        val array = arg.array
        RealBuffer(DoubleArray(arg.size) { tan(array[it]) })
    } else RealBuffer(DoubleArray(arg.size) { tan(arg[it]) })

    public override fun asin(arg: Buffer<Double>): RealBuffer = if (arg is RealBuffer) {
        val array = arg.array
        RealBuffer(DoubleArray(arg.size) { asin(array[it]) })
    } else
        RealBuffer(DoubleArray(arg.size) { asin(arg[it]) })

    public override fun acos(arg: Buffer<Double>): RealBuffer = if (arg is RealBuffer) {
        val array = arg.array
        RealBuffer(DoubleArray(arg.size) { acos(array[it]) })
    } else
        RealBuffer(DoubleArray(arg.size) { acos(arg[it]) })

    public override fun atan(arg: Buffer<Double>): RealBuffer = if (arg is RealBuffer) {
        val array = arg.array
        RealBuffer(DoubleArray(arg.size) { atan(array[it]) })
    } else
        RealBuffer(DoubleArray(arg.size) { atan(arg[it]) })

    public override fun sinh(arg: Buffer<Double>): RealBuffer = if (arg is RealBuffer) {
        val array = arg.array
        RealBuffer(DoubleArray(arg.size) { sinh(array[it]) })
    } else
        RealBuffer(DoubleArray(arg.size) { sinh(arg[it]) })

    public override fun cosh(arg: Buffer<Double>): RealBuffer = if (arg is RealBuffer) {
        val array = arg.array
        RealBuffer(DoubleArray(arg.size) { cosh(array[it]) })
    } else
        RealBuffer(DoubleArray(arg.size) { cosh(arg[it]) })

    public override fun tanh(arg: Buffer<Double>): RealBuffer = if (arg is RealBuffer) {
        val array = arg.array
        RealBuffer(DoubleArray(arg.size) { tanh(array[it]) })
    } else
        RealBuffer(DoubleArray(arg.size) { tanh(arg[it]) })

    public override fun asinh(arg: Buffer<Double>): RealBuffer = if (arg is RealBuffer) {
        val array = arg.array
        RealBuffer(DoubleArray(arg.size) { asinh(array[it]) })
    } else
        RealBuffer(DoubleArray(arg.size) { asinh(arg[it]) })

    public override fun acosh(arg: Buffer<Double>): RealBuffer = if (arg is RealBuffer) {
        val array = arg.array
        RealBuffer(DoubleArray(arg.size) { acosh(array[it]) })
    } else
        RealBuffer(DoubleArray(arg.size) { acosh(arg[it]) })

    public override fun atanh(arg: Buffer<Double>): RealBuffer = if (arg is RealBuffer) {
        val array = arg.array
        RealBuffer(DoubleArray(arg.size) { atanh(array[it]) })
    } else
        RealBuffer(DoubleArray(arg.size) { atanh(arg[it]) })

    public override fun power(arg: Buffer<Double>, pow: Number): RealBuffer = if (arg is RealBuffer) {
        val array = arg.array
        RealBuffer(DoubleArray(arg.size) { array[it].pow(pow.toDouble()) })
    } else
        RealBuffer(DoubleArray(arg.size) { arg[it].pow(pow.toDouble()) })

    public override fun exp(arg: Buffer<Double>): RealBuffer = if (arg is RealBuffer) {
        val array = arg.array
        RealBuffer(DoubleArray(arg.size) { exp(array[it]) })
    } else RealBuffer(DoubleArray(arg.size) { exp(arg[it]) })

    public override fun ln(arg: Buffer<Double>): RealBuffer = if (arg is RealBuffer) {
        val array = arg.array
        RealBuffer(DoubleArray(arg.size) { ln(array[it]) })
    } else
        RealBuffer(DoubleArray(arg.size) { ln(arg[it]) })
}

/**
 * [ExtendedField] over [RealBuffer].
 *
 * @property size the size of buffers to operate on.
 */
public class RealBufferField(public val size: Int) : ExtendedField<Buffer<Double>> {
    public override val zero: Buffer<Double> by lazy { RealBuffer(size) { 0.0 } }
    public override val one: Buffer<Double> by lazy { RealBuffer(size) { 1.0 } }

    public override fun add(a: Buffer<Double>, b: Buffer<Double>): RealBuffer {
        require(a.size == size) { "The buffer size ${a.size} does not match context size $size" }
        return RealBufferFieldOperations.add(a, b)
    }

    public override fun multiply(a: Buffer<Double>, k: Number): RealBuffer {
        require(a.size == size) { "The buffer size ${a.size} does not match context size $size" }
        return RealBufferFieldOperations.multiply(a, k)
    }

    public override fun multiply(a: Buffer<Double>, b: Buffer<Double>): RealBuffer {
        require(a.size == size) { "The buffer size ${a.size} does not match context size $size" }
        return RealBufferFieldOperations.multiply(a, b)
    }

    public override fun divide(a: Buffer<Double>, b: Buffer<Double>): RealBuffer {
        require(a.size == size) { "The buffer size ${a.size} does not match context size $size" }
        return RealBufferFieldOperations.divide(a, b)
    }

    public override fun sin(arg: Buffer<Double>): RealBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return RealBufferFieldOperations.sin(arg)
    }

    public override fun cos(arg: Buffer<Double>): RealBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return RealBufferFieldOperations.cos(arg)
    }

    public override fun tan(arg: Buffer<Double>): RealBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return RealBufferFieldOperations.tan(arg)
    }

    public override fun asin(arg: Buffer<Double>): RealBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return RealBufferFieldOperations.asin(arg)
    }

    public override fun acos(arg: Buffer<Double>): RealBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return RealBufferFieldOperations.acos(arg)
    }

    public override fun atan(arg: Buffer<Double>): RealBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return RealBufferFieldOperations.atan(arg)
    }

    public override fun sinh(arg: Buffer<Double>): RealBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return RealBufferFieldOperations.sinh(arg)
    }

    public override fun cosh(arg: Buffer<Double>): RealBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return RealBufferFieldOperations.cosh(arg)
    }

    public override fun tanh(arg: Buffer<Double>): RealBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return RealBufferFieldOperations.tanh(arg)
    }

    public override fun asinh(arg: Buffer<Double>): RealBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return RealBufferFieldOperations.asinh(arg)
    }

    public override fun acosh(arg: Buffer<Double>): RealBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return RealBufferFieldOperations.acosh(arg)
    }

    public override fun atanh(arg: Buffer<Double>): RealBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return RealBufferFieldOperations.atanh(arg)
    }

    public override fun power(arg: Buffer<Double>, pow: Number): RealBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return RealBufferFieldOperations.power(arg, pow)
    }

    public override fun exp(arg: Buffer<Double>): RealBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return RealBufferFieldOperations.exp(arg)
    }

    public override fun ln(arg: Buffer<Double>): RealBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return RealBufferFieldOperations.ln(arg)
    }
}
