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
 * MinimumError keeps the inverse 2nd derivative (inverse Hessian) used for
 * calculating the parameter step size (-V*g) and for the covariance update
 * (ErrorUpdator). The covariance matrix is equal to twice the inverse Hessian.
 *
 * @version $Id$
 */
class MinimumError {
    private var theAvailable = false
    private var theDCovar: Double
    private var theHesseFailed = false
    private var theInvertFailed = false
    private var theMadePosDef = false
    private var theMatrix: MnAlgebraicSymMatrix
    private var thePosDef = false
    private var theValid = false

    constructor(n: Int) {
        theMatrix = MnAlgebraicSymMatrix(n)
        theDCovar = 1.0
    }

    constructor(mat: MnAlgebraicSymMatrix, dcov: Double) {
        theMatrix = mat
        theDCovar = dcov
        theValid = true
        thePosDef = true
        theAvailable = true
    }

    constructor(mat: MnAlgebraicSymMatrix, x: MnHesseFailed?) {
        theMatrix = mat
        theDCovar = 1.0
        theValid = false
        thePosDef = false
        theMadePosDef = false
        theHesseFailed = true
        theInvertFailed = false
        theAvailable = true
    }

    constructor(mat: MnAlgebraicSymMatrix, x: MnMadePosDef?) {
        theMatrix = mat
        theDCovar = 1.0
        theValid = false
        thePosDef = false
        theMadePosDef = true
        theHesseFailed = false
        theInvertFailed = false
        theAvailable = true
    }

    constructor(mat: MnAlgebraicSymMatrix, x: MnInvertFailed?) {
        theMatrix = mat
        theDCovar = 1.0
        theValid = false
        thePosDef = true
        theMadePosDef = false
        theHesseFailed = false
        theInvertFailed = true
        theAvailable = true
    }

    constructor(mat: MnAlgebraicSymMatrix, x: MnNotPosDef?) {
        theMatrix = mat
        theDCovar = 1.0
        theValid = false
        thePosDef = false
        theMadePosDef = false
        theHesseFailed = false
        theInvertFailed = false
        theAvailable = true
    }

    fun dcovar(): Double {
        return theDCovar
    }

    fun hesseFailed(): Boolean {
        return theHesseFailed
    }

    fun hessian(): MnAlgebraicSymMatrix {
        return try {
            val tmp: MnAlgebraicSymMatrix = theMatrix.copy()
            tmp.invert()
            tmp
        } catch (x: SingularMatrixException) {
            MINUITPlugin.logStatic("BasicMinimumError inversion fails; return diagonal matrix.")
            val tmp = MnAlgebraicSymMatrix(theMatrix.nrow())
            var i = 0
            while (i < theMatrix.nrow()) {
                tmp[i, i] = 1.0 / theMatrix[i, i]
                i++
            }
            tmp
        }
    }

    fun invHessian(): MnAlgebraicSymMatrix {
        return theMatrix
    }

    fun invertFailed(): Boolean {
        return theInvertFailed
    }

    fun isAccurate(): Boolean {
        return theDCovar < 0.1
    }

    fun isAvailable(): Boolean {
        return theAvailable
    }

    fun isMadePosDef(): Boolean {
        return theMadePosDef
    }

    fun isPosDef(): Boolean {
        return thePosDef
    }

    fun isValid(): Boolean {
        return theValid
    }

    fun matrix(): MnAlgebraicSymMatrix {
        return MnUtils.mul(theMatrix, 2)
    }

    internal class MnHesseFailed
    internal class MnInvertFailed
    internal class MnMadePosDef
    internal class MnNotPosDef
}