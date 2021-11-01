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
import kotlin.jvm.JvmOverloads

/**
 * API class for Minos error analysis (asymmetric errors). Minimization has to
 * be done before and minimum must be valid; possibility to ask only for one
 * side of the Minos error;
 *
 * @version $Id$
 * @author Darksnake
 */
class MnMinos(fcn: MultiFunction?, min: FunctionMinimum?, stra: MnStrategy?) {
    private var theFCN: MultiFunction? = null
    private var theMinimum: FunctionMinimum? = null
    private var theStrategy: MnStrategy? = null

    /**
     * construct from FCN + minimum
     *
     * @param fcn a [MultiFunction] object.
     * @param min a [hep.dataforge.MINUIT.FunctionMinimum] object.
     */
    constructor(fcn: MultiFunction?, min: FunctionMinimum?) : this(fcn, min, MnApplication.DEFAULT_STRATEGY)

    /**
     * construct from FCN + minimum + strategy
     *
     * @param stra a int.
     * @param min a [hep.dataforge.MINUIT.FunctionMinimum] object.
     * @param fcn a [MultiFunction] object.
     */
    constructor(fcn: MultiFunction?, min: FunctionMinimum?, stra: Int) : this(fcn, min, MnStrategy(stra))
    //    public MnMinos(MultiFunction fcn, MnUserParameterState state, double errDef, MnStrategy stra) {
    //        theFCN = fcn;
    //        theStrategy = stra;
    //        
    //        MinimumState minState = null;
    //                
    //        MnUserTransformation transformation = state.getTransformation();
    //        
    //        MinimumSeed seed = new MinimumSeed(minState, transformation);
    //        
    //        theMinimum = new FunctionMinimum(seed,errDef);
    //    }
    /**
     *
     * loval.
     *
     * @param par a int.
     * @return a [hep.dataforge.MINUIT.MnCross] object.
     */
    fun loval(par: Int): MnCross {
        return loval(par, 1.0)
    }

    /**
     *
     * loval.
     *
     * @param par a int.
     * @param errDef a double.
     * @return a [hep.dataforge.MINUIT.MnCross] object.
     */
    fun loval(par: Int, errDef: Double): MnCross {
        return loval(par, errDef, MnApplication.DEFAULT_MAXFCN)
    }

    /**
     *
     * loval.
     *
     * @param par a int.
     * @param errDef a double.
     * @param maxcalls a int.
     * @return a [hep.dataforge.MINUIT.MnCross] object.
     */
    fun loval(par: Int, errDef: Double, maxcalls: Int): MnCross {
        var errDef = errDef
        var maxcalls = maxcalls
        errDef *= theMinimum!!.errorDef()
        assert(theMinimum!!.isValid())
        assert(!theMinimum!!.userState().parameter(par).isFixed())
        assert(!theMinimum!!.userState().parameter(par).isConst())
        if (maxcalls == 0) {
            val nvar: Int = theMinimum!!.userState().variableParameters()
            maxcalls = 2 * (nvar + 1) * (200 + 100 * nvar + 5 * nvar * nvar)
        }
        val para = intArrayOf(par)
        val upar: MnUserParameterState = theMinimum!!.userState().copy()
        val err: Double = upar.error(par)
        val `val`: Double = upar.value(par) - err
        val xmid = doubleArrayOf(`val`)
        val xdir = doubleArrayOf(-err)
        val ind: Int = upar.intOfExt(par)
        val m: MnAlgebraicSymMatrix = theMinimum!!.error().matrix()
        val xunit: Double = sqrt(errDef / err)
        for (i in 0 until m.nrow()) {
            if (i == ind) {
                continue
            }
            val xdev: Double = xunit * m[ind, i]
            val ext: Int = upar.extOfInt(i)
            upar.setValue(ext, upar.value(ext) - xdev)
        }
        upar.fix(par)
        upar.setValue(par, `val`)
        val toler = 0.1
        val cross = MnFunctionCross(theFCN, upar, theMinimum!!.fval(), theStrategy, errDef)
        val aopt: MnCross = cross.cross(para, xmid, xdir, toler, maxcalls)
        if (aopt.atLimit()) {
            MINUITPlugin.logStatic("MnMinos parameter $par is at lower limit.")
        }
        if (aopt.atMaxFcn()) {
            MINUITPlugin.logStatic("MnMinos maximum number of function calls exceeded for parameter $par")
        }
        if (aopt.newMinimum()) {
            MINUITPlugin.logStatic("MnMinos new minimum found while looking for parameter $par")
        }
        if (!aopt.isValid()) {
            MINUITPlugin.logStatic("MnMinos could not find lower value for parameter $par.")
        }
        return aopt
    }
    /**
     * calculate one side (negative or positive error) of the parameter
     *
     * @param maxcalls a int.
     * @param par a int.
     * @param errDef a double.
     * @return a double.
     */
    /**
     *
     * lower.
     *
     * @param par a int.
     * @param errDef a double.
     * @return a double.
     */
    /**
     *
     * lower.
     *
     * @param par a int.
     * @return a double.
     */
    @JvmOverloads
    fun lower(par: Int, errDef: Double = 1.0, maxcalls: Int = MnApplication.DEFAULT_MAXFCN): Double {
        val upar: MnUserParameterState = theMinimum!!.userState()
        val err: Double = theMinimum!!.userState().error(par)
        val aopt: MnCross = loval(par, errDef, maxcalls)
        return if (aopt.isValid()) -1.0 * err * (1.0 + aopt.value()) else if (aopt.atLimit()) upar.parameter(par)
            .lowerLimit() else upar.value(par)
    }

    /**
     *
     * minos.
     *
     * @param par a int.
     * @return a [hep.dataforge.MINUIT.MinosError] object.
     */
    fun minos(par: Int): MinosError {
        return minos(par, 1.0)
    }

    /**
     *
     * minos.
     *
     * @param par a int.
     * @param errDef a double.
     * @return a [hep.dataforge.MINUIT.MinosError] object.
     */
    fun minos(par: Int, errDef: Double): MinosError {
        return minos(par, errDef, MnApplication.DEFAULT_MAXFCN)
    }

    /**
     * Causes a MINOS error analysis to be performed on the parameter whose
     * number is specified. MINOS errors may be expensive to calculate, but are
     * very reliable since they take account of non-linearities in the problem
     * as well as parameter correlations, and are in general asymmetric.
     *
     * @param maxcalls Specifies the (approximate) maximum number of function
     * calls per parameter requested, after which the calculation will be
     * stopped for that parameter.
     * @param errDef a double.
     * @param par a int.
     * @return a [hep.dataforge.MINUIT.MinosError] object.
     */
    fun minos(par: Int, errDef: Double, maxcalls: Int): MinosError {
        assert(theMinimum!!.isValid())
        assert(!theMinimum!!.userState().parameter(par).isFixed())
        assert(!theMinimum!!.userState().parameter(par).isConst())
        val up: MnCross = upval(par, errDef, maxcalls)
        val lo: MnCross = loval(par, errDef, maxcalls)
        return MinosError(par, theMinimum!!.userState().value(par), lo, up)
    }

    /**
     *
     * range.
     *
     * @param par a int.
     * @return
     */
    fun range(par: Int): Range {
        return range(par, 1.0)
    }

    /**
     *
     * range.
     *
     * @param par a int.
     * @param errDef a double.
     * @return
     */
    fun range(par: Int, errDef: Double): Range {
        return range(par, errDef, MnApplication.DEFAULT_MAXFCN)
    }

    /**
     * Causes a MINOS error analysis for external parameter n.
     *
     * @param maxcalls a int.
     * @param errDef a double.
     * @return The lower and upper bounds of parameter
     * @param par a int.
     */
    fun range(par: Int, errDef: Double, maxcalls: Int): Range {
        val mnerr: MinosError = minos(par, errDef, maxcalls)
        return mnerr.range()
    }
    /**
     *
     * upper.
     *
     * @param par a int.
     * @param errDef a double.
     * @param maxcalls a int.
     * @return a double.
     */
    /**
     *
     * upper.
     *
     * @param par a int.
     * @param errDef a double.
     * @return a double.
     */
    /**
     *
     * upper.
     *
     * @param par a int.
     * @return a double.
     */
    @JvmOverloads
    fun upper(par: Int, errDef: Double = 1.0, maxcalls: Int = MnApplication.DEFAULT_MAXFCN): Double {
        val upar: MnUserParameterState = theMinimum!!.userState()
        val err: Double = theMinimum!!.userState().error(par)
        val aopt: MnCross = upval(par, errDef, maxcalls)
        return if (aopt.isValid()) err * (1.0 + aopt.value()) else if (aopt.atLimit()) upar.parameter(par)
            .upperLimit() else upar.value(par)
    }

    /**
     *
     * upval.
     *
     * @param par a int.
     * @return a [hep.dataforge.MINUIT.MnCross] object.
     */
    fun upval(par: Int): MnCross {
        return upval(par, 1.0)
    }

    /**
     *
     * upval.
     *
     * @param par a int.
     * @param errDef a double.
     * @return a [hep.dataforge.MINUIT.MnCross] object.
     */
    fun upval(par: Int, errDef: Double): MnCross {
        return upval(par, errDef, MnApplication.DEFAULT_MAXFCN)
    }

    /**
     *
     * upval.
     *
     * @param par a int.
     * @param errDef a double.
     * @param maxcalls a int.
     * @return a [hep.dataforge.MINUIT.MnCross] object.
     */
    fun upval(par: Int, errDef: Double, maxcalls: Int): MnCross {
        var errDef = errDef
        var maxcalls = maxcalls
        errDef *= theMinimum!!.errorDef()
        assert(theMinimum!!.isValid())
        assert(!theMinimum!!.userState().parameter(par).isFixed())
        assert(!theMinimum!!.userState().parameter(par).isConst())
        if (maxcalls == 0) {
            val nvar: Int = theMinimum!!.userState().variableParameters()
            maxcalls = 2 * (nvar + 1) * (200 + 100 * nvar + 5 * nvar * nvar)
        }
        val para = intArrayOf(par)
        val upar: MnUserParameterState = theMinimum!!.userState().copy()
        val err: Double = upar.error(par)
        val `val`: Double = upar.value(par) + err
        val xmid = doubleArrayOf(`val`)
        val xdir = doubleArrayOf(err)
        val ind: Int = upar.intOfExt(par)
        val m: MnAlgebraicSymMatrix = theMinimum!!.error().matrix()
        val xunit: Double = sqrt(errDef / err)
        for (i in 0 until m.nrow()) {
            if (i == ind) {
                continue
            }
            val xdev: Double = xunit * m[ind, i]
            val ext: Int = upar.extOfInt(i)
            upar.setValue(ext, upar.value(ext) + xdev)
        }
        upar.fix(par)
        upar.setValue(par, `val`)
        val toler = 0.1
        val cross = MnFunctionCross(theFCN, upar, theMinimum!!.fval(), theStrategy, errDef)
        val aopt: MnCross = cross.cross(para, xmid, xdir, toler, maxcalls)
        if (aopt.atLimit()) {
            MINUITPlugin.logStatic("MnMinos parameter $par is at upper limit.")
        }
        if (aopt.atMaxFcn()) {
            MINUITPlugin.logStatic("MnMinos maximum number of function calls exceeded for parameter $par")
        }
        if (aopt.newMinimum()) {
            MINUITPlugin.logStatic("MnMinos new minimum found while looking for parameter $par")
        }
        if (!aopt.isValid()) {
            MINUITPlugin.logStatic("MnMinos could not find upper value for parameter $par.")
        }
        return aopt
    }

    /**
     * construct from FCN + minimum + strategy
     *
     * @param stra a [hep.dataforge.MINUIT.MnStrategy] object.
     * @param min a [hep.dataforge.MINUIT.FunctionMinimum] object.
     * @param fcn a [MultiFunction] object.
     */
    init {
        theFCN = fcn
        theMinimum = min
        theStrategy = stra
    }
}