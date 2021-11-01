/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.noa

import space.kscience.kmath.noa.memory.NoaScope
import space.kscience.kmath.operations.Ring
import kotlin.test.Test
import kotlin.test.assertEquals


internal val SEED = 987654
internal val TOLERANCE = 1e-6

internal fun <T, A : Ring<T>, ArrayT, TensorT : NoaTensor<T>, AlgebraT : NoaAlgebra<T, A, ArrayT, TensorT>>
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
    fun testSetNumThreads() {
        val numThreads = 2
        setNumThreads(numThreads)
        assertEquals(numThreads, getNumThreads())
    }

    @Test
    fun testSetSeed() = NoaFloat {
        withCuda { device ->
            testingSetSeed(device)
        }
    }!!

    @Test
    fun testScoping(): Unit {
        val scope = NoaScope()
        val tensor = NoaFloat(scope){
             full(5f, intArrayOf(1))
        }!!
        assertEquals(tensor.numElements, 1)
        assertEquals(scope.disposables.size, 1)
        scope.disposeAll()
        assertEquals(scope.disposables.size, 0)
    }

}
