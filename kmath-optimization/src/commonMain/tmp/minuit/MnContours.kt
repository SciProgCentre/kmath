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
 * API class for Contours error analysis (2-dim errors). Minimization has to be
 * done before and minimum must be valid. Possibility to ask only for the points
 * or the points and associated Minos errors.
 *
 * @version $Id$
 * @author Darksnake
 */
class MnContours(fcn: MultiFunction?, min: FunctionMinimum?, stra: MnStrategy?) {
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

    /**
     *
     * contour.
     *
     * @param px a int.
     * @param py a int.
     * @return a [hep.dataforge.MINUIT.ContoursError] object.
     */
    fun contour(px: Int, py: Int): ContoursError {
        return contour(px, py, 1.0)
    }

    /**
     *
     * contour.
     *
     * @param px a int.
     * @param py a int.
     * @param errDef a double.
     * @return a [hep.dataforge.MINUIT.ContoursError] object.
     */
    fun contour(px: Int, py: Int, errDef: Double): ContoursError {
        return contour(px, py, errDef, 20)
    }

    /**
     * Causes a CONTOURS error analysis and returns the result in form of
     * ContoursError. As a by-product ContoursError keeps the MinosError
     * information of parameters parx and pary. The result ContoursError can be
     * easily printed using MnPrint or toString().
     *
     * @param npoints a int.
     * @param px a int.
     * @param py a int.
     * @param errDef a double.
     * @return a [hep.dataforge.MINUIT.ContoursError] object.
     */
    fun contour(px: Int, py: Int, errDef: Double, npoints: Int): ContoursError {
        var errDef = errDef
        errDef *= theMinimum!!.errorDef()
        assert(npoints > 3)
        val maxcalls: Int = 100 * (npoints + 5) * (theMinimum!!.userState().variableParameters() + 1)
        var nfcn = 0
        val result: MutableList<Range> = java.util.ArrayList<Range>(npoints)
        val states: List<MnUserParameterState> = java.util.ArrayList<MnUserParameterState>()
        val toler = 0.05

        //get first four points
        val minos = MnMinos(theFCN, theMinimum, theStrategy)
        val valx: Double = theMinimum!!.userState().value(px)
        val valy: Double = theMinimum!!.userState().value(py)
        val mex: MinosError = minos.minos(px, errDef)
        nfcn += mex.nfcn()
        if (!mex.isValid()) {
            MINUITPlugin.logStatic("MnContours is unable to find first two points.")
            return ContoursError(px, py, result, mex, mex, nfcn)
        }
        val ex: Range = mex.range()
        val mey: MinosError = minos.minos(py, errDef)
        nfcn += mey.nfcn()
        if (!mey.isValid()) {
            MINUITPlugin.logStatic("MnContours is unable to find second two points.")
            return ContoursError(px, py, result, mex, mey, nfcn)
        }
        val ey: Range = mey.range()
        val migrad = MnMigrad(theFCN,
            theMinimum!!.userState().copy(),
            MnStrategy(max(0, theStrategy!!.strategy() - 1)))
        migrad.fix(px)
        migrad.setValue(px, valx + ex.getSecond())
        val exy_up: FunctionMinimum = migrad.minimize()
        nfcn += exy_up.nfcn()
        if (!exy_up.isValid()) {
            MINUITPlugin.logStatic("MnContours is unable to find upper y value for x parameter $px.")
            return ContoursError(px, py, result, mex, mey, nfcn)
        }
        migrad.setValue(px, valx + ex.getFirst())
        val exy_lo: FunctionMinimum = migrad.minimize()
        nfcn += exy_lo.nfcn()
        if (!exy_lo.isValid()) {
            MINUITPlugin.logStatic("MnContours is unable to find lower y value for x parameter $px.")
            return ContoursError(px, py, result, mex, mey, nfcn)
        }
        val migrad1 = MnMigrad(theFCN,
            theMinimum!!.userState().copy(),
            MnStrategy(max(0, theStrategy!!.strategy() - 1)))
        migrad1.fix(py)
        migrad1.setValue(py, valy + ey.getSecond())
        val eyx_up: FunctionMinimum = migrad1.minimize()
        nfcn += eyx_up.nfcn()
        if (!eyx_up.isValid()) {
            MINUITPlugin.logStatic("MnContours is unable to find upper x value for y parameter $py.")
            return ContoursError(px, py, result, mex, mey, nfcn)
        }
        migrad1.setValue(py, valy + ey.getFirst())
        val eyx_lo: FunctionMinimum = migrad1.minimize()
        nfcn += eyx_lo.nfcn()
        if (!eyx_lo.isValid()) {
            MINUITPlugin.logStatic("MnContours is unable to find lower x value for y parameter $py.")
            return ContoursError(px, py, result, mex, mey, nfcn)
        }
        val scalx: Double = 1.0 / (ex.getSecond() - ex.getFirst())
        val scaly: Double = 1.0 / (ey.getSecond() - ey.getFirst())
        result.add(Range(valx + ex.getFirst(), exy_lo.userState().value(py)))
        result.add(Range(eyx_lo.userState().value(px), valy + ey.getFirst()))
        result.add(Range(valx + ex.getSecond(), exy_up.userState().value(py)))
        result.add(Range(eyx_up.userState().value(px), valy + ey.getSecond()))
        val upar: MnUserParameterState = theMinimum!!.userState().copy()
        upar.fix(px)
        upar.fix(py)
        val par = intArrayOf(px, py)
        val cross = MnFunctionCross(theFCN, upar, theMinimum!!.fval(), theStrategy, errDef)
        for (i in 4 until npoints) {
            var idist1: Range = result[result.size - 1]
            var idist2: Range = result[0]
            var pos2 = 0
            val distx: Double = idist1.getFirst() - idist2.getFirst()
            val disty: Double = idist1.getSecond() - idist2.getSecond()
            var bigdis = scalx * scalx * distx * distx + scaly * scaly * disty * disty
            for (j in 0 until result.size - 1) {
                val ipair: Range = result[j]
                val distx2: Double = ipair.getFirst() - result[j + 1].getFirst()
                val disty2: Double = ipair.getSecond() - result[j + 1].getSecond()
                val dist = scalx * scalx * distx2 * distx2 + scaly * scaly * disty2 * disty2
                if (dist > bigdis) {
                    bigdis = dist
                    idist1 = ipair
                    idist2 = result[j + 1]
                    pos2 = j + 1
                }
            }
            val a1 = 0.5
            val a2 = 0.5
            var sca = 1.0
            while (true) {
                if (nfcn > maxcalls) {
                    MINUITPlugin.logStatic("MnContours: maximum number of function calls exhausted.")
                    return ContoursError(px, py, result, mex, mey, nfcn)
                }
                val xmidcr: Double = a1 * idist1.getFirst() + a2 * idist2.getFirst()
                val ymidcr: Double = a1 * idist1.getSecond() + a2 * idist2.getSecond()
                val xdir: Double = idist2.getSecond() - idist1.getSecond()
                val ydir: Double = idist1.getFirst() - idist2.getFirst()
                val scalfac: Double =
                    sca * max(abs(xdir * scalx), abs(ydir * scaly))
                val xdircr = xdir / scalfac
                val ydircr = ydir / scalfac
                val pmid = doubleArrayOf(xmidcr, ymidcr)
                val pdir = doubleArrayOf(xdircr, ydircr)
                val opt: MnCross = cross.cross(par, pmid, pdir, toler, maxcalls)
                nfcn += opt.nfcn()
                if (opt.isValid()) {
                    val aopt: Double = opt.value()
                    if (pos2 == 0) {
                        result.add(Range(xmidcr + aopt * xdircr, ymidcr + aopt * ydircr))
                    } else {
                        result.add(pos2, Range(xmidcr + aopt * xdircr, ymidcr + aopt * ydircr))
                    }
                    break
                }
                if (sca < 0.0) {
                    MINUITPlugin.logStatic("MnContours is unable to find point " + (i + 1) + " on contour.")
                    MINUITPlugin.logStatic("MnContours finds only $i points.")
                    return ContoursError(px, py, result, mex, mey, nfcn)
                }
                sca = -1.0
            }
        }
        return ContoursError(px, py, result, mex, mey, nfcn)
    }

    /**
     *
     * points.
     *
     * @param px a int.
     * @param py a int.
     * @return a [List] object.
     */
    fun points(px: Int, py: Int): List<Range> {
        return points(px, py, 1.0)
    }

    /**
     *
     * points.
     *
     * @param px a int.
     * @param py a int.
     * @param errDef a double.
     * @return a [List] object.
     */
    fun points(px: Int, py: Int, errDef: Double): List<Range> {
        return points(px, py, errDef, 20)
    }

    /**
     * Calculates one function contour of FCN with respect to parameters parx
     * and pary. The return value is a list of (x,y) points. FCN minimized
     * always with respect to all other n - 2 variable parameters (if any).
     * MINUITPlugin will try to find n points on the contour (default 20). To
     * calculate more than one contour, the user needs to set the error
     * definition in its FCN to the appropriate value for the desired confidence
     * level and call this method for each contour.
     *
     * @param npoints a int.
     * @param px a int.
     * @param py a int.
     * @param errDef a double.
     * @return a [List] object.
     */
    fun points(px: Int, py: Int, errDef: Double, npoints: Int): List<Range> {
        val cont: ContoursError = contour(px, py, errDef, npoints)
        return cont.points()
    }

    fun strategy(): MnStrategy? {
        return theStrategy
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