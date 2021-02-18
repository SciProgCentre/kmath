package space.kscience.kmath.benchmarks

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import java.nio.IntBuffer

@State(Scope.Benchmark)
internal class ArrayBenchmark {
    @Benchmark
    fun benchmarkArrayRead() {
        var res = 0
        for (i in 1..space.kscience.kmath.benchmarks.ArrayBenchmark.Companion.size) res += space.kscience.kmath.benchmarks.ArrayBenchmark.Companion.array[space.kscience.kmath.benchmarks.ArrayBenchmark.Companion.size - i]
    }

    @Benchmark
    fun benchmarkBufferRead() {
        var res = 0
        for (i in 1..space.kscience.kmath.benchmarks.ArrayBenchmark.Companion.size) res += space.kscience.kmath.benchmarks.ArrayBenchmark.Companion.arrayBuffer[space.kscience.kmath.benchmarks.ArrayBenchmark.Companion.size - i]
    }

    @Benchmark
    fun nativeBufferRead() {
        var res = 0
        for (i in 1..space.kscience.kmath.benchmarks.ArrayBenchmark.Companion.size) res += space.kscience.kmath.benchmarks.ArrayBenchmark.Companion.nativeBuffer[space.kscience.kmath.benchmarks.ArrayBenchmark.Companion.size - i]
    }

    companion object {
        const val size: Int = 1000
        val array: IntArray = IntArray(space.kscience.kmath.benchmarks.ArrayBenchmark.Companion.size) { it }
        val arrayBuffer: IntBuffer = IntBuffer.wrap(space.kscience.kmath.benchmarks.ArrayBenchmark.Companion.array)
        val nativeBuffer: IntBuffer = IntBuffer.allocate(space.kscience.kmath.benchmarks.ArrayBenchmark.Companion.size).also { for (i in 0 until space.kscience.kmath.benchmarks.ArrayBenchmark.Companion.size) it.put(i, i) }
    }
}
