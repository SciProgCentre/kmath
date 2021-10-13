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

import org.apache.commons.math3.linear.RealVector
import ru.inr.mass.minuit.*

/**
 *
 * @version $Id$
 */
internal object MnLineSearch {
    fun search(
        fcn: MnFcn,
        st: MinimumParameters,
        step: RealVector,
        gdel: Double,
        prec: MnMachinePrecision
    ): MnParabolaPoint {
        var overal = 1000.0
        var undral = -100.0
        val toler = 0.05
        var slamin = 0.0
        val slambg = 5.0
        val alpha = 2.0
        val maxiter = 12
        var niter = 0
        for (i in 0 until step.getDimension()) {
            if (abs(step.getEntry(i)) < prec.eps()) {
                continue
            }
            val ratio: Double = abs(st.vec().getEntry(i) / step.getEntry(i))
            if (abs(slamin) < prec.eps()) {
                slamin = ratio
            }
            if (ratio < slamin) {
                slamin = ratio
            }
        }
        if (abs(slamin) < prec.eps()) {
            slamin = prec.eps()
        }
        slamin *= prec.eps2()
        val F0: Double = st.fval()
        val F1: Double = fcn.value(MnUtils.add(st.vec(), step))
        var fvmin: Double = st.fval()
        var xvmin = 0.0
        if (F1 < F0) {
            fvmin = F1
            xvmin = 1.0
        }
        var toler8 = toler
        var slamax = slambg
        var flast = F1
        var slam = 1.0
        var iterate = false
        var p0 = MnParabolaPoint(0.0, F0)
        var p1 = MnParabolaPoint(slam, flast)
        var F2 = 0.0
        do {
            // cut toler8 as function goes up
            iterate = false
            val pb: MnParabola = MnParabolaFactory.create(p0, gdel, p1)
            var denom = 2.0 * (flast - F0 - gdel * slam) / (slam * slam)
            if (abs(denom) < prec.eps()) {
                denom = -0.1 * gdel
                slam = 1.0
            }
            if (abs(denom) > prec.eps()) {
                slam = -gdel / denom
            }
            if (slam < 0.0) {
                slam = slamax
            }
            if (slam > slamax) {
                slam = slamax
            }
            if (slam < toler8) {
                slam = toler8
            }
            if (slam < slamin) {
                return MnParabolaPoint(xvmin, fvmin)
            }
            if (abs(slam - 1.0) < toler8 && p1.y() < p0.y()) {
                return MnParabolaPoint(xvmin, fvmin)
            }
            if (abs(slam - 1.0) < toler8) {
                slam = 1.0 + toler8
            }
            F2 = fcn.value(MnUtils.add(st.vec(), MnUtils.mul(step, slam)))
            if (F2 < fvmin) {
                fvmin = F2
                xvmin = slam
            }
            if (p0.y() - prec.eps() < fvmin && fvmin < p0.y() + prec.eps()) {
                iterate = true
                flast = F2
                toler8 = toler * slam
                overal = slam - toler8
                slamax = overal
                p1 = MnParabolaPoint(slam, flast)
                niter++
            }
        } while (iterate && niter < maxiter)
        if (niter >= maxiter) {
            // exhausted max number of iterations
            return MnParabolaPoint(xvmin, fvmin)
        }
        var p2 = MnParabolaPoint(slam, F2)
        do {
            slamax = max(slamax, alpha * abs(xvmin))
            val pb: MnParabola = MnParabolaFactory.create(p0, p1, p2)
            if (pb.a() < prec.eps2()) {
                val slopem: Double = 2.0 * pb.a() * xvmin + pb.b()
                slam = if (slopem < 0.0) {
                    xvmin + slamax
                } else {
                    xvmin - slamax
                }
            } else {
                slam = pb.min()
                if (slam > xvmin + slamax) {
                    slam = xvmin + slamax
                }
                if (slam < xvmin - slamax) {
                    slam = xvmin - slamax
                }
            }
            if (slam > 0.0) {
                if (slam > overal) {
                    slam = overal
                }
            } else {
                if (slam < undral) {
                    slam = undral
                }
            }
            var F3 = 0.0
            do {
                iterate = false
                val toler9: Double = max(toler8, abs(toler8 * slam))
                // min. of parabola at one point
                if (abs(p0.x() - slam) < toler9 || abs(p1.x() - slam) < toler9 || abs(
                        p2.x() - slam) < toler9
                ) {
                    return MnParabolaPoint(xvmin, fvmin)
                }
                F3 = fcn.value(MnUtils.add(st.vec(), MnUtils.mul(step, slam)))
                // if latest point worse than all three previous, cut step
                if (F3 > p0.y() && F3 > p1.y() && F3 > p2.y()) {
                    if (slam > xvmin) {
                        overal = min(overal, slam - toler8)
                    }
                    if (slam < xvmin) {
                        undral = max(undral, slam + toler8)
                    }
                    slam = 0.5 * (slam + xvmin)
                    iterate = true
                    niter++
                }
            } while (iterate && niter < maxiter)
            if (niter >= maxiter) {
                // exhausted max number of iterations
                return MnParabolaPoint(xvmin, fvmin)
            }

            // find worst previous point out of three and replace
            val p3 = MnParabolaPoint(slam, F3)
            if (p0.y() > p1.y() && p0.y() > p2.y()) {
                p0 = p3
            } else if (p1.y() > p0.y() && p1.y() > p2.y()) {
                p1 = p3
            } else {
                p2 = p3
            }
            if (F3 < fvmin) {
                fvmin = F3
                xvmin = slam
            } else {
                if (slam > xvmin) {
                    overal = min(overal, slam - toler8)
                }
                if (slam < xvmin) {
                    undral = max(undral, slam + toler8)
                }
            }
            niter++
        } while (niter < maxiter)
        return MnParabolaPoint(xvmin, fvmin)
    }
}