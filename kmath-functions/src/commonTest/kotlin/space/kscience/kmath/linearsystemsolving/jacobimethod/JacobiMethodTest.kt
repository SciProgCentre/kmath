/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.linearsystemsolving.jacobimethod

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.linear.Point
import space.kscience.kmath.linear.linearSpace
import space.kscience.kmath.linear.matrix
import space.kscience.kmath.linear.vector
import space.kscience.kmath.operations.algebra
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertTrue

@UnstableKMathAPI
internal class JacobiMethodTest {

    /**
     * Positive test,
     * WHEN matrix 'A' is 3x3 with sufficient condition of the convergence,
     * without the custom initial approximation input,
     * with the custom epsilonPrecision input (machineEpsilonPrecision will be used).
     */
    @Test
    fun positiveTest1() =
        Double.algebra.linearSpace.run {
            val matrixA = matrix(3, 3)(
                115.0, -20.0, -75.0,
                15.0, -50.0, -5.0,
                6.0, 2.0, 20.0,
            )

            val vectorB = vector(
                20.0, -40.0, 28.0,
            )

            val result: Point<Double> = solveSystemByJacobiMethod(
                A = matrixA,
                B = vectorB,
                epsilonPrecision = 0.001,
            )

            assertEquals(3, result.size)
            val absoluteTolerance = 0.001
            assertEquals(1.0, result[0], absoluteTolerance)
            assertEquals(1.0, result[1], absoluteTolerance)
            assertEquals(1.0, result[2], absoluteTolerance)

            val result2 = matrixA.dot(result)

            for (i in 0 until result2.size) {
                assertEquals(vectorB[i], result2[i], 0.05)
            }
        }


    /**
     * Positive test,
     * WHEN matrix 'A' is 4x4 with sufficient condition of the convergence,
     * without the custom initial approximation input,
     * without the custom epsilonPrecision input (machineEpsilonPrecision will be used).
     */
    @Test
    fun positiveTest2() =
        Double.algebra.linearSpace.run {
            val matrixA = matrix(4, 4)(
                20.9, 1.2, 2.1, 0.9,
                1.2, 21.2, 1.5, 2.5,
                2.1, 1.5, 19.8, 1.3,
                0.9, 2.5, 1.3, 32.1,
            )

            val vectorB = vector(
                21.70, 27.46, 28.76, 49.72,
            )

            val result: Point<Double> = solveSystemByJacobiMethod(
                A = matrixA,
                B = vectorB,
            )

            assertEquals(4, result.size)
            val absoluteTolerance = 0.00000000000001
            assertEquals(0.8, result[0], absoluteTolerance)
            assertEquals(1.0, result[1], absoluteTolerance)
            assertEquals(1.2, result[2], absoluteTolerance)
            assertEquals(1.4, result[3], absoluteTolerance)

            val result2 = matrixA.dot(result)
            for (i in 0 until result2.size) {
                assertEquals(vectorB[i], result2[i], absoluteTolerance)
            }
        }

    /**
     * Positive test,
     * WHEN matrix 'A' is 4x4 with sufficient condition of the convergence,
     * with the custom initial approximation input,
     * without the custom epsilonPrecision input (machineEpsilonPrecision will be used).
     */
    @Test
    fun positiveTest3() =
        Double.algebra.linearSpace.run {
            val matrixA = matrix(4, 4)(
                20.9, 1.2, 2.1, 0.9,
                1.2, 21.2, 1.5, 2.5,
                2.1, 1.5, 19.8, 1.3,
                0.9, 2.5, 1.3, 32.1,
            )

            val vectorB = vector(
                21.70, 27.46, 28.76, 49.72,
            )

            val result: Point<Double> = solveSystemByJacobiMethod(
                A = matrixA,
                B = vectorB,
                initialApproximation = vector(1.0, 1.0, 1.0, 1.0),
            )

            assertEquals(4, result.size)
            val absoluteTolerance = 0.00000000000001
            assertEquals(0.8, result[0], absoluteTolerance)
            assertEquals(1.0, result[1], absoluteTolerance)
            assertEquals(1.2, result[2], absoluteTolerance)
            assertEquals(1.4, result[3], absoluteTolerance)

            val result2 = matrixA.dot(result)
            for (i in 0 until result2.size) {
                assertEquals(vectorB[i], result2[i], absoluteTolerance)
            }
        }

    /**
     * Positive test,
     * WHEN matrix 'A' is 4x4 with sufficient condition of the convergence,
     * without the custom initial approximation input,
     * with the custom epsilonPrecision input (machineEpsilonPrecision will be used).
     */
    @Test
    fun positiveTest4() =
        Double.algebra.linearSpace.run {
            val matrixA = matrix(4, 4)(
                20.9, 1.2, 2.1, 0.9,
                1.2, 21.2, 1.5, 2.5,
                2.1, 1.5, 19.8, 1.3,
                0.9, 2.5, 1.3, 32.1,
            )

            val vectorB = vector(
                21.70, 27.46, 28.76, 49.72,
            )

            val result: Point<Double> = solveSystemByJacobiMethod(
                A = matrixA,
                B = vectorB,
                epsilonPrecision = 0.001,
            )

            assertEquals(4, result.size)
            val absoluteTolerance = 0.001
            assertEquals(0.8, result[0], absoluteTolerance)
            assertEquals(1.0, result[1], absoluteTolerance)
            assertEquals(1.2, result[2], absoluteTolerance)
            assertEquals(1.4, result[3], absoluteTolerance)

            val result2 = matrixA.dot(result)
            for (i in 0 until result2.size) {
                assertEquals(vectorB[i], result2[i], absoluteTolerance)
            }
        }

    /**
     * Positive test,
     * WHEN matrix 'A' is 4x4 with sufficient condition of the convergence,
     * with the custom initial approximation input,
     * with the custom epsilonPrecision input (machineEpsilonPrecision will be used).
     */
    @Test
    fun positiveTest5() =
        Double.algebra.linearSpace.run {
            val matrixA = matrix(4, 4)(
                20.9, 1.2, 2.1, 0.9,
                1.2, 21.2, 1.5, 2.5,
                2.1, 1.5, 19.8, 1.3,
                0.9, 2.5, 1.3, 32.1,
            )

            val vectorB = vector(
                21.70, 27.46, 28.76, 49.72,
            )

            val result: Point<Double> = solveSystemByJacobiMethod(
                A = matrixA,
                B = vectorB,
                initialApproximation = vector(1.0, 1.0, 1.0, 1.0),
                epsilonPrecision = 0.001,
            )

            assertEquals(4, result.size)
            val absoluteTolerance = 0.001
            assertEquals(0.8, result[0], absoluteTolerance)
            assertEquals(1.0, result[1], absoluteTolerance)
            assertEquals(1.2, result[2], absoluteTolerance)
            assertEquals(1.4, result[3], absoluteTolerance)

            val result2 = matrixA.dot(result)
            for (i in 0 until result2.size) {
                assertEquals(vectorB[i], result2[i], absoluteTolerance)
            }
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
                solveSystemByJacobiMethod(matrixA, vectorB)
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
                solveSystemByJacobiMethod(matrixA, vectorB)
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
                solveSystemByJacobiMethod(matrixA, vectorB)
            }
            assertTrue(true) // Needs, because single assertFails is not supporting:
            // Execution failed for task ':kmath-functions:jvmTest'.
            // > No tests found for given includes: [space.kscience.kmath.linearsystemsolving.thomasmethod.ThomasMethodTest.exceptionTestWhenAisNotSquareMatrix](--tests filter)
        }

    /**
     * Exception test,
     * WHEN the size of the vector 'B' does not match the size of the vector 'initialApproximation'.
     */
    @Test
    fun exceptionTest4() =
        Double.algebra.linearSpace.run {
            val matrixA = matrix(4, 4)(
                20.9, 1.2, 2.1, 0.9,
                1.2, 21.2, 1.5, 2.5,
                2.1, 1.5, 19.8, 1.3,
                0.9, 2.5, 1.3, 32.1,
            )

            val vectorB = vector(
                21.70, 27.46, 28.76, 49.72,
            )

            assertFails {
                solveSystemByJacobiMethod(
                    A = matrixA,
                    B = vectorB,
                    initialApproximation = vector(1.0, 1.0, 1.0)
                )
            }
            assertTrue(true) // Needs, because single assertFails is not supporting:
            // Execution failed for task ':kmath-functions:jvmTest'.
            // > No tests found for given includes: [space.kscience.kmath.linearsystemsolving.thomasmethod.ThomasMethodTest.exceptionTestWhenAisNotSquareMatrix](--tests filter)
        }

    /**
     * Exception test,
     * WHEN matrix 'A' is 4x4 without the sufficient condition for the convergence of the Jacobi method:
     * there is no diagonal dominance of the matrix.
     */
    @Test
    fun exceptionTest5() =
        Double.algebra.linearSpace.run {
            val matrixA = matrix(4, 4)(
                20.9, 1.2, 2.1, 0.9,
                1.2, 21.2, 1.5, 2.5,
                2.1, 1.5, 0.00000000000000000, 1.3,
                0.9, 2.5, 1.3, 32.1,
            )

            val vectorB = vector(
                21.70, 27.46, 28.76, 49.72,
            )

            assertFails {
                solveSystemByJacobiMethod(matrixA, vectorB)
            }
            assertTrue(true) // Needs, because single assertFails is not supporting:
            // Execution failed for task ':kmath-functions:jvmTest'.
            // > No tests found for given includes: [space.kscience.kmath.linearsystemsolving.thomasmethod.ThomasMethodTest.exceptionTestWhenAisNotSquareMatrix](--tests filter)
        }
}