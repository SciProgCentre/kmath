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
 * With MnHesse the user can instructs MINUITPlugin to calculate, by finite
 * differences, the Hessian or error matrix. That is, it calculates the full
 * matrix of second derivatives of the function with respect to the currently
 * variable parameters, and inverts it.
 *
 * @version $Id$
 * @author Darksnake
 */
class MnHesse {
    private var theStrategy: MnStrategy

    /**
     * default constructor with default strategy
     */
    constructor() {
        theStrategy = MnStrategy(1)
    }

    /**
     * constructor with user-defined strategy level
     *
     * @param stra a int.
     */
    constructor(stra: Int) {
        theStrategy = MnStrategy(stra)
    }

    /**
     * conctructor with specific strategy
     *
     * @param stra a [hep.dataforge.MINUIT.MnStrategy] object.
     */
    constructor(stra: MnStrategy) {
        theStrategy = stra
    }
    ///
    /// low-level API
    ///
    /**
     *
     * calculate.
     *
     * @param fcn a [MultiFunction] object.
     * @param par an array of double.
     * @param err an array of double.
     * @return a [hep.dataforge.MINUIT.MnUserParameterState] object.
     */
    fun calculate(fcn: MultiFunction?, par: DoubleArray, err: DoubleArray): MnUserParameterState {
        return calculate(fcn, par, err, 0)
    }

    /**
     * FCN + parameters + errors
     *
     * @param maxcalls a int.
     * @param fcn a [MultiFunction] object.
     * @param par an array of double.
     * @param err an array of double.
     * @return a [hep.dataforge.MINUIT.MnUserParameterState] object.
     */
    fun calculate(fcn: MultiFunction?, par: DoubleArray, err: DoubleArray, maxcalls: Int): MnUserParameterState {
        return calculate(fcn, MnUserParameterState(par, err), maxcalls)
    }

    /**
     *
     * calculate.
     *
     * @param fcn a [MultiFunction] object.
     * @param par an array of double.
     * @param cov a [hep.dataforge.MINUIT.MnUserCovariance] object.
     * @return a [hep.dataforge.MINUIT.MnUserParameterState] object.
     */
    fun calculate(fcn: MultiFunction?, par: DoubleArray, cov: MnUserCovariance): MnUserParameterState {
        return calculate(fcn, par, cov, 0)
    }

    /**
     * FCN + parameters + MnUserCovariance
     *
     * @param maxcalls a int.
     * @param fcn a [MultiFunction] object.
     * @param par an array of double.
     * @param cov a [hep.dataforge.MINUIT.MnUserCovariance] object.
     * @return a [hep.dataforge.MINUIT.MnUserParameterState] object.
     */
    fun calculate(fcn: MultiFunction?, par: DoubleArray, cov: MnUserCovariance, maxcalls: Int): MnUserParameterState {
        return calculate(fcn, MnUserParameterState(par, cov), maxcalls)
    }
    ///
    /// high-level API
    ///
    /**
     *
     * calculate.
     *
     * @param fcn a [MultiFunction] object.
     * @param par a [hep.dataforge.MINUIT.MnUserParameters] object.
     * @return a [hep.dataforge.MINUIT.MnUserParameterState] object.
     */
    fun calculate(fcn: MultiFunction?, par: MnUserParameters): MnUserParameterState {
        return calculate(fcn, par, 0)
    }

    /**
     * FCN + MnUserParameters
     *
     * @param maxcalls a int.
     * @param fcn a [MultiFunction] object.
     * @param par a [hep.dataforge.MINUIT.MnUserParameters] object.
     * @return a [hep.dataforge.MINUIT.MnUserParameterState] object.
     */
    fun calculate(fcn: MultiFunction?, par: MnUserParameters, maxcalls: Int): MnUserParameterState {
        return calculate(fcn, MnUserParameterState(par), maxcalls)
    }

    /**
     *
     * calculate.
     *
     * @param fcn a [MultiFunction] object.
     * @param par a [hep.dataforge.MINUIT.MnUserParameters] object.
     * @param cov a [hep.dataforge.MINUIT.MnUserCovariance] object.
     * @return a [hep.dataforge.MINUIT.MnUserParameterState] object.
     */
    fun calculate(fcn: MultiFunction?, par: MnUserParameters, cov: MnUserCovariance?): MnUserParameterState {
        return calculate(fcn, par, 0)
    }

    /**
     * FCN + MnUserParameters + MnUserCovariance
     *
     * @param maxcalls a int.
     * @param fcn a [MultiFunction] object.
     * @param par a [hep.dataforge.MINUIT.MnUserParameters] object.
     * @param cov a [hep.dataforge.MINUIT.MnUserCovariance] object.
     * @return a [hep.dataforge.MINUIT.MnUserParameterState] object.
     */
    fun calculate(
        fcn: MultiFunction?,
        par: MnUserParameters,
        cov: MnUserCovariance,
        maxcalls: Int
    ): MnUserParameterState {
        return calculate(fcn, MnUserParameterState(par, cov), maxcalls)
    }

    /**
     * FCN + MnUserParameterState
     *
     * @param maxcalls a int.
     * @param fcn a [MultiFunction] object.
     * @param state a [hep.dataforge.MINUIT.MnUserParameterState] object.
     * @return a [hep.dataforge.MINUIT.MnUserParameterState] object.
     */
    fun calculate(fcn: MultiFunction?, state: MnUserParameterState, maxcalls: Int): MnUserParameterState {
        val errDef = 1.0 // FixMe!
        val n: Int = state.variableParameters()
        val mfcn = MnUserFcn(fcn, errDef, state.getTransformation())
        val x: RealVector = ArrayRealVector(n)
        for (i in 0 until n) {
            x.setEntry(i, state.intParameters()[i])
        }
        val amin: Double = mfcn.value(x)
        val gc = Numerical2PGradientCalculator(mfcn, state.getTransformation(), theStrategy)
        val par = MinimumParameters(x, amin)
        val gra: FunctionGradient = gc.gradient(par)
        val tmp: MinimumState = calculate(mfcn,
            MinimumState(par, MinimumError(MnAlgebraicSymMatrix(n), 1.0), gra, state.edm(), state.nfcn()),
            state.getTransformation(),
            maxcalls)
        return MnUserParameterState(tmp, errDef, state.getTransformation())
    }

    ///
    /// internal interface
    ///
    fun calculate(mfcn: MnFcn, st: MinimumState, trafo: MnUserTransformation, maxcalls: Int): MinimumState {
        var maxcalls = maxcalls
        val prec: MnMachinePrecision = trafo.precision()
        // make sure starting at the right place
        val amin: Double = mfcn.value(st.vec())
        val aimsag: Double = sqrt(prec.eps2()) * (abs(amin) + mfcn.errorDef())

        // diagonal elements first
        val n: Int = st.parameters().vec().getDimension()
        if (maxcalls == 0) {
            maxcalls = 200 + 100 * n + 5 * n * n
        }
        var vhmat = MnAlgebraicSymMatrix(n)
        var g2: RealVector = st.gradient().getGradientDerivative().copy()
        var gst: RealVector = st.gradient().getStep().copy()
        var grd: RealVector = st.gradient().getGradient().copy()
        var dirin: RealVector = st.gradient().getStep().copy()
        val yy: RealVector = ArrayRealVector(n)
        if (st.gradient().isAnalytical()) {
            val igc = InitialGradientCalculator(mfcn, trafo, theStrategy)
            val tmp: FunctionGradient = igc.gradient(st.parameters())
            gst = tmp.getStep().copy()
            dirin = tmp.getStep().copy()
            g2 = tmp.getGradientDerivative().copy()
        }
        return try {
            val x: RealVector = st.parameters().vec().copy()
            for (i in 0 until n) {
                val xtf: Double = x.getEntry(i)
                val dmin: Double = 8.0 * prec.eps2() * (abs(xtf) + prec.eps2())
                var d: Double = abs(gst.getEntry(i))
                if (d < dmin) {
                    d = dmin
                }
                for (icyc in 0 until ncycles()) {
                    var sag = 0.0
                    var fs1 = 0.0
                    var fs2 = 0.0
                    var multpy = 0
                    while (multpy < 5) {
                        x.setEntry(i, xtf + d)
                        fs1 = mfcn.value(x)
                        x.setEntry(i, xtf - d)
                        fs2 = mfcn.value(x)
                        x.setEntry(i, xtf)
                        sag = 0.5 * (fs1 + fs2 - 2.0 * amin)
                        if (sag > prec.eps2()) {
                            break
                        }
                        if (trafo.parameter(i).hasLimits()) {
                            if (d > 0.5) {
                                throw MnHesseFailedException("MnHesse: 2nd derivative zero for parameter")
                            }
                            d *= 10.0
                            if (d > 0.5) {
                                d = 0.51
                            }
                            multpy++
                            continue
                        }
                        d *= 10.0
                        multpy++
                    }
                    if (multpy >= 5) {
                        throw MnHesseFailedException("MnHesse: 2nd derivative zero for parameter")
                    }
                    val g2bfor: Double = g2.getEntry(i)
                    g2.setEntry(i, 2.0 * sag / (d * d))
                    grd.setEntry(i, (fs1 - fs2) / (2.0 * d))
                    gst.setEntry(i, d)
                    dirin.setEntry(i, d)
                    yy.setEntry(i, fs1)
                    val dlast = d
                    d = sqrt(2.0 * aimsag / abs(g2.getEntry(i)))
                    if (trafo.parameter(i).hasLimits()) {
                        d = min(0.5, d)
                    }
                    if (d < dmin) {
                        d = dmin
                    }

                    // see if converged
                    if (abs((d - dlast) / d) < tolerstp()) {
                        break
                    }
                    if (abs((g2.getEntry(i) - g2bfor) / g2.getEntry(i)) < tolerg2()) {
                        break
                    }
                    d = min(d, 10.0 * dlast)
                    d = max(d, 0.1 * dlast)
                }
                vhmat[i, i] = g2.getEntry(i)
                if (mfcn.numOfCalls() - st.nfcn() > maxcalls) {
                    throw MnHesseFailedException("MnHesse: maximum number of allowed function calls exhausted.")
                }
            }
            if (theStrategy.strategy() > 0) {
                // refine first derivative
                val hgc = HessianGradientCalculator(mfcn, trafo, theStrategy)
                val gr: FunctionGradient = hgc.gradient(st.parameters(), FunctionGradient(grd, g2, gst))
                grd = gr.getGradient()
            }

            //off-diagonal elements
            for (i in 0 until n) {
                x.setEntry(i, x.getEntry(i) + dirin.getEntry(i))
                for (j in i + 1 until n) {
                    x.setEntry(j, x.getEntry(j) + dirin.getEntry(j))
                    val fs1: Double = mfcn.value(x)
                    val elem: Double =
                        (fs1 + amin - yy.getEntry(i) - yy.getEntry(j)) / (dirin.getEntry(i) * dirin.getEntry(j))
                    vhmat[i, j] = elem
                    x.setEntry(j, x.getEntry(j) - dirin.getEntry(j))
                }
                x.setEntry(i, x.getEntry(i) - dirin.getEntry(i))
            }

            //verify if matrix pos-def (still 2nd derivative)
            val tmp: MinimumError = MnPosDef.test(MinimumError(vhmat, 1.0), prec)
            vhmat = tmp.invHessian()
            try {
                vhmat.invert()
            } catch (xx: SingularMatrixException) {
                throw MnHesseFailedException("MnHesse: matrix inversion fails!")
            }
            val gr = FunctionGradient(grd, g2, gst)
            if (tmp.isMadePosDef()) {
                MINUITPlugin.logStatic("MnHesse: matrix is invalid!")
                MINUITPlugin.logStatic("MnHesse: matrix is not pos. def.!")
                MINUITPlugin.logStatic("MnHesse: matrix was forced pos. def.")
                return MinimumState(st.parameters(),
                    MinimumError(vhmat, MnMadePosDef()),
                    gr,
                    st.edm(),
                    mfcn.numOfCalls())
            }

            //calculate edm
            val err = MinimumError(vhmat, 0.0)
            val edm: Double = VariableMetricEDMEstimator().estimate(gr, err)
            MinimumState(st.parameters(), err, gr, edm, mfcn.numOfCalls())
        } catch (x: MnHesseFailedException) {
            MINUITPlugin.logStatic(x.message)
            MINUITPlugin.logStatic("MnHesse fails and will return diagonal matrix ")
            var j = 0
            while (j < n) {
                val tmp = if (g2.getEntry(j) < prec.eps2()) 1.0 else 1.0 / g2.getEntry(j)
                vhmat[j, j] = if (tmp < prec.eps2()) 1.0 else tmp
                j++
            }
            MinimumState(st.parameters(),
                MinimumError(vhmat, MnHesseFailed()),
                st.gradient(),
                st.edm(),
                st.nfcn() + mfcn.numOfCalls())
        }
    }

    /// forward interface of MnStrategy
    fun ncycles(): Int {
        return theStrategy.hessianNCycles()
    }

    fun tolerg2(): Double {
        return theStrategy.hessianG2Tolerance()
    }

    fun tolerstp(): Double {
        return theStrategy.hessianStepTolerance()
    }

    private inner class MnHesseFailedException(message: String?) : java.lang.Exception(message)
}