/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.core

import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.nd.*
import space.kscience.kmath.operations.invoke
import space.kscience.kmath.structures.DoubleBuffer
import space.kscience.kmath.structures.toDoubleArray
import space.kscience.kmath.tensors.core.internal.matrixSequence
import space.kscience.kmath.testutils.assertBufferEquals
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(PerformancePitfall::class)
internal class TestDoubleTensor {

    @Test
    fun testValue() = DoubleTensorAlgebra {
        val value = 12.5
        val tensor = fromArray(ShapeND(1), doubleArrayOf(value))
        assertEquals(tensor.value(), value)
    }

    @OptIn(PerformancePitfall::class)
    @Test
    fun testStrides() = DoubleTensorAlgebra {
        val tensor = fromArray(ShapeND(2, 2), doubleArrayOf(3.5, 5.8, 58.4, 2.4))
        assertEquals(tensor[intArrayOf(0, 1)], 5.8)
        assertTrue(
            tensor.elements().map { it.second }.toList()
                .toDoubleArray() contentEquals tensor.source.toDoubleArray()
        )
    }

    @Test
    fun testGet() = DoubleTensorAlgebra {
        val tensor = fromArray(ShapeND(1, 2, 2), doubleArrayOf(3.5, 5.8, 58.4, 2.4))
        val matrix = tensor.getTensor(0).asDoubleTensor2D()
        assertEquals(matrix[0, 1], 5.8)

        val vector = tensor.getTensor(0, 1).as1D()
        assertEquals(vector[0], 58.4)

        matrix[0, 1] = 77.89
        assertEquals(tensor[intArrayOf(0, 0, 1)], 77.89)

        vector[0] = 109.56
        assertEquals(tensor[intArrayOf(0, 1, 0)], 109.56)

        tensor.matrixSequence().forEach {
            val a = it.asDoubleTensor()
            val secondRow = a.getTensor(1).asDoubleBuffer()
            val secondColumn = a.transposed(0, 1).getTensor(1).asDoubleBuffer()
            assertEquals(secondColumn[0], 77.89)
            assertEquals(secondRow[1], secondColumn[1])
        }
    }

    @Test
    fun testNoBufferProtocol() {

        // create buffer
        val doubleArray = DoubleBuffer(1.0, 2.0, 3.0)

        // create ND buffers, no data is copied
        val ndArray: MutableBufferND<Double> = DoubleBufferND(ColumnStrides(ShapeND(3)), doubleArray)

        // map to tensors
        val tensorArray = ndArray.asDoubleTensor() // Data is copied because of strides change.

        //protective copy
        val tensorArrayPublic = ndArray.copyToTensor() // public API, data copied twice
        val sharedTensorArray = tensorArrayPublic.asDoubleTensor() // no data copied by matching type

        assertTrue(tensorArray.source contentEquals sharedTensorArray.source)

        tensorArray[intArrayOf(0)] = 55.9
        assertEquals(tensorArrayPublic[intArrayOf(0)], 1.0)

        tensorArrayPublic[intArrayOf(0)] = 57.9
        assertEquals(sharedTensorArray[intArrayOf(0)], 57.9)
        assertEquals(tensorArray[intArrayOf(0)], 55.9)

        tensorArray[intArrayOf(0)] = 55.9
        assertEquals(ndArray[intArrayOf(0)], 1.0)
    }

    @Test
    fun test2D() = with(DoubleTensorAlgebra) {
        val tensor: DoubleTensor = structureND(ShapeND(3, 3)) { (i, j) -> (i - j).toDouble() }
        //println(tensor.toPrettyString())
        val tensor2d = tensor.asDoubleTensor2D()
        assertBufferEquals(DoubleBuffer(1.0, 0.0, -1.0), tensor2d.rows[1])
        assertBufferEquals(DoubleBuffer(-2.0, -1.0, 0.0), tensor2d.columns[2])
    }

    @Test
    fun testMatrixIteration() = with(DoubleTensorAlgebra) {
        val tensor = structureND(ShapeND(3, 3, 3, 3)) { index -> index.sum().toDouble() }
        tensor.forEachMatrix { index, matrix ->
            println(index.joinToString { it.toString() })
            println(matrix)
        }
    }
}
