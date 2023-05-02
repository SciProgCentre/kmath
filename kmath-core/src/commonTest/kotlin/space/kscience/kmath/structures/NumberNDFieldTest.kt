/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.structures

import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.linear.linearSpace
import space.kscience.kmath.nd.StructureND
import space.kscience.kmath.nd.get
import space.kscience.kmath.nd.ndAlgebra
import space.kscience.kmath.nd.structureND
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.Norm
import space.kscience.kmath.operations.algebra
import space.kscience.kmath.operations.invoke
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.math.abs
import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(PerformancePitfall::class)
@Suppress("UNUSED_VARIABLE")
class NumberNDFieldTest {
    val algebra = DoubleField.ndAlgebra
    val array1 = algebra.structureND(3, 3) { (i, j) -> (i + j).toDouble() }
    val array2 = algebra.structureND(3, 3) { (i, j) -> (i - j).toDouble() }

    @Test
    fun testSum() {
        algebra {
            val sum = array1 + array2
            assertEquals(4.0, sum[2, 2])
        }
    }

    @Test
    fun testProduct() {
        algebra {
            val product = array1 * array2
            assertEquals(0.0, product[2, 2])
        }
    }

    @Test
    fun testGeneration() = Double.algebra.linearSpace.run {

        val array = buildMatrix(3, 3) { i, j ->
            (i * 10 + j).toDouble()
        }

        for (i in 0..2) {
            for (j in 0..2) {
                val expected = (i * 10 + j).toDouble()
                assertEquals(expected, array[i, j], "Error at index [$i, $j]")
            }
        }
    }

    @Test
    fun testExternalFunction() {
        algebra {
            val function: (Double) -> Double = { x -> x.pow(2) + 2 * x + 1 }
            val result = function(array1) + 1.0
            assertEquals(10.0, result[1, 1])
        }
    }

    @Test
    fun testLibraryFunction() {
        algebra {
            val abs: (Double) -> Double = ::abs
            val result = abs(array2)
            assertEquals(2.0, result[0, 2])
        }
    }

    @Test
    fun combineTest() {
        algebra {
            val division = zip(array1, array2) { l, r -> l / r }
        }
    }

    object L2Norm : Norm<StructureND<Number>, Double> {
        @OptIn(PerformancePitfall::class)
        override fun norm(arg: StructureND<Number>): Double =
            kotlin.math.sqrt(arg.elements().sumOf { it.second.toDouble() })
    }

    @Test
    fun testInternalContext() {
        algebra {
            (DoubleField.ndAlgebra(array1.shape)) { with(L2Norm) { 1 + norm(array1) + exp(array2) } }
        }
    }
}
