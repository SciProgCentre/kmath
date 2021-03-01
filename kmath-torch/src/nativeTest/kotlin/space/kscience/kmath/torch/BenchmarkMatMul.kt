package space.kscience.kmath.torch

import kotlin.test.Test


internal class BenchmarkMatMul {

    @Test
    fun benchmarkMatMulDouble() = TorchTensorRealAlgebra {
        benchmarkMatMul(20, 10, 100000, "Real")
        benchmarkMatMul(200, 10, 10000, "Real")
        benchmarkMatMul(2000, 3, 20, "Real")
    }

    @Test
    fun benchmarkMatMulFloat() = TorchTensorFloatAlgebra {
        benchmarkMatMul(20, 10, 100000, "Float")
        benchmarkMatMul(200, 10, 10000, "Float")
        benchmarkMatMul(2000, 3, 20, "Float")
        if (cudaAvailable()) {
            benchmarkMatMul(20, 10, 100000, "Float", space.kscience.kmath.torch.Device.CUDA(0))
            benchmarkMatMul(200, 10, 10000, "Float", space.kscience.kmath.torch.Device.CUDA(0))
            benchmarkMatMul(2000, 10, 1000, "Float", space.kscience.kmath.torch.Device.CUDA(0))
        }
    }
}