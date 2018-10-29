package scientifik.kmath.structures

import org.openjdk.jmh.annotations.*
import java.nio.IntBuffer


@Fork(1)
@Warmup(iterations = 2)
@Measurement(iterations = 5)
@State(Scope.Benchmark)
open class ArrayBenchmark {

    lateinit var array: IntArray
    lateinit var arrayBuffer: IntBuffer
    lateinit var nativeBuffer: IntBuffer

    @Setup
    fun setup() {
        array = IntArray(10000) { it }
        arrayBuffer = IntBuffer.wrap(array)
        nativeBuffer = IntBuffer.allocate(10000)
        for (i in 0 until 10000) {
            nativeBuffer.put(i,i)
        }
    }

    @Benchmark
    fun benchmarkArrayRead() {
        var res = 0
        for (i in 1..10000) {
            res += array[10000 - i]
        }
        print(res)
    }

    @Benchmark
    fun benchmarkBufferRead() {
        var res = 0
        for (i in 1..10000) {
            res += arrayBuffer.get(10000 - i)
        }
        print(res)
    }

    @Benchmark
    fun nativeBufferRead() {
        var res = 0
        for (i in 1..10000) {
            res += nativeBuffer.get(10000 - i)
        }
        print(res)
    }
}