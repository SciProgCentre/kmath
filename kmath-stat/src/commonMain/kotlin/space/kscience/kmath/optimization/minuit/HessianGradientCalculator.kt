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
 *
 * @version $Id$
 */
internal class HessianGradientCalculator(fcn: MnFcn, par: MnUserTransformation, stra: MnStrategy) : GradientCalculator {
    private val theFcn: MnFcn = fcn
    private val theStrategy: MnStrategy
    private val theTransformation: MnUserTransformation
    fun deltaGradient(par: MinimumParameters, gradient: FunctionGradient): Pair<FunctionGradient, RealVector> {
        require(par.isValid()) { "parameters are invalid" }
        val x: RealVector = par.vec().copy()
        val grd: RealVector = gradient.getGradient().copy()
        val g2: RealVector = gradient.getGradientDerivative()
        val gstep: RealVector = gradient.getStep()
        val fcnmin: Double = par.fval()
        //   std::cout<<"fval: "<<fcnmin<<std::endl;
        val dfmin: Double = 4.0 * precision().eps2() * (abs(fcnmin) + theFcn.errorDef())
        val n: Int = x.getDimension()
        val dgrd: RealVector = ArrayRealVector(n)

        // initial starting values
        for (i in 0 until n) {
            val xtf: Double = x.getEntry(i)
            val dmin: Double = 4.0 * precision().eps2() * (xtf + precision().eps2())
            val epspri: Double = precision().eps2() + abs(grd.getEntry(i) * precision().eps2())
            val optstp: Double = sqrt(dfmin / (abs(g2.getEntry(i)) + epspri))
            var d: Double = 0.2 * abs(gstep.getEntry(i))
            if (d > optstp) {
                d = optstp
            }
            if (d < dmin) {
                d = dmin
            }
            var chgold = 10000.0
            var dgmin = 0.0
            var grdold = 0.0
            var grdnew = 0.0
            for (j in 0 until ncycle()) {
                x.setEntry(i, xtf + d)
                val fs1: Double = theFcn.value(x)
                x.setEntry(i, xtf - d)
                val fs2: Double = theFcn.value(x)
                x.setEntry(i, xtf)
                //       double sag = 0.5*(fs1+fs2-2.*fcnmin);
                grdold = grd.getEntry(i)
                grdnew = (fs1 - fs2) / (2.0 * d)
                dgmin = precision().eps() * (abs(fs1) + abs(fs2)) / d
                if (abs(grdnew) < precision().eps()) {
                    break
                }
                val change: Double = abs((grdold - grdnew) / grdnew)
                if (change > chgold && j > 1) {
                    break
                }
                chgold = change
                grd.setEntry(i, grdnew)
                if (change < 0.05) {
                    break
                }
                if (abs(grdold - grdnew) < dgmin) {
                    break
                }
                if (d < dmin) {
                    break
                }
                d *= 0.2
            }
            dgrd.setEntry(i, max(dgmin, abs(grdold - grdnew)))
        }
        return Pair(FunctionGradient(grd, g2, gstep), dgrd)
    }

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
        return deltaGradient(par, gradient).getFirst()
    }

    fun ncycle(): Int {
        return strategy().hessianGradientNCycles()
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