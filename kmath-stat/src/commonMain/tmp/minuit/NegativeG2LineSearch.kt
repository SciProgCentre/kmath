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
 * In case that one of the components of the second derivative g2 calculated by
 * the numerical gradient calculator is negative, a 1dim line search in the
 * direction of that component is done in order to find a better position where
 * g2 is again positive.
 *
 * @version $Id$
 */
internal object NegativeG2LineSearch {
    fun hasNegativeG2(grad: FunctionGradient, prec: MnMachinePrecision): Boolean {
        for (i in 0 until grad.getGradient().getDimension()) {
            if (grad.getGradientDerivative().getEntry(i) < prec.eps2()) {
                return true
            }
        }
        return false
    }

    fun search(fcn: MnFcn, st: MinimumState, gc: GradientCalculator, prec: MnMachinePrecision): MinimumState {
        val negG2 = hasNegativeG2(st.gradient(), prec)
        if (!negG2) {
            return st
        }
        val n: Int = st.parameters().vec().getDimension()
        var dgrad: FunctionGradient = st.gradient()
        var pa: MinimumParameters = st.parameters()
        var iterate = false
        var iter = 0
        do {
            iterate = false
            for (i in 0 until n) {
                if (dgrad.getGradientDerivative().getEntry(i) < prec.eps2()) {
                    // do line search if second derivative negative
                    var step: RealVector = ArrayRealVector(n)
                    step.setEntry(i, dgrad.getStep().getEntry(i) * dgrad.getGradient().getEntry(i))
                    if (abs(dgrad.getGradient().getEntry(i)) > prec.eps2()) {
                        step.setEntry(i,
                            step.getEntry(i) * (-1.0 / abs(dgrad.getGradient().getEntry(i))))
                    }
                    val gdel: Double = step.getEntry(i) * dgrad.getGradient().getEntry(i)
                    val pp: MnParabolaPoint = MnLineSearch.search(fcn, pa, step, gdel, prec)
                    step = MnUtils.mul(step, pp.x())
                    pa = MinimumParameters(MnUtils.add(pa.vec(), step), pp.y())
                    dgrad = gc.gradient(pa, dgrad)
                    iterate = true
                    break
                }
            }
        } while (iter++ < 2 * n && iterate)
        val mat = MnAlgebraicSymMatrix(n)
        for (i in 0 until n) {
            mat[i, i] = if (abs(dgrad.getGradientDerivative()
                    .getEntry(i)) > prec.eps2()
            ) 1.0 / dgrad.getGradientDerivative().getEntry(i) else 1.0
        }
        val err = MinimumError(mat, 1.0)
        val edm: Double = VariableMetricEDMEstimator().estimate(dgrad, err)
        return MinimumState(pa, err, dgrad, edm, fcn.numOfCalls())
    }
}