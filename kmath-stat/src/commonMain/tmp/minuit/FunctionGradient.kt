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
 *
 * @version $Id$
 */
class FunctionGradient {
    private var theAnalytical = false
    private var theG2ndDerivative: RealVector
    private var theGStepSize: RealVector
    private var theGradient: RealVector
    private var theValid = false

    constructor(n: Int) {
        theGradient = ArrayRealVector(n)
        theG2ndDerivative = ArrayRealVector(n)
        theGStepSize = ArrayRealVector(n)
    }

    constructor(grd: RealVector) {
        theGradient = grd
        theG2ndDerivative = ArrayRealVector(grd.getDimension())
        theGStepSize = ArrayRealVector(grd.getDimension())
        theValid = true
        theAnalytical = true
    }

    constructor(grd: RealVector, g2: RealVector, gstep: RealVector) {
        theGradient = grd
        theG2ndDerivative = g2
        theGStepSize = gstep
        theValid = true
        theAnalytical = false
    }

    fun getGradient(): RealVector {
        return theGradient
    }

    fun getGradientDerivative(): RealVector {
        return theG2ndDerivative
    }

    fun getStep(): RealVector {
        return theGStepSize
    }

    fun isAnalytical(): Boolean {
        return theAnalytical
    }

    fun isValid(): Boolean {
        return theValid
    }
}