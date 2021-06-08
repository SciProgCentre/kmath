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
 * API class for the user interaction with the parameters. Serves as input to
 * the minimizer as well as output from it; users can interact: fix/release
 * parameters, set values and errors, etc.; parameters can be accessed via their
 * parameter number or via their user-specified name.
 *
 * @version $Id$
 * @author Darksnake
 */
class MnUserParameters {
    private var theTransformation: MnUserTransformation

    /**
     * Creates a new instance of MnUserParameters
     */
    constructor() {
        theTransformation = MnUserTransformation()
    }

    /**
     *
     * Constructor for MnUserParameters.
     *
     * @param par an array of double.
     * @param err an array of double.
     */
    constructor(par: DoubleArray, err: DoubleArray) {
        theTransformation = MnUserTransformation(par, err)
    }

    private constructor(other: MnUserParameters) {
        theTransformation = other.theTransformation.copy()
    }

    /**
     * Add free parameter name, value, error
     *
     *
     * When adding parameters, MINUIT assigns indices to each parameter which
     * will be the same as in the double[] in the
     * MultiFunction.valueOf(). That means the first parameter the user
     * adds gets index 0, the second index 1, and so on. When calculating the
     * function value inside FCN, MINUIT will call
     * MultiFunction.valueOf() with the elements at their respective
     * positions.
     *
     * @param err a double.
     * @param val a double.
     * @param name a [String] object.
     */
    fun add(name: String, `val`: Double, err: Double) {
        theTransformation.add(name, `val`, err)
    }

    /**
     * Add limited parameter name, value, lower bound, upper bound
     *
     * @param up a double.
     * @param low a double.
     * @param name a [String] object.
     * @param val a double.
     * @param err a double.
     */
    fun add(name: String, `val`: Double, err: Double, low: Double, up: Double) {
        theTransformation.add(name, `val`, err, low, up)
    }

    /**
     * Add const parameter name, value
     *
     * @param name a [String] object.
     * @param val a double.
     */
    fun add(name: String, `val`: Double) {
        theTransformation.add(name, `val`)
    }

    /**
     *
     * copy.
     *
     * @return a [hep.dataforge.MINUIT.MnUserParameters] object.
     */
    fun copy(): MnUserParameters {
        return MnUserParameters(this)
    }

    /**
     *
     * error.
     *
     * @param index a int.
     * @return a double.
     */
    fun error(index: Int): Double {
        return theTransformation.error(index)
    }

    /**
     *
     * error.
     *
     * @param name a [String] object.
     * @return a double.
     */
    fun error(name: String?): Double {
        return theTransformation.error(name)
    }

    fun errors(): DoubleArray {
        return theTransformation.errors()
    }
    /// interaction via external number of parameter
    /**
     * Fixes the specified parameter (so that the minimizer will no longer vary
     * it)
     *
     * @param index a int.
     */
    fun fix(index: Int) {
        theTransformation.fix(index)
    }
    /// interaction via name of parameter
    /**
     * Fixes the specified parameter (so that the minimizer will no longer vary
     * it)
     *
     * @param name a [String] object.
     */
    fun fix(name: String?) {
        theTransformation.fix(name)
    }

    /**
     * convert name into external number of parameter
     * @param name
     * @return
     */
    fun index(name: String?): Int {
        return theTransformation.index(name)
    }

    /**
     * convert external number into name of parameter
     * @param index
     * @return
     */
    fun name(index: Int): String {
        return theTransformation.name(index)
    }

    /**
     * access to single parameter
     * @param index
     * @return
     */
    fun parameter(index: Int): MinuitParameter {
        return theTransformation.parameter(index)
    }

    /**
     * access to parameters (row-wise)
     * @return
     */
    fun parameters(): List<MinuitParameter> {
        return theTransformation.parameters()
    }

    /**
     * access to parameters and errors in column-wise representation
     * @return
     */
    fun params(): DoubleArray {
        return theTransformation.params()
    }

    /**
     *
     * precision.
     *
     * @return a [hep.dataforge.MINUIT.MnMachinePrecision] object.
     */
    fun precision(): MnMachinePrecision {
        return theTransformation.precision()
    }

    /**
     * Releases the specified parameter (so that the minimizer can vary it)
     *
     * @param index a int.
     */
    fun release(index: Int) {
        theTransformation.release(index)
    }

    /**
     * Releases the specified parameter (so that the minimizer can vary it)
     *
     * @param name a [String] object.
     */
    fun release(name: String?) {
        theTransformation.release(name)
    }

    /**
     *
     * removeLimits.
     *
     * @param index a int.
     */
    fun removeLimits(index: Int) {
        theTransformation.removeLimits(index)
    }

    /**
     *
     * removeLimits.
     *
     * @param name a [String] object.
     */
    fun removeLimits(name: String?) {
        theTransformation.removeLimits(name)
    }

    /**
     *
     * setError.
     *
     * @param index a int.
     * @param err a double.
     */
    fun setError(index: Int, err: Double) {
        theTransformation.setError(index, err)
    }

    /**
     *
     * setError.
     *
     * @param name a [String] object.
     * @param err a double.
     */
    fun setError(name: String?, err: Double) {
        theTransformation.setError(name, err)
    }

    /**
     * Set the lower and upper bound on the specified variable.
     *
     * @param up a double.
     * @param low a double.
     * @param index a int.
     */
    fun setLimits(index: Int, low: Double, up: Double) {
        theTransformation.setLimits(index, low, up)
    }

    /**
     * Set the lower and upper bound on the specified variable.
     *
     * @param up a double.
     * @param low a double.
     * @param name a [String] object.
     */
    fun setLimits(name: String?, low: Double, up: Double) {
        theTransformation.setLimits(name, low, up)
    }

    /**
     *
     * setLowerLimit.
     *
     * @param index a int.
     * @param low a double.
     */
    fun setLowerLimit(index: Int, low: Double) {
        theTransformation.setLowerLimit(index, low)
    }

    /**
     *
     * setLowerLimit.
     *
     * @param name a [String] object.
     * @param low a double.
     */
    fun setLowerLimit(name: String?, low: Double) {
        theTransformation.setLowerLimit(name, low)
    }

    /**
     *
     * setPrecision.
     *
     * @param eps a double.
     */
    fun setPrecision(eps: Double) {
        theTransformation.setPrecision(eps)
    }

    /**
     *
     * setUpperLimit.
     *
     * @param index a int.
     * @param up a double.
     */
    fun setUpperLimit(index: Int, up: Double) {
        theTransformation.setUpperLimit(index, up)
    }

    /**
     *
     * setUpperLimit.
     *
     * @param name a [String] object.
     * @param up a double.
     */
    fun setUpperLimit(name: String?, up: Double) {
        theTransformation.setUpperLimit(name, up)
    }

    /**
     * Set the value of parameter. The parameter in question may be variable,
     * fixed, or constant, but must be defined.
     *
     * @param index a int.
     * @param val a double.
     */
    fun setValue(index: Int, `val`: Double) {
        theTransformation.setValue(index, `val`)
    }

    /**
     * Set the value of parameter. The parameter in question may be variable,
     * fixed, or constant, but must be defined.
     *
     * @param name a [String] object.
     * @param val a double.
     */
    fun setValue(name: String?, `val`: Double) {
        theTransformation.setValue(name, `val`)
    }

    /** {@inheritDoc}  */
    override fun toString(): String {
        return MnPrint.toString(this)
    }

    fun trafo(): MnUserTransformation {
        return theTransformation
    }

    /**
     *
     * value.
     *
     * @param index a int.
     * @return a double.
     */
    fun value(index: Int): Double {
        return theTransformation.value(index)
    }

    /**
     *
     * value.
     *
     * @param name a [String] object.
     * @return a double.
     */
    fun value(name: String?): Double {
        return theTransformation.value(name)
    }

    /**
     *
     * variableParameters.
     *
     * @return a int.
     */
    fun variableParameters(): Int {
        return theTransformation.variableParameters()
    }
}