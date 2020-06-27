package scientifik.kmath.structures

import scientifik.kmath.operations.ExtendedField
import scientifik.kmath.operations.ExtendedFieldOperations
import kotlin.math.*


/**
 * A simple field over linear buffers of [Double]
 */
object RealBufferFieldOperations : ExtendedFieldOperations<Buffer<Double>> {
    override fun add(a: Buffer<Double>, b: Buffer<Double>): RealBuffer {
        require(b.size == a.size) { "The size of the first buffer ${a.size} should be the same as for second one: ${b.size} " }

        return if (a is RealBuffer && b is RealBuffer) {
            val aArray = a.array
            val bArray = b.array
            RealBuffer(DoubleArray(a.size) { aArray[it] + bArray[it] })
        } else
            RealBuffer(DoubleArray(a.size) { a[it] + b[it] })
    }

    override fun multiply(a: Buffer<Double>, k: Number): RealBuffer {
        val kValue = k.toDouble()

        return if (a is RealBuffer) {
            val aArray = a.array
            RealBuffer(DoubleArray(a.size) { aArray[it] * kValue })
        } else
            RealBuffer(DoubleArray(a.size) { a[it] * kValue })
    }

    override fun multiply(a: Buffer<Double>, b: Buffer<Double>): RealBuffer {
        require(b.size == a.size) { "The size of the first buffer ${a.size} should be the same as for second one: ${b.size} " }

        return if (a is RealBuffer && b is RealBuffer) {
            val aArray = a.array
            val bArray = b.array
            RealBuffer(DoubleArray(a.size) { aArray[it] * bArray[it] })
        } else
            RealBuffer(DoubleArray(a.size) { a[it] * b[it] })
    }

    override fun divide(a: Buffer<Double>, b: Buffer<Double>): RealBuffer {
        require(b.size == a.size) { "The size of the first buffer ${a.size} should be the same as for second one: ${b.size} " }

        return if (a is RealBuffer && b is RealBuffer) {
            val aArray = a.array
            val bArray = b.array
            RealBuffer(DoubleArray(a.size) { aArray[it] / bArray[it] })
        } else
            RealBuffer(DoubleArray(a.size) { a[it] / b[it] })
    }

    override fun sin(arg: Buffer<Double>): RealBuffer = if (arg is RealBuffer) {
        val array = arg.array
        RealBuffer(DoubleArray(arg.size) { sin(array[it]) })
    } else {
        RealBuffer(DoubleArray(arg.size) { sin(arg[it]) })
    }

    override fun cos(arg: Buffer<Double>): RealBuffer = if (arg is RealBuffer) {
        val array = arg.array
        RealBuffer(DoubleArray(arg.size) { cos(array[it]) })
    } else
        RealBuffer(DoubleArray(arg.size) { cos(arg[it]) })

    override fun tan(arg: Buffer<Double>): RealBuffer = if (arg is RealBuffer) {
        val array = arg.array
        RealBuffer(DoubleArray(arg.size) { tan(array[it]) })
    } else
        RealBuffer(DoubleArray(arg.size) { tan(arg[it]) })

    override fun asin(arg: Buffer<Double>): RealBuffer = if (arg is RealBuffer) {
        val array = arg.array
        RealBuffer(DoubleArray(arg.size) { asin(array[it]) })
    } else {
        RealBuffer(DoubleArray(arg.size) { asin(arg[it]) })
    }

    override fun acos(arg: Buffer<Double>): RealBuffer = if (arg is RealBuffer) {
        val array = arg.array
        RealBuffer(DoubleArray(arg.size) { acos(array[it]) })
    } else
        RealBuffer(DoubleArray(arg.size) { acos(arg[it]) })

    override fun atan(arg: Buffer<Double>): RealBuffer = if (arg is RealBuffer) {
        val array = arg.array
        RealBuffer(DoubleArray(arg.size) { atan(array[it]) })
    } else
        RealBuffer(DoubleArray(arg.size) { atan(arg[it]) })

    override fun power(arg: Buffer<Double>, pow: Number): RealBuffer = if (arg is RealBuffer) {
        val array = arg.array
        RealBuffer(DoubleArray(arg.size) { array[it].pow(pow.toDouble()) })
    } else
        RealBuffer(DoubleArray(arg.size) { arg[it].pow(pow.toDouble()) })

    override fun exp(arg: Buffer<Double>): RealBuffer = if (arg is RealBuffer) {
        val array = arg.array
        RealBuffer(DoubleArray(arg.size) { exp(array[it]) })
    } else
        RealBuffer(DoubleArray(arg.size) { exp(arg[it]) })

    override fun ln(arg: Buffer<Double>): RealBuffer = if (arg is RealBuffer) {
        val array = arg.array
        RealBuffer(DoubleArray(arg.size) { ln(array[it]) })
    } else
        RealBuffer(DoubleArray(arg.size) { ln(arg[it]) })
}

class RealBufferField(val size: Int) : ExtendedField<Buffer<Double>> {
    override val zero: Buffer<Double> by lazy { RealBuffer(size) { 0.0 } }
    override val one: Buffer<Double> by lazy { RealBuffer(size) { 1.0 } }

    override fun add(a: Buffer<Double>, b: Buffer<Double>): RealBuffer {
        require(a.size == size) { "The buffer size ${a.size} does not match context size $size" }
        return RealBufferFieldOperations.add(a, b)
    }

    override fun multiply(a: Buffer<Double>, k: Number): RealBuffer {
        require(a.size == size) { "The buffer size ${a.size} does not match context size $size" }
        return RealBufferFieldOperations.multiply(a, k)
    }

    override fun multiply(a: Buffer<Double>, b: Buffer<Double>): RealBuffer {
        require(a.size == size) { "The buffer size ${a.size} does not match context size $size" }
        return RealBufferFieldOperations.multiply(a, b)
    }

    override fun divide(a: Buffer<Double>, b: Buffer<Double>): RealBuffer {
        require(a.size == size) { "The buffer size ${a.size} does not match context size $size" }
        return RealBufferFieldOperations.divide(a, b)
    }

    override fun sin(arg: Buffer<Double>): RealBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return RealBufferFieldOperations.sin(arg)
    }

    override fun cos(arg: Buffer<Double>): RealBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return RealBufferFieldOperations.cos(arg)
    }

    override fun tan(arg: Buffer<Double>): RealBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return RealBufferFieldOperations.tan(arg)
    }

    override fun asin(arg: Buffer<Double>): RealBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return RealBufferFieldOperations.asin(arg)
    }

    override fun acos(arg: Buffer<Double>): RealBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return RealBufferFieldOperations.acos(arg)
    }

    override fun atan(arg: Buffer<Double>): RealBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return RealBufferFieldOperations.atan(arg)
    }

    override fun power(arg: Buffer<Double>, pow: Number): RealBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return RealBufferFieldOperations.power(arg, pow)
    }

    override fun exp(arg: Buffer<Double>): RealBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return RealBufferFieldOperations.exp(arg)
    }

    override fun ln(arg: Buffer<Double>): RealBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return RealBufferFieldOperations.ln(arg)
    }
}
