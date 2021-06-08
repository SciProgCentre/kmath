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
 * Scans the values of FCN as a function of one parameter and retains the best
 * function and parameter values found
 *
 * @version $Id$
 */
internal class MnParameterScan {
    private var theAmin: Double
    private var theFCN: MultiFunction?
    private var theParameters: MnUserParameters

    constructor(fcn: MultiFunction, par: MnUserParameters) {
        theFCN = fcn
        theParameters = par
        theAmin = fcn.value(par.params())
    }

    constructor(fcn: MultiFunction?, par: MnUserParameters, fval: Double) {
        theFCN = fcn
        theParameters = par
        theAmin = fval
    }

    fun fval(): Double {
        return theAmin
    }

    fun parameters(): MnUserParameters {
        return theParameters
    }

    fun scan(par: Int): List<Range> {
        return scan(par, 41)
    }

    fun scan(par: Int, maxsteps: Int): List<Range> {
        return scan(par, maxsteps, 0.0, 0.0)
    }

    /**
     * returns pairs of (x,y) points, x=parameter value, y=function value of FCN
     * @param high
     * @return
     */
    fun scan(par: Int, maxsteps: Int, low: Double, high: Double): List<Range> {
        var maxsteps = maxsteps
        var low = low
        var high = high
        if (maxsteps > 101) {
            maxsteps = 101
        }
        val result: MutableList<Range> = java.util.ArrayList<Range>(maxsteps + 1)
        val params: DoubleArray = theParameters.params()
        result.add(Range(params[par], theAmin))
        if (low > high) {
            return result
        }
        if (maxsteps < 2) {
            return result
        }
        if (low == 0.0 && high == 0.0) {
            low = params[par] - 2.0 * theParameters.error(par)
            high = params[par] + 2.0 * theParameters.error(par)
        }
        if (low == 0.0 && high == 0.0 && theParameters.parameter(par).hasLimits()) {
            if (theParameters.parameter(par).hasLowerLimit()) {
                low = theParameters.parameter(par).lowerLimit()
            }
            if (theParameters.parameter(par).hasUpperLimit()) {
                high = theParameters.parameter(par).upperLimit()
            }
        }
        if (theParameters.parameter(par).hasLimits()) {
            if (theParameters.parameter(par).hasLowerLimit()) {
                low = max(low, theParameters.parameter(par).lowerLimit())
            }
            if (theParameters.parameter(par).hasUpperLimit()) {
                high = min(high, theParameters.parameter(par).upperLimit())
            }
        }
        val x0 = low
        val stp = (high - low) / (maxsteps - 1.0)
        for (i in 0 until maxsteps) {
            params[par] = x0 + i.toDouble() * stp
            val fval: Double = theFCN.value(params)
            if (fval < theAmin) {
                theParameters.setValue(par, params[par])
                theAmin = fval
            }
            result.add(Range(params[par], fval))
        }
        return result
    }
}