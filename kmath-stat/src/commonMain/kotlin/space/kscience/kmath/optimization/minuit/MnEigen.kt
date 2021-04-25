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

import org.apache.commons.math3.linear.RealVector

/**
 * Calculates and the eigenvalues of the user covariance matrix
 * MnUserCovariance.
 *
 * @version $Id$
 * @author Darksnake
 */
object MnEigen {
    /* Calculate eigenvalues of the covariance matrix.
     * Will perform the calculation of the eigenvalues of the covariance matrix
     * and return the result in the form of a double array.
     * The eigenvalues are ordered from the smallest to the largest eigenvalue.
     */
    /**
     *
     * eigenvalues.
     *
     * @param covar a [hep.dataforge.MINUIT.MnUserCovariance] object.
     * @return an array of double.
     */
    fun eigenvalues(covar: MnUserCovariance): DoubleArray {
        val cov = MnAlgebraicSymMatrix(covar.nrow())
        for (i in 0 until covar.nrow()) {
            for (j in i until covar.nrow()) {
                cov[i, j] = covar[i, j]
            }
        }
        val eigen: RealVector = cov.eigenvalues()
        return eigen.toArray()
    }
}