/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.benchmarks

import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State
import space.kscience.kmath.commons.linear.CMLinearSpace
import space.kscience.kmath.ejml.EjmlLinearSpaceDDRM
import space.kscience.kmath.linear.Float64ParallelLinearSpace
import space.kscience.kmath.linear.invoke
import space.kscience.kmath.linear.linearSpace
import space.kscience.kmath.operations.Float64Field
import space.kscience.kmath.tensorflow.produceWithTF
import space.kscience.kmath.tensors.core.tensorAlgebra
import kotlin.random.Random

@State(Scope.Benchmark)
internal class DotBenchmark {
    companion object {
        val random = Random(12224)
        const val dim = 1000

        //creating invertible matrix
        val matrix1 = Float64Field.linearSpace.buildMatrix(dim, dim) { _, _ ->
            random.nextDouble()
        }
        val matrix2 = Float64Field.linearSpace.buildMatrix(dim, dim) { _, _ ->
            random.nextDouble()
        }

        val cmMatrix1 = CMLinearSpace { matrix1.toCM() }
        val cmMatrix2 = CMLinearSpace { matrix2.toCM() }

        val ejmlMatrix1 = EjmlLinearSpaceDDRM { matrix1.toEjml() }
        val ejmlMatrix2 = EjmlLinearSpaceDDRM { matrix2.toEjml() }
    }


    @Benchmark
    fun tfDot(blackhole: Blackhole) {
        blackhole.consume(
            Float64Field.produceWithTF {
                matrix1 dot matrix1
            }
        )
    }

    @Benchmark
    fun cmDotWithConversion(blackhole: Blackhole) = CMLinearSpace {
        blackhole.consume(matrix1 dot matrix2)
    }

    @Benchmark
    fun cmDot(blackhole: Blackhole) = CMLinearSpace {
        blackhole.consume(cmMatrix1 dot cmMatrix2)
    }

    @Benchmark
    fun ejmlDot(blackhole: Blackhole) = EjmlLinearSpaceDDRM {
        blackhole.consume(ejmlMatrix1 dot ejmlMatrix2)
    }

    @Benchmark
    fun ejmlDotWithConversion(blackhole: Blackhole) = EjmlLinearSpaceDDRM {
        blackhole.consume(matrix1 dot matrix2)
    }

    @Benchmark
    fun multikDot(blackhole: Blackhole) = with(multikAlgebra) {
        blackhole.consume(matrix1 dot matrix2)
    }

    @Benchmark
    fun tensorDot(blackhole: Blackhole) = with(Float64Field.tensorAlgebra) {
        blackhole.consume(matrix1 dot matrix2)
    }

    @Benchmark
    fun bufferedDot(blackhole: Blackhole) = with(Float64Field.linearSpace) {
        blackhole.consume(matrix1 dot matrix2)
    }

    @Benchmark
    fun parallelDot(blackhole: Blackhole) = with(Float64ParallelLinearSpace) {
        blackhole.consume(matrix1 dot matrix2)
    }

}
