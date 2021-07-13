/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.noa

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


internal fun NoaFloat.testingCopying(device: Device = Device.CPU): Unit {
    val array = (1..24).map { 10f * it * it }.toFloatArray()
    val shape = intArrayOf(2, 3, 4)
    val tensor = copyFromArray(array, shape = shape, device = device)
    val copyOfTensor = tensor.copy()
    tensor[intArrayOf(1, 2, 3)] = 0.1f
    assertTrue(copyOfTensor.copyToArray() contentEquals array)
    assertEquals(0.1f, tensor[intArrayOf(1, 2, 3)])
    if(device != Device.CPU){
        val normalCpu = randNormal(intArrayOf(2, 3))
        val normalGpu = normalCpu.copyToDevice(device)
        assertTrue(normalCpu.copyToArray() contentEquals normalGpu.copyToArray())

        val uniformGpu = randUniform(intArrayOf(3,2),device)
        val uniformCpu = uniformGpu.copyToDevice(Device.CPU)
        assertTrue(uniformGpu.copyToArray() contentEquals uniformCpu.copyToArray())
    }
}


class TestTensor {
    @Test
    fun testCopying(): Unit = NoaFloat {
        withCuda { device ->
            testingCopying(device)
        }
    }!!
}