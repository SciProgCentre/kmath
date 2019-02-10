package scientifik.kmath.structures

import scientifik.kmath.operations.Field

/**
 * A simple field over linear buffers of [Double]
 */
class RealBufferField(val size: Int) : Field<Buffer<Double>> {
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
}