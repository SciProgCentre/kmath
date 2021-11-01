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
import kotlin.math.*

/**
 *
 * @version $Id$
 */
internal class MnFunctionCross(
    fcn: MultiFunction?,
    state: MnUserParameterState,
    fval: Double,
    stra: MnStrategy?,
    errorDef: Double
) {
    private val theErrorDef: Double
    private val theFCN: MultiFunction?
    private val theFval: Double
    private val theState: MnUserParameterState
    private val theStrategy: MnStrategy?
    fun cross(par: IntArray, pmid: DoubleArray, pdir: DoubleArray, tlr: Double, maxcalls: Int): MnCross {
        val npar = par.size
        var nfcn = 0
        val prec: MnMachinePrecision = theState.precision()
        val tlf = tlr * theErrorDef
        var tla = tlr
        val maxitr = 15
        var ipt = 0
        val aminsv = theFval
        val aim = aminsv + theErrorDef
        var aopt = 0.0
        var limset = false
        val alsb = DoubleArray(3)
        val flsb = DoubleArray(3)
        val up = theErrorDef
        var aulim = 100.0
        for (i in par.indices) {
            val kex = par[i]
            if (theState.parameter(kex).hasLimits()) {
                val zmid = pmid[i]
                val zdir = pdir[i]
                if (abs(zdir) < theState.precision().eps()) {
                    continue
                }
                if (zdir > 0.0 && theState.parameter(kex).hasUpperLimit()) {
                    val zlim: Double = theState.parameter(kex).upperLimit()
                    aulim = min(aulim, (zlim - zmid) / zdir)
                } else if (zdir < 0.0 && theState.parameter(kex).hasLowerLimit()) {
                    val zlim: Double = theState.parameter(kex).lowerLimit()
                    aulim = min(aulim, (zlim - zmid) / zdir)
                }
            }
        }
        if (aulim < aopt + tla) {
            limset = true
        }
        val migrad = MnMigrad(theFCN, theState, MnStrategy(max(0, theStrategy!!.strategy() - 1)))
        for (i in 0 until npar) {
            migrad.setValue(par[i], pmid[i])
        }
        val min0: FunctionMinimum = migrad.minimize(maxcalls, tlr)
        nfcn += min0.nfcn()
        if (min0.hasReachedCallLimit()) {
            return MnCross(min0.userState(), nfcn, MnCross.CrossFcnLimit())
        }
        if (!min0.isValid()) {
            return MnCross(nfcn)
        }
        if (limset && min0.fval() < aim) {
            return MnCross(min0.userState(), nfcn, MnCross.CrossParLimit())
        }
        ipt++
        alsb[0] = 0.0
        flsb[0] = min0.fval()
        flsb[0] = max(flsb[0], aminsv + 0.1 * up)
        aopt = sqrt(up / (flsb[0] - aminsv)) - 1.0
        if (abs(flsb[0] - aim) < tlf) {
            return MnCross(aopt, min0.userState(), nfcn)
        }
        if (aopt > 1.0) {
            aopt = 1.0
        }
        if (aopt < -0.5) {
            aopt = -0.5
        }
        limset = false
        if (aopt > aulim) {
            aopt = aulim
            limset = true
        }
        for (i in 0 until npar) {
            migrad.setValue(par[i], pmid[i] + aopt * pdir[i])
        }
        var min1: FunctionMinimum = migrad.minimize(maxcalls, tlr)
        nfcn += min1.nfcn()
        if (min1.hasReachedCallLimit()) {
            return MnCross(min1.userState(), nfcn, MnCross.CrossFcnLimit())
        }
        if (!min1.isValid()) {
            return MnCross(nfcn)
        }
        if (limset && min1.fval() < aim) {
            return MnCross(min1.userState(), nfcn, MnCross.CrossParLimit())
        }
        ipt++
        alsb[1] = aopt
        flsb[1] = min1.fval()
        var dfda = (flsb[1] - flsb[0]) / (alsb[1] - alsb[0])
        var ecarmn = 0.0
        var ecarmx = 0.0
        var ibest = 0
        var iworst = 0
        var noless = 0
        var min2: FunctionMinimum? = null
        L300@ while (true) {
            if (dfda < 0.0) {
                val maxlk = maxitr - ipt
                for (it in 0 until maxlk) {
                    alsb[0] = alsb[1]
                    flsb[0] = flsb[1]
                    aopt = alsb[0] + 0.2 * it
                    limset = false
                    if (aopt > aulim) {
                        aopt = aulim
                        limset = true
                    }
                    for (i in 0 until npar) {
                        migrad.setValue(par[i], pmid[i] + aopt * pdir[i])
                    }
                    min1 = migrad.minimize(maxcalls, tlr)
                    nfcn += min1.nfcn()
                    if (min1.hasReachedCallLimit()) {
                        return MnCross(min1.userState(), nfcn, MnCross.CrossFcnLimit())
                    }
                    if (!min1.isValid()) {
                        return MnCross(nfcn)
                    }
                    if (limset && min1.fval() < aim) {
                        return MnCross(min1.userState(), nfcn, MnCross.CrossParLimit())
                    }
                    ipt++
                    alsb[1] = aopt
                    flsb[1] = min1.fval()
                    dfda = (flsb[1] - flsb[0]) / (alsb[1] - alsb[0])
                    if (dfda > 0.0) {
                        break
                    }
                }
                if (ipt > maxitr) {
                    return MnCross(nfcn)
                }
            }
            L460@ while (true) {
                aopt = alsb[1] + (aim - flsb[1]) / dfda
                val fdist: Double =
                    min(abs(aim - flsb[0]), abs(aim - flsb[1]))
                val adist: Double =
                    min(abs(aopt - alsb[0]), abs(aopt - alsb[1]))
                tla = tlr
                if (abs(aopt) > 1.0) {
                    tla = tlr * abs(aopt)
                }
                if (adist < tla && fdist < tlf) {
                    return MnCross(aopt, min1.userState(), nfcn)
                }
                if (ipt > maxitr) {
                    return MnCross(nfcn)
                }
                val bmin: Double = min(alsb[0], alsb[1]) - 1.0
                if (aopt < bmin) {
                    aopt = bmin
                }
                val bmax: Double = max(alsb[0], alsb[1]) + 1.0
                if (aopt > bmax) {
                    aopt = bmax
                }
                limset = false
                if (aopt > aulim) {
                    aopt = aulim
                    limset = true
                }
                for (i in 0 until npar) {
                    migrad.setValue(par[i], pmid[i] + aopt * pdir[i])
                }
                min2 = migrad.minimize(maxcalls, tlr)
                nfcn += min2.nfcn()
                if (min2.hasReachedCallLimit()) {
                    return MnCross(min2.userState(), nfcn, CrossFcnLimit())
                }
                if (!min2.isValid()) {
                    return MnCross(nfcn)
                }
                if (limset && min2.fval() < aim) {
                    return MnCross(min2.userState(), nfcn, MnCross.CrossParLimit())
                }
                ipt++
                alsb[2] = aopt
                flsb[2] = min2.fval()
                ecarmn = abs(flsb[2] - aim)
                ecarmx = 0.0
                ibest = 2
                iworst = 0
                noless = 0
                for (i in 0..2) {
                    val ecart: Double = abs(flsb[i] - aim)
                    if (ecart > ecarmx) {
                        ecarmx = ecart
                        iworst = i
                    }
                    if (ecart < ecarmn) {
                        ecarmn = ecart
                        ibest = i
                    }
                    if (flsb[i] < aim) {
                        noless++
                    }
                }
                if (noless == 1 || noless == 2) {
                    break@L300
                }
                if (noless == 0 && ibest != 2) {
                    return MnCross(nfcn)
                }
                if (noless == 3 && ibest != 2) {
                    alsb[1] = alsb[2]
                    flsb[1] = flsb[2]
                    continue@L300
                }
                flsb[iworst] = flsb[2]
                alsb[iworst] = alsb[2]
                dfda = (flsb[1] - flsb[0]) / (alsb[1] - alsb[0])
            }
        }
        do {
            val parbol: MnParabola = MnParabolaFactory.create(MnParabolaPoint(alsb[0], flsb[0]),
                MnParabolaPoint(alsb[1], flsb[1]),
                MnParabolaPoint(
                    alsb[2], flsb[2]))
            val coeff1: Double = parbol.c()
            val coeff2: Double = parbol.b()
            val coeff3: Double = parbol.a()
            val determ = coeff2 * coeff2 - 4.0 * coeff3 * (coeff1 - aim)
            if (determ < prec.eps()) {
                return MnCross(nfcn)
            }
            val rt: Double = sqrt(determ)
            val x1 = (-coeff2 + rt) / (2.0 * coeff3)
            val x2 = (-coeff2 - rt) / (2.0 * coeff3)
            val s1 = coeff2 + 2.0 * x1 * coeff3
            val s2 = coeff2 + 2.0 * x2 * coeff3
            if (s1 * s2 > 0.0) {
                MINUITPlugin.logStatic("MnFunctionCross problem 1")
            }
            aopt = x1
            var slope = s1
            if (s2 > 0.0) {
                aopt = x2
                slope = s2
            }
            tla = tlr
            if (abs(aopt) > 1.0) {
                tla = tlr * abs(aopt)
            }
            if (abs(aopt - alsb[ibest]) < tla && abs(flsb[ibest] - aim) < tlf) {
                return MnCross(aopt, min2!!.userState(), nfcn)
            }
            var ileft = 3
            var iright = 3
            var iout = 3
            ibest = 0
            ecarmx = 0.0
            ecarmn = abs(aim - flsb[0])
            for (i in 0..2) {
                val ecart: Double = abs(flsb[i] - aim)
                if (ecart < ecarmn) {
                    ecarmn = ecart
                    ibest = i
                }
                if (ecart > ecarmx) {
                    ecarmx = ecart
                }
                if (flsb[i] > aim) {
                    if (iright == 3) {
                        iright = i
                    } else if (flsb[i] > flsb[iright]) {
                        iout = i
                    } else {
                        iout = iright
                        iright = i
                    }
                } else if (ileft == 3) {
                    ileft = i
                } else if (flsb[i] < flsb[ileft]) {
                    iout = i
                } else {
                    iout = ileft
                    ileft = i
                }
            }
            if (ecarmx > 10.0 * abs(flsb[iout] - aim)) {
                aopt = 0.5 * (aopt + 0.5 * (alsb[iright] + alsb[ileft]))
            }
            var smalla = 0.1 * tla
            if (slope * smalla > tlf) {
                smalla = tlf / slope
            }
            val aleft = alsb[ileft] + smalla
            val aright = alsb[iright] - smalla
            if (aopt < aleft) {
                aopt = aleft
            }
            if (aopt > aright) {
                aopt = aright
            }
            if (aleft > aright) {
                aopt = 0.5 * (aleft + aright)
            }
            limset = false
            if (aopt > aulim) {
                aopt = aulim
                limset = true
            }
            for (i in 0 until npar) {
                migrad.setValue(par[i], pmid[i] + aopt * pdir[i])
            }
            min2 = migrad.minimize(maxcalls, tlr)
            nfcn += min2.nfcn()
            if (min2.hasReachedCallLimit()) {
                return MnCross(min2.userState(), nfcn, CrossFcnLimit())
            }
            if (!min2.isValid()) {
                return MnCross(nfcn)
            }
            if (limset && min2.fval() < aim) {
                return MnCross(min2.userState(), nfcn, CrossParLimit())
            }
            ipt++
            alsb[iout] = aopt
            flsb[iout] = min2.fval()
            ibest = iout
        } while (ipt < maxitr)
        return MnCross(nfcn)
    }

    init {
        theFCN = fcn
        theState = state
        theFval = fval
        theStrategy = stra
        theErrorDef = errorDef
    }
}