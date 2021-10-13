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
 * Base class for minimizers.
 *
 * @version $Id$
 * @author Darksnake
 */
abstract class MnApplication {
    /* package protected */
    var checkAnalyticalDerivatives: Boolean

    /* package protected */ /* package protected */
    var theErrorDef = 1.0 /* package protected */
    var theFCN: MultiFunction?

    /* package protected */ /* package protected */
    var theNumCall /* package protected */ = 0
    var theState: MnUserParameterState

    /* package protected */
    var theStrategy: MnStrategy

    /* package protected */
    var useAnalyticalDerivatives: Boolean

    /* package protected */
    internal constructor(fcn: MultiFunction?, state: MnUserParameterState, stra: MnStrategy) {
        theFCN = fcn
        theState = state
        theStrategy = stra
        checkAnalyticalDerivatives = true
        useAnalyticalDerivatives = true
    }

    internal constructor(fcn: MultiFunction?, state: MnUserParameterState, stra: MnStrategy, nfcn: Int) {
        theFCN = fcn
        theState = state
        theStrategy = stra
        theNumCall = nfcn
        checkAnalyticalDerivatives = true
        useAnalyticalDerivatives = true
    }

    /**
     *
     * MultiFunction.
     *
     * @return a [MultiFunction] object.
     */
    fun MultiFunction(): MultiFunction? {
        return theFCN
    }

    /**
     * add free parameter
     *
     * @param err a double.
     * @param val a double.
     * @param name a [String] object.
     */
    fun add(name: String, `val`: Double, err: Double) {
        theState.add(name, `val`, err)
    }

    /**
     * add limited parameter
     *
     * @param up a double.
     * @param low a double.
     * @param name a [String] object.
     * @param val a double.
     * @param err a double.
     */
    fun add(name: String, `val`: Double, err: Double, low: Double, up: Double) {
        theState.add(name, `val`, err, low, up)
    }

    /**
     * add const parameter
     *
     * @param name a [String] object.
     * @param val a double.
     */
    fun add(name: String, `val`: Double) {
        theState.add(name, `val`)
    }

    /**
     *
     * checkAnalyticalDerivatives.
     *
     * @return a boolean.
     */
    fun checkAnalyticalDerivatives(): Boolean {
        return checkAnalyticalDerivatives
    }

    /**
     *
     * covariance.
     *
     * @return a [hep.dataforge.MINUIT.MnUserCovariance] object.
     */
    fun covariance(): MnUserCovariance {
        return theState.covariance()
    }

    /**
     *
     * error.
     *
     * @param index a int.
     * @return a double.
     */
    fun error(index: Int): Double {
        return theState.error(index)
    }

    /**
     *
     * error.
     *
     * @param name a [String] object.
     * @return a double.
     */
    fun error(name: String?): Double {
        return theState.error(name)
    }

    /**
     *
     * errorDef.
     *
     * @return a double.
     */
    fun errorDef(): Double {
        return theErrorDef
    }

    /**
     *
     * errors.
     *
     * @return an array of double.
     */
    fun errors(): DoubleArray {
        return theState.errors()
    }

    fun ext2int(i: Int, value: Double): Double {
        return theState.ext2int(i, value)
    }

    fun extOfInt(i: Int): Int {
        return theState.extOfInt(i)
    }
    //interaction via external number of parameter
    /**
     *
     * fix.
     *
     * @param index a int.
     */
    fun fix(index: Int) {
        theState.fix(index)
    }
    //interaction via name of parameter
    /**
     *
     * fix.
     *
     * @param name a [String] object.
     */
    fun fix(name: String?) {
        theState.fix(name)
    }

    /**
     * convert name into external number of parameter
     *
     * @param name a [String] object.
     * @return a int.
     */
    fun index(name: String?): Int {
        return theState.index(name)
    }

    // transformation internal <-> external
    fun int2ext(i: Int, value: Double): Double {
        return theState.int2ext(i, value)
    }

    fun intOfExt(i: Int): Int {
        return theState.intOfExt(i)
    }

    /**
     *
     * minimize.
     *
     * @return a [hep.dataforge.MINUIT.FunctionMinimum] object.
     */
    fun minimize(): FunctionMinimum {
        return minimize(DEFAULT_MAXFCN)
    }

    /**
     *
     * minimize.
     *
     * @param maxfcn a int.
     * @return a [hep.dataforge.MINUIT.FunctionMinimum] object.
     */
    fun minimize(maxfcn: Int): FunctionMinimum {
        return minimize(maxfcn, DEFAULT_TOLER)
    }

    /**
     * Causes minimization of the FCN and returns the result in form of a
     * FunctionMinimum.
     *
     * @param maxfcn specifies the (approximate) maximum number of function
     * calls after which the calculation will be stopped even if it has not yet
     * converged.
     * @param toler specifies the required tolerance on the function value at
     * the minimum. The default tolerance value is 0.1, and the minimization
     * will stop when the estimated vertical distance to the minimum (EDM) is
     * less than 0:001*tolerance*errorDef
     * @return a [hep.dataforge.MINUIT.FunctionMinimum] object.
     */
    fun minimize(maxfcn: Int, toler: Double): FunctionMinimum {
        var maxfcn = maxfcn
        check(theState.isValid()) { "Invalid state" }
        val npar = variableParameters()
        if (maxfcn == 0) {
            maxfcn = 200 + 100 * npar + 5 * npar * npar
        }
        val min: FunctionMinimum = minimizer().minimize(theFCN,
            theState,
            theStrategy,
            maxfcn,
            toler,
            theErrorDef,
            useAnalyticalDerivatives,
            checkAnalyticalDerivatives)
        theNumCall += min.nfcn()
        theState = min.userState()
        return min
    }

    abstract fun minimizer(): ModularFunctionMinimizer

    // facade: forward interface of MnUserParameters and MnUserTransformation
    fun minuitParameters(): List<MinuitParameter> {
        return theState.minuitParameters()
    }

    /**
     * convert external number into name of parameter
     *
     * @param index a int.
     * @return a [String] object.
     */
    fun name(index: Int): String {
        return theState.name(index)
    }

    /**
     *
     * numOfCalls.
     *
     * @return a int.
     */
    fun numOfCalls(): Int {
        return theNumCall
    }

    /**
     * access to single parameter
     * @param i
     * @return
     */
    fun parameter(i: Int): MinuitParameter {
        return theState.parameter(i)
    }

    /**
     *
     * parameters.
     *
     * @return a [hep.dataforge.MINUIT.MnUserParameters] object.
     */
    fun parameters(): MnUserParameters {
        return theState.parameters()
    }

    /**
     * access to parameters and errors in column-wise representation
     *
     * @return an array of double.
     */
    fun params(): DoubleArray {
        return theState.params()
    }

    /**
     *
     * precision.
     *
     * @return a [hep.dataforge.MINUIT.MnMachinePrecision] object.
     */
    fun precision(): MnMachinePrecision {
        return theState.precision()
    }

    /**
     *
     * release.
     *
     * @param index a int.
     */
    fun release(index: Int) {
        theState.release(index)
    }

    /**
     *
     * release.
     *
     * @param name a [String] object.
     */
    fun release(name: String?) {
        theState.release(name)
    }

    /**
     *
     * removeLimits.
     *
     * @param index a int.
     */
    fun removeLimits(index: Int) {
        theState.removeLimits(index)
    }

    /**
     *
     * removeLimits.
     *
     * @param name a [String] object.
     */
    fun removeLimits(name: String?) {
        theState.removeLimits(name)
    }

    /**
     * Minuit does a check of the user gradient at the beginning, if this is not
     * wanted the set this to "false".
     *
     * @param check a boolean.
     */
    fun setCheckAnalyticalDerivatives(check: Boolean) {
        checkAnalyticalDerivatives = check
    }

    /**
     *
     * setError.
     *
     * @param index a int.
     * @param err a double.
     */
    fun setError(index: Int, err: Double) {
        theState.setError(index, err)
    }

    /**
     *
     * setError.
     *
     * @param name a [String] object.
     * @param err a double.
     */
    fun setError(name: String?, err: Double) {
        theState.setError(name, err)
    }

    /**
     * errorDef() is the error definition of the function. E.g. is 1 if function
     * is Chi2 and 0.5 if function is -logLikelihood. If the user wants instead
     * the 2-sigma errors, errorDef() = 4, as Chi2(x+n*sigma) = Chi2(x) + n*n.
     *
     * @param errorDef a double.
     */
    fun setErrorDef(errorDef: Double) {
        theErrorDef = errorDef
    }

    /**
     *
     * setLimits.
     *
     * @param index a int.
     * @param low a double.
     * @param up a double.
     */
    fun setLimits(index: Int, low: Double, up: Double) {
        theState.setLimits(index, low, up)
    }

    /**
     *
     * setLimits.
     *
     * @param name a [String] object.
     * @param low a double.
     * @param up a double.
     */
    fun setLimits(name: String?, low: Double, up: Double) {
        theState.setLimits(name, low, up)
    }

    /**
     *
     * setPrecision.
     *
     * @param prec a double.
     */
    fun setPrecision(prec: Double) {
        theState.setPrecision(prec)
    }

    /**
     * By default if the function to be minimized implements MultiFunction then
     * the analytical gradient provided by the function will be used. Set this
     * to
     * <CODE>false</CODE> to disable this behaviour and force numerical
     * calculation of the gradient.
     *
     * @param use a boolean.
     */
    fun setUseAnalyticalDerivatives(use: Boolean) {
        useAnalyticalDerivatives = use
    }

    /**
     *
     * setValue.
     *
     * @param index a int.
     * @param val a double.
     */
    fun setValue(index: Int, `val`: Double) {
        theState.setValue(index, `val`)
    }

    /**
     *
     * setValue.
     *
     * @param name a [String] object.
     * @param val a double.
     */
    fun setValue(name: String?, `val`: Double) {
        theState.setValue(name, `val`)
    }

    /**
     *
     * state.
     *
     * @return a [hep.dataforge.MINUIT.MnUserParameterState] object.
     */
    fun state(): MnUserParameterState {
        return theState
    }

    /**
     *
     * strategy.
     *
     * @return a [hep.dataforge.MINUIT.MnStrategy] object.
     */
    fun strategy(): MnStrategy {
        return theStrategy
    }

    /**
     *
     * useAnalyticalDerivaties.
     *
     * @return a boolean.
     */
    fun useAnalyticalDerivaties(): Boolean {
        return useAnalyticalDerivatives
    }

    /**
     *
     * value.
     *
     * @param index a int.
     * @return a double.
     */
    fun value(index: Int): Double {
        return theState.value(index)
    }

    /**
     *
     * value.
     *
     * @param name a [String] object.
     * @return a double.
     */
    fun value(name: String?): Double {
        return theState.value(name)
    }

    /**
     *
     * variableParameters.
     *
     * @return a int.
     */
    fun variableParameters(): Int {
        return theState.variableParameters()
    }

    companion object {
        var DEFAULT_MAXFCN = 0
        var DEFAULT_STRATEGY = 1
        var DEFAULT_TOLER = 0.1
    }
}