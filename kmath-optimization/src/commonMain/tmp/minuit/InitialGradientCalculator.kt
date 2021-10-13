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
import ru.inr.mass.minuit.*

/**
 * Calculating derivatives via finite differences
 * @version $Id$
 */
internal class InitialGradientCalculator(fcn: MnFcn, par: MnUserTransformation, stra: MnStrategy) {
    private val theFcn: MnFcn = fcn
    private val theStrategy: MnStrategy
    private val theTransformation: MnUserTransformation
    fun fcn(): MnFcn {
        return theFcn
    }

    fun gradTolerance(): Double {
        return strategy().gradientTolerance()
    }

    fun gradient(par: MinimumParameters): FunctionGradient {
        require(par.isValid()) { "Parameters are invalid" }
        val n: Int = trafo().variableParameters()
        require(n == par.vec().getDimension()) { "Parameters have invalid size" }
        val gr: RealVector = ArrayRealVector(n)
        val gr2: RealVector = ArrayRealVector(n)
        val gst: RealVector = ArrayRealVector(n)

        // initial starting values
        for (i in 0 until n) {
            val exOfIn: Int = trafo().extOfInt(i)
            val `var`: Double = par.vec().getEntry(i) //parameter value
            val werr: Double = trafo().parameter(exOfIn).error() //parameter error
            val sav: Double = trafo().int2ext(i, `var`) //value after transformation
            var sav2 = sav + werr //value after transfomation + error
            if (trafo().parameter(exOfIn).hasLimits()) {
                if (trafo().parameter(exOfIn).hasUpperLimit()
                    && sav2 > trafo().parameter(exOfIn).upperLimit()
                ) {
                    sav2 = trafo().parameter(exOfIn).upperLimit()
                }
            }
            var var2: Double = trafo().ext2int(exOfIn, sav2)
            val vplu = var2 - `var`
            sav2 = sav - werr
            if (trafo().parameter(exOfIn).hasLimits()) {
                if (trafo().parameter(exOfIn).hasLowerLimit()
                    && sav2 < trafo().parameter(exOfIn).lowerLimit()
                ) {
                    sav2 = trafo().parameter(exOfIn).lowerLimit()
                }
            }
            var2 = trafo().ext2int(exOfIn, sav2)
            val vmin = var2 - `var`
            val dirin: Double = 0.5 * (abs(vplu) + abs(vmin))
            val g2: Double = 2.0 * theFcn.errorDef() / (dirin * dirin)
            val gsmin: Double = 8.0 * precision().eps2() * (abs(`var`) + precision().eps2())
            var gstep: Double = max(gsmin, 0.1 * dirin)
            val grd = g2 * dirin
            if (trafo().parameter(exOfIn).hasLimits()) {
                if (gstep > 0.5) {
                    gstep = 0.5
                }
            }
            gr.setEntry(i, grd)
            gr2.setEntry(i, g2)
            gst.setEntry(i, gstep)
        }
        return FunctionGradient(gr, gr2, gst)
    }

    fun gradient(par: MinimumParameters, gra: FunctionGradient?): FunctionGradient {
        return gradient(par)
    }

    fun ncycle(): Int {
        return strategy().gradientNCycles()
    }

    fun precision(): MnMachinePrecision {
        return theTransformation.precision()
    }

    fun stepTolerance(): Double {
        return strategy().gradientStepTolerance()
    }

    fun strategy(): MnStrategy {
        return theStrategy
    }

    fun trafo(): MnUserTransformation {
        return theTransformation
    }

    init {
        theTransformation = par
        theStrategy = stra
    }
}