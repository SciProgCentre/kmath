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
import ru.inr.mass.minuit.*

/**
 *
 * @version $Id$
 */
abstract class ModularFunctionMinimizer {
    abstract fun builder(): MinimumBuilder
    fun minimize(
        fcn: MultiFunction?,
        st: MnUserParameterState,
        strategy: MnStrategy,
        maxfcn: Int,
        toler: Double,
        errorDef: Double,
        useAnalyticalGradient: Boolean,
        checkGradient: Boolean
    ): FunctionMinimum {
        var maxfcn = maxfcn
        val mfcn = MnUserFcn(fcn, errorDef, st.getTransformation())
        val gc: GradientCalculator
        var providesAllDerivs = true
        /*
        * Проверяем в явном виде, что все аналитические производные присутствуют
        * TODO сделать возможность того, что часть производных задается аналитически, а часть численно
        */for (i in 0 until fcn.getDimension()) {
            if (!fcn.providesDeriv(i)) providesAllDerivs = false
        }
        gc = if (providesAllDerivs && useAnalyticalGradient) {
            AnalyticalGradientCalculator(fcn, st.getTransformation(), checkGradient)
        } else {
            Numerical2PGradientCalculator(mfcn, st.getTransformation(), strategy)
        }
        val npar: Int = st.variableParameters()
        if (maxfcn == 0) {
            maxfcn = 200 + 100 * npar + 5 * npar * npar
        }
        val mnseeds: MinimumSeed = seedGenerator().generate(mfcn, gc, st, strategy)
        return minimize(mfcn, gc, mnseeds, strategy, maxfcn, toler)
    }

    fun minimize(
        mfcn: MnFcn,
        gc: GradientCalculator?,
        seed: MinimumSeed?,
        strategy: MnStrategy?,
        maxfcn: Int,
        toler: Double
    ): FunctionMinimum {
        return builder().minimum(mfcn, gc, seed, strategy, maxfcn, toler * mfcn.errorDef())
    }

    abstract fun seedGenerator(): MinimumSeedGenerator
}