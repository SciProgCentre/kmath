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

    public override fun add(a: Buffer<Double>, b: Buffer<Double>): DoubleBuffer {
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
//    public override fun multiply(a: Buffer<Double>, k: Number): RealBuffer {
//        val kValue = k.toDouble()
//
//        return if (a is RealBuffer) {
//            val aArray = a.array
//            RealBuffer(DoubleArray(a.size) { aArray[it] * kValue })
//        } else RealBuffer(DoubleArray(a.size) { a[it] * kValue })
//    }
//
//    public override fun divide(a: Buffer<Double>, k: Number): RealBuffer {
//        val kValue = k.toDouble()
//
//        return if (a is RealBuffer) {
//            val aArray = a.array
//            RealBuffer(DoubleArray(a.size) { aArray[it] / kValue })
//        } else RealBuffer(DoubleArray(a.size) { a[it] / kValue })
//    }

    public override fun multiply(a: Buffer<Double>, b: Buffer<Double>): DoubleBuffer {
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

    public override fun divide(a: Buffer<Double>, b: Buffer<Double>): DoubleBuffer {
        require(b.size == a.size) {
            "The size of the first buffer ${a.size} should be the same as for second one: ${b.size} "
        }

        return if (a is DoubleBuffer && b is DoubleBuffer) {
            val aArray = a.array
            val bArray = b.array
            DoubleBuffer(DoubleArray(a.size) { aArray[it] / bArray[it] })
        } else DoubleBuffer(DoubleArray(a.size) { a[it] / b[it] })
    }

    public override fun sin(arg: Buffer<Double>): DoubleBuffer = if (arg is DoubleBuffer) {
        val array = arg.array
        DoubleBuffer(DoubleArray(arg.size) { sin(array[it]) })
    } else DoubleBuffer(DoubleArray(arg.size) { sin(arg[it]) })

    public override fun cos(arg: Buffer<Double>): DoubleBuffer = if (arg is DoubleBuffer) {
        val array = arg.array
        DoubleBuffer(DoubleArray(arg.size) { cos(array[it]) })
    } else DoubleBuffer(DoubleArray(arg.size) { cos(arg[it]) })

    public override fun tan(arg: Buffer<Double>): DoubleBuffer = if (arg is DoubleBuffer) {
        val array = arg.array
        DoubleBuffer(DoubleArray(arg.size) { tan(array[it]) })
    } else DoubleBuffer(DoubleArray(arg.size) { tan(arg[it]) })

    public override fun asin(arg: Buffer<Double>): DoubleBuffer = if (arg is DoubleBuffer) {
        val array = arg.array
        DoubleBuffer(DoubleArray(arg.size) { asin(array[it]) })
    } else
        DoubleBuffer(DoubleArray(arg.size) { asin(arg[it]) })

    public override fun acos(arg: Buffer<Double>): DoubleBuffer = if (arg is DoubleBuffer) {
        val array = arg.array
        DoubleBuffer(DoubleArray(arg.size) { acos(array[it]) })
    } else
        DoubleBuffer(DoubleArray(arg.size) { acos(arg[it]) })

    public override fun atan(arg: Buffer<Double>): DoubleBuffer = if (arg is DoubleBuffer) {
        val array = arg.array
        DoubleBuffer(DoubleArray(arg.size) { atan(array[it]) })
    } else
        DoubleBuffer(DoubleArray(arg.size) { atan(arg[it]) })

    public override fun sinh(arg: Buffer<Double>): DoubleBuffer = if (arg is DoubleBuffer) {
        val array = arg.array
        DoubleBuffer(DoubleArray(arg.size) { sinh(array[it]) })
    } else
        DoubleBuffer(DoubleArray(arg.size) { sinh(arg[it]) })

    public override fun cosh(arg: Buffer<Double>): DoubleBuffer = if (arg is DoubleBuffer) {
        val array = arg.array
        DoubleBuffer(DoubleArray(arg.size) { cosh(array[it]) })
    } else
        DoubleBuffer(DoubleArray(arg.size) { cosh(arg[it]) })

    public override fun tanh(arg: Buffer<Double>): DoubleBuffer = if (arg is DoubleBuffer) {
        val array = arg.array
        DoubleBuffer(DoubleArray(arg.size) { tanh(array[it]) })
    } else
        DoubleBuffer(DoubleArray(arg.size) { tanh(arg[it]) })

    public override fun asinh(arg: Buffer<Double>): DoubleBuffer = if (arg is DoubleBuffer) {
        val array = arg.array
        DoubleBuffer(DoubleArray(arg.size) { asinh(array[it]) })
    } else
        DoubleBuffer(DoubleArray(arg.size) { asinh(arg[it]) })

    public override fun acosh(arg: Buffer<Double>): DoubleBuffer = if (arg is DoubleBuffer) {
        val array = arg.array
        DoubleBuffer(DoubleArray(arg.size) { acosh(array[it]) })
    } else
        DoubleBuffer(DoubleArray(arg.size) { acosh(arg[it]) })

    public override fun atanh(arg: Buffer<Double>): DoubleBuffer = if (arg is DoubleBuffer) {
        val array = arg.array
        DoubleBuffer(DoubleArray(arg.size) { atanh(array[it]) })
    } else
        DoubleBuffer(DoubleArray(arg.size) { atanh(arg[it]) })

    public override fun power(arg: Buffer<Double>, pow: Number): DoubleBuffer = if (arg is DoubleBuffer) {
        val array = arg.array
        DoubleBuffer(DoubleArray(arg.size) { array[it].pow(pow.toDouble()) })
    } else
        DoubleBuffer(DoubleArray(arg.size) { arg[it].pow(pow.toDouble()) })

    public override fun exp(arg: Buffer<Double>): DoubleBuffer = if (arg is DoubleBuffer) {
        val array = arg.array
        DoubleBuffer(DoubleArray(arg.size) { exp(array[it]) })
    } else DoubleBuffer(DoubleArray(arg.size) { exp(arg[it]) })

    public override fun ln(arg: Buffer<Double>): DoubleBuffer = if (arg is DoubleBuffer) {
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
    public override val zero: Buffer<Double> by lazy { DoubleBuffer(size) { 0.0 } }
    public override val one: Buffer<Double> by lazy { DoubleBuffer(size) { 1.0 } }

    override fun number(value: Number): Buffer<Double> = DoubleBuffer(size) { value.toDouble() }

    override fun Buffer<Double>.unaryMinus(): Buffer<Double> = DoubleBufferFieldOperations.run {
        -this@unaryMinus
    }

    public override fun add(a: Buffer<Double>, b: Buffer<Double>): DoubleBuffer {
        require(a.size == size) { "The buffer size ${a.size} does not match context size $size" }
        return DoubleBufferFieldOperations.add(a, b)
    }

    public override fun scale(a: Buffer<Double>, value: Double): DoubleBuffer {
        require(a.size == size) { "The buffer size ${a.size} does not match context size $size" }

        return if (a is DoubleBuffer) {
            val aArray = a.array
            DoubleBuffer(DoubleArray(a.size) { aArray[it] * value })
        } else DoubleBuffer(DoubleArray(a.size) { a[it] * value })
    }

    public override fun multiply(a: Buffer<Double>, b: Buffer<Double>): DoubleBuffer {
        require(a.size == size) { "The buffer size ${a.size} does not match context size $size" }
        return DoubleBufferFieldOperations.multiply(a, b)
    }

    public override fun divide(a: Buffer<Double>, b: Buffer<Double>): DoubleBuffer {
        require(a.size == size) { "The buffer size ${a.size} does not match context size $size" }
        return DoubleBufferFieldOperations.divide(a, b)
    }

    public override fun sin(arg: Buffer<Double>): DoubleBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return DoubleBufferFieldOperations.sin(arg)
    }

    public override fun cos(arg: Buffer<Double>): DoubleBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return DoubleBufferFieldOperations.cos(arg)
    }

    public override fun tan(arg: Buffer<Double>): DoubleBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return DoubleBufferFieldOperations.tan(arg)
    }

    public override fun asin(arg: Buffer<Double>): DoubleBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return DoubleBufferFieldOperations.asin(arg)
    }

    public override fun acos(arg: Buffer<Double>): DoubleBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return DoubleBufferFieldOperations.acos(arg)
    }

    public override fun atan(arg: Buffer<Double>): DoubleBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return DoubleBufferFieldOperations.atan(arg)
    }

    public override fun sinh(arg: Buffer<Double>): DoubleBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return DoubleBufferFieldOperations.sinh(arg)
    }

    public override fun cosh(arg: Buffer<Double>): DoubleBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return DoubleBufferFieldOperations.cosh(arg)
    }

    public override fun tanh(arg: Buffer<Double>): DoubleBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return DoubleBufferFieldOperations.tanh(arg)
    }

    public override fun asinh(arg: Buffer<Double>): DoubleBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return DoubleBufferFieldOperations.asinh(arg)
    }

    public override fun acosh(arg: Buffer<Double>): DoubleBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return DoubleBufferFieldOperations.acosh(arg)
    }

    public override fun atanh(arg: Buffer<Double>): DoubleBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return DoubleBufferFieldOperations.atanh(arg)
    }

    public override fun power(arg: Buffer<Double>, pow: Number): DoubleBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return DoubleBufferFieldOperations.power(arg, pow)
    }

    public override fun exp(arg: Buffer<Double>): DoubleBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return DoubleBufferFieldOperations.exp(arg)
    }

    public override fun ln(arg: Buffer<Double>): DoubleBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return DoubleBufferFieldOperations.ln(arg)
    }
}
