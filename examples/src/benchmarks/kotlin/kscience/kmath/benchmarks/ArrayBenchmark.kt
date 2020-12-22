package kscience.kmath.benchmarks

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import java.nio.IntBuffer

@State(Scope.Benchmark)
internal class ArrayBenchmark {
    @Benchmark
    fun benchmarkArrayRead() {
        var res = 0
        for (i in 1..size) res += array[size - i]
    }

    @Benchmark
    fun benchmarkBufferRead() {
        var res = 0
        for (i in 1..size) res += arrayBuffer.get(
            size - i
        )
    }

    @Benchmark
    fun nativeBufferRead() {
        var res = 0
        for (i in 1..size) res += nativeBuffer.get(
            size - i
        )
    }

    companion object {
        const val size: Int = 1000
        val array: IntArray = IntArray(size) { it }
        val arrayBuffer: IntBuffer = IntBuffer.wrap(array)
        val nativeBuffer: IntBuffer = IntBuffer.allocate(size).also { for (i in 0 until size) it.put(i, i) }
    }
}
