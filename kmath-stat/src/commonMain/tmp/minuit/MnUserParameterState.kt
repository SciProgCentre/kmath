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

import ru.inr.mass.minuit.*

/**
 * The class MnUserParameterState contains the MnUserParameters and the
 * MnUserCovariance. It can be created on input by the user, or by MINUIT itself
 * as user representable format of the result of the minimization.
 *
 * @version $Id$
 * @author Darksnake
 */
class MnUserParameterState {
    private var theCovariance: MnUserCovariance
    private var theCovarianceValid = false
    private var theEDM = 0.0
    private var theFVal = 0.0
    private var theGCCValid = false
    private var theGlobalCC: MnGlobalCorrelationCoeff? = null
    private var theIntCovariance: MnUserCovariance
    private var theIntParameters: MutableList<Double>
    private var theNFcn = 0
    private var theParameters: MnUserParameters
    private var theValid: Boolean

    internal constructor() {
        theValid = false
        theCovarianceValid = false
        theParameters = MnUserParameters()
        theCovariance = MnUserCovariance()
        theIntParameters = java.util.ArrayList<Double>()
        theIntCovariance = MnUserCovariance()
    }

    private constructor(other: MnUserParameterState) {
        theValid = other.theValid
        theCovarianceValid = other.theCovarianceValid
        theGCCValid = other.theGCCValid
        theFVal = other.theFVal
        theEDM = other.theEDM
        theNFcn = other.theNFcn
        theParameters = other.theParameters.copy()
        theCovariance = other.theCovariance
        theGlobalCC = other.theGlobalCC
        theIntParameters = java.util.ArrayList<Double>(other.theIntParameters)
        theIntCovariance = other.theIntCovariance.copy()
    }

    /**
     * construct from user parameters (before minimization)
     * @param par
     * @param err
     */
    internal constructor(par: DoubleArray, err: DoubleArray) {
        theValid = true
        theParameters = MnUserParameters(par, err)
        theCovariance = MnUserCovariance()
        theGlobalCC = MnGlobalCorrelationCoeff()
        theIntParameters = java.util.ArrayList<Double>(par.size)
        for (i in par.indices) {
            theIntParameters.add(par[i])
        }
        theIntCovariance = MnUserCovariance()
    }

    internal constructor(par: MnUserParameters) {
        theValid = true
        theParameters = par
        theCovariance = MnUserCovariance()
        theGlobalCC = MnGlobalCorrelationCoeff()
        theIntParameters = java.util.ArrayList(par.variableParameters())
        theIntCovariance = MnUserCovariance()
        val i = 0
        for (ipar in par.parameters()) {
            if (ipar.isConst() || ipar.isFixed()) {
                continue
            }
            if (ipar.hasLimits()) {
                theIntParameters.add(ext2int(ipar.number(), ipar.value()))
            } else {
                theIntParameters.add(ipar.value())
            }
        }
    }

    /**
     * construct from user parameters + covariance (before minimization)
     * @param nrow
     * @param cov
     */
    internal constructor(par: DoubleArray, cov: DoubleArray, nrow: Int) {
        theValid = true
        theCovarianceValid = true
        theCovariance = MnUserCovariance(cov, nrow)
        theGlobalCC = MnGlobalCorrelationCoeff()
        theIntParameters = java.util.ArrayList<Double>(par.size)
        theIntCovariance = MnUserCovariance(cov, nrow)
        val err = DoubleArray(par.size)
        for (i in par.indices) {
            assert(theCovariance[i, i] > 0.0)
            err[i] = sqrt(theCovariance[i, i])
            theIntParameters.add(par[i])
        }
        theParameters = MnUserParameters(par, err)
        assert(theCovariance.nrow() === variableParameters())
    }

    internal constructor(par: DoubleArray, cov: MnUserCovariance) {
        theValid = true
        theCovarianceValid = true
        theCovariance = cov
        theGlobalCC = MnGlobalCorrelationCoeff()
        theIntParameters = java.util.ArrayList<Double>(par.size)
        theIntCovariance = cov.copy()
        require(!(theCovariance.nrow() !== variableParameters())) { "Bad covariance size" }
        val err = DoubleArray(par.size)
        for (i in par.indices) {
            require(theCovariance[i, i] > 0.0) { "Bad covariance" }
            err[i] = sqrt(theCovariance[i, i])
            theIntParameters.add(par[i])
        }
        theParameters = MnUserParameters(par, err)
    }

    internal constructor(par: MnUserParameters, cov: MnUserCovariance) {
        theValid = true
        theCovarianceValid = true
        theParameters = par
        theCovariance = cov
        theGlobalCC = MnGlobalCorrelationCoeff()
        theIntParameters = java.util.ArrayList<Double>()
        theIntCovariance = cov.copy()
        theIntCovariance.scale(0.5)
        val i = 0
        for (ipar in par.parameters()) {
            if (ipar.isConst() || ipar.isFixed()) {
                continue
            }
            if (ipar.hasLimits()) {
                theIntParameters.add(ext2int(ipar.number(), ipar.value()))
            } else {
                theIntParameters.add(ipar.value())
            }
        }
        assert(theCovariance.nrow() === variableParameters())
    }

    /**
     * construct from internal parameters (after minimization)
     * @param trafo
     * @param up
     */
    internal constructor(st: MinimumState, up: Double, trafo: MnUserTransformation) {
        theValid = st.isValid()
        theCovarianceValid = false
        theGCCValid = false
        theFVal = st.fval()
        theEDM = st.edm()
        theNFcn = st.nfcn()
        theParameters = MnUserParameters()
        theCovariance = MnUserCovariance()
        theGlobalCC = MnGlobalCorrelationCoeff()
        theIntParameters = java.util.ArrayList<Double>()
        theIntCovariance = MnUserCovariance()
        for (ipar in trafo.parameters()) {
            if (ipar.isConst()) {
                add(ipar.name(), ipar.value())
            } else if (ipar.isFixed()) {
                add(ipar.name(), ipar.value(), ipar.error())
                if (ipar.hasLimits()) {
                    if (ipar.hasLowerLimit() && ipar.hasUpperLimit()) {
                        setLimits(ipar.name(), ipar.lowerLimit(), ipar.upperLimit())
                    } else if (ipar.hasLowerLimit() && !ipar.hasUpperLimit()) {
                        setLowerLimit(ipar.name(), ipar.lowerLimit())
                    } else {
                        setUpperLimit(ipar.name(), ipar.upperLimit())
                    }
                }
                fix(ipar.name())
            } else if (ipar.hasLimits()) {
                val i: Int = trafo.intOfExt(ipar.number())
                val err: Double = if (st.hasCovariance()) sqrt(2.0 * up * st.error().invHessian()[i, i]) else st.parameters().dirin().getEntry(i)
                add(ipar.name(),
                    trafo.int2ext(i, st.vec().getEntry(i)),
                    trafo.int2extError(i, st.vec().getEntry(i), err))
                if (ipar.hasLowerLimit() && ipar.hasUpperLimit()) {
                    setLimits(ipar.name(), ipar.lowerLimit(), ipar.upperLimit())
                } else if (ipar.hasLowerLimit() && !ipar.hasUpperLimit()) {
                    setLowerLimit(ipar.name(), ipar.lowerLimit())
                } else {
                    setUpperLimit(ipar.name(), ipar.upperLimit())
                }
            } else {
                val i: Int = trafo.intOfExt(ipar.number())
                val err: Double = if (st.hasCovariance()) sqrt(2.0 * up * st.error().invHessian()[i, i]) else st.parameters().dirin().getEntry(i)
                add(ipar.name(), st.vec().getEntry(i), err)
            }
        }
        theCovarianceValid = st.error().isValid()
        if (theCovarianceValid) {
            theCovariance = trafo.int2extCovariance(st.vec(), st.error().invHessian())
            theIntCovariance = MnUserCovariance(st.error().invHessian().data().clone(), st.error().invHessian().nrow())
            theCovariance.scale(2.0 * up)
            theGlobalCC = MnGlobalCorrelationCoeff(st.error().invHessian())
            theGCCValid = true
            assert(theCovariance.nrow() === variableParameters())
        }
    }

    /**
     * add free parameter name, value, error
     *
     * @param err a double.
     * @param val a double.
     * @param name a [String] object.
     */
    fun add(name: String, `val`: Double, err: Double) {
        theParameters.add(name, `val`, err)
        theIntParameters.add(`val`)
        theCovarianceValid = false
        theGCCValid = false
        theValid = true
    }

    /**
     * add limited parameter name, value, lower bound, upper bound
     *
     * @param name a [String] object.
     * @param val a double.
     * @param low a double.
     * @param err a double.
     * @param up a double.
     */
    fun add(name: String, `val`: Double, err: Double, low: Double, up: Double) {
        theParameters.add(name, `val`, err, low, up)
        theCovarianceValid = false
        theIntParameters.add(ext2int(index(name), `val`))
        theGCCValid = false
        theValid = true
    }

    /**
     * add const parameter name, value
     *
     * @param name a [String] object.
     * @param val a double.
     */
    fun add(name: String, `val`: Double) {
        theParameters.add(name, `val`)
        theValid = true
    }

    /**
     *
     * copy.
     *
     * @return a [hep.dataforge.MINUIT.MnUserParameterState] object.
     */
    fun copy(): MnUserParameterState {
        return MnUserParameterState(this)
    }

    /**
     * Covariance matrix in the external representation
     *
     * @return a [hep.dataforge.MINUIT.MnUserCovariance] object.
     */
    fun covariance(): MnUserCovariance {
        return theCovariance
    }

    /**
     * Returns the expected vertival distance to the minimum (EDM)
     *
     * @return a double.
     */
    fun edm(): Double {
        return theEDM
    }

    /**
     *
     * error.
     *
     * @param index a int.
     * @return a double.
     */
    fun error(index: Int): Double {
        return theParameters.error(index)
    }

    /**
     *
     * error.
     *
     * @param name a [String] object.
     * @return a double.
     */
    fun error(name: String?): Double {
        return error(index(name))
    }

    /**
     *
     * errors.
     *
     * @return an array of double.
     */
    fun errors(): DoubleArray {
        return theParameters.errors()
    }

    fun ext2int(i: Int, `val`: Double): Double {
        return theParameters.trafo().ext2int(i, `val`)
    }

    /**
     *
     * extOfInt.
     *
     * @param internal a int.
     * @return a int.
     */
    fun extOfInt(internal: Int): Int {
        return theParameters.trafo().extOfInt(internal)
    }
    /// interaction via external number of parameter
    /**
     *
     * fix.
     *
     * @param e a int.
     */
    fun fix(e: Int) {
        val i = intOfExt(e)
        if (theCovarianceValid) {
            theCovariance = MnCovarianceSqueeze.squeeze(theCovariance, i)
            theIntCovariance = MnCovarianceSqueeze.squeeze(theIntCovariance, i)
        }
        theIntParameters.removeAt(i)
        theParameters.fix(e)
        theGCCValid = false
    }
    /// interaction via name of parameter
    /**
     *
     * fix.
     *
     * @param name a [String] object.
     */
    fun fix(name: String?) {
        fix(index(name))
    }

    /**
     * returns the function value at the minimum
     *
     * @return a double.
     */
    fun fval(): Double {
        return theFVal
    }

    /**
     * transformation internal <-> external
     * @return
     */
    fun getTransformation(): MnUserTransformation {
        return theParameters.trafo()
    }

    fun globalCC(): MnGlobalCorrelationCoeff? {
        return theGlobalCC
    }

    /**
     * Returns
     * <CODE>true</CODE> if the the state has a valid covariance,
     * <CODE>false</CODE> otherwise.
     *
     * @return a boolean.
     */
    fun hasCovariance(): Boolean {
        return theCovarianceValid
    }

    /**
     *
     * hasGlobalCC.
     *
     * @return a boolean.
     */
    fun hasGlobalCC(): Boolean {
        return theGCCValid
    }

    /**
     * convert name into external number of parameter
     *
     * @param name a [String] object.
     * @return a int.
     */
    fun index(name: String?): Int {
        return theParameters.index(name)
    }

    // transformation internal <-> external
    fun int2ext(i: Int, `val`: Double): Double {
        return theParameters.trafo().int2ext(i, `val`)
    }

    fun intCovariance(): MnUserCovariance {
        return theIntCovariance
    }

    fun intOfExt(ext: Int): Int {
        return theParameters.trafo().intOfExt(ext)
    }

    /**
     * Minuit internal representation
     * @return
     */
    fun intParameters(): List<Double> {
        return theIntParameters
    }

    /**
     * Returns
     * <CODE>true</CODE> if the the state is valid,
     * <CODE>false</CODE> if not
     *
     * @return a boolean.
     */
    fun isValid(): Boolean {
        return theValid
    }

    // facade: forward interface of MnUserParameters and MnUserTransformation
    fun minuitParameters(): List<MinuitParameter> {
        return theParameters.parameters()
    }

    /**
     * convert external number into name of parameter
     *
     * @param index a int.
     * @return a [String] object.
     */
    fun name(index: Int): String {
        return theParameters.name(index)
    }

    /**
     * Returns the number of function calls during the minimization.
     *
     * @return a int.
     */
    fun nfcn(): Int {
        return theNFcn
    }

    fun parameter(i: Int): MinuitParameter {
        return theParameters.parameter(i)
    }

    //user external representation
    fun parameters(): MnUserParameters {
        return theParameters
    }

    /**
     * access to parameters and errors in column-wise representation
     *
     * @return an array of double.
     */
    fun params(): DoubleArray {
        return theParameters.params()
    }

    /**
     *
     * precision.
     *
     * @return a [hep.dataforge.MINUIT.MnMachinePrecision] object.
     */
    fun precision(): MnMachinePrecision {
        return theParameters.precision()
    }

    /**
     *
     * release.
     *
     * @param e a int.
     */
    fun release(e: Int) {
        theParameters.release(e)
        theCovarianceValid = false
        theGCCValid = false
        val i = intOfExt(e)
        if (parameter(e).hasLimits()) {
            theIntParameters.add(i, ext2int(e, parameter(e).value()))
        } else {
            theIntParameters.add(i, parameter(e).value())
        }
    }

    /**
     *
     * release.
     *
     * @param name a [String] object.
     */
    fun release(name: String?) {
        release(index(name))
    }

    /**
     *
     * removeLimits.
     *
     * @param e a int.
     */
    fun removeLimits(e: Int) {
        theParameters.removeLimits(e)
        theCovarianceValid = false
        theGCCValid = false
        if (!parameter(e).isFixed() && !parameter(e).isConst()) {
            theIntParameters[intOfExt(e)] = value(e)
        }
    }

    /**
     *
     * removeLimits.
     *
     * @param name a [String] object.
     */
    fun removeLimits(name: String?) {
        removeLimits(index(name))
    }

    /**
     *
     * setError.
     *
     * @param e a int.
     * @param err a double.
     * @param err a double.
     */
    fun setError(e: Int, err: Double) {
        theParameters.setError(e, err)
    }

    /**
     *
     * setError.
     *
     * @param name a [String] object.
     * @param err a double.
     */
    fun setError(name: String?, err: Double) {
        setError(index(name), err)
    }

    /**
     *
     * setLimits.
     *
     * @param e a int.
     * @param low a double.
     * @param up a double.
     */
    fun setLimits(e: Int, low: Double, up: Double) {
        theParameters.setLimits(e, low, up)
        theCovarianceValid = false
        theGCCValid = false
        if (!parameter(e).isFixed() && !parameter(e).isConst()) {
            val i = intOfExt(e)
            if (low < theIntParameters[i] && theIntParameters[i] < up) {
                theIntParameters[i] = ext2int(e, theIntParameters[i])
            } else {
                theIntParameters[i] = ext2int(e, 0.5 * (low + up))
            }
        }
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
        setLimits(index(name), low, up)
    }

    /**
     *
     * setLowerLimit.
     *
     * @param e a int.
     * @param low a double.
     */
    fun setLowerLimit(e: Int, low: Double) {
        theParameters.setLowerLimit(e, low)
        theCovarianceValid = false
        theGCCValid = false
        if (!parameter(e).isFixed() && !parameter(e).isConst()) {
            val i = intOfExt(e)
            if (low < theIntParameters[i]) {
                theIntParameters[i] = ext2int(e, theIntParameters[i])
            } else {
                theIntParameters[i] = ext2int(e, low + 0.5 * abs(low + 1.0))
            }
        }
    }

    /**
     *
     * setLowerLimit.
     *
     * @param name a [String] object.
     * @param low a double.
     */
    fun setLowerLimit(name: String?, low: Double) {
        setLowerLimit(index(name), low)
    }

    /**
     *
     * setPrecision.
     *
     * @param eps a double.
     */
    fun setPrecision(eps: Double) {
        theParameters.setPrecision(eps)
    }

    /**
     *
     * setUpperLimit.
     *
     * @param e a int.
     * @param up a double.
     */
    fun setUpperLimit(e: Int, up: Double) {
        theParameters.setUpperLimit(e, up)
        theCovarianceValid = false
        theGCCValid = false
        if (!parameter(e).isFixed() && !parameter(e).isConst()) {
            val i = intOfExt(e)
            if (theIntParameters[i] < up) {
                theIntParameters[i] = ext2int(e, theIntParameters[i])
            } else {
                theIntParameters[i] = ext2int(e, up - 0.5 * abs(up + 1.0))
            }
        }
    }

    /**
     *
     * setUpperLimit.
     *
     * @param name a [String] object.
     * @param up a double.
     */
    fun setUpperLimit(name: String?, up: Double) {
        setUpperLimit(index(name), up)
    }

    /**
     *
     * setValue.
     *
     * @param e a int.
     * @param val a double.
     */
    fun setValue(e: Int, `val`: Double) {
        theParameters.setValue(e, `val`)
        if (!parameter(e).isFixed() && !parameter(e).isConst()) {
            val i = intOfExt(e)
            if (parameter(e).hasLimits()) {
                theIntParameters[i] = ext2int(e, `val`)
            } else {
                theIntParameters[i] = `val`
            }
        }
    }

    /**
     *
     * setValue.
     *
     * @param name a [String] object.
     * @param val a double.
     */
    fun setValue(name: String?, `val`: Double) {
        setValue(index(name), `val`)
    }

    /** {@inheritDoc}  */
    override fun toString(): String {
        return MnPrint.toString(this)
    }

    /**
     *
     * value.
     *
     * @param index a int.
     * @return a double.
     */
    fun value(index: Int): Double {
        return theParameters.value(index)
    }

    /**
     *
     * value.
     *
     * @param name a [String] object.
     * @return a double.
     */
    fun value(name: String?): Double {
        return value(index(name))
    }

    /**
     *
     * variableParameters.
     *
     * @return a int.
     */
    fun variableParameters(): Int {
        return theParameters.variableParameters()
    }
}