package space.kscience.kmath.benchmarks

import kotlinx.benchmark.Blackhole
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import space.kscience.kmath.operations.BigIntField
import space.kscience.kmath.operations.JBigIntegerField
import space.kscience.kmath.operations.invoke

@State(Scope.Benchmark)
internal class BigIntBenchmark {
    @Benchmark
    fun kmAdd(blackhole: Blackhole) = BigIntField{
        blackhole.consume(one + number(Int.MAX_VALUE))
    }

    @Benchmark
    fun jvmAdd(blackhole: Blackhole) = JBigIntegerField{
        blackhole.consume(one + number(Int.MAX_VALUE))
    }

    @Benchmark
    fun kmMultiply(blackhole: Blackhole) = BigIntField{
        blackhole.consume(number(Int.MAX_VALUE)* number(Int.MAX_VALUE))
    }

    @Benchmark
    fun jvmMultiply(blackhole: Blackhole) = JBigIntegerField{
        blackhole.consume(number(Int.MAX_VALUE)* number(Int.MAX_VALUE))
    }
}