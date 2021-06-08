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

/**
 *
 * @author tonyj
 * @version $Id$
 */
internal class VariableMetricEDMEstimator {
    fun estimate(g: FunctionGradient, e: MinimumError): Double {
        if (e.invHessian().size() === 1) {
            return 0.5 * g.getGradient().getEntry(0) * g.getGradient().getEntry(0) * e.invHessian()[0, 0]
        }
        val rho: Double = MnUtils.similarity(g.getGradient(), e.invHessian())
        return 0.5 * rho
    }
}