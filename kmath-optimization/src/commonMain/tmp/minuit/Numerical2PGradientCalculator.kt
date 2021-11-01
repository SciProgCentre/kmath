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
import ru.inr.mass.minuit.*

/**
 *
 * @version $Id$
 */
internal class Numerical2PGradientCalculator(fcn: MnFcn, par: MnUserTransformation, stra: MnStrategy) :
    GradientCalculator {
    private val theFcn: MnFcn = fcn
    private val theStrategy: MnStrategy
    private val theTransformation: MnUserTransformation
    fun fcn(): MnFcn {
        return theFcn
    }

    fun gradTolerance(): Double {
        return strategy().gradientTolerance()
    }

    /** {@inheritDoc}  */
    fun gradient(par: MinimumParameters): FunctionGradient {
        val gc = InitialGradientCalculator(theFcn, theTransformation, theStrategy)
        val gra: FunctionGradient = gc.gradient(par)
        return gradient(par, gra)
    }

    /** {@inheritDoc}  */
    fun gradient(par: MinimumParameters, gradient: FunctionGradient): FunctionGradient {
        require(par.isValid()) { "Parameters are invalid" }
        val x: RealVector = par.vec().copy()
        val fcnmin: Double = par.fval()
        val dfmin: Double = 8.0 * precision().eps2() * (abs(fcnmin) + theFcn.errorDef())
        val vrysml: Double = 8.0 * precision().eps() * precision().eps()
        val n: Int = x.getDimension()
        val grd: RealVector = gradient.getGradient().copy()
        val g2: RealVector = gradient.getGradientDerivative().copy()
        val gstep: RealVector = gradient.getStep().copy()
        for (i in 0 until n) {
            val xtf: Double = x.getEntry(i)
            val epspri: Double = precision().eps2() + abs(grd.getEntry(i) * precision().eps2())
            var stepb4 = 0.0
            for (j in 0 until ncycle()) {
                val optstp: Double = sqrt(dfmin / (abs(g2.getEntry(i)) + epspri))
                var step: Double = max(optstp, abs(0.1 * gstep.getEntry(i)))
                if (trafo().parameter(trafo().extOfInt(i)).hasLimits()) {
                    if (step > 0.5) {
                        step = 0.5
                    }
                }
                val stpmax: Double = 10.0 * abs(gstep.getEntry(i))
                if (step > stpmax) {
                    step = stpmax
                }
                val stpmin: Double =
                    max(vrysml, 8.0 * abs(precision().eps2() * x.getEntry(i)))
                if (step < stpmin) {
                    step = stpmin
                }
                if (abs((step - stepb4) / step) < stepTolerance()) {
                    break
                }
                gstep.setEntry(i, step)
                stepb4 = step
                x.setEntry(i, xtf + step)
                val fs1: Double = theFcn.value(x)
                x.setEntry(i, xtf - step)
                val fs2: Double = theFcn.value(x)
                x.setEntry(i, xtf)
                val grdb4: Double = grd.getEntry(i)
                grd.setEntry(i, 0.5 * (fs1 - fs2) / step)
                g2.setEntry(i, (fs1 + fs2 - 2.0 * fcnmin) / step / step)
                if (abs(grdb4 - grd.getEntry(i)) / (abs(grd.getEntry(i)) + dfmin / step) < gradTolerance()) {
                    break
                }
            }
        }
        return FunctionGradient(grd, g2, gstep)
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