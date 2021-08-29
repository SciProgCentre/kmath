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
class MinimumParameters {
    private var theFVal = 0.0
    private var theHasStep = false
    private var theParameters: RealVector
    private var theStepSize: RealVector
    private var theValid = false

    constructor(n: Int) {
        theParameters = ArrayRealVector(n)
        theStepSize = ArrayRealVector(n)
    }

    constructor(avec: RealVector, fval: Double) {
        theParameters = avec
        theStepSize = ArrayRealVector(avec.getDimension())
        theFVal = fval
        theValid = true
    }

    constructor(avec: RealVector, dirin: RealVector, fval: Double) {
        theParameters = avec
        theStepSize = dirin
        theFVal = fval
        theValid = true
        theHasStep = true
    }

    fun dirin(): RealVector {
        return theStepSize
    }

    fun fval(): Double {
        return theFVal
    }

    fun hasStepSize(): Boolean {
        return theHasStep
    }

    fun isValid(): Boolean {
        return theValid
    }

    fun vec(): RealVector {
        return theParameters
    }
}