package kscience.kmath.torch

import kotlin.test.*

class TestTorchTensorGPU {

    @Test
    fun testCopyFromArray() = testingCopyFromArray(TorchDevice.TorchCUDA(0))

    @Test
    fun testCopyToDevice() = TorchTensorRealAlgebra {
        setSeed(SEED)
        val normalCpu = randNormal(intArrayOf(2, 3))
        val normalGpu = normalCpu.copyToDevice(TorchDevice.TorchCUDA(0))
        assertTrue(normalCpu.copyToArray() contentEquals normalGpu.copyToArray())

        val uniformGpu = randUniform(intArrayOf(3,2),TorchDevice.TorchCUDA(0))
        val uniformCpu = uniformGpu.copyToDevice(TorchDevice.TorchCPU)
        assertTrue(uniformGpu.copyToArray() contentEquals uniformCpu.copyToArray())
    }

}
