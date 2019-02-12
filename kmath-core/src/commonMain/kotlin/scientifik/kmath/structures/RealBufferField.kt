package scientifik.kmath.structures

import scientifik.kmath.operations.ExtendedField
import kotlin.math.*

/**
 * A simple field over linear buffers of [Double]
 */
class RealBufferField(val size: Int) : ExtendedField<Buffer<Double>> {
    override val zero: Buffer<Double> = Buffer.DoubleBufferFactory(size) { 0.0 }

    override val one: Buffer<Double> = Buffer.DoubleBufferFactory(size) { 1.0 }

    override fun add(a: Buffer<Double>, b: Buffer<Double>): DoubleBuffer {
        require(a.size == size) { "The size of buffer is ${a.size} but context requires $size " }
        require(b.size == size) { "The size of buffer is ${b.size} but context requires $size " }
        return if (a is DoubleBuffer && b is DoubleBuffer) {
            val aArray = a.array
            val bArray = b.array
            DoubleBuffer(DoubleArray(size) { aArray[it] + bArray[it] })
        } else {
            DoubleBuffer(DoubleArray(size) { a[it] + b[it] })
        }
    }

    override fun multiply(a: Buffer<Double>, k: Number): DoubleBuffer {
        require(a.size == size) { "The size of buffer is ${a.size} but context requires $size " }
        val kValue = k.toDouble()
        return if (a is DoubleBuffer) {
            val aArray = a.array
            DoubleBuffer(DoubleArray(size) { aArray[it] * kValue })
        } else {
            DoubleBuffer(DoubleArray(size) { a[it] * kValue })
        }
    }

    override fun multiply(a: Buffer<Double>, b: Buffer<Double>): DoubleBuffer {
        require(a.size == size) { "The size of buffer is ${a.size} but context requires $size " }
        require(b.size == size) { "The size of buffer is ${b.size} but context requires $size " }
        return if (a is DoubleBuffer && b is DoubleBuffer) {
            val aArray = a.array
            val bArray = b.array
            DoubleBuffer(DoubleArray(size) { aArray[it] * bArray[it] })
        } else {
            DoubleBuffer(DoubleArray(size) { a[it] * b[it] })
        }
    }

    override fun divide(a: Buffer<Double>, b: Buffer<Double>): DoubleBuffer {
        require(a.size == size) { "The size of buffer is ${a.size} but context requires $size " }
        require(b.size == size) { "The size of buffer is ${b.size} but context requires $size " }
        return if (a is DoubleBuffer && b is DoubleBuffer) {
            val aArray = a.array
            val bArray = b.array
            DoubleBuffer(DoubleArray(size) { aArray[it] / bArray[it] })
        } else {
            DoubleBuffer(DoubleArray(size) { a[it] / b[it] })
        }
    }

    override fun sin(arg: Buffer<Double>): Buffer<Double> {
        require(arg.size == size) { "The size of buffer is ${arg.size} but context requires $size " }
        return if (arg is DoubleBuffer) {
            val array = arg.array
            DoubleBuffer(DoubleArray(size) { sin(array[it]) })
        } else {
            DoubleBuffer(DoubleArray(size) { sin(arg[it]) })
        }
    }

    override fun cos(arg: Buffer<Double>): Buffer<Double> {
        require(arg.size == size) { "The size of buffer is ${arg.size} but context requires $size " }
        return if (arg is DoubleBuffer) {
            val array = arg.array
            DoubleBuffer(DoubleArray(size) { cos(array[it]) })
        } else {
            DoubleBuffer(DoubleArray(size) { cos(arg[it]) })
        }
    }

    override fun power(arg: Buffer<Double>, pow: Number): Buffer<Double> {
        require(arg.size == size) { "The size of buffer is ${arg.size} but context requires $size " }
        return if (arg is DoubleBuffer) {
            val array = arg.array
            DoubleBuffer(DoubleArray(size) { array[it].pow(pow.toDouble()) })
        } else {
            DoubleBuffer(DoubleArray(size) { arg[it].pow(pow.toDouble()) })
        }
    }

    override fun exp(arg: Buffer<Double>): Buffer<Double> {
        require(arg.size == size) { "The size of buffer is ${arg.size} but context requires $size " }
        return if (arg is DoubleBuffer) {
            val array = arg.array
            DoubleBuffer(DoubleArray(size) { exp(array[it]) })
        } else {
            DoubleBuffer(DoubleArray(size) { exp(arg[it]) })
        }
    }

    override fun ln(arg: Buffer<Double>): Buffer<Double> {
        require(arg.size == size) { "The size of buffer is ${arg.size} but context requires $size " }
        return if (arg is DoubleBuffer) {
            val array = arg.array
            DoubleBuffer(DoubleArray(size) { ln(array[it]) })
        } else {
            DoubleBuffer(DoubleArray(size) { ln(arg[it]) })
        }
    }
}