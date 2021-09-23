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
import ru.inr.mass.minuit.*
import space.kscience.kmath.optimization.minuit.MinimumSeed

/**
 *
 * @version $Id$
 */
internal class MnSeedGenerator : MinimumSeedGenerator {
    /** {@inheritDoc}  */
    fun generate(fcn: MnFcn, gc: GradientCalculator, st: MnUserParameterState, stra: MnStrategy): MinimumSeed {
        val n: Int = st.variableParameters()
        val prec: MnMachinePrecision = st.precision()

        // initial starting values
        val x: RealVector = ArrayRealVector(n)
        for (i in 0 until n) {
            x.setEntry(i, st.intParameters()[i])
        }
        val fcnmin: Double = fcn.value(x)
        val pa = MinimumParameters(x, fcnmin)
        val dgrad: FunctionGradient
        if (gc is AnalyticalGradientCalculator) {
            val igc = InitialGradientCalculator(fcn, st.getTransformation(), stra)
            val tmp: FunctionGradient = igc.gradient(pa)
            val grd: FunctionGradient = gc.gradient(pa)
            dgrad = FunctionGradient(grd.getGradient(), tmp.getGradientDerivative(), tmp.getStep())
            if (gc.checkGradient()) {
                val good = true
                val hgc = HessianGradientCalculator(fcn, st.getTransformation(), MnStrategy(2))
                val hgrd: Pair<FunctionGradient, RealVector> = hgc.deltaGradient(pa, dgrad)
                for (i in 0 until n) {
                    val provided: Double = grd.getGradient().getEntry(i)
                    val calculated: Double = hgrd.getFirst().getGradient().getEntry(i)
                    val delta: Double = hgrd.getSecond().getEntry(i)
                    if (abs(calculated - provided) > delta) {
                        MINUITPlugin.logStatic(""
                                + "gradient discrepancy of external parameter \"%d\" "
                                + "(internal parameter \"%d\") too large. Expected: \"%f\", provided: \"%f\"",
                            st.getTransformation().extOfInt(i), i, provided, calculated)

//                        
//                        MINUITPlugin.logStatic("gradient discrepancy of external parameter "
//                                + st.getTransformation().extOfInt(i) 
//                                + " (internal parameter " + i + ") too large.");
//                        good = false;
                    }
                }
                if (!good) {
                    MINUITPlugin.logStatic("Minuit does not accept user specified gradient.")
                    //               assert(good);
                }
            }
        } else {
            dgrad = gc.gradient(pa)
        }
        val mat = MnAlgebraicSymMatrix(n)
        var dcovar = 1.0
        if (st.hasCovariance()) {
            for (i in 0 until n) {
                for (j in i until n) {
                    mat[i, j] = st.intCovariance()[i, j]
                }
            }
            dcovar = 0.0
        } else {
            for (i in 0 until n) {
                mat[i, i] = if (abs(dgrad.getGradientDerivative()
                        .getEntry(i)) > prec.eps2()
                ) 1.0 / dgrad.getGradientDerivative().getEntry(i) else 1.0
            }
        }
        val err = MinimumError(mat, dcovar)
        val edm: Double = VariableMetricEDMEstimator().estimate(dgrad, err)
        var state = MinimumState(pa, err, dgrad, edm, fcn.numOfCalls())
        if (NegativeG2LineSearch.hasNegativeG2(dgrad, prec)) {
            state = if (gc is AnalyticalGradientCalculator) {
                val ngc = Numerical2PGradientCalculator(fcn, st.getTransformation(), stra)
                NegativeG2LineSearch.search(fcn, state, ngc, prec)
            } else {
                NegativeG2LineSearch.search(fcn, state, gc, prec)
            }
        }
        if (stra.strategy() === 2 && !st.hasCovariance()) {
            //calculate full 2nd derivative
            val tmp: MinimumState = MnHesse(stra).calculate(fcn, state, st.getTransformation(), 0)
            return MinimumSeed(tmp, st.getTransformation())
        }
        return MinimumSeed(state, st.getTransformation())
    }
}