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

import ru.inr.mass.maths.MultiFunction

/**
 *
 * @version $Id$
 */
internal class AnalyticalGradientCalculator(fcn: MultiFunction?, state: MnUserTransformation, checkGradient: Boolean) :
    GradientCalculator {
    private val function: MultiFunction?
    private val theCheckGradient: Boolean
    private val theTransformation: MnUserTransformation
    fun checkGradient(): Boolean {
        return theCheckGradient
    }

    /** {@inheritDoc}  */
    fun gradient(par: MinimumParameters): FunctionGradient {
//      double[] grad = theGradCalc.gradientValue(theTransformation.andThen(par.vec()).data());
        val point: DoubleArray = theTransformation.transform(par.vec()).toArray()
        require(!(function.getDimension() !== theTransformation.parameters().size())) { "Invalid parameter size" }
        val v: RealVector = ArrayRealVector(par.vec().getDimension())
        for (i in 0 until par.vec().getDimension()) {
            val ext: Int = theTransformation.extOfInt(i)
            if (theTransformation.parameter(ext).hasLimits()) {
                val dd: Double = theTransformation.dInt2Ext(i, par.vec().getEntry(i))
                v.setEntry(i, dd * function.derivValue(ext, point))
            } else {
                v.setEntry(i, function.derivValue(ext, point))
            }
        }
        return FunctionGradient(v)
    }

    /** {@inheritDoc}  */
    fun gradient(par: MinimumParameters, grad: FunctionGradient?): FunctionGradient {
        return gradient(par)
    }

    init {
        function = fcn
        theTransformation = state
        theCheckGradient = checkGradient
    }
}