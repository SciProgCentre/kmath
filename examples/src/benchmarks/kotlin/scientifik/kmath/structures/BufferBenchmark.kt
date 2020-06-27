package scientifik.kmath.structures

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import scientifik.kmath.operations.Complex
import scientifik.kmath.operations.complex

@State(Scope.Benchmark)
class BufferBenchmark {

    @Benchmark
    fun genericRealBufferReadWrite() {
        val buffer = RealBuffer(size){it.toDouble()}

        (0 until size).forEach {
            buffer[it]
        }
    }

    @Benchmark
    fun complexBufferReadWrite() {
        val buffer = MutableBuffer.complex(size / 2){Complex(it.toDouble(), -it.toDouble())}

        (0 until size / 2).forEach {
            buffer[it]
        }
    }

    companion object {
        const val size = 100
    }
}