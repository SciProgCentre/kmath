package scientifik.kmath.structures

import scientifik.kmath.operations.ExtendedField
import scientifik.kmath.operations.ExtendedFieldOperations
import kotlin.math.*


/**
 * A simple field over linear buffers of [Double]
 */
object RealBufferFieldOperations : ExtendedFieldOperations<Buffer<Double>> {

    override fun add(a: Buffer<Double>, b: Buffer<Double>): DoubleBuffer {
        require(b.size == a.size) { "The size of the first buffer ${a.size} should be the same as for second one: ${b.size} " }

        return if (a is DoubleBuffer && b is DoubleBuffer) {
            val aArray = a.array
            val bArray = b.array
            DoubleBuffer(DoubleArray(a.size) { aArray[it] + bArray[it] })
        } else {
            DoubleBuffer(DoubleArray(a.size) { a[it] + b[it] })
        }
    }

    override fun multiply(a: Buffer<Double>, k: Number): DoubleBuffer {
        val kValue = k.toDouble()

        return if (a is DoubleBuffer) {
            val aArray = a.array
            DoubleBuffer(DoubleArray(a.size) { aArray[it] * kValue })
        } else {
            DoubleBuffer(DoubleArray(a.size) { a[it] * kValue })
        }
    }

    override fun multiply(a: Buffer<Double>, b: Buffer<Double>): DoubleBuffer {
        require(b.size == a.size) { "The size of the first buffer ${a.size} should be the same as for second one: ${b.size} " }

        return if (a is DoubleBuffer && b is DoubleBuffer) {
            val aArray = a.array
            val bArray = b.array
            DoubleBuffer(DoubleArray(a.size) { aArray[it] * bArray[it] })
        } else {
            DoubleBuffer(DoubleArray(a.size) { a[it] * b[it] })
        }
    }

    override fun divide(a: Buffer<Double>, b: Buffer<Double>): DoubleBuffer {
        require(b.size == a.size) { "The size of the first buffer ${a.size} should be the same as for second one: ${b.size} " }

        return if (a is DoubleBuffer && b is DoubleBuffer) {
            val aArray = a.array
            val bArray = b.array
            DoubleBuffer(DoubleArray(a.size) { aArray[it] / bArray[it] })
        } else {
            DoubleBuffer(DoubleArray(a.size) { a[it] / b[it] })
        }
    }

    override fun sin(arg: Buffer<Double>): DoubleBuffer = if (arg is DoubleBuffer) {
        val array = arg.array
        DoubleBuffer(DoubleArray(arg.size) { sin(array[it]) })
    } else {
        DoubleBuffer(DoubleArray(arg.size) { sin(arg[it]) })
    }

    override fun cos(arg: Buffer<Double>): DoubleBuffer = if (arg is DoubleBuffer) {
        val array = arg.array
        DoubleBuffer(DoubleArray(arg.size) { cos(array[it]) })
    } else {
        DoubleBuffer(DoubleArray(arg.size) { cos(arg[it]) })
    }

    override fun tan(arg: Buffer<Double>): DoubleBuffer = if (arg is DoubleBuffer) {
        val array = arg.array
        DoubleBuffer(DoubleArray(arg.size) { tan(array[it]) })
    } else {
        DoubleBuffer(DoubleArray(arg.size) { tan(arg[it]) })
    }

    override fun asin(arg: Buffer<Double>): DoubleBuffer = if (arg is DoubleBuffer) {
        val array = arg.array
        DoubleBuffer(DoubleArray(arg.size) { asin(array[it]) })
    } else {
        DoubleBuffer(DoubleArray(arg.size) { asin(arg[it]) })
    }

    override fun acos(arg: Buffer<Double>): DoubleBuffer = if (arg is DoubleBuffer) {
        val array = arg.array
        DoubleBuffer(DoubleArray(arg.size) { acos(array[it]) })
    } else {
        DoubleBuffer(DoubleArray(arg.size) { acos(arg[it]) })
    }

    override fun atan(arg: Buffer<Double>): DoubleBuffer = if (arg is DoubleBuffer) {
        val array = arg.array
        DoubleBuffer(DoubleArray(arg.size) { atan(array[it]) })
    } else {
        DoubleBuffer(DoubleArray(arg.size) { atan(arg[it]) })
    }

    override fun power(arg: Buffer<Double>, pow: Number): DoubleBuffer = if (arg is DoubleBuffer) {
        val array = arg.array
        DoubleBuffer(DoubleArray(arg.size) { array[it].pow(pow.toDouble()) })
    } else {
        DoubleBuffer(DoubleArray(arg.size) { arg[it].pow(pow.toDouble()) })
    }

    override fun exp(arg: Buffer<Double>): DoubleBuffer = if (arg is DoubleBuffer) {
        val array = arg.array
        DoubleBuffer(DoubleArray(arg.size) { exp(array[it]) })
    } else {
        DoubleBuffer(DoubleArray(arg.size) { exp(arg[it]) })
    }

    override fun ln(arg: Buffer<Double>): DoubleBuffer = if (arg is DoubleBuffer) {
        val array = arg.array
        DoubleBuffer(DoubleArray(arg.size) { ln(array[it]) })
    } else {
        DoubleBuffer(DoubleArray(arg.size) { ln(arg[it]) })
    }
}

class RealBufferField(val size: Int) : ExtendedField<Buffer<Double>> {

    override val zero: Buffer<Double> by lazy { DoubleBuffer(size) { 0.0 } }

    override val one: Buffer<Double> by lazy { DoubleBuffer(size) { 1.0 } }

    override fun add(a: Buffer<Double>, b: Buffer<Double>): DoubleBuffer {
        require(a.size == size) { "The buffer size ${a.size} does not match context size $size" }
        return RealBufferFieldOperations.add(a, b)
    }

    override fun multiply(a: Buffer<Double>, k: Number): DoubleBuffer {
        require(a.size == size) { "The buffer size ${a.size} does not match context size $size" }
        return RealBufferFieldOperations.multiply(a, k)
    }

    override fun multiply(a: Buffer<Double>, b: Buffer<Double>): DoubleBuffer {
        require(a.size == size) { "The buffer size ${a.size} does not match context size $size" }
        return RealBufferFieldOperations.multiply(a, b)
    }

    override fun divide(a: Buffer<Double>, b: Buffer<Double>): DoubleBuffer {
        require(a.size == size) { "The buffer size ${a.size} does not match context size $size" }
        return RealBufferFieldOperations.divide(a, b)
    }

    override fun sin(arg: Buffer<Double>): DoubleBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return RealBufferFieldOperations.sin(arg)
    }

    override fun cos(arg: Buffer<Double>): DoubleBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return RealBufferFieldOperations.cos(arg)
    }

    override fun tan(arg: Buffer<Double>): DoubleBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return RealBufferFieldOperations.tan(arg)
    }

    override fun asin(arg: Buffer<Double>): DoubleBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return RealBufferFieldOperations.asin(arg)
    }

    override fun acos(arg: Buffer<Double>): DoubleBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return RealBufferFieldOperations.acos(arg)
    }

    override fun atan(arg: Buffer<Double>): DoubleBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return RealBufferFieldOperations.atan(arg)
    }

    override fun power(arg: Buffer<Double>, pow: Number): DoubleBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return RealBufferFieldOperations.power(arg, pow)
    }

    override fun exp(arg: Buffer<Double>): DoubleBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return RealBufferFieldOperations.exp(arg)
    }

    override fun ln(arg: Buffer<Double>): DoubleBuffer {
        require(arg.size == size) { "The buffer size ${arg.size} does not match context size $size" }
        return RealBufferFieldOperations.ln(arg)
    }
}
