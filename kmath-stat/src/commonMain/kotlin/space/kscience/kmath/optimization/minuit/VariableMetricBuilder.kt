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

/**
 *
 * @version $Id$
 */
internal class VariableMetricBuilder : MinimumBuilder {
    private val theErrorUpdator: DavidonErrorUpdator
    private val theEstimator: VariableMetricEDMEstimator = VariableMetricEDMEstimator()
    fun errorUpdator(): DavidonErrorUpdator {
        return theErrorUpdator
    }

    fun estimator(): VariableMetricEDMEstimator {
        return theEstimator
    }

    /** {@inheritDoc}  */
    fun minimum(
        fcn: MnFcn,
        gc: GradientCalculator,
        seed: MinimumSeed,
        strategy: MnStrategy,
        maxfcn: Int,
        edmval: Double
    ): FunctionMinimum {
        val min: FunctionMinimum = minimum(fcn, gc, seed, maxfcn, edmval)
        if (strategy.strategy() === 2 || strategy.strategy() === 1 && min.error().dcovar() > 0.05) {
            val st: MinimumState = MnHesse(strategy).calculate(fcn, min.state(), min.seed().trafo(), 0)
            min.add(st)
        }
        if (!min.isValid()) {
            MINUITPlugin.logStatic("FunctionMinimum is invalid.")
        }
        return min
    }

    fun minimum(fcn: MnFcn, gc: GradientCalculator, seed: MinimumSeed, maxfcn: Int, edmval: Double): FunctionMinimum {
        var edmval = edmval
        edmval *= 0.0001
        if (seed.parameters().vec().getDimension() === 0) {
            return FunctionMinimum(seed, fcn.errorDef())
        }
        val prec: MnMachinePrecision = seed.precision()
        val result: MutableList<MinimumState> = java.util.ArrayList<MinimumState>(8)
        var edm: Double = seed.state().edm()
        if (edm < 0.0) {
            MINUITPlugin.logStatic("VariableMetricBuilder: initial matrix not pos.def.")
            if (seed.error().isPosDef()) {
                throw RuntimeException("Something is wrong!")
            }
            return FunctionMinimum(seed, fcn.errorDef())
        }
        result.add(seed.state())

        // iterate until edm is small enough or max # of iterations reached
        edm *= 1.0 + 3.0 * seed.error().dcovar()
        var step: RealVector // = new ArrayRealVector(seed.gradient().getGradient().getDimension());
        do {
            var s0: MinimumState = result[result.size - 1]
            step = MnUtils.mul(MnUtils.mul(s0.error().invHessian(), s0.gradient().getGradient()), -1)
            var gdel: Double = MnUtils.innerProduct(step, s0.gradient().getGradient())
            if (gdel > 0.0) {
                MINUITPlugin.logStatic("VariableMetricBuilder: matrix not pos.def.")
                MINUITPlugin.logStatic("gdel > 0: $gdel")
                s0 = MnPosDef.test(s0, prec)
                step = MnUtils.mul(MnUtils.mul(s0.error().invHessian(), s0.gradient().getGradient()), -1)
                gdel = MnUtils.innerProduct(step, s0.gradient().getGradient())
                MINUITPlugin.logStatic("gdel: $gdel")
                if (gdel > 0.0) {
                    result.add(s0)
                    return FunctionMinimum(seed, result, fcn.errorDef())
                }
            }
            val pp: MnParabolaPoint = MnLineSearch.search(fcn, s0.parameters(), step, gdel, prec)
            if (abs(pp.y() - s0.fval()) < prec.eps()) {
                MINUITPlugin.logStatic("VariableMetricBuilder: no improvement")
                break //no improvement
            }
            val p = MinimumParameters(MnUtils.add(s0.vec(), MnUtils.mul(step, pp.x())), pp.y())
            val g: FunctionGradient = gc.gradient(p, s0.gradient())
            edm = estimator().estimate(g, s0.error())
            if (edm < 0.0) {
                MINUITPlugin.logStatic("VariableMetricBuilder: matrix not pos.def.")
                MINUITPlugin.logStatic("edm < 0")
                s0 = MnPosDef.test(s0, prec)
                edm = estimator().estimate(g, s0.error())
                if (edm < 0.0) {
                    result.add(s0)
                    return FunctionMinimum(seed, result, fcn.errorDef())
                }
            }
            val e: MinimumError = errorUpdator().update(s0, p, g)
            result.add(MinimumState(p, e, g, edm, fcn.numOfCalls()))
            //     result[0] = MinimumState(p, e, g, edm, fcn.numOfCalls());
            edm *= 1.0 + 3.0 * e.dcovar()
        } while (edm > edmval && fcn.numOfCalls() < maxfcn)
        if (fcn.numOfCalls() >= maxfcn) {
            MINUITPlugin.logStatic("VariableMetricBuilder: call limit exceeded.")
            return FunctionMinimum(seed, result, fcn.errorDef(), MnReachedCallLimit())
        }
        return if (edm > edmval) {
            if (edm < abs(prec.eps2() * result[result.size - 1].fval())) {
                MINUITPlugin.logStatic("VariableMetricBuilder: machine accuracy limits further improvement.")
                FunctionMinimum(seed, result, fcn.errorDef())
            } else if (edm < 10.0 * edmval) {
                FunctionMinimum(seed, result, fcn.errorDef())
            } else {
                MINUITPlugin.logStatic("VariableMetricBuilder: finishes without convergence.")
                MINUITPlugin.logStatic("VariableMetricBuilder: edm= $edm requested: $edmval")
                FunctionMinimum(seed, result, fcn.errorDef(), MnAboveMaxEdm())
            }
        } else FunctionMinimum(seed, result, fcn.errorDef())
    }

    init {
        theErrorUpdator = DavidonErrorUpdator()
    }
}