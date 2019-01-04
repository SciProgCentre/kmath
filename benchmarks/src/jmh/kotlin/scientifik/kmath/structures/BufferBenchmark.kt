package scientifik.kmath.structures

import org.openjdk.jmh.annotations.*
import scientifik.kmath.operations.Complex

@Warmup(iterations = 1)
@Measurement(iterations = 5)
@State(Scope.Benchmark)
open class BufferBenchmark {

    @Benchmark
    fun genericDoubleBufferReadWrite() {
        val buffer = Double.createBuffer(size)
        (0 until size).forEach {
            buffer[it] = it.toDouble()
        }

        (0 until size).forEach {
            buffer[it]
        }
    }

    @Benchmark
    fun complexBufferReadWrite() {
        val buffer = Complex.createBuffer(size / 2)
        (0 until size / 2).forEach {
            buffer[it] = Complex(it.toDouble(), -it.toDouble())
        }

        (0 until size / 2).forEach {
            buffer[it]
        }
    }

    companion object {
        const val size = 1000
    }
}