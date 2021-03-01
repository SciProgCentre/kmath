package space.kscience.kmath.benchmarks

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import space.kscience.kmath.complex.Complex
import space.kscience.kmath.complex.complex
import space.kscience.kmath.structures.MutableBuffer
import space.kscience.kmath.structures.RealBuffer

@State(Scope.Benchmark)
internal class BufferBenchmark {
    @Benchmark
    fun genericRealBufferReadWrite() {
        val buffer = RealBuffer(size) { it.toDouble() }

        (0 until size).forEach {
            buffer[it]
        }
    }

    @Benchmark
    fun complexBufferReadWrite() {
        val buffer = MutableBuffer.complex(size / 2) { Complex(it.toDouble(), -it.toDouble()) }

        (0 until size / 2).forEach {
            buffer[it]
        }
    }

    companion object {
        const val size: Int = 100
    }
}
