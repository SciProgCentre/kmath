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
 * MinosError class.
 *
 * @author Darksnake
 * @version $Id$
 */
class MinosError {
    private var theLower: MnCross
    private var theMinValue = 0.0
    private var theParameter = 0
    private var theUpper: MnCross

    internal constructor() {
        theUpper = MnCross()
        theLower = MnCross()
    }

    internal constructor(par: Int, min: Double, low: MnCross, up: MnCross) {
        theParameter = par
        theMinValue = min
        theUpper = up
        theLower = low
    }

    /**
     *
     * atLowerLimit.
     *
     * @return a boolean.
     */
    fun atLowerLimit(): Boolean {
        return theLower.atLimit()
    }

    /**
     *
     * atLowerMaxFcn.
     *
     * @return a boolean.
     */
    fun atLowerMaxFcn(): Boolean {
        return theLower.atMaxFcn()
    }

    /**
     *
     * atUpperLimit.
     *
     * @return a boolean.
     */
    fun atUpperLimit(): Boolean {
        return theUpper.atLimit()
    }

    /**
     *
     * atUpperMaxFcn.
     *
     * @return a boolean.
     */
    fun atUpperMaxFcn(): Boolean {
        return theUpper.atMaxFcn()
    }

    /**
     *
     * isValid.
     *
     * @return a boolean.
     */
    fun isValid(): Boolean {
        return theLower.isValid() && theUpper.isValid()
    }

    /**
     *
     * lower.
     *
     * @return a double.
     */
    fun lower(): Double {
        return -1.0 * lowerState().error(parameter()) * (1.0 + theLower.value())
    }

    /**
     *
     * lowerNewMin.
     *
     * @return a boolean.
     */
    fun lowerNewMin(): Boolean {
        return theLower.newMinimum()
    }

    /**
     *
     * lowerState.
     *
     * @return a [hep.dataforge.MINUIT.MnUserParameterState] object.
     */
    fun lowerState(): MnUserParameterState {
        return theLower.state()
    }

    /**
     *
     * lowerValid.
     *
     * @return a boolean.
     */
    fun lowerValid(): Boolean {
        return theLower.isValid()
    }

    /**
     *
     * min.
     *
     * @return a double.
     */
    fun min(): Double {
        return theMinValue
    }

    /**
     *
     * nfcn.
     *
     * @return a int.
     */
    fun nfcn(): Int {
        return theUpper.nfcn() + theLower.nfcn()
    }

    /**
     *
     * parameter.
     *
     * @return a int.
     */
    fun parameter(): Int {
        return theParameter
    }

    /**
     *
     * range.
     *
     * @return
     */
    fun range(): Range {
        return Range(lower(), upper())
    }

    /**
     * {@inheritDoc}
     */
    override fun toString(): String {
        return MnPrint.toString(this)
    }

    /**
     *
     * upper.
     *
     * @return a double.
     */
    fun upper(): Double {
        return upperState().error(parameter()) * (1.0 + theUpper.value())
    }

    /**
     *
     * upperNewMin.
     *
     * @return a boolean.
     */
    fun upperNewMin(): Boolean {
        return theUpper.newMinimum()
    }

    /**
     *
     * upperState.
     *
     * @return a [hep.dataforge.MINUIT.MnUserParameterState] object.
     */
    fun upperState(): MnUserParameterState {
        return theUpper.state()
    }

    /**
     *
     * upperValid.
     *
     * @return a boolean.
     */
    fun upperValid(): Boolean {
        return theUpper.isValid()
    }
}