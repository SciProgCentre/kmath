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

import org.apache.commons.math3.linear.SingularMatrixException

/**
 *
 * MnGlobalCorrelationCoeff class.
 *
 * @version $Id$
 * @author Darksnake
 */
class MnGlobalCorrelationCoeff {
    private var theGlobalCC: DoubleArray
    private var theValid = false

    internal constructor() {
        theGlobalCC = DoubleArray(0)
    }

    internal constructor(cov: MnAlgebraicSymMatrix) {
        try {
            val inv: MnAlgebraicSymMatrix = cov.copy()
            inv.invert()
            theGlobalCC = DoubleArray(cov.nrow())
            for (i in 0 until cov.nrow()) {
                val denom: Double = inv[i, i] * cov[i, i]
                if (denom < 1.0 && denom > 0.0) {
                    theGlobalCC[i] = 0
                } else {
                    theGlobalCC[i] = sqrt(1.0 - 1.0 / denom)
                }
            }
            theValid = true
        } catch (x: SingularMatrixException) {
            theValid = false
            theGlobalCC = DoubleArray(0)
        }
    }

    /**
     *
     * globalCC.
     *
     * @return an array of double.
     */
    fun globalCC(): DoubleArray {
        return theGlobalCC
    }

    /**
     *
     * isValid.
     *
     * @return a boolean.
     */
    fun isValid(): Boolean {
        return theValid
    }

    /** {@inheritDoc}  */
    override fun toString(): String {
        return MnPrint.toString(this)
    }
}