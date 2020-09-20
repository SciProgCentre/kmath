package kscience.kmath.structures

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import java.nio.IntBuffer

@State(Scope.Benchmark)
class ArrayBenchmark {
    @Benchmark
    fun benchmarkArrayRead() {
        var res = 0
        for (i in 1.._root_ide_package_.kscience.kmath.structures.ArrayBenchmark.Companion.size) res += _root_ide_package_.kscience.kmath.structures.ArrayBenchmark.Companion.array[_root_ide_package_.kscience.kmath.structures.ArrayBenchmark.Companion.size - i]
    }

    @Benchmark
    fun benchmarkBufferRead() {
        var res = 0
        for (i in 1.._root_ide_package_.kscience.kmath.structures.ArrayBenchmark.Companion.size) res += _root_ide_package_.kscience.kmath.structures.ArrayBenchmark.Companion.arrayBuffer.get(
            _root_ide_package_.kscience.kmath.structures.ArrayBenchmark.Companion.size - i)
    }

    @Benchmark
    fun nativeBufferRead() {
        var res = 0
        for (i in 1.._root_ide_package_.kscience.kmath.structures.ArrayBenchmark.Companion.size) res += _root_ide_package_.kscience.kmath.structures.ArrayBenchmark.Companion.nativeBuffer.get(
            _root_ide_package_.kscience.kmath.structures.ArrayBenchmark.Companion.size - i)
    }

    companion object {
        const val size: Int = 1000
        val array: IntArray = IntArray(_root_ide_package_.kscience.kmath.structures.ArrayBenchmark.Companion.size) { it }
        val arrayBuffer: IntBuffer = IntBuffer.wrap(_root_ide_package_.kscience.kmath.structures.ArrayBenchmark.Companion.array)

        val nativeBuffer: IntBuffer = IntBuffer.allocate(_root_ide_package_.kscience.kmath.structures.ArrayBenchmark.Companion.size).also {
            for (i in 0 until _root_ide_package_.kscience.kmath.structures.ArrayBenchmark.Companion.size) it.put(i, i)
        }
    }
}
