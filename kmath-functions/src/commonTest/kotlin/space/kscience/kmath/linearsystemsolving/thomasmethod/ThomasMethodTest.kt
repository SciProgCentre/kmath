/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.linearsystemsolving.thomasmethod

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.linear.Point
import space.kscience.kmath.linear.linearSpace
import space.kscience.kmath.linear.matrix
import space.kscience.kmath.linear.vector
import space.kscience.kmath.operations.algebra
import space.kscience.kmath.structures.toDoubleArray
import kotlin.test.*

@UnstableKMathAPI
internal class ThomasMethodTest {

    /**
     * Positive test,
     * WHEN matrix 'A' is 3x3 tridiagonal matrix.
     */
    @Test
    fun positiveTest1() =
        Double.algebra.linearSpace.run {
            val matrixA = matrix(3, 3)(
                2.0, -1.0, 0.0,
                5.0, 4.0, 2.0,
                0.0, 1.0, -3.0,
            )

            val vectorB = vector(
                3.0, 6.0, 2.0,
            )

            val result: Point<Double> = solveSystemByThomasMethod(matrixA, vectorB)

            assertEquals(3, result.size)
            assertTrue(result[0].toString().startsWith("1.4883"))
            assertTrue(result[1].toString().startsWith("-0.0232"))
            assertTrue(result[2].toString().startsWith("-0.6744"))

            assertContentEquals(vectorB.toDoubleArray(), matrixA.dot(result).array)
        }

    /**
     * Positive test,
     * WHEN matrix 'A' is 4x4 tridiagonal matrix.
     */
    @Test
    fun positiveTest2() =
        Double.algebra.linearSpace.run {
            val matrixA = matrix(4, 4)(
                4.0, 1.0, 0.0, 0.0,
                1.0, 4.0, 1.0, 0.0,
                0.0, 1.0, 4.0, 1.0,
                0.0, 0.0, 1.0, 4.0,
            )

            val vectorB = vector(
                5.0, 6.0, 6.0, 5.0,
            )

            val result: Point<Double> = solveSystemByThomasMethod(matrixA, vectorB)

            assertEquals(4, result.size)
            assertEquals(1.0, result[0])
            assertEquals(1.0, result[1])
            assertEquals(1.0, result[2])
            assertEquals(1.0, result[3])

            assertContentEquals(vectorB.toDoubleArray(), matrixA.dot(result).array)
        }

    /**
     * Exception test,
     * WHEN matrix 'A' is not square matrix.
     */
    @Test
    fun exceptionTest1() =
        Double.algebra.linearSpace.run {
            val matrixA = matrix(3, 4)(
                4.0, 1.0, 0.0, 0.0,
                1.0, 4.0, 1.0, 0.0,
                0.0, 1.0, 4.0, 1.0,
            )

            val vectorB = vector(
                5.0, 6.0, 6.0, 5.0,
            )

            assertFails {
                solveSystemByThomasMethod(matrixA, vectorB)
            }
            assertTrue(true) // Needs, because single assertFails is not supporting:
            // Execution failed for task ':kmath-functions:jvmTest'.
            // > No tests found for given includes: [space.kscience.kmath.linearsystemsolving.thomasmethod.ThomasMethodTest.exceptionTestWhenAisNotSquareMatrix](--tests filter)
        }

    /**
     * Exception test,
     * WHEN the dimension of the matrix 'A' is bigger than the dimension of the vector 'B'.
     */
    @Test
    fun exceptionTest2() =
        Double.algebra.linearSpace.run {
            val matrixA = matrix(4, 4)(
                4.0, 1.0, 0.0, 0.0,
                1.0, 4.0, 1.0, 0.0,
                0.0, 1.0, 4.0, 1.0,
                0.0, 0.0, 1.0, 4.0,
            )

            val vectorB = vector(
                5.0, 6.0, 6.0,
            )

            assertFails {
                solveSystemByThomasMethod(matrixA, vectorB)
            }
            assertTrue(true) // Needs, because single assertFails is not supporting:
            // Execution failed for task ':kmath-functions:jvmTest'.
            // > No tests found for given includes: [space.kscience.kmath.linearsystemsolving.thomasmethod.ThomasMethodTest.exceptionTestWhenAisNotSquareMatrix](--tests filter)
        }

    /**
     * Exception test,
     * WHEN the dimension of the matrix 'A' is smaller than the dimension of the vector 'B'.
     */
    @Test
    fun exceptionTest3() =
        Double.algebra.linearSpace.run {
            val matrixA = matrix(4, 4)(
                4.0, 1.0, 0.0, 0.0,
                1.0, 4.0, 1.0, 0.0,
                0.0, 1.0, 4.0, 1.0,
                0.0, 0.0, 1.0, 4.0,
            )

            val vectorB = vector(
                5.0, 6.0, 6.0, 5.0, 7.0,
            )

            assertFails {
                solveSystemByThomasMethod(matrixA, vectorB)
            }
            assertTrue(true) // Needs, because single assertFails is not supporting:
            // Execution failed for task ':kmath-functions:jvmTest'.
            // > No tests found for given includes: [space.kscience.kmath.linearsystemsolving.thomasmethod.ThomasMethodTest.exceptionTestWhenAisNotSquareMatrix](--tests filter)
        }

    /**
     * Exception test,
     * WHEN the matrix 'A' is not tridiagonal matrix.
     */
    @Test
    fun exceptionTest4() =
        Double.algebra.linearSpace.run {
            val matrixA = matrix(4, 4)(
                4.0, 1.0, 0.0, 0.0,
                1.0, 4.0, 1.0, 0.0,
                0.0, 1.0, 4.0, 1.0,
                99999999.9999, 0.0, 1.0, 4.0,
            )

            val vectorB = vector(
                5.0, 6.0, 6.0, 5.0,
            )

            assertFails {
                solveSystemByThomasMethod(matrixA, vectorB, isStrictMode = true)
            }
            assertTrue(true) // Needs, because single assertFails is not supporting:
            // Execution failed for task ':kmath-functions:jvmTest'.
            // > No tests found for given includes: [space.kscience.kmath.linearsystemsolving.thomasmethod.ThomasMethodTest.exceptionTestWhenAisNotSquareMatrix](--tests filter)
        }
}