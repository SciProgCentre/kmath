package kscience.kmath.torch

import kotlin.test.Test


internal class BenchmarkRandomGenerators {

    @Test
    fun benchmarkRand1() = TorchTensorFloatAlgebra{
        benchmarkingRand1()
    }

    @Test
    fun benchmarkRand3() = TorchTensorFloatAlgebra{
        benchmarkingRand3()
    }

    @Test
    fun benchmarkRand5() = TorchTensorFloatAlgebra{
        benchmarkingRand5()
    }

    @Test
    fun benchmarkRand7() = TorchTensorFloatAlgebra{
        benchmarkingRand7()
    }

}