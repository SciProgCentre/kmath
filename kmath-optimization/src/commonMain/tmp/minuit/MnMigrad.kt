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
 * MnMigrad provides minimization of the function by the method of MIGRAD, the
 * most efficient and complete single method, recommended for general functions,
 * and the functionality for parameters interaction. It also retains the result
 * from the last minimization in case the user may want to do subsequent
 * minimization steps with parameter interactions in between the minimization
 * requests. The minimization produces as a by-product the error matrix of the
 * parameters, which is usually reliable unless warning messages are produced.
 *
 * @version $Id$
 * @author Darksnake
 */
class MnMigrad
/**
 * construct from MultiFunction + MnUserParameterState + MnStrategy
 *
 * @param str a [hep.dataforge.MINUIT.MnStrategy] object.
 * @param par a [hep.dataforge.MINUIT.MnUserParameterState] object.
 * @param fcn a [MultiFunction] object.
 */
    (fcn: MultiFunction?, par: MnUserParameterState, str: MnStrategy) : MnApplication(fcn, par, str) {
    private val theMinimizer: VariableMetricMinimizer = VariableMetricMinimizer()

    /**
     * construct from MultiFunction + double[] for parameters and errors
     * with default strategy
     *
     * @param err an array of double.
     * @param par an array of double.
     * @param fcn a [MultiFunction] object.
     */
    constructor(fcn: MultiFunction?, par: DoubleArray, err: DoubleArray) : this(fcn, par, err, DEFAULT_STRATEGY)

    /**
     * construct from MultiFunction + double[] for parameters and errors
     *
     * @param stra a int.
     * @param err an array of double.
     * @param fcn a [MultiFunction] object.
     * @param par an array of double.
     */
    constructor(fcn: MultiFunction?, par: DoubleArray, err: DoubleArray, stra: Int) : this(fcn,
        MnUserParameterState(par, err),
        MnStrategy(stra))

    /**
     * construct from MultiFunction + double[] for parameters and
     * MnUserCovariance with default strategy
     *
     * @param cov a [hep.dataforge.MINUIT.MnUserCovariance] object.
     * @param par an array of double.
     * @param fcn a [MultiFunction] object.
     */
    constructor(fcn: MultiFunction?, par: DoubleArray, cov: MnUserCovariance) : this(fcn, par, cov, DEFAULT_STRATEGY)

    /**
     * construct from MultiFunction + double[] for parameters and
     * MnUserCovariance
     *
     * @param stra a int.
     * @param cov a [hep.dataforge.MINUIT.MnUserCovariance] object.
     * @param fcn a [MultiFunction] object.
     * @param par an array of double.
     */
    constructor(fcn: MultiFunction?, par: DoubleArray, cov: MnUserCovariance, stra: Int) : this(fcn,
        MnUserParameterState(par, cov),
        MnStrategy(stra))

    /**
     * construct from MultiFunction + MnUserParameters with default
     * strategy
     *
     * @param fcn a [MultiFunction] object.
     * @param par a [hep.dataforge.MINUIT.MnUserParameters] object.
     */
    constructor(fcn: MultiFunction?, par: MnUserParameters) : this(fcn, par, DEFAULT_STRATEGY)

    /**
     * construct from MultiFunction + MnUserParameters
     *
     * @param stra a int.
     * @param par a [hep.dataforge.MINUIT.MnUserParameters] object.
     * @param fcn a [MultiFunction] object.
     */
    constructor(fcn: MultiFunction?, par: MnUserParameters, stra: Int) : this(fcn,
        MnUserParameterState(par),
        MnStrategy(stra))

    /**
     * construct from MultiFunction + MnUserParameters + MnUserCovariance
     * with default strategy
     *
     * @param cov a [hep.dataforge.MINUIT.MnUserCovariance] object.
     * @param par a [hep.dataforge.MINUIT.MnUserParameters] object.
     * @param fcn a [MultiFunction] object.
     */
    constructor(fcn: MultiFunction?, par: MnUserParameters, cov: MnUserCovariance) : this(fcn,
        par,
        cov,
        DEFAULT_STRATEGY)

    /**
     * construct from MultiFunction + MnUserParameters + MnUserCovariance
     *
     * @param stra a int.
     * @param cov a [hep.dataforge.MINUIT.MnUserCovariance] object.
     * @param fcn a [MultiFunction] object.
     * @param par a [hep.dataforge.MINUIT.MnUserParameters] object.
     */
    constructor(fcn: MultiFunction?, par: MnUserParameters, cov: MnUserCovariance, stra: Int) : this(fcn,
        MnUserParameterState(par, cov),
        MnStrategy(stra))

    override fun minimizer(): ModularFunctionMinimizer {
        return theMinimizer
    }
}