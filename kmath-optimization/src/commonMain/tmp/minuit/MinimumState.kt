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
 * MinimumState keeps the information (position, gradient, 2nd deriv, etc) after
 * one minimization step (usually in MinimumBuilder).
 *
 * @version $Id$
 */
class MinimumState {
    private var theEDM = 0.0
    private var theError: MinimumError
    private var theGradient: FunctionGradient
    private var theNFcn = 0
    private var theParameters: MinimumParameters

    constructor(n: Int) {
        theParameters = MinimumParameters(n)
        theError = MinimumError(n)
        theGradient = FunctionGradient(n)
    }

    constructor(states: MinimumParameters, err: MinimumError, grad: FunctionGradient, edm: Double, nfcn: Int) {
        theParameters = states
        theError = err
        theGradient = grad
        theEDM = edm
        theNFcn = nfcn
    }

    constructor(states: MinimumParameters, edm: Double, nfcn: Int) {
        theParameters = states
        theError = MinimumError(states.vec().getDimension())
        theGradient = FunctionGradient(states.vec().getDimension())
        theEDM = edm
        theNFcn = nfcn
    }

    fun edm(): Double {
        return theEDM
    }

    fun error(): MinimumError {
        return theError
    }

    fun fval(): Double {
        return theParameters.fval()
    }

    fun gradient(): FunctionGradient {
        return theGradient
    }

    fun hasCovariance(): Boolean {
        return theError.isAvailable()
    }

    fun hasParameters(): Boolean {
        return theParameters.isValid()
    }

    fun isValid(): Boolean {
        return if (hasParameters() && hasCovariance()) {
            parameters().isValid() && error().isValid()
        } else if (hasParameters()) {
            parameters().isValid()
        } else {
            false
        }
    }

    fun nfcn(): Int {
        return theNFcn
    }

    fun parameters(): MinimumParameters {
        return theParameters
    }

    fun size(): Int {
        return theParameters.vec().getDimension()
    }

    fun vec(): RealVector {
        return theParameters.vec()
    }
}