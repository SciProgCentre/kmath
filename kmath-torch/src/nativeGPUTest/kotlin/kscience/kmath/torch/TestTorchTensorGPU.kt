package kscience.kmath.torch

import kotlin.test.*

class TestTorchTensorGPU {

    @Test
    fun testCopyFromArray() = testingCopyFromArray(Device.CUDA(0))

    @Test
    fun testCopyToDevice() = TorchTensorRealAlgebra {
        setSeed(SEED)
        val normalCpu = randNormal(intArrayOf(2, 3))
        val normalGpu = normalCpu.copyToDevice(Device.CUDA(0))
        assertTrue(normalCpu.copyToArray() contentEquals normalGpu.copyToArray())

        val uniformGpu = randUniform(intArrayOf(3,2),Device.CUDA(0))
        val uniformCpu = uniformGpu.copyToDevice(Device.CPU)
        assertTrue(uniformGpu.copyToArray() contentEquals uniformCpu.copyToArray())
    }

}
