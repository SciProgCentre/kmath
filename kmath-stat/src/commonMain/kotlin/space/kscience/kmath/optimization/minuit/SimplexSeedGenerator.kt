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
internal class SimplexSeedGenerator : MinimumSeedGenerator {
    /** {@inheritDoc}  */
    fun generate(fcn: MnFcn, gc: GradientCalculator?, st: MnUserParameterState, stra: MnStrategy): MinimumSeed {
        val n: Int = st.variableParameters()
        val prec: MnMachinePrecision = st.precision()

        // initial starting values
        val x: RealVector = ArrayRealVector(n)
        for (i in 0 until n) {
            x.setEntry(i, st.intParameters()[i])
        }
        val fcnmin: Double = fcn.value(x)
        val pa = MinimumParameters(x, fcnmin)
        val igc = InitialGradientCalculator(fcn, st.getTransformation(), stra)
        val dgrad: FunctionGradient = igc.gradient(pa)
        val mat = MnAlgebraicSymMatrix(n)
        val dcovar = 1.0
        for (i in 0 until n) {
            mat[i, i] = if (abs(dgrad.getGradientDerivative()
                    .getEntry(i)) > prec.eps2()
            ) 1.0 / dgrad.getGradientDerivative().getEntry(i) else 1.0
        }
        val err = MinimumError(mat, dcovar)
        val edm: Double = VariableMetricEDMEstimator().estimate(dgrad, err)
        val state = MinimumState(pa, err, dgrad, edm, fcn.numOfCalls())
        return MinimumSeed(state, st.getTransformation())
    }
}