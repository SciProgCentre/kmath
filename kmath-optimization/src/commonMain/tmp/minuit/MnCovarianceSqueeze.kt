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

import space.kscience.kmath.optimization.minuit.MINUITPlugin

/**
 *
 * @version $Id$
 */
internal object MnCovarianceSqueeze {
    fun squeeze(cov: MnUserCovariance, n: Int): MnUserCovariance {
        assert(cov.nrow() > 0)
        assert(n < cov.nrow())
        val hess = MnAlgebraicSymMatrix(cov.nrow())
        for (i in 0 until cov.nrow()) {
            for (j in i until cov.nrow()) {
                hess[i, j] = cov[i, j]
            }
        }
        try {
            hess.invert()
        } catch (x: SingularMatrixException) {
            MINUITPlugin.logStatic("MnUserCovariance inversion failed; return diagonal matrix;")
            val result = MnUserCovariance(cov.nrow() - 1)
            var i = 0
            var j = 0
            while (i < cov.nrow()) {
                if (i == n) {
                    i++
                    continue
                }
                result[j, j] = cov[i, i]
                j++
                i++
            }
            return result
        }
        val squeezed: MnAlgebraicSymMatrix = squeeze(hess, n)
        try {
            squeezed.invert()
        } catch (x: SingularMatrixException) {
            MINUITPlugin.logStatic("MnUserCovariance back-inversion failed; return diagonal matrix;")
            val result = MnUserCovariance(squeezed.nrow())
            var i = 0
            while (i < squeezed.nrow()) {
                result[i, i] = 1.0 / squeezed[i, i]
                i++
            }
            return result
        }
        return MnUserCovariance(squeezed.data(), squeezed.nrow())
    }

    fun squeeze(err: MinimumError, n: Int): MinimumError {
        val hess: MnAlgebraicSymMatrix = err.hessian()
        val squeezed: MnAlgebraicSymMatrix = squeeze(hess, n)
        try {
            squeezed.invert()
        } catch (x: SingularMatrixException) {
            MINUITPlugin.logStatic("MnCovarianceSqueeze: MinimumError inversion fails; return diagonal matrix.")
            val tmp = MnAlgebraicSymMatrix(squeezed.nrow())
            var i = 0
            while (i < squeezed.nrow()) {
                tmp[i, i] = 1.0 / squeezed[i, i]
                i++
            }
            return MinimumError(tmp, MnInvertFailed())
        }
        return MinimumError(squeezed, err.dcovar())
    }

    fun squeeze(hess: MnAlgebraicSymMatrix, n: Int): MnAlgebraicSymMatrix {
        assert(hess.nrow() > 0)
        assert(n < hess.nrow())
        val hs = MnAlgebraicSymMatrix(hess.nrow() - 1)
        var i = 0
        var j = 0
        while (i < hess.nrow()) {
            if (i == n) {
                i++
                continue
            }
            var k = i
            var l = j
            while (k < hess.nrow()) {
                if (k == n) {
                    k++
                    continue
                }
                hs[j, l] = hess[i, k]
                l++
                k++
            }
            j++
            i++
        }
        return hs
    }
}