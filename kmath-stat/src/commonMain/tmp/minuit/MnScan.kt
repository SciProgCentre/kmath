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
 * MnScan scans the value of the user function by varying one parameter. It is
 * sometimes useful for debugging the user function or finding a reasonable
 * starting point.
 * construct from MultiFunction + MnUserParameterState + MnStrategy
 *
 * @param str a [hep.dataforge.MINUIT.MnStrategy] object.
 * @param par a [hep.dataforge.MINUIT.MnUserParameterState] object.
 * @param fcn a [MultiFunction] object.
 * @version $Id$
 * @author Darksnake
 */
class MnScan(fcn: MultiFunction?, par: MnUserParameterState, str: MnStrategy) : MnApplication(fcn, par, str) {
    private val theMinimizer: ScanMinimizer = ScanMinimizer()

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

    /**
     *
     * scan.
     *
     * @param par a int.
     * @return a [List] object.
     */
    fun scan(par: Int): List<Range> {
        return scan(par, 41)
    }

    /**
     *
     * scan.
     *
     * @param par a int.
     * @param maxsteps a int.
     * @return a [List] object.
     */
    fun scan(par: Int, maxsteps: Int): List<Range> {
        return scan(par, maxsteps, 0.0, 0.0)
    }

    /**
     * Scans the value of the user function by varying parameter number par,
     * leaving all other parameters fixed at the current value. If par is not
     * specified, all variable parameters are scanned in sequence. The number of
     * points npoints in the scan is 40 by default, and cannot exceed 100. The
     * range of the scan is by default 2 standard deviations on each side of the
     * current best value, but can be specified as from low to high. After each
     * scan, if a new minimum is found, the best parameter values are retained
     * as start values for future scans or minimizations. The curve resulting
     * from each scan can be plotted on the output terminal using MnPlot in
     * order to show the approximate behaviour of the function.
     *
     * @param high a double.
     * @param par a int.
     * @param maxsteps a int.
     * @param low a double.
     * @return a [List] object.
     */
    fun scan(par: Int, maxsteps: Int, low: Double, high: Double): List<Range> {
        val scan = MnParameterScan(theFCN, theState.parameters())
        var amin: Double = scan.fval()
        val result: List<Range> = scan.scan(par, maxsteps, low, high)
        if (scan.fval() < amin) {
            theState.setValue(par, scan.parameters().value(par))
            amin = scan.fval()
        }
        return result
    }
}