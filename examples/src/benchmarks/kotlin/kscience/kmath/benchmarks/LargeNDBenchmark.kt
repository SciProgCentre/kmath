package kscience.kmath.benchmarks

import kscience.kmath.structures.NDField
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.infra.Blackhole
import kotlin.random.Random

@State(Scope.Benchmark)
class LargeNDBenchmark {
    val arraySize = 10000
    val RANDOM = Random(222)
    val src1 = DoubleArray(arraySize) { RANDOM.nextDouble() }
    val src2 = DoubleArray(arraySize) { RANDOM.nextDouble() }
    val field = NDField.real(arraySize)
    val kmathArray1 = field.produce { (a) -> src1[a] }
    val kmathArray2 = field.produce { (a) -> src2[a] }

    @Benchmark
    fun test10000(bh: Blackhole) {
        bh.consume(field.add(kmathArray1, kmathArray2))
    }

}