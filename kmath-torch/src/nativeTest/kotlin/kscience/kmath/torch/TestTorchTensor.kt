package kscience.kmath.torch

import kotlinx.cinterop.*
import kotlin.test.*


class TestTorchTensor {

    @Test
    fun testCopying() = TorchTensorFloatAlgebra {
        withCuda {
            device -> testingCopying(device)
        }
    }

    @Test
    fun testNativeNoCopyDataTransferOnCPU() = memScoped {
        val data = allocArray<DoubleVar>(1)
        data[0] = 1.0
        TorchTensorRealAlgebra {
            val tensor = fromBlob(data, intArrayOf(1))
            assertEquals(tensor[intArrayOf(0)], 1.0)
            data[0] = 2.0
            assertEquals(tensor[intArrayOf(0)], 2.0)
            val tensorData = tensor.getData()
            tensorData[0] = 3.0
            assertEquals(tensor[intArrayOf(0)], 3.0)
        }
        assertEquals(data[0], 3.0)
    }

    @Test
    fun testRequiresGrad() = TorchTensorRealAlgebra {
        testingRequiresGrad()
    }

    @Test
    fun testTypeMoving() = TorchTensorFloatAlgebra {
        val tensorInt = copyFromArray(floatArrayOf(1f, 2f, 3f), intArrayOf(3)).copyToInt()
        TorchTensorIntAlgebra {
            val temporalTensor = copyFromArray(intArrayOf(4, 5, 6), intArrayOf(3))
            tensorInt swap temporalTensor
            assertTrue(temporalTensor.copyToArray() contentEquals intArrayOf(1, 2, 3))
        }
        assertTrue(tensorInt.copyToFloat().copyToArray() contentEquals floatArrayOf(4f, 5f, 6f))
    }

    @Test
    fun testViewWithNoCopy() = TorchTensorIntAlgebra {
        withChecks {
            withCuda {
                    device ->  testingViewWithNoCopy(device)
            }
        }
    }

}