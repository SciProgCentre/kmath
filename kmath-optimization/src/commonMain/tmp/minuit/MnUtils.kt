/* 
 * Copyright 2015 Alexander Nozik.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.inr.mass.minuit

import org.apache.commons.math3.linear.ArrayRealVector

/**
 * Utilities for operating on vectors and matrices
 *
 * @version $Id$
 */
internal object MnUtils {
    fun absoluteSumOfElements(m: MnAlgebraicSymMatrix): Double {
        val data: DoubleArray = m.data()
        var result = 0.0
        for (i in data.indices) {
            result += abs(data[i])
        }
        return result
    }

    fun add(v1: RealVector, v2: RealVector?): RealVector {
        return v1.add(v2)
    }

    fun add(m1: MnAlgebraicSymMatrix, m2: MnAlgebraicSymMatrix): MnAlgebraicSymMatrix {
        require(!(m1.size() !== m2.size())) { "Incompatible matrices" }
        val result: MnAlgebraicSymMatrix = m1.copy()
        val a: DoubleArray = result.data()
        val b: DoubleArray = m2.data()
        for (i in a.indices) {
            a[i] += b[i]
        }
        return result
    }

    fun div(m: MnAlgebraicSymMatrix?, scale: Double): MnAlgebraicSymMatrix {
        return mul(m, 1 / scale)
    }

    fun div(m: RealVector?, scale: Double): RealVector {
        return mul(m, 1 / scale)
    }

    fun innerProduct(v1: RealVector, v2: RealVector): Double {
        require(!(v1.getDimension() !== v2.getDimension())) { "Incompatible vectors" }
        var total = 0.0
        for (i in 0 until v1.getDimension()) {
            total += v1.getEntry(i) * v2.getEntry(i)
        }
        return total
    }

    fun mul(v1: RealVector, scale: Double): RealVector {
        return v1.mapMultiply(scale)
    }

    fun mul(m1: MnAlgebraicSymMatrix, scale: Double): MnAlgebraicSymMatrix {
        val result: MnAlgebraicSymMatrix = m1.copy()
        val a: DoubleArray = result.data()
        for (i in a.indices) {
            a[i] *= scale
        }
        return result
    }

    fun mul(m1: MnAlgebraicSymMatrix, v1: RealVector): ArrayRealVector {
        require(!(m1.nrow() !== v1.getDimension())) { "Incompatible arguments" }
        val result = ArrayRealVector(m1.nrow())
        for (i in 0 until result.getDimension()) {
            var total = 0.0
            for (k in 0 until result.getDimension()) {
                total += m1[i, k] * v1.getEntry(k)
            }
            result.setEntry(i, total)
        }
        return result
    }

    fun mul(m1: MnAlgebraicSymMatrix, m2: MnAlgebraicSymMatrix): MnAlgebraicSymMatrix {
        require(!(m1.size() !== m2.size())) { "Incompatible matrices" }
        val n: Int = m1.nrow()
        val result = MnAlgebraicSymMatrix(n)
        for (i in 0 until n) {
            for (j in 0..i) {
                var total = 0.0
                for (k in 0 until n) {
                    total += m1[i, k] * m2[k, j]
                }
                result[i, j] = total
            }
        }
        return result
    }

    fun outerProduct(v2: RealVector): MnAlgebraicSymMatrix {
        // Fixme: check this. I am assuming this is just an outer-product of vector
        //        with itself.
        val n: Int = v2.getDimension()
        val result = MnAlgebraicSymMatrix(n)
        val data: DoubleArray = v2.toArray()
        for (i in 0 until n) {
            for (j in 0..i) {
                result[i, j] = data[i] * data[j]
            }
        }
        return result
    }

    fun similarity(avec: RealVector, mat: MnAlgebraicSymMatrix): Double {
        val n: Int = avec.getDimension()
        val tmp: RealVector = mul(mat, avec)
        var result = 0.0
        for (i in 0 until n) {
            result += tmp.getEntry(i) * avec.getEntry(i)
        }
        return result
    }

    fun sub(v1: RealVector, v2: RealVector?): RealVector {
        return v1.subtract(v2)
    }

    fun sub(m1: MnAlgebraicSymMatrix, m2: MnAlgebraicSymMatrix): MnAlgebraicSymMatrix {
        require(!(m1.size() !== m2.size())) { "Incompatible matrices" }
        val result: MnAlgebraicSymMatrix = m1.copy()
        val a: DoubleArray = result.data()
        val b: DoubleArray = m2.data()
        for (i in a.indices) {
            a[i] -= b[i]
        }
        return result
    }
}