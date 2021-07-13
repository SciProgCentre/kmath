/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.noa

import kotlin.test.Test
import kotlin.test.assertEquals


internal val SEED = 987654
internal val TOLERANCE = 1e-6

internal fun <T, ArrayT, TensorT : NoaTensor<T>, AlgebraT : NoaAlgebra<T, ArrayT, TensorT>>
        AlgebraT.withCuda(block: AlgebraT.(Device) -> Unit): Unit {
    this.block(Device.CPU)
    if (cudaAvailable()) this.block(Device.CUDA(0))
}

internal fun NoaFloat.testingSetSeed(device: Device = Device.CPU): Unit {
    setSeed(SEED)
    val integral = randDiscrete(0, 100, IntArray(0), device = device).value()
    val normal = randNormal(IntArray(0), device = device).value()
    val uniform = randUniform(IntArray(0), device = device).value()
    setSeed(SEED)
    val nextIntegral = randDiscrete(0, 100, IntArray(0), device = device).value()
    val nextNormal = randNormal(IntArray(0), device = device).value()
    val nextUniform = randUniform(IntArray(0), device = device).value()
    assertEquals(normal, nextNormal)
    assertEquals(uniform, nextUniform)
    assertEquals(integral, nextIntegral)
}


class TestUtils {

    @Test
    fun testException() {
        val i = try {
            JNoa.testException(5)
        } catch (e: NoaException) {
            10
        }
        assertEquals(i, 10)
    }

    @Test
    fun testSetNumThreads(){
        val numThreads = 2
        setNumThreads(numThreads)
        assertEquals(numThreads, getNumThreads())
    }

    @Test
    fun testSetSeed(): Unit = NoaFloat {
        withCuda { device ->
            testingSetSeed(device)
        }
    }!!


    
}
